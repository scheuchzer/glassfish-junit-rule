/*
Copyright 2013 Thomas Scheuchzer, java-adventures.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.ja.junit.rule.glassfish;

import static org.junit.Assert.fail;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JUnit Rule that controlls an embedded Glassfish instance.
 * 
 * @author Thomas Scheuchzer, www.java-adventures.com
 *
 */
public class GlassfishController extends ExternalResource {
	private Logger log = LoggerFactory.getLogger(GlassfishController.class);
	private static GlassFishRuntime gfr;
	private static GlassFish gf;
	private final Collection<StartupCommand> startupCommands = new ArrayBlockingQueue<>(10000);
	private final Stack<TeardownCommand> teardownCommands = new Stack<>();
	
	private GlassFishFuture glassfishFuture = new GlassFishFuture();
	
	private TemporaryFolder tmpFolder = new TemporaryFolder();
	private File loginConf;
	private GlassFishProperties props = new GlassFishProperties();

	public GlassfishController() {
	}

	@Override
	protected void before() throws Throwable {
		tmpFolder.create();
		start();
	}

	public void start() {
		setupLoginConfig();
		try {
			if (gfr == null) {
				gfr = GlassFishRuntime.bootstrap();
				gf = gfr.newGlassFish(props);
				glassfishFuture.setGlassFish(gf);
				gf.start();
			}
// TODO: worker thread
			for (StartupCommand command : startupCommands) {
				try {
					command.execute();
				} catch (Exception e) {
					log.error("Startup failed. ", e);
					fail();
				}
			}
		} catch (GlassFishException e) {
			throw new RuntimeException("Startup failed", e);
		}
	}

	private void setupLoginConfig() {
		if (loginConf == null) {
			return;
		}
		log.info("Login-Config={}", loginConf.getAbsolutePath());
		final String loginConfProperty = "java.security.auth.login.config";
		final String loginConfBackup = System.getProperty(loginConfProperty);
		System.setProperty(loginConfProperty, loginConf.getAbsolutePath());
		teardownCommands.add(new TeardownCommand() {

			@Override
			public void execute() {
				System.setProperty(loginConfProperty, loginConfBackup);

			}
		});
	}

	public void stop() {
		try {
			gf.stop();
			gf.dispose();
			gfr.shutdown();
		} catch (Exception e) {
			throw new RuntimeException("Shutdown failed", e);
		}
		log.info("GF has been shutdown");
	}

	@Override
	protected void after() {
		cleanup();
		// stop();
		tmpFolder.delete();
	}

	public void cleanup() {
		log.info("Executing {} cleanup commands.", teardownCommands.size());
		while (!teardownCommands.isEmpty()) {
			try {
				teardownCommands.pop().execute();
			} catch (Exception e) {
				log.info("CleanupCommand failed");
			}
		}
	}


	@SuppressWarnings("unchecked")
	public <T> T lookup(String jndiName) {
		try {
			return (T) new InitialContext().lookup(jndiName);
		} catch (NamingException e) {
			throw new RuntimeException("Lookup failed", e);
		}
	}

	public GlassfishController setLoginConf(File loginConf) {
		this.loginConf = loginConf;
		return this;
	}

	public GlassfishController setHttpPort(int port) {
		props.setPort("http-listener", port);
		return this;
	}

	public HttpURLConnection executeHttpRequest(String path) {
		return executeHttpRequest(path, null, null);
	}

	public HttpURLConnection executeHttpRequest(final String path,
			final String username, final String password) {
		return new HttpExecutor(path, username, password, props).execute();
	}
}

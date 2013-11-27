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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishRuntime;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import com.ja.junit.rule.glassfish.admin.AbstractAdminObject;
import com.ja.junit.rule.glassfish.execute.HttpExecutor;

/**
 * A JUnit Rule that controlls an embedded Glassfish instance.
 * 
 * @author Thomas Scheuchzer, www.java-adventures.com
 * 
 */
@Slf4j
public class GlassfishController extends ExternalResource {
	private static GlassFishRuntime gfr;
	private static GlassFish gf;
	private final Collection<AbstractAdminObject> startupCommands = new ArrayList<>();
	private final Stack<AbstractAdminObject> teardownCommands = new Stack<>();

	private final GlassfishFuture glassfishFuture = new GlassfishFuture();

	private final TemporaryFolder tmpFolder = new TemporaryFolder();

	private final GlassfishPreStartConfigurator configurator;

	private final TestContext ctx = new TestContext(tmpFolder, startupCommands,
			teardownCommands, glassfishFuture);

	public GlassfishController(
			@NonNull final GlassfishPreStartConfigurator configurator) {
		this.configurator = configurator;
	}

	@Override
	protected void before() throws Throwable {
		tmpFolder.create();
		start();
	}

	public void start() {
		try {
			if (gfr == null) {
				gfr = GlassFishRuntime.bootstrap();
			}
			if (gf == null) {
				gf = gfr.newGlassFish(configurator.getProps());
				gf.start();
			}
			glassfishFuture.setGlassFish(gf);

			log.info("Executing {} startup commands.", startupCommands.size());
			for (AbstractAdminObject command : startupCommands) {
				try {
					log.info("Executing command: {}", command);
					command.execute(ctx);
				} catch (Exception e) {
					log.error("Startup failed. ", e);
					fail();
				}
			}
		} catch (GlassFishException e) {
			log.error("Startup failed", e);
			fail();
		}
	}

	public void stop() {
		try {
			gf.stop();
			gf.dispose();
			gf = null;
			gfr.shutdown();
			gfr = null;
		} catch (Exception e) {
			throw new RuntimeException("Shutdown failed", e);
		}
		log.info("GF has been shutdown");
	}

	@Override
	protected void after() {
		cleanup();
		/*
		 * This is strange. If we stop Glassfish after every test,
		 * authentication won't work anymore. Every call is OK.
		 */
		// stop();
		tmpFolder.delete();
	}

	public void cleanup() {
		log.info("Executing {} teardown commands.", teardownCommands.size());
		while (!teardownCommands.isEmpty()) {
			try {
				teardownCommands.pop().execute(ctx);
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

	public Response executeHttpRequest(String path) {
		return executeHttpRequest(path, null, null);
	}

	public Response executeHttpRequest(final String path,
			final String username, final String password) {
		return new HttpExecutor(path, username, password,
				configurator.getProps()).execute();
	}

	public GlassfishController create(AbstractAdminObject cfg) {
		try {
			ctx.addStartCommand(cfg);
		} catch (Exception e) {
			log.error("Command execution failed", cfg);
			fail();
		}
		return this;
	}

}

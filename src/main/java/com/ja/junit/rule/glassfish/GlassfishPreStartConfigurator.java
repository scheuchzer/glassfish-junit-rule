package com.ja.junit.rule.glassfish;

import java.io.File;
import java.util.Stack;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.glassfish.embeddable.GlassFishProperties;

@Slf4j
public class GlassfishPreStartConfigurator {

	@Getter
	private final Stack<TeardownCommand> teardownCommands = new Stack<>();

	@Getter
	private final GlassFishProperties props = new GlassFishProperties();

	public GlassfishPreStartConfigurator setLoginConf(File loginConf) {
		log.info("Login-Config={}", loginConf.getAbsolutePath());
		final String loginConfProperty = "java.security.auth.login.config";
		final String loginConfBackup = System.getProperty(loginConfProperty);
		System.setProperty(loginConfProperty, loginConf.getAbsolutePath());
		teardownCommands.add(new TeardownCommand() {

			@Override
			public void execute(final TestContext ctx) {
				System.setProperty(loginConfProperty, loginConfBackup);

			}
		});
		return this;
	}

	public GlassfishPreStartConfigurator setHttpPort(int port) {
		props.setPort("http-listener", port);
		return this;
	}
}

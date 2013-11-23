package com.ja.junit.rule.glassfish;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.GlassFishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthRealm {

	private Logger log = LoggerFactory.getLogger(AuthRealm.class);

	protected TestContext ctx;

	public AuthRealm(TestContext ctx) {
		this.ctx = ctx;
	}
	public void list() throws GlassFishException {
		log.info("list auth realm");
		CommandResult result = ctx.runCommand("list-auth-realms");
		log.info("auth-realms={}", result.getOutput());
	}
}

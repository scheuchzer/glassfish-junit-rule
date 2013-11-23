package com.ja.junit.rule.glassfish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorResource {

	private Logger log = LoggerFactory.getLogger(ConnectorResource.class);

	private TestContext ctx;

	public ConnectorResource(TestContext ctx) {
		this.ctx = ctx;
	}

	public void create(final String poolName, final String jndiName) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				log.info("Create connection resource");
				ctx.runCommand("create-connector-resource", "--poolname="
						+ poolName, jndiName);
			}
		});
		ctx.add(new TeardownCommand() {

			@Override
			public void execute() throws Exception {
				delete(poolName);

			}
		});
	}

	public void delete(final String jndiName) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				log.info("delete connetor resource");
				ctx.runCommand("delete-connector-resource", jndiName);
			}
		});
	}
}

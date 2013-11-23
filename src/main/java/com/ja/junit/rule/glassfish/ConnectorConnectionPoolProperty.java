package com.ja.junit.rule.glassfish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorConnectionPoolProperty {

	private Logger log = LoggerFactory
			.getLogger(ConnectorConnectionPoolProperty.class);

	private TestContext ctx;

	public ConnectorConnectionPoolProperty(TestContext ctx) {
		this.ctx = ctx;
	}

	public void create(final String poolName, final String key,
			final String value) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				log.info("Create connection pool property");
				String cmd = String
						.format("domain.resources.connector-connection-pool.%s.property.%s=%s",
								poolName, key, value);
				ctx.runCommand("set", cmd);
			}
		});

	}

}

package com.ja.junit.rule.glassfish;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorConnectionPool {

	private Logger log = LoggerFactory.getLogger(ConnectorConnectionPool.class);

	private TestContext ctx;

	public ConnectorConnectionPool(TestContext ctx) {
		this.ctx = ctx;
	}

	public void create(final String raname,
			final Class<?> connectionDefinition, final String poolName,
			final Properties connectionConfigProperties) {

		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				log.info("Create connection pool");
				ctx.runCommand("create-connector-connection-pool", "--raname="
						+ raname, "--connectiondefinition="
						+ connectionDefinition.getName(), poolName);

			}
		});

		if (connectionConfigProperties != null) {
			ConnectorConnectionPoolProperty prop = new ConnectorConnectionPoolProperty(
					ctx);
			for (Map.Entry<Object, Object> entry : connectionConfigProperties
					.entrySet()) {
				prop.create(poolName, (String) entry.getKey(),
						(String) entry.getValue());
			}
		}
		ctx.add(new TeardownCommand() {

			@Override
			public void execute() {
				delete(poolName);

			}
		});
	}

	public void delete(final String poolName) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				log.info("delete connector connection pool");
				ctx.runCommand("delete-connector-connection-pool", poolName);
			}
		});
	}
}

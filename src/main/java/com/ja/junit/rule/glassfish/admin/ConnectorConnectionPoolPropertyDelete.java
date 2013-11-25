package com.ja.junit.rule.glassfish.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class ConnectorConnectionPoolPropertyDelete extends AbstractAdminObject {

	private final String poolName;
	private final String key;
	private final String value;

	@Override
	public void execute(final TestContext ctx) {
		log.info("Create connection pool property");
		String cmd = String.format(
				"domain.resources.connector-connection-pool.%s.property.%s=%s",
				poolName, key, value);
		ctx.runCommand("set", cmd);
	}

}

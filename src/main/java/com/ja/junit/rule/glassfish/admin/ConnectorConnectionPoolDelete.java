package com.ja.junit.rule.glassfish.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class ConnectorConnectionPoolDelete extends AbstractAdminObject {

	private final String poolName;

	@Override
	public void execute(final TestContext ctx) {
		log.info("delete connector connection pool");
		ctx.runCommand("delete-connector-connection-pool", poolName);
	}
}

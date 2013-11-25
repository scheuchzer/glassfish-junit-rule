package com.ja.junit.rule.glassfish.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class ConnectorResourceCreate extends AbstractAdminObject {

	private final String poolName;
	private final String jndiName;

	@Override
	public void execute(final TestContext ctx) {
		log.info("Create connection resource");
		ctx.runCommand("create-connector-resource", "--poolname=" + poolName,
				jndiName);
		ctx.addTeardownCommand(new ConnectorResourceDelete(jndiName));
	}

}

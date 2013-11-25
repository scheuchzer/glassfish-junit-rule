package com.ja.junit.rule.glassfish.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class ConnectorResourceDelete extends AbstractAdminObject {

	private final String jndiName;

	@Override
	public void execute(final TestContext ctx) {
		log.info("delete connetor resource");
		ctx.runCommand("delete-connector-resource", jndiName);
	}
}

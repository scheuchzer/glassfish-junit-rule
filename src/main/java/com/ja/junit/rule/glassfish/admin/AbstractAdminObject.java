package com.ja.junit.rule.glassfish.admin;

import com.ja.junit.rule.glassfish.TestContext;

public abstract class AbstractAdminObject {

	public abstract void execute(final TestContext ctx) throws Exception;

}

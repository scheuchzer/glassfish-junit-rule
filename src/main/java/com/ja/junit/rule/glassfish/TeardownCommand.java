package com.ja.junit.rule.glassfish;

public interface TeardownCommand {

	void execute(final TestContext ctx) throws Exception;
}

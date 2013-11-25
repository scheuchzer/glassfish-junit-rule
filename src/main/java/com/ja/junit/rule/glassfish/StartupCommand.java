package com.ja.junit.rule.glassfish;

public interface StartupCommand {

	void execute(final TestContext ctx) throws Exception;
}

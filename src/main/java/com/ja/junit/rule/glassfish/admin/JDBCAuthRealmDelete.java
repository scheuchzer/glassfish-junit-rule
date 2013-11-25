package com.ja.junit.rule.glassfish.admin;

import static org.junit.Assert.fail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandResult.ExitStatus;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class JDBCAuthRealmDelete extends AbstractAdminObject {

	private final String realmName;

	@Override
	public void execute(final TestContext ctx) throws Exception {
		log.info("delete auth realm");
		CommandResult result = ctx.runCommand("delete-auth-realm", realmName);
		log.info("result={}", result.getExitStatus());
		if (ExitStatus.FAILURE.equals(result.getExitStatus())) {
			log.error("command failed", result.getFailureCause());
			fail();
		}

	}

}

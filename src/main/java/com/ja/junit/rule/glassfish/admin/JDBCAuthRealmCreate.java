package com.ja.junit.rule.glassfish.admin;

import static org.junit.Assert.fail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandResult.ExitStatus;

import com.ja.junit.rule.glassfish.TestContext;
import com.sun.enterprise.security.auth.realm.Realm;

@Slf4j
@RequiredArgsConstructor
public class JDBCAuthRealmCreate extends AbstractAdminObject {

	private final String realmName;
	private final String jaasContext;
	private final Class<? extends Realm> realmClass;

	@Override
	public void execute(final TestContext ctx) throws Exception {
		log.info("Create auth realm");
		CommandResult result = ctx
				.runCommand(
						"create-auth-realm",
						"--classname",
						realmClass.getName(),
						"--property",
						"jaas-context="
								+ jaasContext
								+ ":datasource-jndi=jdbc/__default:user-table=users:group-table=groups:user-name-column=username:password-column=password:group-name-column=groupname",
						realmName);
		log.info("result={}", result.getExitStatus());
		if (ExitStatus.FAILURE.equals(result.getExitStatus())) {
			log.error("command failed", result.getFailureCause());
			fail();
		}

		ctx.addTeardownCommand(new JDBCAuthRealmDelete(realmName));
	}

}

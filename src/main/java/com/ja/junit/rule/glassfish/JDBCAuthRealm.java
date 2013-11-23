package com.ja.junit.rule.glassfish;

import static org.junit.Assert.fail;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandResult.ExitStatus;
import org.glassfish.embeddable.GlassFishException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.enterprise.security.auth.realm.Realm;

public class JDBCAuthRealm extends AuthRealm {

	public JDBCAuthRealm(TestContext ctx) {
		super(ctx);
	}

	private Logger log = LoggerFactory.getLogger(JDBCAuthRealm.class);

	public void create(final String realmName, final String jaasContext,
			final Class<? extends Realm> realmClass) throws GlassFishException {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {

				log.info("Create auth realm");
				CommandResult result = ctx.runCommand(
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
			}
		});

		ctx.add(new TeardownCommand() {

			@Override
			public void execute() throws Exception {
				delete(realmName);
			}
		});
	}

	public void delete(final String realmName) {
		ctx.add(new StartupCommand() {

			@Override
			public void execute() throws Exception {
				log.info("delete auth realm");
				CommandResult result = ctx.runCommand("delete-auth-realm",
						realmName);
				log.info("result={}", result.getExitStatus());
				if (ExitStatus.FAILURE.equals(result.getExitStatus())) {
					log.error("command failed", result.getFailureCause());
					fail();
				}

			}
		});
	}

}

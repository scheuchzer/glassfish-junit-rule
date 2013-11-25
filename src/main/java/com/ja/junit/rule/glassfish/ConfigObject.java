package com.ja.junit.rule.glassfish;

import org.jboss.shrinkwrap.api.Archive;

import com.ja.junit.rule.glassfish.admin.DefaultUserAndGroupTableEntryCreate;
import com.ja.junit.rule.glassfish.admin.DefaultUserAndGroupTablesCreate;
import com.ja.junit.rule.glassfish.admin.DeploymentCreate;
import com.ja.junit.rule.glassfish.admin.JDBCAuthRealmCreate;
import com.sun.enterprise.security.auth.realm.Realm;

public class ConfigObject {

	public static DefaultUserAndGroupTablesCreate defaultUserAndGroupTables() {
		return new DefaultUserAndGroupTablesCreate();
	}

	public static JDBCAuthRealmCreate jdbcAuthRealm(String realmName,
			String jaasContext, Class<? extends Realm> realmClass) {
		return new JDBCAuthRealmCreate(realmName, jaasContext, realmClass);
	}

	public static DefaultUserAndGroupTableEntryCreate user(
			final String username, final String password,
			final String... groups) {
		return new DefaultUserAndGroupTableEntryCreate(username, password,
				groups);
	}

	public static DeploymentCreate deployment(final Archive<?> archive) {
		return new DeploymentCreate(archive);
	}
}

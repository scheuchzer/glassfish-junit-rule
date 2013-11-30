package com.ja.junit.rule.glassfish;

import java.util.Properties;

import org.jboss.shrinkwrap.api.Archive;

import com.ja.junit.rule.glassfish.admin.ConnectorConnectionPoolCreate;
import com.ja.junit.rule.glassfish.admin.ConnectorResourceCreate;
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

	/**
	 * 
	 * @param raname
	 *            the name of the connector/resource adapter
	 * @param connectionDefinition
	 *            The connection interface
	 * @param poolName
	 * @param connectionConfigProperties
	 * @return
	 */
	public static ConnectorConnectionPoolCreate connectorConnectionPool(final String raname, final Class<?> connectionDefinition,
				final String poolName, Properties connectionConfigProperties) {
		return new ConnectorConnectionPoolCreate(raname, connectionDefinition, poolName, connectionConfigProperties);
	}

	public static ConnectorResourceCreate connectorResource(
			final String poolName, final String jndiName) {
		return new ConnectorResourceCreate(poolName, jndiName);
	}
}

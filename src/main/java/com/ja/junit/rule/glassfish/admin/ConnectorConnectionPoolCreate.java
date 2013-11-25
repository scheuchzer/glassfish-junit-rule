package com.ja.junit.rule.glassfish.admin;

import java.util.Map;
import java.util.Properties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class ConnectorConnectionPoolCreate extends AbstractAdminObject {

	private final String raname;
	private final Class<?> connectionDefinition;
	private final String poolName;
	private final Properties connectionConfigProperties;

	@Override
	public void execute(final TestContext ctx) throws Exception {
		log.info("Create connection pool");
		ctx.runCommand("create-connector-connection-pool",
				"--raname=" + raname, "--connectiondefinition="
						+ connectionDefinition.getName(), poolName);

		if (connectionConfigProperties != null) {
			for (Map.Entry<Object, Object> entry : connectionConfigProperties
					.entrySet()) {
				ctx.addStartCommand(new ConnectorConnectionPoolPropertyCreate(
						poolName, (String) entry.getKey(), (String) entry
								.getValue()));
			}
		}
		ctx.addTeardownCommand(new ConnectorConnectionPoolDelete(poolName));
	}

}

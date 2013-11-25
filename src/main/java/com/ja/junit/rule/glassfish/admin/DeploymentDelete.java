package com.ja.junit.rule.glassfish.admin;

import java.util.HashSet;
import java.util.Set;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class DeploymentDelete extends AbstractAdminObject {

	private static final Set<String> TYPES = new HashSet<>();

	static {
		TYPES.add(".war");
		TYPES.add(".rar");
		TYPES.add(".ear");
	}

	@NonNull
	private final String appName;

	@Override
	public void execute(final TestContext ctx) throws Exception {
		log.info("Undeploy {}", appName);
		if (appName.length() > 4
				&& TYPES.contains(appName.substring(appName.length() - 4))) {
			final String stripedName = appName.substring(0,
					appName.length() - 4);
			log.info("Undeploy {}", stripedName);
			ctx.undeploy(stripedName);
		} else {
			ctx.undeploy(appName);
		}
	}
}

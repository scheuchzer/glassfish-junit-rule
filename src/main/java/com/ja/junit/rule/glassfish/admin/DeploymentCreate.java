package com.ja.junit.rule.glassfish.admin;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jboss.shrinkwrap.api.Archive;

import com.ja.junit.rule.glassfish.TestContext;

@Slf4j
@RequiredArgsConstructor
public class DeploymentCreate extends AbstractAdminObject {

	@NonNull
	private final Archive<?> archive;

	@Override
	public void execute(final TestContext ctx) throws Exception {
		log.info("Deploying archive. {}", archive.getName());
		ctx.deploy(archive);
		ctx.addTeardownCommand(new DeploymentDelete(archive.getName()));
	}
}

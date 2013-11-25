package com.ja.junit.rule.glassfish;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.ja.junit.rule.glassfish.admin.AbstractAdminObject;

@RequiredArgsConstructor
public class TeardownDelegateCommand implements TeardownCommand {

	@NonNull
	private final AbstractAdminObject target;

	@Override
	public void execute(TestContext ctx) throws Exception {
		// target.setCtx(ctx);
		// target.execute();
	}

}

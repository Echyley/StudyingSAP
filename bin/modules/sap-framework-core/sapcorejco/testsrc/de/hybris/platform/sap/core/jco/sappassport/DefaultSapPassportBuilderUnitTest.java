/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

import org.junit.Before;
import org.junit.Test;

import com.sap.jdsr.passport.DSRPassport;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class DefaultSapPassportBuilderUnitTest
{
	private static final int VERSION = 4;
	private static final int TRACE_FLAG = 10;
	private static final String SYSTEM_ID = "someSystemId";
	private static final int SERVICE = 100;
	private static final String USER = "someUser";
	private static final String ACTION = "someAction";
	private static final int ACTION_TYPE = 101;
	private static final String PREV_SYSTEM_ID = "somePrevSystemId";
	private static final String TRANS_ID = "someTransId";
	private static final String CLIENT_NUMBER = "someClientNumber";
	private static final int SYSTEM_TYPE = 102;
	private static final byte[] ROOT_CONTEXT_ID = new byte[] {1, 2, 3};
	private static final byte[] CONNECTION_ID = new byte[] {4, 5, 6};
	private static final int CONNECTION_COUNTER = 103;

	private DefaultSapPassportBuilder builder = DefaultSapPassportBuilder.newSapPassportBuilder();

	@Before
	public void setUp()
	{
		builder.withVersion(VERSION)
			   .withTraceFlag(TRACE_FLAG)
			   .withSystemId(SYSTEM_ID)
			   .withService(SERVICE)
			   .withUser(USER)
			   .withAction(ACTION)
			   .withActionType(ACTION_TYPE)
			   .withPrevSystemId(PREV_SYSTEM_ID)
			   .withTransId(TRANS_ID)
			   .withClientNumber(CLIENT_NUMBER)
			   .withSystemType(SYSTEM_TYPE)
			   .withRootContextId(ROOT_CONTEXT_ID)
			   .withConnectionId(CONNECTION_ID)
			   .withConnectionCounter(CONNECTION_COUNTER);
	}

	@Test
	public void testBuild()
	{
		DSRPassport passport = builder.build();
		assertThat(ACTION).isEqualTo(passport.getAction());
		assertThat(ACTION_TYPE).isEqualTo(passport.getActionType());
	}

	@Test
	public void testVersionCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withVersion(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("version cannot be null");
	}

	@Test
	public void testTraceFlagCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withTraceFlag(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("traceFlag cannot be null");
	}

	@Test
	public void testSystemIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withSystemId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("systemId cannot be null");
	}

	@Test
	public void testServiceCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withService(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("service cannot be null");
	}

	@Test
	public void testUserCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withUser(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("user cannot be null");
	}

	@Test
	public void testActionCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withAction(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("action cannot be null");
	}

	@Test
	public void testActionTypeCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withActionType(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("actionType cannot be null");
	}

	@Test
	public void testPrevSystemIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withPrevSystemId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("prevSystemId cannot be null");
	}

	@Test
	public void testTransIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withTransId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("transId cannot be null");
	}

	@Test
	public void testClientNumberCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withClientNumber(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("clientNumber cannot be null");
	}

	@Test
	public void testSystemTypeCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withSystemType(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("systemType cannot be null");
	}

	@Test
	public void testRootContextIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withRootContextId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("rootContextId cannot be null");
	}

	@Test
	public void testConnectionIdCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withConnectionId(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("connectionId cannot be null");
	}

	@Test
	public void testConnectionCounterCannotBeNull()
	{
		assertThatThrownBy(() -> builder.withConnectionCounter(null).build())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("connectionCounter cannot be null");
	}
}

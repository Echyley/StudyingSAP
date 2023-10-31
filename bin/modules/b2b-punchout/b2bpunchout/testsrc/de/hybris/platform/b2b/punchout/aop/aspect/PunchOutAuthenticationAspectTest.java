/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.aop.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.actions.inbound.DefaultPunchOutAuthenticationVerifier;

import org.aspectj.lang.JoinPoint;
import org.cxml.CXML;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutAuthenticationAspectTest
{

	@InjectMocks
	private PunchOutAuthenticationAspect punchOutAuthenticationAspect;

	@Mock
	private DefaultPunchOutAuthenticationVerifier punchOutAuthenticationVerifier;

	@Mock
	private JoinPoint joinPoint;

	private final Object[] args = new Object[]{new CXML()};

	@Before
	public void init()
	{
		when(joinPoint.getArgs()).thenReturn(args);
	}

	@Test
	public void validateSuccessTest()
	{
		punchOutAuthenticationAspect.authenticate(joinPoint);
		verify(punchOutAuthenticationVerifier).verify(any());
	}

	@Test
	public void validateFailTest()
	{
		doThrow(new PunchOutException(HttpStatus.UNAUTHORIZED, "Auth fail")).when(punchOutAuthenticationVerifier).verify(any());
		final PunchOutException punchOutException = assertThrows(PunchOutException.class, () -> punchOutAuthenticationAspect.authenticate(joinPoint));
		assertThat(punchOutException)
				.hasMessage("Auth fail")
				.hasFieldOrPropertyWithValue("errorCode", Integer.toString(HttpStatus.UNAUTHORIZED.value()));
	}
}

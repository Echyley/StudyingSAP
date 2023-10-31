/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.aop.aspect;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.actions.inbound.DefaultPunchOutAuthenticationVerifier;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.cxml.CXML;
import org.springframework.http.HttpStatus;


public class PunchOutAuthenticationAspect
{
	private DefaultPunchOutAuthenticationVerifier punchOutAuthenticationVerifier;

	public void authenticate(final JoinPoint joinPoint)
	{
		Arrays.stream(joinPoint.getArgs())
				.filter(CXML.class::isInstance)
				.findFirst()
				.ifPresentOrElse(
						cXML -> getPunchOutAuthenticationVerifier().verify((CXML) cXML),
						() -> { throw new PunchOutException(HttpStatus.INTERNAL_SERVER_ERROR, "Missing required CXML parameter"); });
	}

	protected DefaultPunchOutAuthenticationVerifier getPunchOutAuthenticationVerifier()
	{
		return punchOutAuthenticationVerifier;
	}

	public void setPunchOutAuthenticationVerifier(final DefaultPunchOutAuthenticationVerifier punchOutAuthenticationVerifier)
	{
		this.punchOutAuthenticationVerifier = punchOutAuthenticationVerifier;
	}
}

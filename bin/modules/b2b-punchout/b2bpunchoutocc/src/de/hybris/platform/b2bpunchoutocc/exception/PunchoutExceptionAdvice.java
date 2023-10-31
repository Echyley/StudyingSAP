/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bpunchoutocc.exception;

import org.apache.log4j.Logger;
import org.cxml.CXML;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2bpunchoutocc.controllers.PunchOutController;


@RestControllerAdvice(assignableTypes={ PunchOutController.class})
public class PunchoutExceptionAdvice 
{
	
	private static final Logger LOG = Logger.getLogger(PunchoutExceptionAdvice.class);

	@ExceptionHandler(value = PunchOutException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public CXML handlePunchoutException(final PunchOutException exc) 
	{
		LOG.error("Could not process PunchOut Request", exc);
		final PunchOutException punchoutException = exc;
		return CXMLBuilder.newInstance().withResponseCode(punchoutException.getErrorCode())
				.withResponseMessage(exc.getLocalizedMessage()).create();

	}
	
	@ExceptionHandler(value = Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CXML handleGenericException(final Exception exc) 
	{
		LOG.error("Could not process PunchOut Request", exc);
		return CXMLBuilder.newInstance().withResponseCode(HttpStatus.INTERNAL_SERVER_ERROR)
				.withResponseMessage(exc.getLocalizedMessage()).create();

	}

}

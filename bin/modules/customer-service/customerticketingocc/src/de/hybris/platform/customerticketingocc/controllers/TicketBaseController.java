/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingocc.controllers;

import de.hybris.platform.webservicescommons.dto.error.ErrorListWsDTO;
import de.hybris.platform.webservicescommons.dto.error.ErrorWsDTO;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.util.YSanitizer;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.google.common.collect.Lists;


/**
 * Base Controller. It defines the exception handler to be used by all controllers. Extending controllers can add or
 * overwrite the exception handler if needed.
 */
public class TicketBaseController
{
	protected static final String DEFAULT_PAGE_SIZE = "20";
	protected static final String DEFAULT_CURRENT_PAGE = "0";
	protected static final String INVALID_REQUEST_BODY_ERROR_MESSAGE = "Request body is invalid or missing";

	private static final Logger LOG = LoggerFactory.getLogger(TicketBaseController.class);

	@Resource(name = "dataMapper")
	private DataMapper dataMapper;

	protected static String logParam(final String paramName, final Long paramValue)
	{
		return paramName + " = " + paramValue;
	}

	protected static String logParam(final String paramName, final String paramValue)
	{
		return paramName + " = " + logValue(paramValue);
	}

	protected static String logValue(final String paramValue)
	{
		return "'" + sanitize(paramValue) + "'";
	}

	protected static String sanitize(final String input)
	{
		return YSanitizer.sanitize(input);
	}

	protected ErrorListWsDTO handleErrorInternal(final String type, final String message)
	{
		final ErrorListWsDTO errorListDto = new ErrorListWsDTO();
		final ErrorWsDTO error = new ErrorWsDTO();
		error.setType(type.replace("Exception", "Error"));
		error.setMessage(sanitize(message));
		errorListDto.setErrors(Lists.newArrayList(error));
		return errorListDto;
	}

	protected void validate(final Object object, final String objectName, final Validator validator)
	{
		final Errors errors = new BeanPropertyBindingResult(object, objectName);
		validator.validate(object, errors);
		if (errors.hasErrors())
		{
			throw new WebserviceValidationException(errors);
		}
	}

	public DataMapper getDataMapper()
	{
		return dataMapper;
	}

	public void setDataMapper(final DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ResponseBody
	@ExceptionHandler({ HttpMessageNotReadableException.class })
	public ErrorListWsDTO handleHttpMessageNotReadableException(final Exception ex)
	{
		LOG.error(INVALID_REQUEST_BODY_ERROR_MESSAGE, ex);
		return handleErrorInternal(HttpMessageNotReadableException.class.getSimpleName(), INVALID_REQUEST_BODY_ERROR_MESSAGE);
	}
}

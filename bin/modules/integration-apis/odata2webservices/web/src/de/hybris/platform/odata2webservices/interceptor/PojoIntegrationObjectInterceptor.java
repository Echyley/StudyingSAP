/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2webservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * An interceptor that intercepts requests and checks
 * if {@link IntegrationObjectModel} contains {@link IntegrationObjectClassModel}s, the interceptor only
 * allows GET metadata request to proceed; otherwise an exception will be thrown.
 * If {@link IntegrationObjectModel} doesn't contain {@link IntegrationObjectClassModel}, the interceptor allows all requests to proceed.
 */
public class PojoIntegrationObjectInterceptor implements HandlerInterceptor
{
	private static final Logger LOG = Log.getLogger(PojoIntegrationObjectInterceptor.class);
	private static final int MIN_PATH_INFO_SEGMENTS = 2;
	private static final int MAX_EXTRACT_INFO_SEGMENTS = 3;
	private static final String METADATA_PATH = "$metadata";
	private static final String ERROR_MESSAGE = "Only $metadata requests are supported for Integration Objects modeled with IntegrationObjectClass";
	private static final String ERROR_CODE = "inaccessible_integration_object";

	private final IntegrationObjectService integrationObjectService;

	/**
	 * Instantiates interceptor with the dependency on the {@link IntegrationObjectService} injected
	 *
	 * @param ios to find the integration object
	 */
	public PojoIntegrationObjectInterceptor(@NotNull final IntegrationObjectService ios)
	{
		integrationObjectService = Objects.requireNonNull(ios, "IntegrationObjectService cannot be null");
	}

	@Override
	public boolean preHandle(@Nonnull final HttpServletRequest request,
	                         @Nonnull final HttpServletResponse response,
	                         @Nonnull final Object o)
	{
		if (isGetPojoMetadataRequest(request))
		{
			return true;
		}

		throw new ODataRuntimeApplicationException(ERROR_MESSAGE, Locale.ENGLISH, HttpStatusCodes.METHOD_NOT_ALLOWED, ERROR_CODE);
	}

	private boolean isGetPojoMetadataRequest(final HttpServletRequest request)
	{
		final String ioCode = extractCode(request.getPathInfo());
		try
		{
			final var ioModel = integrationObjectService.findIntegrationObject(ioCode);
			return CollectionUtils.isEmpty(ioModel.getClasses()) || isMetadataGetRequest(request);
		}
		catch (final ModelNotFoundException | IllegalArgumentException e)
		{
			LOG.trace("IntegrationObject not found for code: '{}'", ioCode, e);
			return false;
		}
	}

	private String extractCode(final String pathInfo)
	{
		if (pathInfo == null)
		{
			return StringUtils.EMPTY;
		}
		final String[] elements = pathInfo.split("/", MAX_EXTRACT_INFO_SEGMENTS);
		return elements.length >= MIN_PATH_INFO_SEGMENTS ? elements[1] : StringUtils.EMPTY;
	}

	private boolean isMetadataGetRequest(final HttpServletRequest request)
	{
		return HttpMethod.GET.name().equalsIgnoreCase(request.getMethod()) && StringUtils.contains(request.getPathInfo(),
				METADATA_PATH);
	}
}

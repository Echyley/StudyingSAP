/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.punchout.util.CXmlDateUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.UUID;

import org.cxml.CXML;
import org.cxml.Response;
import org.cxml.Status;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public final class CXMLBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger(CXMLBuilder.class);

	private final CXmlDateUtil dateUtil = new CXmlDateUtil();

	private final CXML cXML;

	private final CXMLElementBrowser cxmlElementBrowser;

	private String responseMessage;

	private String responseCode;

	private String responseValue;


	private CXMLBuilder()
	{
		cXML = createNewCXML();
		cxmlElementBrowser = new CXMLElementBrowser(cXML);
	}

	public static CXMLBuilder newInstance()
	{
		return new CXMLBuilder();
	}

	protected CXML createNewCXML()
	{
		final CXML instance = new CXML();

		instance.setTimestamp(dateUtil.formatDate(new Date()));
		instance.setPayloadID(generatePayload());
		// xml:lang is optional according to documentation and accepts default values, we set it
		// to en as default for now to simplify implementation
		instance.setXmlLang("en-US");
		return instance;
	}

	/**
	 * Generates a new payload ID , use uuid now
	 *
	 * @return the payload ID
	 */
	protected String generatePayload()
	{
		return UUID.randomUUID().toString();
	}

	/**
	 * @return the newly created {@link CXML} instance
	 */
	public CXML create()
	{
		if (responseCode != null || responseMessage != null)
		{
			final Status status = new Status();
			status.setCode(responseCode);
			status.setText(responseMessage);
			if (responseValue != null)
			{
				status.setvalue(responseValue);
			}

			if (cxmlElementBrowser.hasResponse())
			{
				final Response response = cxmlElementBrowser.findResponse();
				response.setStatus(status);
			}
			else
			{
				final Response response = new Response();
				response.setStatus(status);
				cXML.getHeaderOrMessageOrRequestOrResponse().add(response);

			}
		}
		return cXML;
	}

	public CXMLBuilder withResponseCode(final String code)
	{
		this.responseCode = code;
		return this;
	}

	public CXMLBuilder withResponseValue(final String value)
	{
		this.responseValue = value;
		return this;
	}

	public CXMLBuilder withResponseMessage(final String message)
	{
		this.responseMessage = message;
		return this;
	}

	public CXMLBuilder withResponseCode(HttpStatus httpStatus)
	{
		if (null == httpStatus)
		{
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			LOG.warn("HttpStatus can not be null");
		}
		this.responseCode = Integer.toString(httpStatus.value());
		return this;
	}

}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.actions.inbound.PunchOutInboundProcessor;
import de.hybris.platform.b2b.punchout.actions.outbound.PunchOutOutboundProcessor;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;
import de.hybris.platform.b2b.punchout.services.PunchOutService;

import org.apache.commons.lang3.Validate;
import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.From;
import org.cxml.Header;


/**
 * Default implementation of {@link PunchOutService}.
 */
public class DefaultPunchOutService implements PunchOutService
{
	private PunchOutOutboundProcessor punchOutOrderMessageProcessor;
	private PunchOutOutboundProcessor punchOutCancelOrderMessageProcessor;
	private PunchOutInboundProcessor punchOutProfileRequestProcessor;
	private PunchOutInboundProcessor punchOutSetupRequestProcessor;
	private PunchOutInboundProcessor punchOutOrderRequestProcessor;
	private PunchOutCredentialService punchOutCredentialService;

	@Override
	public CXML processPunchOutSetUpRequest(final CXML request)
	{
		return getPunchOutSetupRequestProcessor().generatecXML(request);
	}

	@Override
	public CXML processPunchOutOrderMessage()
	{
		return getPunchOutOrderMessageProcessor().generatecXML();
	}

	@Override
	public CXML processCancelPunchOutOrderMessage()
	{
		return getPunchOutCancelOrderMessageProcessor().generatecXML();
	}

	@Override
	public CXML processPurchaseOrderRequest(final CXML requestBody)
	{
		return getPunchOutOrderRequestProcessor().generatecXML(requestBody);
	}

	@Override
	public String retrieveIdentity(final CXML request)
	{
		String userId = null;
		final CXMLElementBrowser cXmlBrowser = new CXMLElementBrowser(request);

		final Header header = cXmlBrowser.findHeader();
		Validate.notNull(header, "Punchout cXML request incomplete. Missing Header node.");

		final From from = header.getFrom();

		B2BCustomerModel customer = null;
		for (final Credential credential : from.getCredential())
		{
			customer = this.getPunchOutCredentialService().getCustomerForCredentialNoAuth(credential);
			if (customer != null)
			{
				userId = customer.getUid();
				break;
			}
		}

		return userId;
	}

	@Override
	public CXML processProfileRequest(final CXML request)
	{
		return getPunchOutProfileRequestProcessor().generatecXML(request);
	}

	protected PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}

	/**
	 * @return the punchOutOrderMessageProcessor
	 */
	protected PunchOutOutboundProcessor getPunchOutOrderMessageProcessor()
	{
		return punchOutOrderMessageProcessor;
	}

	/**
	 * @param punchOutOrderMessageProcessor
	 *           the punchOutOrderMessageProcessor to set
	 */
	public void setPunchOutOrderMessageProcessor(final PunchOutOutboundProcessor punchOutOrderMessageProcessor)
	{
		this.punchOutOrderMessageProcessor = punchOutOrderMessageProcessor;
	}


	/**
	 * @return the punchOutCancelOrderMessageProcessor
	 */
	protected PunchOutOutboundProcessor getPunchOutCancelOrderMessageProcessor()
	{
		return punchOutCancelOrderMessageProcessor;
	}

	/**
	 * @param punchOutCancelOrderMessageProcessor
	 *           the punchOutCancelOrderMessageProcessor to set
	 */
	public void setPunchOutCancelOrderMessageProcessor(final PunchOutOutboundProcessor punchOutCancelOrderMessageProcessor)
	{
		this.punchOutCancelOrderMessageProcessor = punchOutCancelOrderMessageProcessor;
	}

	/**
	 * @return the punchOutProfileRequestProcessor
	 */
	protected PunchOutInboundProcessor getPunchOutProfileRequestProcessor()
	{
		return punchOutProfileRequestProcessor;
	}

	/**
	 * @param punchOutProfileRequestProcessor
	 *           the punchOutProfileRequestProcessor to set
	 */
	public void setPunchOutProfileRequestProcessor(final PunchOutInboundProcessor punchOutProfileRequestProcessor)
	{
		this.punchOutProfileRequestProcessor = punchOutProfileRequestProcessor;
	}

	/**
	 * @return the punchOutSetupRequestProcessor
	 */
	protected PunchOutInboundProcessor getPunchOutSetupRequestProcessor()
	{
		return punchOutSetupRequestProcessor;
	}

	/**
	 * @param punchOutSetupRequestProcessor
	 *           the punchOutSetupRequestProcessor to set
	 */
	public void setPunchOutSetupRequestProcessor(final PunchOutInboundProcessor punchOutSetupRequestProcessor)
	{
		this.punchOutSetupRequestProcessor = punchOutSetupRequestProcessor;
	}

	/**
	 * @return the punchOutOrderRequestProcessor
	 */
	protected PunchOutInboundProcessor getPunchOutOrderRequestProcessor()
	{
		return punchOutOrderRequestProcessor;
	}

	/**
	 * @param punchOutOrderRequestProcessor
	 *           the punchOutOrderRequestProcessor to set
	 */
	public void setPunchOutOrderRequestProcessor(final PunchOutInboundProcessor punchOutOrderRequestProcessor)
	{
		this.punchOutOrderRequestProcessor = punchOutOrderRequestProcessor;
	}

}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import org.cxml.CXML;

/**
 * Default implementation of {@link PunchOutService}.
 */
public class DefaultPunchOutProfileRequestProcessor implements PunchOutInboundProcessor
{
	private DefaultPopulateProfileResponseProcessing populateProfileResponseProcessing;

	@Override
	public CXML generatecXML(final CXML request)
	{
		final CXML response = CXMLBuilder.newInstance().create();
		getPopulateProfileResponseProcessing().process(request, response);
		return response;
	}

	/**
	 * @return the populateProfileResponseProcessing
	 */
	protected DefaultPopulateProfileResponseProcessing getPopulateProfileResponseProcessing()
	{
		return populateProfileResponseProcessing;
	}

	/**
	 * @param populateProfileResponseProcessing
	 *           the populateProfileResponseProcessing to set
	 */
	public void setPopulateProfileResponseProcessing(final DefaultPopulateProfileResponseProcessing populateProfileResponseProcessing)
	{
		this.populateProfileResponseProcessing = populateProfileResponseProcessing;
	}

}

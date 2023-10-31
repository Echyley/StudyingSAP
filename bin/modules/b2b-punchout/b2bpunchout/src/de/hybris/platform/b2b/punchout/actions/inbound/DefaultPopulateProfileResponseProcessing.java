/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.converters.Populator;
import org.cxml.CXML;
import org.cxml.ProfileRequest;
import org.cxml.ProfileResponse;
import org.cxml.Response;
import org.springframework.http.HttpStatus;


/**
 * This implementation is meant to process the body of a Profile Request.
 */
public class DefaultPopulateProfileResponseProcessing
{
	private Populator<CXML, ProfileResponse> profileResponsePopulator;

	public void process(final CXML input, final CXML output)
	{
		final CXMLElementBrowser cxmlBrowser = new CXMLElementBrowser(input);

		final ProfileRequest profileRequest = cxmlBrowser.findRequestByType(ProfileRequest.class);

		if (profileRequest == null)
		{
			throw new PunchOutException(HttpStatus.CONFLICT,
					"Profile request is invalid. No ProfileRequest element in CXML");
		}
		final Response response = new Response();
		output.getHeaderOrMessageOrRequestOrResponse().add(response);
		final ProfileResponse profileResponse = new ProfileResponse();
		response
				.getProfileResponseOrPunchOutSetupResponseOrProviderSetupResponseOrGetPendingResponseOrSubscriptionListResponseOrSubscriptionContentResponseOrSupplierListResponseOrSupplierDataResponseOrAuthResponseOrDataResponseOrOrganizationDataResponse()
				.add(profileResponse);

		getProfileResponsePopulator().populate(input, profileResponse);

	}

	protected Populator<CXML, ProfileResponse> getProfileResponsePopulator()
	{
		return profileResponsePopulator;
	}

	public void setProfileResponsePopulator(final Populator<CXML, ProfileResponse> profileResponsePopulator)
	{
		this.profileResponsePopulator = profileResponsePopulator;
	}

}


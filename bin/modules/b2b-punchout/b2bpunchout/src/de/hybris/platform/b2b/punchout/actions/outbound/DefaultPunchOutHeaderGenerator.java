/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;

import java.util.ArrayList;
import java.util.List;

import org.cxml.Credential;
import org.cxml.From;
import org.cxml.Header;
import org.cxml.Identity;
import org.cxml.Sender;
import org.cxml.To;


/**
 * Create the header for a PunchOut message. The Header element contains addressing and authentication information. The
 * Header element is the same regardless of the specific Request or Response within the body of the cXML message.
 */
public class DefaultPunchOutHeaderGenerator
{
	private PunchOutSessionService punchOutSessionService;

	public Header generate()
	{
		final Header header = new Header();
		final PunchOutSession currentPunchOutSession = getPunchOutSessionService().getCurrentPunchOutSession();

		header.setFrom(createFrom(currentPunchOutSession));
		header.setTo(createTo(currentPunchOutSession));
		header.setSender(createSender(currentPunchOutSession));
		return header;
	}

	protected From createFrom(final PunchOutSession currentPunchOutSession)
	{
		final From headerFrom = new From();
		// Since it is a request the from well be initiated by this system, the original <To> in the request will become
		// the <From> in the response.
		headerFrom.getCredential().addAll(convertOrganizationsToCredentials(currentPunchOutSession.getTargetedTo()));

		return headerFrom;
	}

	protected To createTo(final PunchOutSession currentPunchOutSession)
	{
		final To headerTo = new To();
		// Since it is a request the from well be initiated by this system, the original <From> in the request will become
		// the <To> in the response.
		headerTo.getCredential().addAll(convertOrganizationsToCredentials(currentPunchOutSession.getInitiatedBy()));

		return headerTo;
	}

	protected Sender createSender(final PunchOutSession currentPunchOutSession)
	{
		final Sender headerSender = new Sender();
		headerSender.setUserAgent(currentPunchOutSession.getSentByUserAgent());
		// Sender stays the same as original but without the shared secret.
		headerSender.getCredential().addAll(convertOrganizationsToCredentials(currentPunchOutSession.getSentBy()));

		return headerSender;
	}

	/**
	 * this method will convert an Organization into a cmxl credential ignoring the shared secret.
	 *
	 * @param organizations intended to be converted.
	 * @return the resulting list of credentials.
	 */
	protected List<Credential> convertOrganizationsToCredentials(final List<Organization> organizations)
	{
		final List<Credential> credentials = new ArrayList<>();

		for (final Organization organization : organizations)
		{
			final Credential credential = new Credential();
			credential.setDomain(organization.getDomain());
			credential.setIdentity(new Identity());
			credential.getIdentity().getContent().add(organization.getIdentity());

			credentials.add(credential);
		}

		return credentials;
	}

	protected PunchOutSessionService getPunchOutSessionService()
	{
		return punchOutSessionService;
	}

	public void setPunchOutSessionService(final PunchOutSessionService punchOutSessionService)
	{
		this.punchOutSessionService = punchOutSessionService;
	}
}

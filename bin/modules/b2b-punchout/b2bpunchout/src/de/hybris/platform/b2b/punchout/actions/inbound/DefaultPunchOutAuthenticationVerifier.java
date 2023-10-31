/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutCredentialService;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.From;
import org.cxml.Header;
import org.cxml.Sender;
import org.springframework.http.HttpStatus;


/**
 * Authenticates information in the cXML.
 */
public class DefaultPunchOutAuthenticationVerifier
{
	private PunchOutCredentialService punchOutCredentialService;

	/**
	 * Extracts the header information and matches the sender details with the configuration.
	 *
	 * @param request
	 *           the CXML request message
	 */
	public void verify(final CXML request)
	{
		final CXMLElementBrowser cXmlBrowser = new CXMLElementBrowser(request);

		final Header header = cXmlBrowser.findHeader();
		if (header == null)
		{
			throw new PunchOutException(HttpStatus.BAD_REQUEST, "PunchOut cXML request incomplete. Missing Header node.");
		}

		final Sender sender = header.getSender();
		if (sender == null)
		{
			throw new PunchOutException(HttpStatus.BAD_REQUEST, "PunchOut cXML request incomplete. Missing Sender node.");
		}

		final From from = header.getFrom();
		if (from == null)
		{
			throw new PunchOutException(HttpStatus.BAD_REQUEST, "PunchOut cXML request incomplete. Missing From node.");
		}

		final boolean isSenderAuthenticated = authenticate(sender.getCredential(), true);
		if (!isSenderAuthenticated)
		{
			final String message = String.format(
					"Authentication failed, please check if the credential for sender element in the cXML document is mapped to an hybris user and the Shared "
							+ "Secret matches the configuration.");
			throw new PunchOutException(HttpStatus.UNAUTHORIZED, message);
		}
		final boolean isFromAuthenticated = authenticate(from.getCredential(), false);
		if (!isFromAuthenticated)
		{
			final String message = "Authentication failed, please check if the credential for originator of the cXML request is mapped to an hybris user.";
			throw new PunchOutException(HttpStatus.UNAUTHORIZED, message);
		}
	}

	/**
	 * Matches the given credentials against the configured ones in the system. It is necessary for at least one of the
	 * credentials to match.
	 * @param credentials the credentials to check
	 * @param verifySharedSecret set to true verify shared secret, false to not
	 * @return true if at least one matches
	 */
	protected boolean authenticate(final List<Credential> credentials, final boolean verifySharedSecret)
	{
		boolean authenticated = false;
		for (final Credential credential : credentials)
		{

			final B2BCustomerModel customer = verifySharedSecret ?
					getPunchOutCredentialService().getCustomerForCredential(credential) :
					getPunchOutCredentialService().getCustomerForCredentialNoAuth(credential);
			if (customer != null)
			{
				authenticated = true;
				break;
			}
		}
		return authenticated;
	}

	/**
	 * Matches the given credentials against the configured ones in the system. It is necessary for at least one of the
	 * credentials to match.
	 *
	 * @param credentials the credentials to check
	 * @return true if at least one matches
	 */
	protected boolean authenticateSender(final List<Credential> credentials)
	{
		return authenticate(credentials, true);
	}


	protected PunchOutCredentialService getPunchOutCredentialService()
	{
		return punchOutCredentialService;
	}

	public void setPunchOutCredentialService(final PunchOutCredentialService punchOutCredentialService)
	{
		this.punchOutCredentialService = punchOutCredentialService;
	}

}

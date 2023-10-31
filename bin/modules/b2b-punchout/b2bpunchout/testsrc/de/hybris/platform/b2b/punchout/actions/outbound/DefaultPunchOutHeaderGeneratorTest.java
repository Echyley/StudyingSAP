/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.Organization;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.CartModel;

import java.util.ArrayList;
import java.util.List;

import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutHeaderGeneratorTest
{
	private static final String SHARED_SECRET = "SharedSecret";
	private static final String NETWORK_ID = "NetworkId";
	private static final String CREDENTIAL_ID = "Id123";
	private static final String USER_AGENT = "UserAgent-123";

	@InjectMocks
	private final DefaultPunchOutHeaderGenerator action = new DefaultPunchOutHeaderGenerator();

	@Mock
	private PunchOutSessionService punchOutSessionService;

	private final PunchOutSession punchOutSession = new PunchOutSession();
	private final CartModel cartModel = new CartModel();


	@Before
	public void setUp()
	{
		when(punchOutSessionService.getCurrentPunchOutSession()).thenReturn(punchOutSession);

		final List<Organization> organizationList = new ArrayList<Organization>();
		Organization organization = new Organization();
		organization.setDomain("InitiatedBy.Domain");
		organization.setIdentity("InitiatedBy.Identity");
		organizationList.add(organization);

		punchOutSession.setInitiatedBy(organizationList);
		organizationList.clear();

		organization = new Organization();
		organization.setDomain("TargetedTo.Domain");
		organization.setIdentity("TargetedTo.Identity");

		organizationList.add(organization);
		punchOutSession.setTargetedTo(organizationList);
		organizationList.clear();

		organization = new Organization();
		organization.setDomain(NETWORK_ID);
		organization.setIdentity(CREDENTIAL_ID);
		organization.setSharedsecret(SHARED_SECRET);

		organizationList.add(organization);
		punchOutSession.setSentBy(organizationList);
		punchOutSession.setSentByUserAgent(USER_AGENT);
	}

	@Test
	public void shouldCreatePunchOutHeader()
	{
		final Header header = action.generate();
		final Credential fromCredential = header.getFrom().getCredential().get(0);
		List<Organization> organizationList = punchOutSession.getTargetedTo();


		assertThat(organizationList.get(0)).hasFieldOrPropertyWithValue("domain", fromCredential.getDomain())
										   .hasFieldOrPropertyWithValue("identity", fromCredential.getIdentity().getContent().get(0));

		final Credential toCredential = header.getTo().getCredential().get(0);
		organizationList = punchOutSession.getInitiatedBy();

		assertThat(organizationList.get(0)).hasFieldOrPropertyWithValue("domain", toCredential.getDomain())
										   .hasFieldOrPropertyWithValue("identity", toCredential.getIdentity().getContent().get(0));

		final Credential senderCredential = header.getSender().getCredential().get(0);
		assertThat(senderCredential.getDomain()).isEqualTo(NETWORK_ID);
		assertThat((String) senderCredential.getIdentity().getContent().get(0)).isEqualTo(CREDENTIAL_ID);
		assertThat(header.getSender().getUserAgent()).isEqualTo(USER_AGENT);
		assertThat(senderCredential.getSharedSecretOrDigitalSignatureOrCredentialMac()).isEmpty();
	}
}

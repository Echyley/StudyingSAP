/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.enums.PunchOutLevel;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;

import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cxml.CXML;
import org.cxml.PunchOutSetupRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutSessionPopulatorTest
{
	private final static String productPrefix = "product-";
	@InjectMocks
	private DefaultPunchOutSessionPopulator punchOutSessionPopulator;

	@Mock
	private Map<String, PunchOutLevel> punchOutLevelMapping;

	private PunchOutSession punchoutSession;
	private CXML source;
	private CXMLElementBrowser cXmlBrowser;
	private PunchOutSetupRequest request;

	private Set mapping = new HashSet<Map.Entry<String, PunchOutLevel>>();


	@Before
	public void setUp()
	{
		final Map.Entry<String, PunchOutLevel> entry = new AbstractMap.SimpleEntry(productPrefix, PunchOutLevel.PRODUCT);
		mapping.add(entry);
		when(punchOutLevelMapping.entrySet()).thenReturn(mapping);
	}

	private void processCXmlRequest(String cxmlFile) throws FileNotFoundException
	{
		source = PunchOutUtils.unmarshallCXMLFromFile(cxmlFile);
		cXmlBrowser = new CXMLElementBrowser(source);
		request = cXmlBrowser.findRequestByType(PunchOutSetupRequest.class);
		punchoutSession = new PunchOutSession();
	}

	@Test
	public void testPopulate() throws FileNotFoundException
	{
		processCXmlRequest("b2bpunchout/test/punchoutSetupRequest.xml");
		punchOutSessionPopulator.populate(source, punchoutSession);

		// check header value population
		assertThat(punchoutSession).hasFieldOrPropertyWithValue("operation", request.getOperation())
				.hasFieldOrPropertyWithValue("browserFormPostUrl", request.getBrowserFormPost().getURL().getvalue());

		// check OrganizationInfo population
		assertThat(punchoutSession.getInitiatedBy()).hasSize(cXmlBrowser.findHeader().getFrom().getCredential().size());
		assertThat(punchoutSession.getTargetedTo()).hasSize(cXmlBrowser.findHeader().getTo().getCredential().size());
		assertThat(punchoutSession.getSentBy()).hasSize(cXmlBrowser.findHeader().getSender().getCredential().size());
		assertThat(punchoutSession.getSentByUserAgent()).isEqualTo(cXmlBrowser.findHeader().getSender().getUserAgent());

		// check buyer cookie population
		assertThat(punchoutSession.getBuyerCookie()).isEqualTo(request.getBuyerCookie().getContent().iterator().next());

		//check target type/id
		assertThat(punchoutSession.getPunchoutLevel()).isEqualTo(PunchOutLevel.STORE);
		assertThat(punchoutSession.getSelectedItem()).isEqualTo(request.getSelectedItem().getItemID().getSupplierPartID().getvalue());
	}

	@Test
	public void testPopulateWithoutSelectedItem() throws FileNotFoundException
	{
		processCXmlRequest("b2bpunchout/test/punchoutSetupRequestWithoutSelectedItem.xml");
		punchOutSessionPopulator.populate(source, punchoutSession);

		//check target type/id
		assertThat(punchoutSession.getPunchoutLevel()).isEqualTo(PunchOutLevel.STORE);
	}

	@Test
	public void testPopulateWithEmptySupplierPartID() throws FileNotFoundException
	{
		processCXmlRequest("b2bpunchout/test/punchoutSetupRequestWithEmptySupplierPartID.xml");
		punchOutSessionPopulator.populate(source, punchoutSession);

		//check target type/id
		assertThat(punchoutSession.getPunchoutLevel()).isEqualTo(PunchOutLevel.STORE);
	}


	@Test
	public void testPopulateWithMalformedBrowserFormUrl() throws FileNotFoundException
	{
		processCXmlRequest("b2bpunchout/test/malformedPunchoutSetupRequest.xml");
		final PunchOutException punchOutException = assertThrows(PunchOutException.class,
				() -> punchOutSessionPopulator.populate(source, punchoutSession));
		assertThat(punchOutException).hasMessage("Malformed value for BrowserFormPostUrl")
				.hasFieldOrPropertyWithValue("errorCode", Integer.toString(HttpStatus.BAD_REQUEST.value()));
	}

	@Test
	public void testPopulateWithLevel() throws FileNotFoundException
	{
		processCXmlRequest("b2bpunchout/test/punchoutSetupRequestWithLevelSupplierPartID.xml");
		final String targetId = request.getSelectedItem().getItemID().getSupplierPartID().getvalue()
				.substring(productPrefix.length());
		punchOutSessionPopulator.populate(source, punchoutSession);

		//check target type/id
		assertThat(punchoutSession.getPunchoutLevel()).isEqualTo(PunchOutLevel.PRODUCT);

		assertThat(punchoutSession.getSelectedItem()).isEqualTo(targetId);
	}

	@Test
	public void testPopulateWithoutLevel() throws FileNotFoundException
	{
		processCXmlRequest("b2bpunchout/test/punchoutSetupRequestWithNoLevelSupplierPartID.xml");
		punchOutSessionPopulator.populate(source, punchoutSession);

		//check target type/id
		assertThat(punchoutSession.getPunchoutLevel()).isEqualTo(PunchOutLevel.STORE);
		assertThat(punchoutSession.getSelectedItem()).isEqualTo(request.getSelectedItem().getItemID().getSupplierPartID().getvalue());
	}
}

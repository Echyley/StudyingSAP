/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CXMLDesensitizerTest
{
	@InjectMocks
	private CXMLDesensitizer cXMLDesensitizer;

	@Test
	public void testMaskSensitiveDataOfPunchOutSetupRequest() throws Exception
	{
		final String cxmlString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutSetupRequestForDesensitization.xml");
		final String cxmlStringDesensitized = cXMLDesensitizer.desensitizeCXMLData(cxmlString);
		assertThat(cxmlStringDesensitized).isNotNull();

		final String requestXMLMaskSensitiveResultString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutSetupRequestDesensitizationResult.xml");
		assertThat(cxmlStringDesensitized).isEqualTo(requestXMLMaskSensitiveResultString);
	}

	@Test
	public void testMaskSensitiveDataOfPunchOutOrderRequest() throws Exception
	{
		final String cxmlString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutOrderRequestForDesensitization.xml");
		final String cxmlStringDesensitized = cXMLDesensitizer.desensitizeCXMLData(cxmlString);
		assertThat(cxmlStringDesensitized).isNotNull();

		final String requestXMLMaskSensitiveResultString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutOrderRequestDesensitizationResult.xml");
		assertThat(cxmlStringDesensitized).isEqualTo(requestXMLMaskSensitiveResultString);
	}

	@Test
	public void testMaskSensitiveDataOfPunchOutProfileRequest() throws Exception
	{
		final String cxmlString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutProfileRequestForDesensitization.xml");
		final String cxmlStringDesensitized = cXMLDesensitizer.desensitizeCXMLData(cxmlString);
		assertThat(cxmlStringDesensitized).isNotNull();

		final String requestXMLMaskSensitiveResultString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutProfileRequestDesensitizationResult.xml");
		assertThat(cxmlStringDesensitized).isEqualTo(requestXMLMaskSensitiveResultString);
	}

	@Test
	public void testMaskSensitiveDataOfPunchOutSetupResponse() throws Exception
	{
		final String cxmlString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutSetupResponseForDesensitization.xml");
		final String cxmlStringDesensitized = cXMLDesensitizer.desensitizeCXMLData(cxmlString);
		assertThat(cxmlStringDesensitized).isNotNull();

		final String requestXMLMaskSensitiveResultString = unmarshallCXMLStringFromFile("b2bpunchout/test/punchOutSetupResponseDesensitizationResult.xml");
		assertThat(cxmlStringDesensitized).isEqualTo(requestXMLMaskSensitiveResultString);
	}

	private String unmarshallCXMLStringFromFile(final String relativeFilePath) throws IOException, ParserConfigurationException, SAXException, TransformerException
	{
		final InputStream fileInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(relativeFilePath);
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(false);
		builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		final Document xmlDocument = builder.parse(new InputSource(fileInputStream));
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		final Transformer transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(xmlDocument);
		final StringResult result = new StringResult();
		transformer.transform(source, result);
		return result.toString();
	}

	@Before
	public void setup()
	{
		final Map<String, String> cXMLDesensitizeStrategyMapping = new HashMap<>(){
			{
				put("cXML/Header//Credential/SharedSecret", "password");
				put("cXML/Header//Credential/Identity", "default");
				put("cXML/Request/PunchOutSetupRequest/BuyerCookie", "default");
				put("cXML/Request/PunchOutSetupRequest/Contact/Name", "default");
				put("cXML/Request/PunchOutSetupRequest/Contact/PostalAddress/*", "default");
				put("cXML/Request/PunchOutSetupRequest/Contact//Email", "default");
				put("cXML/Request/PunchOutSetupRequest/Contact//TelephoneNumber/*", "default");
				put("cXML/Request/PunchOutSetupRequest/Contact//URL", "default");
				put("cXML/Request/PunchOutSetupRequest/Contact/IdReference/*", "default");
				put("cXML/Request/PunchOutSetupRequest/ShipTo/Address/Name", "default");
				put("cXML/Request/PunchOutSetupRequest/ShipTo/Address/PostalAddress/*", "default");
				put("cXML/Request/PunchOutSetupRequest/ShipTo/Address//Email", "default");
				put("cXML/Request/PunchOutSetupRequest/ShipTo/Address//TelephoneNumber", "default");
				put("cXML/Request/PunchOutSetupRequest/ShipTo/Address//URL", "default");
				put("cXML/Request/PunchOutSetupRequest/ShipTo/IdReference/*", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader//Address/Name", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader//Address/PostalAddress/*", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader//Address//Email", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader//Address//TelephoneNumber", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader//Address//URL", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/ShipTo/IdReference/*", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/BillTo/IdReference/*", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/BusinessPartner/IdReference/*", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/Contact/Name", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/Contact/PostalAddress/*", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/Contact//Email", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/Contact//TelephoneNumber/*", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/Contact//URL", "default");
				put("cXML/Request/OrderRequest/OrderRequestHeader/Contact/IdReference/*", "default");
				put("cXML/Request/OrderRequest/ItemOut/ShipTo/Address/Name", "default");
				put("cXML/Request/OrderRequest/ItemOut/ShipTo/Address/PostalAddress/*", "default");
				put("cXML/Request/OrderRequest/ItemOut/ShipTo/Address//Email", "default");
				put("cXML/Request/OrderRequest/ItemOut/ShipTo/Address//TelephoneNumber/*", "default");
				put("cXML/Request/OrderRequest/ItemOut/ShipTo/Address//URL", "default");
				put("cXML/Request/OrderRequest/ItemOut/ShipTo/IdReference/*", "default");
				put("cXML/Request/OrderRequest/ItemOut/Contact/Name", "default");
				put("cXML/Request/OrderRequest/ItemOut/Contact/PostalAddress/*", "default");
				put("cXML/Request/OrderRequest/ItemOut/Contact//Email", "default");
				put("cXML/Request/OrderRequest/ItemOut/Contact//TelephoneNumber/*", "default");
				put("cXML/Request/OrderRequest/ItemOut/Contact//URL", "default");
				put("cXML/Request/OrderRequest/ItemOut/Contact/IdReference/*", "default");
				put("cXML/Response/PunchOutSetupResponse/StartPage/URL[1]", "sid");
				put("cXML/Request/OrderRequest/OrderRequestHeader/Payment/PCard[1]/@number", "default");
			}
		};
		cXMLDesensitizer.setCXMLDesensitizeStrategyMapping(cXMLDesensitizeStrategyMapping);
	}

}

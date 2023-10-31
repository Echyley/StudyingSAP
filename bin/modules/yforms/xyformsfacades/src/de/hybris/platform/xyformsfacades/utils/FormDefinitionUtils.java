/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.xyformsfacades.utils;

import de.hybris.platform.xyformsfacades.data.YFormDefinitionData;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class FormDefinitionUtils
{
	private static final String INSTANCE_PATH = "//xh:head/xf:model/xf:instance[@id ='%s']";
	private static final String INSTANCE_METADATA_PROPERTY_PATH = INSTANCE_PATH + "/metadata/%s";
	private static final String ID_FORM_METADATA = "fr-form-metadata";
	private static final String ID_FORM_INSTANCE = "fr-form-instance";

	private FormDefinitionUtils()
	{
	}

	/**
	 * Given a response it returns id of newly created document
	 *
	 * @param response
	 * @throws YFormServiceException
	 */
	public static String getFormDataIdFromResponse(final String response) throws YFormServiceException
	{
		final Element documentIdElement = getElementByXPath(response, "/*/document-id");
		if (null == documentIdElement)
		{
			throw new YFormServiceException("document-id TAG was not found for given formDefinition");
		}
		return documentIdElement.getTextContent();

	}

	/**
	 * Given a content it returns a new content containing the meta information provided by the given yForm Definition
	 *
	 * @param content
	 * @param yformDefinition
	 * @throws YFormServiceException
	 */
	public static String getFormDefinitionContent(final String content, final YFormDefinitionData yformDefinition)
			throws YFormServiceException
	{
		final DocumentBuilder builder = getDocumentBuilder();
		try
		{
			final Document doc = builder.parse(IOUtils.toInputStream(content, StandardCharsets.UTF_8));
			final Element appNameElement = getElementByXPath(doc,
					String.format(INSTANCE_METADATA_PROPERTY_PATH, ID_FORM_METADATA, "application-name"));
			final Element formNameElement = getElementByXPath(doc,
					String.format(INSTANCE_METADATA_PROPERTY_PATH, ID_FORM_METADATA, "form-name"));
			final Element descriptionElement = getElementByXPath(doc,
					String.format(INSTANCE_METADATA_PROPERTY_PATH, ID_FORM_METADATA, "description"));
			final Element titleElement = getElementByXPath(doc,
					String.format(INSTANCE_METADATA_PROPERTY_PATH, ID_FORM_METADATA, "title"));
			Optional.ofNullable(appNameElement).ifPresent(element -> element.setTextContent(yformDefinition.getApplicationId()));
			Optional.ofNullable(formNameElement).ifPresent(element -> element.setTextContent(yformDefinition.getFormId()));
			Optional.ofNullable(descriptionElement).ifPresent(element -> element.setTextContent(yformDefinition.getDescription()));
			Optional.ofNullable(titleElement).ifPresent(element -> element.setTextContent(yformDefinition.getTitle()));

			return getXML(doc);
		}
		catch (final IOException | SAXException e)
		{
			throw new YFormServiceException(e);
		}
	}

	/**
	 * It returns the DOM representation of the Metadata TAG from the given content parameter.
	 *
	 * @param content
	 * @throws YFormServiceException
	 */
	public static Map<String, String> getFormDefinitionMetadata(final String content) throws YFormServiceException
	{
		final Map<String, String> metadataMap = new HashMap<>();
		final Element instance = getElementByXPath(content, String.format(INSTANCE_PATH + "/metadata", ID_FORM_METADATA));
		if (null == instance)
		{
			throw new YFormServiceException("Metadata TAG was not found for given formDefinition");
		}
		else
		{
			final NodeList metadataList = instance.getChildNodes();
			for (int i = 0; i < metadataList.getLength(); i++)
			{
				Node node = metadataList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					metadataMap.put(node.getNodeName(), node.getTextContent());
				}
			}
		}
		return metadataMap;
	}

	/**
	 * Normalizes the given xml content. Useful when comparing contents that have been manipulated using other xml tools.
	 *
	 * @param content
	 * @throws YFormServiceException
	 */
	public static String normalize(final String content) throws YFormServiceException
	{
		try
		{
			final DocumentBuilder builder = getDocumentBuilder();
			final Document doc = builder.parse(IOUtils.toInputStream(content, StandardCharsets.UTF_8));
			return getXML(doc);
		}
		catch (final IOException | SAXException e)
		{
			throw new YFormServiceException(e);
		}
	}

	/**
	 * Used to get the form definition given the content parameter.
	 *
	 * @param content
	 * @throws YFormServiceException
	 */
	public static String getFormDefinition(final String content) throws YFormServiceException
	{
		final Document newDoc = getDocumentBuilder().newDocument();
		final Element instance = getElementByXPath(content, String.format(INSTANCE_PATH + "/form", ID_FORM_INSTANCE));
		if (null == instance)
		{
			throw new YFormServiceException("form TAG was not found for given formDefinition");
		}
		newDoc.appendChild(newDoc.adoptNode(instance.cloneNode(true)));
		return getXML(newDoc);
	}

	public static String getXML(final Document doc) throws YFormServiceException
	{
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final StringWriter writer = new StringWriter();
		try
		{
			final Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
		}
		catch (final TransformerException e)
		{
			throw new YFormServiceException(e);
		}
		return writer.getBuffer().toString();
	}

	protected static Element getElementByXPath(String content, String path) throws YFormServiceException
	{
		final DocumentBuilder builder = getDocumentBuilder();
		final XPath xPath = getXPath();
		try
		{
			final Document doc = builder.parse(IOUtils.toInputStream(content, StandardCharsets.UTF_8));
			return (Element) xPath.compile(path).evaluate(doc, XPathConstants.NODE);
		}
		catch (final XPathExpressionException | IOException | SAXException e)
		{
			throw new YFormServiceException(e);
		}
	}

	protected static Element getElementByXPath(Document doc, String path) throws YFormServiceException
	{
		final XPath xPath = getXPath();
		try
		{
			return (Element) xPath.compile(path).evaluate(doc, XPathConstants.NODE);
		}
		catch (final XPathExpressionException e)
		{
			throw new YFormServiceException(e);
		}
	}

	protected static DocumentBuilder getDocumentBuilder() throws YFormServiceException
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);

		try
		{
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			// The settings below are suggested by the OWASP site https://owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
			factory.setXIncludeAware(false);

			// Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
			// This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
			// A fatal error is thrown if the incoming document contains a DOCTYPE declaration.
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
			// 	Do not include external general entities.
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);

			// Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
			// Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
			// Do not include external parameter entities or the external DTD subset.
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			return factory.newDocumentBuilder();
		}
		catch (final ParserConfigurationException e)
		{
			throw new YFormServiceException(e);
		}
	}

	protected static XPath getXPath()
	{
		final XPath xPath = XPathFactory.newInstance().newXPath();
		xPath.setNamespaceContext(new YFormNamespaceContext());
		return xPath;
	}
}

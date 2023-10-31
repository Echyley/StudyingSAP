/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.punchout.PunchOutException;
import org.hsqldb.lib.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.xml.transform.StringResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

public class CXMLDesensitizer
{
	private static final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";
	private static final String LOAD_EXTERNAL_DTD_FEATURE = "nonvalidating/load-external-dtd";
	private static final String PUNCH_OUT_SESSION_ID_TAG = "?sid=";
	private static final String DESENSITIZE_STRATEGY_DEFAULT = "default";
	private static final String DESENSITIZE_STRATEGY_SID = "sid";
	private static final String DESENSITIZE_STRATEGY_PASSWORD = "password";
	private static final int MASK_PATTERN_CHARACTER_COUNT_MAX = 3;
	private static final int MASK_PATTERN_CHARACTER_COUNT_MIN = 1;
	private static final String MASK_PATTERN_SYMBOL_STRING = "*****";
	protected static final String LOAD_EXTERNAL_DTD = XERCES_FEATURE_PREFIX + LOAD_EXTERNAL_DTD_FEATURE;

	private Map<String, String> cXMLDesensitizeStrategyMapping;

	public String desensitizeCXMLData(final String cxmlString) throws PunchOutException
	{
		if (getCXMLDesensitizeStrategyMapping().isEmpty())
		{
			return cxmlString;
		}
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		final XPath xPath = XPathFactory.newInstance().newXPath();
		try
		{
			builderFactory.setValidating(false);
			builderFactory.setFeature(LOAD_EXTERNAL_DTD, false);
			builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			final DocumentBuilder builder = builderFactory.newDocumentBuilder();
			final Document xmlDocument = builder.parse(new InputSource(new StringReader(cxmlString)));
			for (Map.Entry<String, String> entry : getCXMLDesensitizeStrategyMapping().entrySet())
			{
				final NodeList nodeList = ((NodeList) xPath.compile(entry.getKey()).evaluate(xmlDocument, XPathConstants.NODESET));
				switch (entry.getValue())
				{
					case DESENSITIZE_STRATEGY_DEFAULT:
						desensitizeValue(nodeList);
						break;
					case DESENSITIZE_STRATEGY_SID:
						desensitizeSid(nodeList);
						break;
					case DESENSITIZE_STRATEGY_PASSWORD:
						desensitizePassword(nodeList);
						break;
					default:
						break;
				}
			}
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			final Transformer transformer = transformerFactory.newTransformer();
			final DOMSource source = new DOMSource(xmlDocument);
			final StringResult result = new StringResult();
			transformer.transform(source, result);
			return result.toString();
		}
		catch (final XPathExpressionException | IOException | ParserConfigurationException | TransformerException | SAXException e)
		{
			throw new PunchOutException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
		}
	}

	private String maskPattern(final String originalValue)
	{
		if (!StringUtil.isEmpty(originalValue))
		{
			if (originalValue.length() > MASK_PATTERN_CHARACTER_COUNT_MAX)
			{
				return originalValue.substring(0, MASK_PATTERN_CHARACTER_COUNT_MAX) + MASK_PATTERN_SYMBOL_STRING;
			}
			else
			{
				return originalValue.substring(0, MASK_PATTERN_CHARACTER_COUNT_MIN) + MASK_PATTERN_SYMBOL_STRING;
			}
		}
		else
		{
			return originalValue;
		}
	}

	private void desensitizeValue(final NodeList nodeList)
	{
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			final Node node = nodeList.item(i);
			node.setTextContent(maskPattern(node.getTextContent()));
		}
	}

	private void desensitizeSid(final NodeList nodeList)
	{
		if (nodeList.getLength() == 1)
		{
			final Node node = nodeList.item(0);
			final String value = node.getTextContent();
			if (value != null && value.contains(PUNCH_OUT_SESSION_ID_TAG))
			{
				final int sidBeginIndex = value.indexOf(PUNCH_OUT_SESSION_ID_TAG) + PUNCH_OUT_SESSION_ID_TAG.length();
				final String sid = value.substring(sidBeginIndex);
				node.setTextContent(value.replace(sid, maskPattern(sid)));
			}
		}
	}

	private void desensitizePassword(final NodeList nodeList)
	{
		for (int i = 0; i < nodeList.getLength(); i++)
		{
			final Node node = nodeList.item(i);
			node.setTextContent(MASK_PATTERN_SYMBOL_STRING);
		}
	}

	protected Map<String, String> getCXMLDesensitizeStrategyMapping()
	{
		return cXMLDesensitizeStrategyMapping;
	}

	public void setCXMLDesensitizeStrategyMapping(final Map<String, String> cXMLDesensitizeStrategyMapping)
	{
		this.cXMLDesensitizeStrategyMapping = cXMLDesensitizeStrategyMapping;
	}

}

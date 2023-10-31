/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.output.StringBuilderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A helper object for evaluating Json content by utilizing XPath expressions.
 */
public final class XmlObject
{
	private static final Logger LOG = LoggerFactory.getLogger(XmlObject.class);
	private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();
	private static final String COUNT_EXPR_TEMPLATE = "count(%s)";

	private final Document document;
	private String xml;

	private XmlObject(final Document ctx)
	{
		document = ctx;
	}

	/**
	 * Parses XML content from the input stream and creates new XML object
	 *
	 * @param in an input stream containing XML
	 * @return an object for evaluating structure of the parsed content
	 * @throws IllegalArgumentException if the input stream fails to read or contains a malformed XML
	 */
	public static XmlObject createFrom(final InputStream in)
	{
		final Document context = XmlUtils.getXmlDocument(in);
		return new XmlObject(context);
	}

	/**
	 * Parses XML content and creates new XML object
	 *
	 * @param xml an XML content to parse
	 * @return an object for evaluating structure of the parsed content
	 * @throws IllegalArgumentException if the content contains a malformed XML
	 */
	public static XmlObject createFrom(final String xml)
	{
		final Document context = XmlUtils.getXmlDocument(xml);
		return new XmlObject(context);
	}

	/**
	 * Looks up a value in the parsed XML
	 *
	 * @param path a XML path, e.g. {@code //product/code}, pointing to the element whose value needs to be retrieved.
	 * @return value of the element matching the {@code path} location.
	 */
	public String get(final String path)
	{
		return (String) evaluate(path, XPathConstants.STRING);
	}

	/**
	 * Retrieves all nodes matching the path and transforms them using the function.
	 *
	 * @param path an XML path, e.g. {@code //product/code}, pointing to the nodes to transform.
	 * @param f    a function to apply to each node matching the path.
	 * @return a list containing results of applying the function to the matching nodes in the order they
	 * were selected from the XML content; or an empty list, if there are no matching nodes found.
	 */
	public <T> List<T> transform(final String path, Function<Node, T> f)
	{
		final NodeList nodes = getNodes(path);
		return IntStream.range(0, nodes.getLength())
		                .mapToObj(nodes::item)
		                .map(f)
		                .toList();
	}

	/**
	 * Counts the number of nodes matching the path
	 *
	 * @param path a XML path, e.g. {@code //product/code}, pointing to the elements to be counted.
	 * @return number of the elements matching the {@code path} location.
	 */
	public int count(final String path)
	{
		final String expr = COUNT_EXPR_TEMPLATE.formatted(path);
		final Number count = (Number) evaluate(expr, XPathConstants.NUMBER);
		return count.intValue();
	}

	/**
	 * Checks whether the specified xpath exists in this XML object.
	 *
	 * @param path an xPath, e.g. {@code //product/code} to be verified
	 * @return {@code true}, if the specified path exists in this XML object;
	 */
	public boolean exists(final String path)
	{
		final NodeList nodes = getNodes(path);
		return nodes != null && nodes.getLength() > 0;
	}

	private NodeList getNodes(final String path)
	{
		return (NodeList) evaluate(path, XPathConstants.NODESET);
	}

	private Object evaluate(final String path, final QName result)
	{
		try
		{
			return xpath(path).evaluate(document, result);
		}
		catch (final XPathExpressionException e)
		{
			throw new IllegalArgumentException(path + " is not a valid XPath expression", e);
		}
	}

	private static XPathExpression xpath(final String path) throws XPathExpressionException
	{
		return X_PATH_FACTORY.newXPath().compile(path);
	}

	@Override
	public String toString()
	{
		if (xml == null)
		{
			xml = serialize();
		}
		return xml;
	}

	private String serialize()
	{
		final StringBuilder buffer = new StringBuilder();
		try (final StringBuilderWriter sbw = new StringBuilderWriter(buffer))
		{
			final TransformerFactory factory = TransformerFactory.newInstance();
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			final Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(document), new StreamResult(sbw));
		}
		catch (final TransformerException e)
		{
			LOG.trace("Error while serializing {}", document, e);
		}
		return buffer.toString();
	}
}

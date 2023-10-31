/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import de.hybris.platform.integrationservices.util.XmlUtils;
import de.hybris.platform.servicelayer.type.TypeService;

import java.io.IOException;
import java.io.InputStream;

import org.w3c.dom.Document;

/**
 * A builder for creating {@link TestItemsXml}.
 */
public class ItemsXmlBuilder
{
	private static final String ITEMS_XML_SUFFIX = "-items.xml";
	private final String extensionName;
	private TypeService typeService;

	private ItemsXmlBuilder(final String extName)
	{
		extensionName = extName;
	}

	/**
	 * Creates new instance of this builder.
	 *
	 * @return instance of the builder
	 */
	public static ItemsXmlBuilder itemsXml(final String extName)
	{
		return new ItemsXmlBuilder(extName);
	}

	public ItemsXmlBuilder withTypeService(final TypeService service)
	{
		typeService = service;
		return this;
	}

	public TestItemsXml build()
	{
		TestItemsXml.setTypeService(typeService);
		final Document doc = load();
		return new TestItemsXml(doc);
	}

	private Document load()
	{
		try (final InputStream in = getClass().getClassLoader().getResourceAsStream(extensionName + ITEMS_XML_SUFFIX))
		{
			return XmlUtils.getXmlDocument(in);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException("Unable to load '" + extensionName + "' items.xml", e);
		}
	}
}

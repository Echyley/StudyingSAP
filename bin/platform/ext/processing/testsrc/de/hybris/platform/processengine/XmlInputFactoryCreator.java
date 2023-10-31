/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.processengine;

import javax.xml.stream.XMLInputFactory;

public class XmlInputFactoryCreator
{
	private XmlInputFactoryCreator()
	{
	}

	public static XMLInputFactory createXMLInputFactory()
	{
		final XMLInputFactory xif = XMLInputFactory.newFactory();
		xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		return xif;
	}
}

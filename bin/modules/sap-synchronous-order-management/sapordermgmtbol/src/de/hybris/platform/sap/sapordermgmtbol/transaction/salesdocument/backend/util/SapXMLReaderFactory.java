/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util;

import de.hybris.platform.sap.core.common.DocumentBuilderFactoryUtil;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class SapXMLReaderFactory
{

	private static volatile XMLReader xmlReader = null;

	private SapXMLReaderFactory()
	{
	}

	public static XMLReader createXMLReader() throws SAXException
	{

		if (xmlReader != null)
		{
			return xmlReader;
		}
		else
		{
			synchronized (SapXMLReaderFactory.class)
			{
				if (xmlReader != null)
				{
					return xmlReader;
				}
				else
				{
					xmlReader = XMLReaderFactory.createXMLReader();
					DocumentBuilderFactoryUtil.setSecurityFeatures(xmlReader);
					return xmlReader;
				}
			}
		}

	}

}

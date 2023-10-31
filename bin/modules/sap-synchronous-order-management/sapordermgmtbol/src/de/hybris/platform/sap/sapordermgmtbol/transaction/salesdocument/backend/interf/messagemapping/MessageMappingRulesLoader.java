/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRule;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRulesContainerImpl.Key;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;


/**
 *
 */
public interface MessageMappingRulesLoader
{

	/**
	 * @return Map of message mapping rules
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("squid:S1160")
	Map<Key, List<MessageMappingRule>> loadRules() throws SAXException, IOException;

	/**
	 * @return true if non error messages should be hidden
	 */
	boolean isHideNonErrorMsg();

}

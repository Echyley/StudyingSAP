/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRule;

import java.util.Map;


/**
 * Container for the message mapping rules maintained in messages.xml
 */
public interface MessageMappingRulesContainer
{

	/**
	 * @return Do we hide info and warning messages?
	 */
	public boolean isHideNonErrorMsg();

	/**
	 * @param beMes
	 * @return The most narrow mapping rule
	 */
	public MessageMappingRule mostNarrow(MessageMappingRule.Pattern beMes);

	/**
	 * @return Map of callback implementations
	 */
	Map<String, MessageMappingCallbackProcessor> getCallbacks();

}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;


@SuppressWarnings("javadoc")
public class TestImplementationMessageMappingCallback implements MessageMappingCallbackProcessor
{

	/**
	 * 
	 */
	public static final String TEST_MESSAGE_MAPPING_CALLBACK_ID = "testMessageMappingCallback";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.
	 * MessageMappingCallbackProcessor
	 * #process(de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl
	 * .messagemapping.BackendMessage)
	 */
	@Override
	public boolean process(final BackendMessage message)
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.
	 * AbstractMessageMappingCallbackProcessor#getId()
	 */
	@Override
	public String getId()
	{
		return TEST_MESSAGE_MAPPING_CALLBACK_ID;
	}



}

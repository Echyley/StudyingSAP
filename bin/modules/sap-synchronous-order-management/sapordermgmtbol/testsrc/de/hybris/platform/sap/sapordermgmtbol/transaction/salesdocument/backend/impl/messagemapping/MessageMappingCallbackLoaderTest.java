/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping.MessageMappingCallbackProcessor;
import de.hybris.platform.sap.sapordermgmtbol.unittests.base.SapordermanagmentBolSpringJunitTest;

import java.util.Map;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class MessageMappingCallbackLoaderTest extends SapordermanagmentBolSpringJunitTest
{

	public MessageMappingCallbackLoader classUnderTest;


	@Test
	public void test()
	{

		classUnderTest = genericFactory.getBean("sapOrdermgmtMessageMappingCallbackLoader");

		final Map<String, MessageMappingCallbackProcessor> callbackMap = classUnderTest.loadCallbacks();

		assertTrue(!callbackMap.isEmpty());

		callbackMap.get(TestImplementationMessageMappingCallback.TEST_MESSAGE_MAPPING_CALLBACK_ID).process(null);


	}



}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.unittests;



import de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolCartFacadeUnitTest;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.impl.DefaultBolOrderFacadeUnitTest;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultAddToCartStrategyTest;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultCartRestorationServiceTest;
import de.hybris.platform.sap.sapordermgmtservices.cart.impl.DefaultCartServiceTest;
import de.hybris.platform.sap.sapordermgmtservices.checkout.impl.DefaultCheckoutServiceUnitTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultAbstractOrderEntryPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultAbstractOrderPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultCartPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultDeliveryModePopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultMessagePopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultOrderPopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.converters.populator.DefaultScheduleLinePopulatorTest;
import de.hybris.platform.sap.sapordermgmtservices.impl.DefaultBackendAvailabilityServiceTest;
import de.hybris.platform.sap.sapordermgmtservices.messagemappingcallback.DefaultInternalNumberReplacementMsgMappingCallbackTest;
import de.hybris.platform.sap.sapordermgmtservices.messagemappingcallback.DefaultSapProductIdReplacementMsgMappingCallbackTest;
import de.hybris.platform.sap.sapordermgmtservices.order.impl.DefaultOrderServiceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
{ DefaultCartServiceTest.class, // 
		DefaultCartRestorationServiceTest.class, //
		DefaultBolCartFacadeUnitTest.class, //
		DefaultBolOrderFacadeUnitTest.class, //
		DefaultAbstractOrderPopulatorTest.class, //
		DefaultAbstractOrderEntryPopulatorTest.class, //
		DefaultCartPopulatorTest.class, //
		DefaultOrderPopulatorTest.class, //
		DefaultCheckoutServiceUnitTest.class, //
		DefaultDeliveryModePopulatorTest.class, //
		DefaultMessagePopulatorTest.class, //
		DefaultScheduleLinePopulatorTest.class, //
		DefaultSapProductIdReplacementMsgMappingCallbackTest.class, //
		DefaultBackendAvailabilityServiceTest.class, //
		DefaultInternalNumberReplacementMsgMappingCallbackTest.class, //
		DefaultAddToCartStrategyTest.class, //
		DefaultOrderServiceTest.class //

})
public class UnitTestSuite
{
	//
}

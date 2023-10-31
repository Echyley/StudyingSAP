/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.yorderfulfillment.suites;


import de.hybris.platform.sap.yorderfulfillment.actions.SetCompletionStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.actions.SetConfirmationStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.actions.UpdateERPOrderStatusActionTest;
import de.hybris.platform.sap.yorderfulfillment.jobs.OrderCancelRepairJobTest;
import de.hybris.platform.sap.yorderfulfillment.jobs.OrderExchangeRepairJobTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
        {UpdateERPOrderStatusActionTest.class, SetConfirmationStatusActionTest.class, SetCompletionStatusActionTest.class, OrderExchangeRepairJobTest.class, OrderCancelRepairJobTest.class})
public class UnitTestSuite {
    // Intentionally left blank
}

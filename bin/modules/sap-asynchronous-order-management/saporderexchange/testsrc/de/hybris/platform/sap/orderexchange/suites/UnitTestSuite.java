/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.hybris.platform.sap.orderexchange.cancellation.DefaultSAPOrderCancelStateMappingStrategyTest;
import de.hybris.platform.sap.orderexchange.cancellation.DefaultSapOrderCancelServiceTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubDeliveryTranslatorTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubGoodsIssueTranslatorTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubOrderCancelTranslatorTest;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubOrderCreationTranslatorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.AbstractRawItemBuilderTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPartnerContributorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultPaymentContributorTest;
import de.hybris.platform.sap.orderexchange.outbound.impl.DefaultSalesConditionsContributorTest;
import de.hybris.platform.sap.orderexchange.taskrunners.SendOrderCancelRequestAsCSVTaskRunnerTest;


@SuppressWarnings("javadoc")
@RunWith(Suite.class)
@SuiteClasses(
{ DefaultSapOrderCancelServiceTest.class, DefaultSAPOrderCancelStateMappingStrategyTest.class, AbstractRawItemBuilderTest.class,
		DataHubDeliveryTranslatorTest.class,
		DataHubGoodsIssueTranslatorTest.class, DataHubOrderCreationTranslatorTest.class, DataHubOrderCancelTranslatorTest.class,
		DefaultPaymentContributorTest.class, DefaultPartnerContributorTest.class, DefaultSalesConditionsContributorTest.class,
		SendOrderCancelRequestAsCSVTaskRunnerTest.class })
public class UnitTestSuite
{

}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.process.strategies.impl;

import static de.hybris.platform.warehousing.constants.WarehousingConstants.CONSOLIDATED_CONSIGNMENTS_BP_PARAM_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;
import de.hybris.platform.warehousing.process.strategies.ConsolidatedPickSlipBusinessProcessContextStrategy;
import de.hybris.platform.warehousing.util.BaseWarehousingIntegrationTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;


/**
 * Test class for ProcessContextResolutionStrategy
 */
@IntegrationTest
public class ProcessContextResolutionStrategyIntegrationTest extends BaseWarehousingIntegrationTest
{
	private ConsignmentProcessModel consignmentProcessModel;

	@Resource
	private DefaultProcessContextResolutionStrategy defaultProcessContextResolutionStrategy;

	@Resource
	private ConsolidatedPickSlipBusinessProcessContextStrategy consolidatedPickSlipBusinessProcessContextStrategy;

	@Before
	public void setup()
	{
		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("testSite");

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("zh");
		currency.setSymbol("$");

		final UserModel user = new UserModel();
		user.setUid("testuser");
		final OrderModel order = new OrderModel();
		order.setCode("orderCode");
		order.setCurrency(currency);
		order.setDate(new Date());
		order.setUser(user);
		order.setSite(baseSite);

		final List<ConsignmentModel> consignments = new ArrayList<>();

		final CarrierModel carrier = new CarrierModel();
		carrier.setCode("MockCarrier");

		final ConsignmentModel consignment = new ConsignmentModel();
		consignment.setTrackingID("trackingId");
		consignment.setCode("code");
		consignment.setStatus(ConsignmentStatus.READY);
		consignment.setCarrierDetails(carrier);
		consignment.setOrder(order);

		consignments.add(consignment);

		consignmentProcessModel = new ConsignmentProcessModel();

		final BusinessProcessParameterModel businessProcessParameterModel = new BusinessProcessParameterModel();
		businessProcessParameterModel.setName(CONSOLIDATED_CONSIGNMENTS_BP_PARAM_NAME);
		businessProcessParameterModel.setValue(consignments);
		consignmentProcessModel.setContextParameters(Collections.singletonList(businessProcessParameterModel));
	}

	@Test
	public void shouldGetTheCorrectStrategy() throws Exception
	{
		final Optional<ProcessContextResolutionStrategy<BaseSiteModel>> resultStrategy = defaultProcessContextResolutionStrategy
				.getStrategy(consignmentProcessModel);

		assertThat(resultStrategy).containsSame(consolidatedPickSlipBusinessProcessContextStrategy);
		assertThat(consolidatedPickSlipBusinessProcessContextStrategy.getCmsSite(consignmentProcessModel).getUid())
				.isEqualTo("testSite");
	}
}

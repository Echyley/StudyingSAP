/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.warehousing.process.strategies;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.acceleratorservices.process.strategies.impl.DefaultProcessContextResolutionStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessParameterModel;

import java.util.Arrays;
import java.util.HashMap;

import org.assertj.core.util.Maps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Test class for DefaultProcessContextResolutionStrategy
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessContextResolutionStrategyTest
{
	@Mock
	private ProcessContextResolutionStrategy<BaseSiteModel> consolidatedPickSlipBusinessProcessContextStrategy;

	@Mock
	private ConsignmentProcessModel consignmentProcessModel;

	@Mock
	private BusinessProcessModel businessProcessModel;

	@Mock
	private ConsignmentModel consignmentModel;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	@Mock
	private BaseSiteModel baseSiteModel;

	@Mock
	private BusinessProcessParameterModel businessProcessParameterModel;

	@InjectMocks
	private final DefaultProcessContextResolutionStrategy contextResolutionStrategy = new DefaultProcessContextResolutionStrategy();

	@Before
	public void setUp() throws Exception
	{
		Mockito.lenient().when(businessProcessModel.getItemtype()).thenReturn(BusinessProcessModel._TYPECODE);
	}

	@Test
	public void initializeShouldNotFailWhenStrategyNotFound() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(new HashMap<>());

		contextResolutionStrategy.initializeContext(businessProcessModel);

		verify(consolidatedPickSlipBusinessProcessContextStrategy, never()).initializeContext(any());
	}

	@Test
	public void getContentCatalogVersionShouldNotFailWhenStrategyNotFound() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(new HashMap<>());

		assertThat(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).isNull();
	}

	@Test
	public void getCmsShouldNotFailWhenStrategyNotFound() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(new HashMap<>());

		assertThat(contextResolutionStrategy.getCmsSite(businessProcessModel)).isNull();
	}

	@Test
	public void testShouldInitializeContextForBusinessProcessContext() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(Maps.newHashMap(BusinessProcessModel.class, consolidatedPickSlipBusinessProcessContextStrategy));

		contextResolutionStrategy.initializeContext(businessProcessModel);

		verify(consolidatedPickSlipBusinessProcessContextStrategy).initializeContext(businessProcessModel);
	}

	@Test
	public void testShouldDelegateToConsolidatedPickSlipBusinessProcessContextStrategy() throws Exception
	{
		contextResolutionStrategy.setProcessStrategyMap(Maps.newHashMap(ConsignmentProcessModel.class, consolidatedPickSlipBusinessProcessContextStrategy));

		contextResolutionStrategy.initializeContext(consignmentProcessModel);

		verify(consolidatedPickSlipBusinessProcessContextStrategy).initializeContext(consignmentProcessModel);
	}

	@Test
	public void testShouldReturnBaseSiteFromConsignmentProcessModel() throws Exception
	{
		ConsolidatedPickSlipBusinessProcessContextStrategy strategy = new ConsolidatedPickSlipBusinessProcessContextStrategy();
		when(abstractOrderModel.getSite()).thenReturn(baseSiteModel);
		when(consignmentModel.getOrder()).thenReturn(abstractOrderModel);
		when(consignmentProcessModel.getConsignment()).thenReturn(consignmentModel);
		assertThat(strategy.getCmsSite(consignmentProcessModel)).isSameAs(baseSiteModel);
	}

	@Test
	public void testShouldReturnBaseSiteFromContetParameter() throws Exception
	{
		ConsolidatedPickSlipBusinessProcessContextStrategy strategy = new ConsolidatedPickSlipBusinessProcessContextStrategy();

		BusinessProcessModel businessProcessModelDeepStub = mock(BusinessProcessModel.class, RETURNS_DEEP_STUBS);
		ConsignmentModel consignmentModelDeepStub = mock(ConsignmentModel.class, RETURNS_DEEP_STUBS);
		when(businessProcessModelDeepStub.getContextParameters().iterator().hasNext()).thenReturn(true);
		when(businessProcessModelDeepStub.getContextParameters().iterator().next()).thenReturn(businessProcessParameterModel);
		when(businessProcessParameterModel.getName()).thenReturn("ConsolidatedConsignmentModels");
		when(businessProcessParameterModel.getValue()).thenReturn(Arrays.asList(consignmentModelDeepStub));
		when(consignmentModelDeepStub.getOrder().getSite()).thenReturn(baseSiteModel);
		assertThat(strategy.getCmsSite(businessProcessModelDeepStub)).isSameAs(baseSiteModel);
	}

}

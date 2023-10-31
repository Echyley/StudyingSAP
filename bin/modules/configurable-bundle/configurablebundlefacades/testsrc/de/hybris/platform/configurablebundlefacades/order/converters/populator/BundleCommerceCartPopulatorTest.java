/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.tenant.MockTenant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests for {@link BundleCommerceCartPopulator}
 */
@UnitTest
public class BundleCommerceCartPopulatorTest
{
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PromotionsService promotionsService;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	@Mock
	private EntryGroupService entryGroupService;
	@Mock
	private BundleTemplateService bundleTemplateService;
	@Mock
	private Converter<BundleTemplateModel, BundleTemplateData> bundleTemplateConverter;
	@InjectMocks
	private final BundleCommerceCartPopulator<CartModel, CartData> bundleCartPopulator = new BundleCommerceCartPopulator<>();
	@Mock
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;

	private CartModel source;
	private CartData target;
	MockitoSession mockito;

	@Before
	public void setUp()
	{
		mockito = Mockito.mockitoSession()
				.initMocks(this)
				.startMocking();
		bundleCartPopulator.setModelService(modelService);
		bundleCartPopulator.setPromotionResultConverter(promotionResultConverter);
		bundleCartPopulator.setOrderEntryConverter(orderEntryConverter);

		source = mock(CartModel.class);
		target = new CartData();
		source.setEntries(Collections.emptyList());
	}
	@After
	public void tearDown() {
		//It is necessary to finish the session so that Mockito
		// can detect incorrect stubbing and validate Mockito usage
		//'finishMocking()' is intended to be used in your test framework's 'tear down' method.
		mockito.finishMocking();
	}

	@Test
	public void shouldThrowExceptionWhenSourceIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter source can not be null");

		bundleCartPopulator.populate(null, new CartData());
	}

	@Test
	public void shouldThrowExceptionWhenTargetIsNull()
	{
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Parameter target can not be null");

		bundleCartPopulator.populate(new CartModel(), null);
	}

	@Test
	public void testPopulateNoEntries()
	{
		source.setEntries(null);

		bundleCartPopulator.populate(source, target);

		assertThat(target.getEntries()).isNullOrEmpty();
	}

	@Test
	public void testPopulateEmptyEntries()
	{
		bundleCartPopulator.populate(source, target);

		assertThat(target.getEntries()).isNullOrEmpty();
		assertThat(target.getPotentialOrderPromotions()).isNullOrEmpty();
		assertThat(target.getPotentialProductPromotions()).isNullOrEmpty();
	}

	@Test
	public void testPopulateWithEntries()
	{
		final OrderEntryModel orderEntryModel1 = new OrderEntryModel();
		orderEntryModel1.setEntryGroupNumbers(Collections.singleton(1));
		final OrderEntryModel orderEntryModel2 = new OrderEntryModel();
		orderEntryModel2.setEntryGroupNumbers(Collections.singleton(1));

		final OrderEntryData orderEntryData1 = new OrderEntryData();
		orderEntryData1.setEntryGroupNumbers(Collections.singleton(1));
		final OrderEntryData orderEntryData2 = new OrderEntryData();
		orderEntryData2.setEntryGroupNumbers(Collections.singleton(1));

		final EntryGroup group = new EntryGroup();
		group.setGroupNumber(1);
		group.setExternalReferenceId("component");
		Mockito.lenient().when(entryGroupService.getGroupOfType(eq(source), anyCollection(), any())).thenReturn(group);
		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setId("component");
		Mockito.lenient().when(bundleTemplateService.getBundleTemplateForCode(any())).thenReturn(bundleTemplate);
		final BundleTemplateData bundleTemplateData = new BundleTemplateData();
		bundleTemplateData.setId("component");
		Mockito.lenient().when(bundleTemplateConverter.convert(eq(bundleTemplate))).thenReturn(bundleTemplateData);

		Mockito.lenient().when(source.getEntries()).thenReturn(Arrays.asList(orderEntryModel1, orderEntryModel2));
		Mockito.lenient().when(orderEntryConverter.convertAll(Arrays.asList(orderEntryModel1, orderEntryModel2)))
				.thenReturn(Arrays.asList(orderEntryData1, orderEntryData2));

		bundleCartPopulator.populate(source, target);

		// Some complex bundleTemplateService interactions are left for OrderComparator test
		assertThat(target.getEntries()).containsExactly(orderEntryData1, orderEntryData2);
	}

	@Test
	public void testPopulateWithPotentialPromotions()
	{
		final List<PromotionResult> potentialOrderPromotions = Arrays.asList(newPromotionResult(1), newPromotionResult(2));
		final List<PromotionResult> potentialProductPromotions = Arrays.asList(newPromotionResult(3), newPromotionResult(4));
		// This wrapping is required since there is a cast to ArrayList in ModelService.addAll()
		// call from AbstractOrderPopulator.getPromotions() method.
		final List<PromotionResultModel> orderPromotionModels = new ArrayList<>(Arrays.asList(new PromotionResultModel(), new PromotionResultModel()));
		final List<PromotionResultModel> productPromotionModels = new ArrayList<>(Arrays.asList(new PromotionResultModel(), new PromotionResultModel()));
		final List<PromotionResultData> convertedOrderPromotions = Arrays.asList(new PromotionResultData(), new PromotionResultData());
		final List<PromotionResultData> convertedProductPromotions = Arrays.asList(new PromotionResultData(), new PromotionResultData());

		final PromotionOrderResults promotionOrderResults = Mockito.mock(PromotionOrderResults.class);
		Mockito.lenient().when(promotionOrderResults.getPotentialOrderPromotions()).thenReturn(potentialOrderPromotions);
		Mockito.lenient().when(promotionOrderResults.getPotentialProductPromotions()).thenReturn(potentialProductPromotions);

		Mockito.lenient().when(promotionsService.getPromotionResults(any(AbstractOrderModel.class))).thenReturn(promotionOrderResults);
		Mockito.lenient().when(modelService.getAll(eq(potentialOrderPromotions), any())).thenReturn(
				(orderPromotionModels));
		Mockito.lenient().when(modelService.getAll(eq(potentialProductPromotions),  any())).thenReturn(
				(productPromotionModels));
		Mockito.lenient().when(promotionResultConverter.convertAll(eq(orderPromotionModels))).thenReturn(convertedOrderPromotions);
		Mockito.lenient().when(promotionResultConverter.convertAll(eq(productPromotionModels))).thenReturn(convertedProductPromotions);

		bundleCartPopulator.populate(source, target);

		assertThat(target.getPotentialOrderPromotions()).isEqualTo(convertedOrderPromotions);
		assertThat(target.getPotentialProductPromotions()).isEqualTo(convertedProductPromotions);

		verify(promotionsService).getPromotionResults(source);
		verify(modelService).getAll(potentialOrderPromotions, new ArrayList());
		verify(modelService).getAll(potentialProductPromotions, new ArrayList());
		verify(promotionResultConverter).convertAll(orderPromotionModels);
		verify(promotionResultConverter).convertAll(productPromotionModels);
		verifyNoMoreInteractions(promotionsService, modelService, promotionResultConverter);
	}

	// This method is needed in order for promotionResult.equals() method to work correctly.
	private PromotionResult newPromotionResult(final long pkValue)
	{
		final PK pk = PK.fromLong(pkValue);
		final MockTenant tenant = new MockTenant("tenantId");
		final PromotionResult promotionResult = mock(PromotionResult.class);

		Mockito.lenient().when(promotionResult.getPK()).thenReturn(pk);
		Mockito.lenient().when(promotionResult.getTenant()).thenReturn(tenant);

		return promotionResult;
	}
}

package com.sap.retail.sapppspricing.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.retail.sapppspricing.PPSConfigService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.PromotionOrderEntryConsumedData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Test Class for {@link OPPSExtendedCartPopulator}
 */
@UnitTest
public class OPPSExtendedCartPopulatorTest {

	private static final String APPLIED_PRODUCT_PROMOTION = "Applied Product Promotion";
	private static final String POTENTIAL_PRODUCT_PROMOTION = "Potentail Product Promotion";
	private static final String POTENTIAL_ORDER_PROMOTION = "Potentail Order Promotion";
	private static final String APPLIED_ORDER_PROMOTION = "Applied Order Promotion";
	private static final String APPLIED = "APPLIED";
	private static final String POTENTIAL = "POTENTIAL";
	private OPPSExtendedCartPopulator populator;
	@Mock
	private PPSConfigService configService;
	@Mock
	private PriceDataFactory priceDataFactory;
	private CartModel cart;
	private CartData data;

	@Mock
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;

	@Before
	public void setUp() {
		populator = new OPPSExtendedCartPopulator();
		MockitoAnnotations.initMocks(this);
		populator.setConfigService(configService);
		populator.setPromotionResultConverter(promotionResultConverter);
		populator.setPriceDataFactory(priceDataFactory);
		PriceData price = new PriceData();
		Mockito.when(priceDataFactory.create(any(PriceDataType.class), any(BigDecimal.class), any(CurrencyModel.class)))
				.thenReturn(price);
		cart = new CartModel();
		data = new CartData();
	}

	@Test
	public void testPopulateWhileOPPSOFF() {
		Mockito.when(configService.isPpsActive(any(AbstractOrderModel.class))).thenReturn(false);
		populator.populate(cart, data);
		assertNull(data.getAppliedOrderPromotions());
	}

	@Test
	public void testNullAppliedOrderPromotion() {
		cart.setAllPromotionResults(getPromotionModels());
		Mockito.when(configService.isPpsActive(any(AbstractOrderModel.class))).thenReturn(true);
		Mockito.when(promotionResultConverter.convertAll(getPromotionModels())).thenReturn(null);
		populator.populate(cart, data);
		assertNull(data.getAppliedOrderPromotions());
	}

	@Test
	public void testPopulateAppliedProductPromotion() {
		updateCart();
		Mockito.when(configService.isPpsActive(any(AbstractOrderModel.class))).thenReturn(true);
		List<PromotionResultData> allPromotionResultData = new ArrayList<>();
		allPromotionResultData.add(getPromotion(APPLIED, APPLIED_PRODUCT_PROMOTION, false));
		Mockito.when(promotionResultConverter.convertAll(anySet())).thenReturn(allPromotionResultData);
		populator.populate(cart, data);
		assertEquals(APPLIED_PRODUCT_PROMOTION, data.getAppliedProductPromotions().get(0).getDescription());
	}

	@Test
	public void testPopulatePotentialProductPromotion() {
		updateCart();
		Mockito.when(configService.isPpsActive(any(AbstractOrderModel.class))).thenReturn(true);
		List<PromotionResultData> allPromotionResultData = new ArrayList<>();
		allPromotionResultData.add(getPromotion(POTENTIAL, POTENTIAL_PRODUCT_PROMOTION, false));
		Mockito.when(promotionResultConverter.convertAll(anySet())).thenReturn(allPromotionResultData);
		populator.populate(cart, data);
		assertEquals(POTENTIAL_PRODUCT_PROMOTION, data.getPotentialProductPromotions().get(0).getDescription());
	}

	@Test
	public void testPopulatePotentialOrderPromotion() {
		updateCart();
		Mockito.when(configService.isPpsActive(any(AbstractOrderModel.class))).thenReturn(true);
		List<PromotionResultData> allPromotionResultData = new ArrayList<>();
		allPromotionResultData.add(getPromotion(POTENTIAL, POTENTIAL_ORDER_PROMOTION, true));
		Mockito.when(promotionResultConverter.convertAll(anySet())).thenReturn(allPromotionResultData);
		populator.populate(cart, data);
		assertEquals(POTENTIAL_ORDER_PROMOTION, data.getPotentialOrderPromotions().get(0).getDescription());
	}

	@Test
	public void testPopulateAppliedOrderPromotion() {
		updateCart();
		Mockito.when(configService.isPpsActive(any(AbstractOrderModel.class))).thenReturn(true);
		List<PromotionResultData> allPromotionResultData = new ArrayList<>();
		allPromotionResultData.add(getPromotion(APPLIED, APPLIED_ORDER_PROMOTION, true));
		Mockito.when(promotionResultConverter.convertAll(anySet())).thenReturn(allPromotionResultData);
		populator.populate(cart, data);
		assertEquals(APPLIED_ORDER_PROMOTION, data.getAppliedOrderPromotions().get(0).getDescription());
	}

	private PromotionResultData getPromotion(String promotionType, String promotionDescription, boolean isOrderLevel) {
		PromotionResultData appliedProductPromotion = new PromotionResultData();
		appliedProductPromotion.setOppPromoResultType(promotionType);
		appliedProductPromotion.setDescription(promotionDescription);
		appliedProductPromotion.setConsumedEntries(isOrderLevel ? null : getConsumedEntries());
		return appliedProductPromotion;
	}

	private void updateCart() {
		AbstractOrderEntryModel entry = new CartEntryModel();
		List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(entry);
		cart.setEntries(entries);
		CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("EUR");
		cart.setCurrency(currency);
		cart.setAllPromotionResults(getPromotionModels());

	}

	private Set<PromotionResultModel> getPromotionModels() {
		Set<PromotionResultModel> models = new HashSet<>();
		PromotionResultModel promotionResult = new PromotionResultModel();
		models.add(promotionResult);
		return models;
	}

	private List<PromotionOrderEntryConsumedData> getConsumedEntries() {
		List<PromotionOrderEntryConsumedData> entries = new ArrayList<>();
		PromotionOrderEntryConsumedData entry = new PromotionOrderEntryConsumedData();
		entries.add(entry);
		return entries;
	}
}

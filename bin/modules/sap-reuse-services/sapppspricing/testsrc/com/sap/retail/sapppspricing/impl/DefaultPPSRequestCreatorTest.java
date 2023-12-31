/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.retail.sapppspricing.swagger.businessobject.dto.ARTSCommonHeaderType.ActionCodeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ARTSCommonHeaderType.MessageTypeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.BusinessUnitCommonData.TypeCodeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LineItemDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.ShoppingBasketBase;
import com.sap.retail.sapppspricing.LineItemPopulator;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.enums.InterfaceVersion;
import com.sap.retail.sapppspricing.opps.ObjectFactory;
import com.sap.retail.sapppspricing.opps.PPSClientBeanAccessor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;

@SuppressWarnings("javadoc")
@UnitTest
public class DefaultPPSRequestCreatorTest {
	
	private static final int TEN = 10;
	private static final int TWO = 2;
	private static final int ONE = 1;
	private DefaultPPSRequestCreator cut;
	@Mock
	private PPSClientBeanAccessor accessorMock;
	@Mock
	private LineItemPopulator<ProductModel> lineItemPopulatorMock;
	@Mock
	private CommonI18NService i18nServiceMock;
	@Mock
	private PPSConfigService configService;
	private final RequestHelperImpl helperImpl = new RequestHelperImpl();
	private final ProductModel product = new ProductModel();
	private final UnitModel unit = new UnitModel();
	private final CurrencyModel currency = new CurrencyModel();

	private CatalogVersionModel createCatalogVersionWithSAPConfig() {
		final CatalogVersionModel catalogVersion = new CatalogVersionModel();
		final CatalogModel catalog = new CatalogModel();
		final Collection<BaseStoreModel> baseStores = new LinkedList<>();
		catalog.setBaseStores(baseStores);
		catalogVersion.setCatalog(catalog);
		catalogVersion.setDefaultCurrency(currency);
		return catalogVersion;
	}

	@Before
	public void setUp() {
		cut = new DefaultPPSRequestCreator();
		MockitoAnnotations.initMocks(this);
		unit.setCode("PC");
		product.setUnit(unit);
		product.setCode("4711");
		currency.setIsocode("USD");
		Mockito.when(configService.isPpsActive(Mockito.any(ProductModel.class))).thenReturn(Boolean.TRUE);
		Mockito.when(configService.isPpsActive(Mockito.any(AbstractOrderModel.class))).thenReturn(Boolean.TRUE);
		product.setCatalogVersion(createCatalogVersionWithSAPConfig());
	}

	@Test
	public void testSetGetBaseConfigService() {
		cut = new DefaultPPSRequestCreator();
		assertNull(cut.getConfigService());
		cut.setConfigService(configService);
		assertSame(configService, cut.getConfigService());
	}

	@Test
	public void testSetGetCommonI18NService() {
		cut = new DefaultPPSRequestCreator();
		assertNull(cut.getCommonI18NService());
		cut.setCommonI18NService(i18nServiceMock);
		assertSame(i18nServiceMock, cut.getCommonI18NService());
	}

	private void setHelper() {
		final ObjectFactory objectFactory = new ObjectFactory();
		helperImpl.setObjectFactory(objectFactory);
		Mockito.when(accessorMock.getHelper()).thenReturn(helperImpl);
	}

	private void setMocks() {
		final List<LineItemPopulator<ProductModel>> lineItemPopulators = new ArrayList<>();
		lineItemPopulators.add(lineItemPopulatorMock);
		cut.setLineItemPopulators(lineItemPopulators);
		cut.setAccessor(accessorMock);
		cut.setConfigService(configService);
		cut.setCommonI18NService(i18nServiceMock);
		Mockito.when(configService.getBusinessUnitId(Mockito.any(ProductModel.class))).thenReturn("4711");
		Mockito.when(configService.getBusinessUnitId(Mockito.any(AbstractOrderModel.class))).thenReturn("4711");
		final LanguageModel languageModel = new LanguageModel();
		languageModel.setIsocode("DE");
		Mockito.when(i18nServiceMock.getCurrentLanguage()).thenReturn(languageModel);
	}

	private void createPriceCalculate(final PriceCalculate priceCalculate) {
		final PriceCalculateBase priceCalulateBody = new PriceCalculateBase();
		final ShoppingBasketBase shoppingBasket = new ShoppingBasketBase();
		priceCalulateBody.setShoppingBasket(shoppingBasket);
		priceCalculate.getPriceCalculateBody().add(priceCalulateBody);
	}

	private void createOrderWithOneEntry(final AbstractOrderModel order) {
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
		final UnitModel unit = new UnitModel();
		unit.setCode("ST");
		entry.setUnit(unit);
		entry.setProduct(product);
		final Long quantity = 14L;
		entry.setQuantity(quantity);
		entries.add(entry);
		order.setEntries(entries);
		final BaseStoreModel store = new BaseStoreModel();
		order.setStore(store);
		order.setCode("WattFuernCode?");
	}

	@Test
	public void testFillRequestBodyForCart() {
		// create priceCalculate
		final PriceCalculate priceCalculate = new PriceCalculate();
		createPriceCalculate(priceCalculate);
		// create Order with entries
		final AbstractOrderModel order = new AbstractOrderModel();
		createOrderWithOneEntry(order);
		setMocks();
		setHelper();
		final ArgRecorderAnswer<Void> answer = new ArgRecorderAnswer<Void>();
		Mockito.doAnswer(answer).when(lineItemPopulatorMock).populate(Mockito.any(LineItemDomainSpecific.class),
				Mockito.eq(product));
		cut.fillRequestBodyForCart(order, priceCalculate);
		assertEquals(1, priceCalculate.getPriceCalculateBody().get(0).getShoppingBasket().getLineItem().size());
		final LineItemDomainSpecific lineItem = priceCalculate.getPriceCalculateBody().get(0).getShoppingBasket()
				.getLineItem().get(0);
		assertEquals("4711", lineItem.getSale().getItemID().get(0).getValue());
		assertEquals("Stock", lineItem.getSale().getItemType());
		assertEquals("ST", lineItem.getSale().getQuantity().get(0).getUnitOfMeasureCode());
		assertEquals("14", lineItem.getSale().getQuantity().get(0).getValue().toString());
		final LineItemDomainSpecific lineItemAnswer = answer.getArg(0);
		assertEquals("4711", lineItemAnswer.getSale().getItemID().get(0).getValue());
		final ProductModel productAnswer = answer.getArg(1);
		assertSame(product, productAnswer);
	}

	@Test
	public void testSetGetLineItemPopulator() {
		final List<LineItemPopulator<ProductModel>> lineItemPopulators = new ArrayList<>();
		lineItemPopulators.add(lineItemPopulatorMock);
		assertNull(cut.getLineItemPopulators());
		cut.setLineItemPopulators(lineItemPopulators);
		assertSame(lineItemPopulators, cut.getLineItemPopulators());
	}

	@Test
	public void testCreateRequestForCatalog() {
		setHelper();
		setMocks();
		final PriceCalculate priceCalculate = cut.createRequestForCatalog(product, true);
		assertEquals("DE", priceCalculate.getArTSHeader().getRequestedLanguage());
		assertEquals("4711", priceCalculate.getArTSHeader().getBusinessUnit().get(0).getValue());
		assertEquals(TypeCodeEnum.RETAILSTORE, priceCalculate.getArTSHeader().getBusinessUnit().get(0).getTypeCode());
		assertEquals(ActionCodeEnum.CALCULATE, priceCalculate.getArTSHeader().getActionCode());
		assertEquals(MessageTypeEnum.REQUEST, priceCalculate.getArTSHeader().getMessageType());
		assertEquals(1, priceCalculate.getPriceCalculateBody().size());
	}

	@Test
	public void testDiscountableFlagTrue() {
		setHelper();
		setMocks();
		cut.setDiscountableFlag(true);
		product.setDiscountable(Boolean.TRUE);
		LineItemDomainSpecific item = cut.createLineItem(123, product, "ST", BigDecimal.ONE);
		assertFalse(item.getSale().isNonDiscountableFlag());
	}

	@Test
	public void testDiscountableFlagFalse() {
		setHelper();
		setMocks();
		cut.setDiscountableFlag(true);
		product.setDiscountable(Boolean.FALSE);
		LineItemDomainSpecific item = cut.createLineItem(123, product, "ST", BigDecimal.ONE);
		assertTrue(item.getSale().isNonDiscountableFlag());
	}

	@Test
	public void testDiscountableFlagTrueSwitchoff() {
		setHelper();
		setMocks();
		cut.setDiscountableFlag(false);
		product.setDiscountable(Boolean.TRUE);
		LineItemDomainSpecific item = cut.createLineItem(123, product, "ST", BigDecimal.ONE);
		assertFalse(item.getSale().isNonDiscountableFlag());
	}

	@Test
	public void testVersionDeterminationAndSetting1() {
		setHelper();
		setMocks();
		// create priceCalculate
		final PriceCalculate priceCalculate = new PriceCalculate();
		createPriceCalculate(priceCalculate);
		// create Version
		InterfaceVersion version = InterfaceVersion.VERSION20;
		cut.setVersionForRequest(version, priceCalculate);
		Integer majorVersion =  Integer.valueOf(TWO);
		Integer minorVersion =  Integer.valueOf(ONE);
		assertEquals(majorVersion, priceCalculate.getInternalMajorVersion());
		assertEquals(minorVersion, priceCalculate.getInternalMinorVersion());
	}

	@Test
	public void testVersionDeterminationAndSetting2() {
		setHelper();
		setMocks();
		// create priceCalculate
		final PriceCalculate priceCalculate = new PriceCalculate();
		createPriceCalculate(priceCalculate);
		// create Version
		InterfaceVersion version = InterfaceVersion.VERSION20;
		cut.setVersionForRequest(version, priceCalculate);
		Integer majorVersion =  Integer.valueOf(TWO);
		Integer minorVersion =  Integer.valueOf(ONE);
		assertEquals(majorVersion, priceCalculate.getInternalMajorVersion());
		assertEquals(minorVersion, priceCalculate.getInternalMinorVersion());
	}
	
	@Test
	public void testVersionDeterminationAndSetting3() {
		setHelper();
		setMocks();
		// create priceCalculate
		final PriceCalculate priceCalculate = new PriceCalculate();
		createPriceCalculate(priceCalculate);
		// create Version
		InterfaceVersion version = InterfaceVersion.VERSION100;
		cut.setVersionForRequest(version, priceCalculate);
		Integer majorVersion = Integer.valueOf(TEN);
		Integer minorVersion = Integer.valueOf(0);
		assertEquals(majorVersion, priceCalculate.getInternalMajorVersion());
		assertEquals(minorVersion, priceCalculate.getInternalMinorVersion());
	}
}

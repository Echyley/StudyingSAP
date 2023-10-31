/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtcfgfacades.cart.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.sapproductconfigsomservices.prodconf.impl.DefaultProductConfigurationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultStandardOrderEntryPopulatorTest
{
	DefaultStandardOrderEntryPopulator classUnderTest = new DefaultStandardOrderEntryPopulator();
	private AbstractOrderEntryModel source;
	private String pk;
	private DefaultProductConfigurationService defaultConfigurationService;
	private ProductModel productModel;
	private BaseStoreService baseStoreService;
	private final BaseStoreModel baseStore = new BaseStoreModel();
	private final SAPConfigurationModel sapConfiguration = new SAPConfigurationModel();


	@Test
	public void testPopulate()
	{
		final OrderEntryData target = new OrderEntryData();
		classUnderTest.populate(source, target);
		assertTrue(target.isConfigurationAttached());
		assertEquals(pk, target.getItemPK());
	}

	@Test
	public void testPopulateNotEnabled()
	{
		sapConfiguration.setSapordermgmt_enabled(false);
		final OrderEntryData target = new OrderEntryData();
		classUnderTest.populate(source, target);
		assertFalse(target.isConfigurationAttached());
	}

	@Before
	public void init()
	{
		source = EasyMock.createMock(AbstractOrderEntryModel.class);
		productModel = EasyMock.createMock(ProductModel.class);
		pk = "1";
		final String cfg = "<XML>";
		final PK key = PK.parse(pk);
		EasyMock.expect(source.getPk()).andReturn(key).anyTimes();
		EasyMock.expect(source.getExternalConfiguration()).andReturn(cfg);
		EasyMock.expect(productModel.getSapConfigurable()).andReturn(true);
		defaultConfigurationService = EasyMock.createMock(DefaultProductConfigurationService.class);
		EasyMock.expect(defaultConfigurationService.isInSession(pk)).andReturn(true);
		final SessionAccessService sessionAccessService = EasyMock.createMock(SessionAccessService.class);
		sessionAccessService.setConfigIdForCartEntry(pk, null);
		EasyMock.expect(sessionAccessService.getCartEntryForConfigId(pk)).andReturn(null);
		defaultConfigurationService.setSessionAccessService(sessionAccessService);
		classUnderTest.setProductConfigurationService(defaultConfigurationService);
		EasyMock.expect(source.getProduct()).andReturn(productModel);
		EasyMock.expect(productModel.getSapConfigurable()).andReturn(Boolean.TRUE);

		baseStoreService = EasyMock.createMock(BaseStoreService.class);
		EasyMock.expect(baseStoreService.getCurrentBaseStore()).andReturn(baseStore).anyTimes();
		baseStore.setSAPConfiguration(sapConfiguration);
		sapConfiguration.setSapordermgmt_enabled(true);
		classUnderTest.setBaseStoreService(baseStoreService);

		EasyMock.replay(source, sessionAccessService, defaultConfigurationService, productModel, baseStoreService);
	}

	@Test
	public void testProductConfigurationService()
	{

		assertEquals(defaultConfigurationService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testIsConfigurationSessionAvailable()
	{
		assertTrue(classUnderTest.isConfigurationSessionAvailable(pk));
	}

	@Test
	public void testIsSapOrderManagementEnabled()
	{
		assertTrue(classUnderTest.isSyncOrdermgmtEnabled());
	}

	@Test
	public void testIsSapOrderManagementEnabledNoSapConfiguration()
	{
		baseStore.setSAPConfiguration(null);
		assertFalse(classUnderTest.isSyncOrdermgmtEnabled());
	}

	@Test
	public void testIsSapOrderManagementEnabledNoCurrentBaseStorre()
	{
		EasyMock.reset(baseStoreService);
		// backward compatibility: if current based store is not set, we can't check whether SOM is active, hence we assume true
		assertTrue(classUnderTest.isSyncOrdermgmtEnabled());
	}
}

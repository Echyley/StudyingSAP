/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.acceleratorfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade.ExpressCheckoutResult;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.sap.retail.oaa.acceleratorfacades.order.impl.DefaultSapOaaAcceleratorCheckoutFacade;
import com.sap.retail.oaa.commerce.facades.checkout.OaaCheckoutFacade;
import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;


/**
 *
 */
@UnitTest
public class DefaultSapOaaAcceleratorCheckoutFacadeTest
{

	private DefaultSapOaaAcceleratorCheckoutFacade classUnderTest;
	private OaaCheckoutFacade oaaCheckoutFacadeMock;


	@Before
	public void setup()
	{
		// create partial mock to mock super method call
		classUnderTest = EasyMock.createMockBuilder(DefaultSapOaaAcceleratorCheckoutFacade.class)
				.addMockedMethod("callSuperPerformExpressCheckout").createMock();
		EasyMock.expect(classUnderTest.callSuperPerformExpressCheckout()).andReturn(ExpressCheckoutResult.SUCCESS);
		EasyMock.replay(classUnderTest);
	}

	@Test
	public void performExpressCheckout_Success()
	{
		// Sourcing is successful
		CommonUtils commonUtils=EasyMock.createNiceMock(CommonUtils.class);
		EasyMock.expect(Boolean.valueOf(commonUtils.isCAREnabled())).andReturn(true);
		oaaCheckoutFacadeMock = EasyMock.createNiceMock(OaaCheckoutFacade.class);
		EasyMock.expect(Boolean.valueOf(oaaCheckoutFacadeMock.doSourcingForSessionCart())).andReturn(new Boolean(true));
		EasyMock.replay(oaaCheckoutFacadeMock);
		classUnderTest.setCommonUtils(commonUtils);
		classUnderTest.setOaaCheckoutFacade(oaaCheckoutFacadeMock);
		EasyMock.replay(commonUtils);
		final ExpressCheckoutResult expressCheckoutResult = classUnderTest.performExpressCheckout();
		Assert.assertEquals(ExpressCheckoutResult.SUCCESS, expressCheckoutResult);
	}

	@Test
	public void performExpressCheckout_Failure()
	{
		// Sourcing failed
		oaaCheckoutFacadeMock = EasyMock.createNiceMock(OaaCheckoutFacade.class);
		CommonUtils commonUtils=EasyMock.createNiceMock(CommonUtils.class);
		EasyMock.expect(Boolean.valueOf(commonUtils.isCAREnabled())).andReturn(true);
		EasyMock.expect(Boolean.valueOf(oaaCheckoutFacadeMock.doSourcingForSessionCart())).andReturn(new Boolean(false));
		EasyMock.replay(oaaCheckoutFacadeMock);
		classUnderTest.setOaaCheckoutFacade(oaaCheckoutFacadeMock);
		classUnderTest.setCommonUtils(commonUtils);
		EasyMock.replay(commonUtils);
		final ExpressCheckoutResult expressCheckoutResult = classUnderTest.performExpressCheckout();
		Assert.assertNotEquals(ExpressCheckoutResult.SUCCESS, expressCheckoutResult);
		Assert.assertEquals(ExpressCheckoutResult.ERROR_NOT_AVAILABLE, expressCheckoutResult);
	}

	@Test
	public void performExpressCheckout_FailureSuperCall()
	{
		// create partial mock to mock super method call
		classUnderTest = EasyMock.createMockBuilder(DefaultSapOaaAcceleratorCheckoutFacade.class)
				.addMockedMethod("callSuperPerformExpressCheckout").createMock();
		EasyMock.expect(classUnderTest.callSuperPerformExpressCheckout()).andReturn(ExpressCheckoutResult.ERROR_DELIVERY_ADDRESS);
		EasyMock.replay(classUnderTest);
		CommonUtils commonUtils=EasyMock.createNiceMock(CommonUtils.class);
		classUnderTest.setCommonUtils(commonUtils);
		EasyMock.expect(Boolean.valueOf(commonUtils.isCAREnabled())).andReturn(true);
		EasyMock.replay(commonUtils);
		final ExpressCheckoutResult expressCheckoutResult = classUnderTest.performExpressCheckout();
		Assert.assertNotNull(expressCheckoutResult);
		Assert.assertEquals(ExpressCheckoutResult.ERROR_DELIVERY_ADDRESS, expressCheckoutResult);
	}
	
}

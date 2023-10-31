/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.cart.action.populator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryAction;
import de.hybris.platform.acceleratorfacades.cart.action.CartEntryActionHandlerRegistry;
import de.hybris.platform.acceleratorfacades.cart.action.impl.RemoveCartEntryActionHandler;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AcceleratorCartEntryActionPopulatorTest
{

	private static final String SHOULD_NOT_BE_NULL_MSG = "Supported actions should not be null";

	@InjectMocks
	private final AcceleratorCartEntryActionPopulator acceleratorCartEntryActionPopulator = new AcceleratorCartEntryActionPopulator();

	@Mock
	private CartEntryActionHandlerRegistry cartEntryActionHandlerRegistry;

	@Mock
	private AbstractOrderEntryModel abstractOrderEntryModel;

	@Mock
	private CartEntryModel cartEntryModel;

	@Mock
	private RemoveCartEntryActionHandler removeCartEntryActionHandler;

	private OrderEntryData orderEntryData;

	@Before
	public void setUp()
	{
		orderEntryData = new OrderEntryData();
	}

	@Test
	public void shouldPopulateSupportedActions()
	{
		given(cartEntryActionHandlerRegistry.getHandler(CartEntryAction.REMOVE)).willReturn(removeCartEntryActionHandler);
		given(Boolean.valueOf(removeCartEntryActionHandler.supports(any(CartEntryModel.class)))).willReturn(Boolean.TRUE);

		acceleratorCartEntryActionPopulator.populate(cartEntryModel, orderEntryData);

		Assert.assertNotNull(SHOULD_NOT_BE_NULL_MSG, orderEntryData.getSupportedActions());
		Assert.assertEquals("Actions list should contain 1 entry", 1, orderEntryData.getSupportedActions().size());
		Assert.assertTrue("Action list element should be 'REMOVE'", orderEntryData.getSupportedActions().contains("REMOVE"));
	}

	@Test
	public void shouldNotPopulateUnSupportedActions()
	{
		given(cartEntryActionHandlerRegistry.getHandler(CartEntryAction.REMOVE)).willReturn(removeCartEntryActionHandler);
		given(Boolean.valueOf(removeCartEntryActionHandler.supports(any(CartEntryModel.class)))).willReturn(Boolean.FALSE);

		acceleratorCartEntryActionPopulator.populate(cartEntryModel, orderEntryData);

		Assert.assertNotNull(SHOULD_NOT_BE_NULL_MSG, orderEntryData.getSupportedActions());
		Assert.assertTrue("Actions list should be empty", orderEntryData.getSupportedActions().isEmpty());
	}

	@Test
	public void shouldNotPopulateActionsFromNonConfiguredHandlers()
	{
		given(cartEntryActionHandlerRegistry.getHandler(any())).willReturn(null);

		acceleratorCartEntryActionPopulator.populate(cartEntryModel, orderEntryData);

		Assert.assertNotNull(SHOULD_NOT_BE_NULL_MSG, orderEntryData.getSupportedActions());
		Assert.assertTrue("Actions list should be empty", orderEntryData.getSupportedActions().isEmpty());
	}

	@Test
	public void shouldNotPopulateFromNonCartEntryModel()
	{
		acceleratorCartEntryActionPopulator.populate(abstractOrderEntryModel, orderEntryData);

		Assert.assertNull("Should only populate from a CartEntryModel instance", orderEntryData.getSupportedActions());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowNullSource()
	{
		acceleratorCartEntryActionPopulator.populate(null, orderEntryData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowNullTarget()
	{
		acceleratorCartEntryActionPopulator.populate(cartEntryModel, null);
	}
}

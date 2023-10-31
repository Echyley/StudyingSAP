/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package ydocumentcartpackage.persistence.polyglot.uow;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.persistence.AbstractOptimisticLockingIntegrationTest;
import de.hybris.platform.persistence.hjmp.HJMPUtils;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.AppendSpringConfiguration;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;

import ydocumentcartpackage.persistence.polyglot.repository.documentcart.CartModelsCreator;
import ydocumentcartpackage.test.ConcurrentModificationTestService;

@AppendSpringConfiguration("test/ydocumentcart-test-spring.xml")
@IntegrationTest
public class PolyglotOptimisticLockingIntegrationTest extends AbstractOptimisticLockingIntegrationTest
{
	private CartModelsCreator cartModelsCreator;
	@Resource
	private UserService userService;
	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private ConcurrentModificationTestService concurrentModificationTestService;

	private ProductModel product;
	private UnitModel unit;
	private long initialValue;

	@Before
	public void setUp()
	{
		cartModelsCreator = new CartModelsCreator(modelService, userService, commonI18NService);

		final CatalogModel catalog = cartModelsCreator.createCatalog();
		product = cartModelsCreator.createProduct(cartModelsCreator.createCatalogVersion(catalog));
		unit = cartModelsCreator.createUnit();

		modelService.saveAll();

		super.setUp();
	}

	@Override
	public CartModel createTestModelWithCodeAndIntegerAttrProperty(final String code)
	{
		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 0d);
		cartModel1.setCode(code);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);

		modelService.saveAll(cartModel1, cartEntry1, cartEntry2);
		return cartModel1;
	}

	@Test
	public void shouldUpdateItemInManyThreadsRetryingOperationOnConcurrentModificationExceptionJALO()
	{
		persistenceLegacyModeSwitch.switchToValue("true");
		optimisticLockSwitch.switchToValue("true");

		updateAndAssertItemInManyThreadsRetryingOperationOnConcurrentModificationException(false, testModel1.getPk());
	}

	@Test
	public void shouldUpdateItemInManyThreadsRetryingOperationOnConcurrentModificationExceptionWithPerThreadSettingJALO()
	{
		persistenceLegacyModeSwitch.switchToValue("true");
		optimisticLockSwitch.switchToValue("false");

		updateAndAssertItemInManyThreadsRetryingOperationOnConcurrentModificationException(true, testModel1.getPk());
		assertThat(HJMPUtils.isOptimisticLockingEnabled()).isFalse();
	}

	@Override
	protected void runTestService(final PK testModelPk)
	{

		concurrentModificationTestService.updateCart(testModelPk);

		((CartModel) modelService.get(testModelPk)).getEntries().stream().findFirst().ifPresent(e ->
				concurrentModificationTestService.updateCartEntry(e.getPk()));
	}

	@Override
	protected long getTestTimeout()
	{
		//this test is executed against real databases, therefor it might take some time
		return TimeUnit.MINUTES.toSeconds(2);
	}

	@Override
	protected void verifyResult(final ItemModel testModel)
	{
		final CartModel cart = (CartModel) testModel;
		assertThat(cart.getPaymentCost()).isCloseTo(INTEGER_ATTR_VALUE, Offset.offset(0.1));

		final Optional<AbstractOrderEntryModel> first = cart.getEntries().stream().findFirst();
		assertThat(first).isPresent();
		assertThat(first.get().getQuantity()).isEqualTo(1 + INTEGER_ATTR_VALUE);
	}
}

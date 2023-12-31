/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.europe1.jalo;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationType;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.price.Discount;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.type.ComposedType;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("deprecation")
@IntegrationTest
public class Europe1PriceFactoryDiscountsIntegrationTest extends ServicelayerBaseTest
{
	private static final String TEST_NAME = Europe1PriceFactoryDiscountsIntegrationTest.class.getName();
	private static final String TEST_PRODUCT = "TEST_PRODUCT_" + TEST_NAME;
	private static final String TEST_USER = "TEST_USER_" + TEST_NAME;
	private static final String TEST_USER_GROUP = "TEST_USER_GROUP_" + TEST_NAME;
	private static final String TEST_PRODUCT_GROUP = "TEST_PRODUCT_GROUP_" + TEST_NAME;

	private Europe1PriceFactory factory;
	private Discount discount;
	private Currency currency;

	private AbstractDiscountRow any_any;
	private AbstractDiscountRow any_given;
	private AbstractDiscountRow any_group;
	private AbstractDiscountRow given_any;
	private AbstractDiscountRow given_given;
	private AbstractDiscountRow given_group;
	private AbstractDiscountRow group_any;
	private AbstractDiscountRow group_given;
	private AbstractDiscountRow group_group;
	private AbstractDiscountRow global_any;
	private AbstractDiscountRow global_given;
	private AbstractDiscountRow global_group;
	private AbstractDiscountRow id_any;
	private AbstractDiscountRow id_given;
	private AbstractDiscountRow id_group;
	private Product anyProduct;
	private Product givenProduct;
	private EnumerationValue givenProductGroup;
	private User anyUser;
	private User givenUser;
	private EnumerationValue givenUserGroup;

	@Before
	public void setUp() throws Exception
	{
		factory = Europe1PriceFactory.getInstance();
		final ProductManager productManager = ProductManager.getInstance();
		final UserManager userManager = UserManager.getInstance();
		final OrderManager orderManager = OrderManager.getInstance();
		final EnumerationManager enumerationManager = EnumerationManager.getInstance();
		final EnumerationType productGroupType = enumerationManager.getEnumerationType(
				Europe1Constants.TYPES.PRICE_PRODUCT_GROUP);

		discount = orderManager.createDiscount("TEST_DISCOUNT");
		try
		{
			currency = C2LManager.getInstance().getCurrencyByIsoCode("EUR");
		}
		catch (final JaloItemNotFoundException e)
		{
			currency = C2LManager.getInstance().createCurrency("EUR");
		}

		productManager.createProduct(TEST_PRODUCT);
		userManager.createCustomer(TEST_USER);
		factory.createUserPriceGroup(TEST_USER_GROUP);
		enumerationManager.createEnumerationValue(productGroupType, TEST_PRODUCT_GROUP);

		anyProduct = product(null);
		givenProduct = product(TEST_PRODUCT);
		givenProductGroup = productGroup(TEST_PRODUCT_GROUP);
		anyUser = user(null);
		givenUser = user(TEST_USER);
		givenUserGroup = userGroup(TEST_USER_GROUP);

		any_any = createDiscount(anyProduct, anyUser);
		any_given = createDiscount(anyProduct, givenUser);
		any_group = createDiscount(anyProduct, givenUserGroup);
		given_any = createDiscount(givenProduct, anyUser);
		given_given = createDiscount(givenProduct, givenUser);
		given_group = createDiscount(givenProduct, givenUserGroup);
		group_any = createDiscount(givenProductGroup, anyUser);
		group_given = createDiscount(givenProductGroup, givenUser);
		group_group = createDiscount(givenProductGroup, givenUserGroup);
		global_any = createGlobalDiscount(anyUser);
		global_given = createGlobalDiscount(givenUser);
		global_group = createGlobalDiscount(givenUserGroup);
		id_any = createDiscount(TEST_PRODUCT, anyUser);
		id_given = createDiscount(TEST_PRODUCT, givenUser);
		id_group = createDiscount(TEST_PRODUCT, givenUserGroup);
	}

	@Test
	public void shouldQueryDiscountsForAnyProductAndAnyUser()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(anyProduct, anyUser);

		assertThat(prices).extracting(Item::getPK).containsOnly(global_any.getPK());
	}

	@Test
	public void shouldQueryDiscountsForAnyProductAndGivenUser()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(anyProduct, givenUser);

		assertThat(prices).extracting(Item::getPK).containsOnly(global_any.getPK(), global_given.getPK());
	}

	@Test
	public void shouldQueryDiscountsForAnyProductAndGivenUserGroup()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(anyProduct, givenUserGroup);

		assertThat(prices).extracting(Item::getPK).containsOnly(global_any.getPK(), global_group.getPK());
	}

	@Test
	public void shouldQueryDiscountsForGivenProductAndAnyUser()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(givenProduct, anyUser);

		assertThat(prices).extracting(Item::getPK).containsOnly(any_any.getPK(), given_any.getPK(), id_any.getPK());
	}

	@Test
	public void shouldQueryDiscountsForGivenProductAndGivenUser()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(givenProduct, givenUser);

		assertThat(prices).extracting(Item::getPK)
		                  .containsOnly(any_any.getPK(), any_given.getPK(), given_any.getPK(), given_given.getPK(),
				                  id_any.getPK(), id_given.getPK());
	}

	@Test
	public void shouldQueryDiscountsForGivenProductAndGivenUserGroup()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(givenProduct, givenUserGroup);

		assertThat(prices).extracting(Item::getPK)
		                  .containsOnly(any_any.getPK(), any_group.getPK(), given_any.getPK(), given_group.getPK(),
				                  id_any.getPK(), id_group.getPK());
	}

	@Test
	public void shouldQueryDiscountsForGivenProductGroupAndAnyUser()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(givenProductGroup, anyUser);

		assertThat(prices).extracting(Item::getPK).containsOnly(any_any.getPK(), group_any.getPK());
	}

	@Test
	public void shouldQueryDiscountsForGivenProductGroupAndGivenUser()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(givenProductGroup, givenUser);

		assertThat(prices).extracting(Item::getPK)
		                  .containsOnly(any_any.getPK(), any_given.getPK(), group_any.getPK(), group_given.getPK());
	}

	@Test
	public void shouldQueryDiscountsForGivenProductGroupAndGivenUserGroup()
	{
		final Collection<? extends AbstractDiscountRow> prices = queryForDiscounts(givenProductGroup, givenUserGroup);

		assertThat(prices).extracting(Item::getPK)
		                  .containsOnly(any_any.getPK(), any_group.getPK(), group_any.getPK(), group_group.getPK());
	}

	private Collection<? extends AbstractDiscountRow> queryForDiscounts(final Object product, final Object user)
	{
		final Product prod = (product instanceof Product) ? (Product) product : null;
		final EnumerationValue prodGroup = (product instanceof EnumerationValue) ? (EnumerationValue) product : null;
		final User usr = (user instanceof User) ? (User) user : null;
		final EnumerationValue usrGroup = (user instanceof EnumerationValue) ? (EnumerationValue) user : null;

		return factory.queryDiscounts4Price(null, prod, prodGroup, usr, usrGroup);
	}

	private AbstractDiscountRow createDiscount(final Object product, final Object user) throws Exception
	{
		final Product prod = (product instanceof Product) ? (Product) product : null;
		final EnumerationValue prodGroup = (product instanceof EnumerationValue) ? (EnumerationValue) product : null;
		final User usr = (user instanceof User) ? (User) user : null;
		final EnumerationValue usrGroup = (user instanceof EnumerationValue) ? (EnumerationValue) user : null;
		final String productId = (product instanceof String) ? (String) product : null;

		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();

		return (DiscountRow) ComposedType.newInstance(ctx, DiscountRow.class, DiscountRow.PRODUCT, prod, DiscountRow.PG,
				prodGroup,
				DiscountRow.USER, usr, DiscountRow.UG, usrGroup, DiscountRow.CURRENCY,
				currency, DiscountRow.VALUE,
				23.4, DiscountRow.DISCOUNT, discount, DiscountRow.PRODUCTID,
				productId);
	}

	private AbstractDiscountRow createGlobalDiscount(final Object user) throws JaloPriceFactoryException
	{
		final User usr = (user instanceof User) ? (User) user : null;
		final EnumerationValue usrGroup = (user instanceof EnumerationValue) ? (EnumerationValue) user : null;

		return factory.createGlobalDiscountRow(usr, usrGroup, currency, 12.3, null, discount);
	}

	private User user(final String login)
	{
		if (login == null)
		{
			return null;
		}
		return UserManager.getInstance().getUserByLogin(login);
	}

	private EnumerationValue userGroup(final String code)
	{
		if (code == null)
		{
			return null;
		}

		return factory.getUserPriceGroup(code);
	}

	private Product product(final String code)
	{
		if (code == null)
		{
			return null;
		}

		final Collection candidates = ProductManager.getInstance().getProductsByCode(code);
		if (candidates == null || candidates.isEmpty())
		{
			return null;
		}
		if (candidates.size() > 1)
		{
			throw new IllegalStateException("More than one product for code " + code + " have been found.");
		}
		return (Product) candidates.iterator().next();
	}

	private EnumerationValue productGroup(final String code)
	{
		if (code == null)
		{
			return null;
		}

		final EnumerationManager manager = EnumerationManager.getInstance();
		final EnumerationType type = manager.getEnumerationType(Europe1Constants.TYPES.PRICE_PRODUCT_GROUP);

		return manager.getEnumerationValue(type, code);
	}
}

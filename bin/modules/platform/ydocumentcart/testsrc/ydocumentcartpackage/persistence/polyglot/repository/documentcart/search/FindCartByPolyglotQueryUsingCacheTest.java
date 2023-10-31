package ydocumentcartpackage.persistence.polyglot.repository.documentcart.search;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.CartEntry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Assume;
import org.junit.AssumptionViolatedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ydocumentcartpackage.persistence.polyglot.repository.documentcart.CartModelsCreator;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.cart.DatabaseCartStorage;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.CachedDocumentStorage;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.cache.StorageCache;


@IntegrationTest
public class FindCartByPolyglotQueryUsingCacheTest extends ServicelayerBaseTest
{
	private static final String CART_DESCRIPTION = "cart description";
	private static final String CART_MODIFIED_DESCRIPTION = "cart modified description";
	private static final String FIND_CARTS_BY_DESCRIPTION = "GET {Cart} WHERE {description}=?description";
	private static final String FIND_CARTS_BY_DESCRIPTION_ORDERED = FIND_CARTS_BY_DESCRIPTION + " ORDER BY {paymentCost}";
	private static final String FIND_CART_ENTRIES_BY_QUANTITY_AND_BASE_PRICE_ORDERED = "GET {CartEntry} WHERE {quantity}>?quantity AND {basePrice}>?basePrice ORDER BY {basePrice}";
	private static final Logger LOGGER = LoggerFactory.getLogger(FindCartByPolyglotQueryUsingCacheTest.class);

	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private CachedDocumentStorage cachedDocumentStorage;

	private ProductModel product;
	private UnitModel unit;

	private CartModelsCreator cartModelsCreator;

	@Before
	public void setUp() throws Exception
	{
		assumeFullTableScanEnabled();

		cartModelsCreator = new CartModelsCreator(modelService, userService, commonI18NService);

		final CatalogModel catalog = cartModelsCreator.createCatalog();
		product = cartModelsCreator.createProduct(cartModelsCreator.createCatalogVersion(catalog));
		unit = cartModelsCreator.createUnit();

		modelService.saveAll();
	}

	private void assumeFullTableScanEnabled()
	{
		try
		{
			Assume.assumeFalse("full table scan should be enabled",
					Config.getBoolean(DatabaseCartStorage.PROPERTY_SHOULD_THROW_EX_ON_FULL_TBL_SCAN, false));
		}
		catch (final AssumptionViolatedException e)
		{
			LOGGER.info(e.getMessage());
			throw e;
		}
	}

	@Test
	public void shouldFindOrderedCartsCreatedInCacheWhenRootConditionQuery()
	{
		//given
		final CartModel cart1 = cartModelsCreator.createCart(CART_DESCRIPTION, 1d);
		final CartModel cart2 = cartModelsCreator.createCart(CART_DESCRIPTION, 10d);
		final CartModel cart3 = cartModelsCreator.createCart(CART_DESCRIPTION, 2d);
		final CartModel cart4 = cartModelsCreator.createCart(CART_DESCRIPTION, 9d);
		modelService.saveAll();

		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{

			final CartModel cart6 = cartModelsCreator.createCart(CART_DESCRIPTION, 8d);
			final CartModel cart5 = cartModelsCreator.createCart(CART_DESCRIPTION, 3d);
			modelService.saveAll();

			//when
			final var queryValues = Map.of(CartModel.DESCRIPTION, CART_DESCRIPTION);
			final SearchResult<Cart> result = FlexibleSearch.getInstance()
			                                                .search(FIND_CARTS_BY_DESCRIPTION_ORDERED, queryValues, Cart.class);
			//then
			assertThat(result.getResult())
					.extracting(Cart::getPK)
					.hasSize(6)
					.containsExactly(cart1.getPk(), cart3.getPk(), cart5.getPk(), cart6.getPk(),
							cart4.getPk(), cart2.getPk());

			cacheContext.markAsSuccess();
		}
	}

	@Test
	public void shouldFindOrderedCartsFromStorageAndCacheWhenRootConditionQuery()
	{
		final var queryValues = Map.of(CartModel.DESCRIPTION, CART_DESCRIPTION);

		//given
		final CartModel cart1 = cartModelsCreator.createCart(CART_DESCRIPTION, 1d);
		final CartModel cart2 = cartModelsCreator.createCart(CART_DESCRIPTION, 10d);
		final CartModel cart3 = cartModelsCreator.createCart(CART_DESCRIPTION, 2d);
		final CartModel cart4 = cartModelsCreator.createCart(CART_DESCRIPTION, 9d);
		final CartModel cart5 = cartModelsCreator.createCart(CART_DESCRIPTION, 5d);
		final CartModel cart6 = cartModelsCreator.createCart(CART_DESCRIPTION, 4d);
		CartModel cart7;
		CartModel cart8;

		modelService.saveAll();

		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			cart7 = cartModelsCreator.createCart(CART_DESCRIPTION, 8d);
			cart8 = cartModelsCreator.createCart(CART_DESCRIPTION, 3d);
			modelService.saveAll();

			final CartModel cachedCart5 = modelService.get(cart5.getPk());
			cachedCart5.setDescription(CART_MODIFIED_DESCRIPTION);
			modelService.save(cachedCart5);

			final CartModel cachedCart6 = modelService.get(cart6.getPk());
			cachedCart6.setDescription(CART_MODIFIED_DESCRIPTION);
			modelService.save(cachedCart6);

			final CartModel cachedCart1 = modelService.get(cart1.getPk());
			cachedCart1.setPaymentCost(11d);
			modelService.save(cachedCart1);

			//when
			final SearchResult<Cart> result = FlexibleSearch.getInstance()
			                                                .search(FIND_CARTS_BY_DESCRIPTION_ORDERED, queryValues, Cart.class);

			//then
			assertThat(result.getResult())
					.extracting(Cart::getPK)
					.hasSize(6)
					.containsExactly(cart3.getPk(), cart8.getPk(), cart7.getPk(),
							cart4.getPk(), cart2.getPk(), cart1.getPk());

			cacheContext.markAsSuccess();
		}

		//when search outside cache context
		final SearchResult<Cart> result = FlexibleSearch.getInstance()
		                                                .search(FIND_CARTS_BY_DESCRIPTION_ORDERED, queryValues, Cart.class);

		//then should return data flushed to storage
		assertThat(result.getResult())
				.extracting(Cart::getPK)
				.hasSize(6)
				.containsExactly(cart3.getPk(), cart8.getPk(), cart7.getPk(),
						cart4.getPk(), cart2.getPk(), cart1.getPk());
	}

	@Test
	public void shouldFindOrderedCartsEntriesWhenEntityConditionQuery()
	{
		//given
		final CartModel cart = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cart, 1L);
		cartEntry1.setBasePrice(0.5d);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cart, 4L);
		cartEntry2.setBasePrice(2.1d);
		final CartEntryModel cartEntry3 = cartModelsCreator.createCartEntry(unit, product, cart, 4L);
		cartEntry3.setBasePrice(2d);
		modelService.saveAll();

		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			//when
			final var queryValues = Map.of(CartEntryModel.QUANTITY, 2L, CartEntryModel.BASEPRICE, 1d);
			final SearchResult<CartEntry> result = FlexibleSearch.getInstance()
			                                                     .search(FIND_CART_ENTRIES_BY_QUANTITY_AND_BASE_PRICE_ORDERED,
					                                                     queryValues,
					                                                     CartEntry.class);

			//then
			assertThat(result.getResult())
					.extracting(CartEntry::getPK)
					.hasSize(2)
					.containsExactly(cartEntry3.getPk(), cartEntry2.getPk());

			cacheContext.markAsSuccess();
		}
	}

	@Test
	public void shouldFindOrderedCartsEntriesFromStorageAndCacheWhenEntityConditionQuery()
	{
		//given
		final CartModel cart = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cart, 1L);
		cartEntry1.setBasePrice(0.5d);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cart, 4L);
		cartEntry2.setBasePrice(2.1d);
		final CartEntryModel cartEntry3 = cartModelsCreator.createCartEntry(unit, product, cart, 4L);
		cartEntry3.setBasePrice(3d);
		final CartEntryModel cartEntry4 = cartModelsCreator.createCartEntry(unit, product, cart, 4L);
		cartEntry4.setBasePrice(2d);
		final CartEntryModel cartEntry5 = cartModelsCreator.createCartEntry(unit, product, cart, 5L);
		cartEntry5.setBasePrice(4d);
		modelService.saveAll();

		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			final CartModel cachedCart = modelService.get(cart.getPk());
			final Map<PK, AbstractOrderEntryModel> entryMap = cachedCart.getEntries().stream()
			                                                            .collect(Collectors.toMap(AbstractItemModel::getPk,
					                                                            Function.identity()));
			entryMap.get(cartEntry2.getPk()).setQuantity(1L);
			cart.setEntries(List.of(entryMap.get(cartEntry1.getPk()), entryMap.get(cartEntry2.getPk()),
					entryMap.get(cartEntry3.getPk()), entryMap.get(cartEntry4.getPk())));
			modelService.saveAll();

			//when
			final var queryValues = Map.of(CartEntryModel.QUANTITY, 2L, CartEntryModel.BASEPRICE, 1d);
			final SearchResult<CartEntry> result = FlexibleSearch.getInstance()
			                                                     .search(FIND_CART_ENTRIES_BY_QUANTITY_AND_BASE_PRICE_ORDERED,
					                                                     queryValues,
					                                                     CartEntry.class);
			//then
			assertThat(result.getResult())
					.extracting(CartEntry::getPK)
					.hasSize(2)
					.containsExactly(cartEntry4.getPk(), cartEntry3.getPk());

			cacheContext.markAsSuccess();
		}
	}

	@Test
	public void shouldNotOverwriteModifiedCartInCacheWhenRootConditionQuery()
	{
		//given
		final CartModel cart = cartModelsCreator.createCart(CART_DESCRIPTION, 1d);
		modelService.saveAll();

		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			final CartModel cachedCart = modelService.get(cart.getPk());
			cachedCart.setDescription(CART_MODIFIED_DESCRIPTION);
			modelService.save(cachedCart);

			//when
			final var queryValues = Map.of(CartModel.DESCRIPTION, CART_DESCRIPTION);
			final SearchResult<Cart> cartResult = FlexibleSearch.getInstance()
			                                                    .search(FIND_CARTS_BY_DESCRIPTION, queryValues, Cart.class);
			final CartModel modifiedCartModel = modelService.get(cart.getPk());

			//then
			assertThat(cartResult.getResult()).isEmpty();
			assertThat(modifiedCartModel.getDescription()).isEqualTo(CART_MODIFIED_DESCRIPTION);

			cacheContext.markAsSuccess();
		}
	}

	@Test
	public void shouldNotReturnCartRemovedInCacheWhenRootConditionQuery()
	{
		//given
		final CartModel cart = cartModelsCreator.createCart(CART_DESCRIPTION, 1d);
		modelService.saveAll();

		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			final CartModel cachedCart = modelService.get(cart.getPk());
			modelService.remove(cachedCart);

			//when
			final var queryValues = Map.of(CartModel.DESCRIPTION, CART_DESCRIPTION);
			final SearchResult<Cart> cartResult = FlexibleSearch.getInstance()
			                                                    .search(FIND_CARTS_BY_DESCRIPTION, queryValues, Cart.class);

			//then
			assertThat(cartResult.getResult()).isEmpty();

			cacheContext.markAsSuccess();
		}
	}

	@Test
	public void shouldNotReturnCartCreatedAndRemovedInCacheWhenRootConditionQuery()
	{
		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			//given
			final CartModel cart = cartModelsCreator.createCart(CART_DESCRIPTION, 1d);
			modelService.save(cart);

			final CartModel cachedCart = modelService.get(cart.getPk());
			modelService.remove(cachedCart);

			//when
			final var queryValues = Map.of(CartModel.DESCRIPTION, CART_DESCRIPTION);
			final SearchResult<Cart> cartResult = FlexibleSearch.getInstance()
			                                                    .search(FIND_CARTS_BY_DESCRIPTION, queryValues, Cart.class);

			//then
			assertThat(cartResult.getResult()).isEmpty();

			cacheContext.markAsSuccess();
		}
	}

	@Test
	public void shouldFindCartCreatedAndModifiedInCacheWhenRootConditionQuery()
	{
		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			//given
			final CartModel cart = cartModelsCreator.createCart(CART_DESCRIPTION, 1d);
			modelService.save(cart);

			final CartModel cachedCart = modelService.get(cart.getPk());
			cachedCart.setPaymentCost(10d);
			modelService.save(cachedCart);

			//when
			final var queryValues = Map.of(CartModel.DESCRIPTION, CART_DESCRIPTION);
			final SearchResult<Cart> cartResult = FlexibleSearch.getInstance()
			                                                    .search(FIND_CARTS_BY_DESCRIPTION, queryValues, Cart.class);

			//then
			assertThat(cartResult.getResult())
					.hasSize(1)
					.first()
					.hasFieldOrPropertyWithValue(CartModel.DESCRIPTION, CART_DESCRIPTION)
					.hasFieldOrPropertyWithValue(CartModel.PAYMENTCOST, 10d);

			cacheContext.markAsSuccess();
		}
	}
}

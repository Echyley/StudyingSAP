/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package ydocumentcartpackage.persistence.polyglot.repository.documentcart;

import static de.hybris.platform.persistence.polyglot.TypeInfoFactory.getTypeInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assume.assumeNoException;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jdbcwrapper.JdbcTestSupport;
import de.hybris.platform.jdbcwrapper.TestJdbcLogUtils;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.persistence.polyglot.PolyglotPersistence;
import de.hybris.platform.persistence.polyglot.config.RepositoryResult;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.tx.Transaction;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.CachedDocumentStorage;
import ydocumentcartpackage.persistence.polyglot.repository.documentcart.storage.cache.StorageCache;

@IntegrationTest
public class YDocumentCartRelationItemTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private CommonI18NService commonI18NService;
	@Resource
	private TypeService typeService;

	@Resource
	private CachedDocumentStorage cachedDocumentStorage;
	private ProductModel product;
	private UnitModel unit;

	private CartModelsCreator cartModelsCreator;
	private TestJdbcLogUtils testJdbcLogUtils;

	private Tenant tenant;
	private JdbcTestSupport.JdbcStatistics jdbcStatistics;

	@Before
	public void setUp()
	{
		cartModelsCreator = new CartModelsCreator(modelService, userService, commonI18NService);

		final CatalogModel catalog = cartModelsCreator.createCatalog();
		product = cartModelsCreator.createProduct(cartModelsCreator.createCatalogVersion(catalog));
		unit = cartModelsCreator.createUnit();

		modelService.saveAll();


		testJdbcLogUtils = new TestJdbcLogUtils(Registry.getCurrentTenant().getDataSource().getLogUtils(), typeService);
		jdbcStatistics = JdbcTestSupport.createNewJdbcStatistics();
	}

	@After
	public void tearDown()
	{
		testJdbcLogUtils.reset();
		jdbcStatistics.detach();

		if (tenant != null)
		{
			Registry.setCurrentTenant(tenant);
		}
	}

	@Test
	public void shouldSetRelatedItemsWithCacheContext()
	{
		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			testSetRelatedItems();

			cacheContext.markAsSuccess();
		}
	}

	private void testSetRelatedItems()
	{
		final TestJdbcLogUtils.TestStatementListener jdbcLogListener = testJdbcLogUtils.addListener(
				Set.of(CartModel._TYPECODE, CartEntryModel._TYPECODE),
				Set.of(OrderEntryModel._TYPECODE, OrderModel._TYPECODE));

		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);
		final CartEntryModel cartEntry3 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);

		modelService.saveAll();
		modelService.refresh(cartModel1);

		cartModel1.setEntries(List.of(cartEntry1, cartEntry2));

		modelService.saveAll();
		modelService.refresh(cartModel1);

		assertThat(cartModel1.getEntries()).hasSize(2);
		assertThat(jdbcLogListener.getLoggedStatements()).isEmpty();
	}

	@Test
	public void shouldSetRelatedItemsInTx()
	{
		final TestJdbcLogUtils.TestStatementListener jdbcLogListener = testJdbcLogUtils.addListener(
				Set.of(CartModel._TYPECODE, CartEntryModel._TYPECODE),
				Set.of(OrderEntryModel._TYPECODE, OrderModel._TYPECODE));

		Transaction.current().begin();
		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		modelService.saveAll();

		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);
		final CartEntryModel cartEntry3 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);

		modelService.saveAll();
		modelService.refresh(cartModel1);

		cartModel1.setEntries(List.of(cartEntry1, cartEntry2));

		Transaction.current().commit();
		modelService.saveAll();
		modelService.refresh(cartModel1);

		assertThat(cartModel1.getEntries()).hasSize(2);
		assertThat(jdbcLogListener.getLoggedStatements()).isEmpty();
	}

	@Test
	public void shouldUpdateSameEntityMultipleTime()
	{
		final TestJdbcLogUtils.TestStatementListener jdbcLogListener = testJdbcLogUtils.addListener(
				Set.of(CartModel._TYPECODE, CartEntryModel._TYPECODE),
				Set.of(OrderEntryModel._TYPECODE, OrderModel._TYPECODE));

		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);
		final CartEntryModel cartEntry3 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);

		modelService.saveAll();
		modelService.refresh(cartModel1);

		final String newName1 = "test-" + RandomStringUtils.randomAlphanumeric(10);
		cartModel1.setName(newName1);
		modelService.save(cartModel1);

		final String newName2 = "test-" + RandomStringUtils.randomAlphanumeric(10);
		cartModel1.setName(newName2);
		modelService.save(cartModel1);

		modelService.refresh(cartModel1);
		assertThat(cartModel1.getName()).isEqualTo(newName2);
		assertThat(jdbcLogListener.getLoggedStatements()).isEmpty();

	}

	@Test
	public void shouldSetRelatedItems()
	{
		testSetRelatedItems();
	}

	@Test
	public void shouldUpdateTwoEntitiesInOneDocument()
	{
		final TestJdbcLogUtils.TestStatementListener jdbcLogListener = testJdbcLogUtils.addListener(
				Set.of(CartModel._TYPECODE, CartEntryModel._TYPECODE),
				Set.of(OrderEntryModel._TYPECODE, OrderModel._TYPECODE));
		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);

		modelService.saveAll();

		final String newName = "test-" + RandomStringUtils.randomAlphanumeric(10);
		cartModel1.setName(newName);

		modelService.save(cartModel1);

		cartEntry1.setQuantity(2L);
		modelService.save(cartEntry1);
		modelService.refresh(cartModel1);

		assertThat(cartModel1.getEntries()).hasSize(2)
		                                   .extracting(AbstractOrderEntryModel::getQuantity)
		                                   .containsExactlyInAnyOrder(2L, 4L);
		assertThat(cartModel1.getName()).isEqualTo(newName);
		assertThat(jdbcLogListener.getLoggedStatements()).isEmpty();
	}

	@Test
	public void shouldUpdateTwoEntitiesInOneDocumenWithCacheContext()
	{
		final TestJdbcLogUtils.TestStatementListener jdbcLogListener = testJdbcLogUtils.addListener(
				Set.of(CartModel._TYPECODE, CartEntryModel._TYPECODE),
				Set.of(OrderEntryModel._TYPECODE, OrderModel._TYPECODE));
		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);

		modelService.saveAll();

		final String newName = "test-" + RandomStringUtils.randomAlphanumeric(10);
		try (final StorageCache.CacheContext cacheContext = cachedDocumentStorage.initCacheContext())
		{
			cartModel1.setName(newName);

			modelService.save(cartModel1);

			cartEntry1.setQuantity(2L);
			modelService.save(cartEntry1);
			cacheContext.markAsSuccess();
		}

		modelService.refresh(cartModel1);
		assertThat(cartModel1.getEntries()).hasSize(2)
		                                   .extracting(AbstractOrderEntryModel::getQuantity)
		                                   .containsExactlyInAnyOrder(2L, 4L);
		assertThat(cartModel1.getName()).isEqualTo(newName);
		assertThat(jdbcLogListener.getLoggedStatements()).isEmpty();

	}

	@Test
	public void shouldReturnOrderedRelatedItems()
	{

		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L, 3);
		final CartEntryModel cartEntry2 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L, 1);
		final CartEntryModel cartEntry3 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L, 5);

		modelService.saveAll();

		cartModel1.setEntries(List.of(cartEntry1, cartEntry2, cartEntry3));

		modelService.saveAll();
		modelService.refresh(cartModel1);

		assertThat(cartModel1.getEntries()).hasSize(3)
		                                   .extracting(ItemModel::getPk)
		                                   .containsExactly(cartEntry2.getPk(), cartEntry1.getPk(), cartEntry3.getPk());
	}

	@Test
	public void shouldReturnRelatedItemsFromAbstractOrderEntry()
	{
		assumeNoException(catchThrowable(() -> typeService.getComposedTypeForCode("TextFieldConfiguredProductInfo")));

		final TestJdbcLogUtils.TestStatementListener jdbcLogListener = testJdbcLogUtils.addListener(
				Set.of(CartModel._TYPECODE, CartEntryModel._TYPECODE, "TextFieldConfiguredProductInfo"),
				Set.of(OrderEntryModel._TYPECODE, OrderModel._TYPECODE));

		//given
		final CartModel cartModel1 = cartModelsCreator.createCart(CartModel.DESCRIPTION, 1d);
		final CartEntryModel cartEntry1 = cartModelsCreator.createCartEntry(unit, product, cartModel1, 1L);
		cartModelsCreator.createCartEntry(unit, product, cartModel1, 4L);

		final AbstractOrderEntryProductInfoModel aoepim = modelService.create("TextFieldConfiguredProductInfo");
		aoepim.setOrderEntry(cartEntry1);
		aoepim.setConfiguratorType(ConfiguratorType.valueOf("TEXTFIELD"));

		modelService.saveAll();


		final List<AbstractOrderEntryProductInfoModel> productInfos = cartEntry1.getProductInfos();
		assertThat(jdbcLogListener.getLoggedStatements()).isEmpty();
		assertThat(productInfos).hasSize(1).extracting(ItemModel::getPk).containsExactlyInAnyOrder(aoepim.getPk());
	}

	@Test
	public void shouldReturnRelatedItemsFromPartiallySupportedTypeWithSameAttrNamesAssignedToDiffModels()
	{
		assumeNoException(catchThrowable(() -> typeService.getComposedTypeForCode("PromotionResult")));
		assumeNoException(catchThrowable(() -> typeService.getComposedTypeForCode("PromotionNullAction")));

		assertThat(typeService.getComposedTypeForCode(CartModel._TYPECODE).getJaloclass()).isEqualTo(Cart.class);

		final CartData cartData = prepareCartData();

		Registry.getCurrentTenant().getCache().clear();

		//enable jdbc log listener
		jdbcStatistics.attachToCurrentThread();
		jdbcStatistics.assertThat().containsNoStatements();

		final CartModel cart = modelService.get(cartData.cart);
		final Set<ItemModel> promotionResults = getRelatedItems(Set.of(cart), "allPromotionResults");
		assertThat(promotionResults).hasSize(2);

		final Set<ItemModel> promotionActions = getRelatedItems(promotionResults, "allPromotionActions");
		assertThat(promotionActions).hasSize(4);

		promotionResults.forEach(promotionResult -> {
			final PK promotionResultPk = promotionResult.getPk();
			final List<PK> promotionRelatedActions = getRelatedItems(Set.of(promotionResult),
					"allPromotionActions").stream()
			                              .map(AbstractItemModel::getPk)
			                              .collect(
					                              Collectors.toList());
			assertThat(promotionRelatedActions.size()).isEqualTo(2);
			assertThat(cartData.promotionActions.get(promotionResultPk)).containsExactlyInAnyOrder(
					promotionRelatedActions.toArray(PK[]::new));
		});

		final String[] tables = getTableNames("PromotionResult", "PromotionNullAction");

		System.out.println(Arrays.toString(tables));

		jdbcStatistics.assertThat()
		              .selectStatements()
		              .filteredOn(s -> StringUtils.containsAny(s.toLowerCase(), tables))
		              .isEmpty();
	}


	@Test
	public void shouldReturnRelatedItemsFromPartiallySupportedType()
	{
		assumeNoException(catchThrowable(() -> typeService.getComposedTypeForCode("PromotionResult")));
		assumeNoException(catchThrowable(() -> typeService.getComposedTypeForCode("PromotionNullAction")));

		assertThat(typeService.getComposedTypeForCode(CartModel._TYPECODE).getJaloclass()).isEqualTo(Cart.class);

		final PK cartPk = prepareData();

		Registry.getCurrentTenant().getCache().clear();

		//enable jdbc log listener
		jdbcStatistics.attachToCurrentThread();
		jdbcStatistics.assertThat().containsNoStatements();

		final CartModel cart = modelService.get(cartPk);
		final Set<ItemModel> promotionResults = getRelatedItems(Set.of(cart), "allPromotionResults");
		assertThat(promotionResults).hasSize(1);

		final Set<ItemModel> promotionActions = getRelatedItems(promotionResults, "allPromotionActions");
		assertThat(promotionActions).hasSize(1);

		final String[] tables = getTableNames("PromotionResult", "PromotionNullAction");

		System.out.println(Arrays.toString(tables));

		jdbcStatistics.assertThat()
		              .selectStatements()
		              .filteredOn(s -> StringUtils.containsAny(s.toLowerCase(), tables))
		              .isEmpty();

	}

	private String[] getTableNames(final String... types)
	{
		return Stream.of(types)
		             .map(typename -> " " + getTableName(typename) + " ")
		             .map(String::toLowerCase)
		             .collect(Collectors.toSet())
		             .toArray(new String[]{});
	}

	private Set<ItemModel> getRelatedItems(final Collection<ItemModel> itemModels, final String attribute)
	{
		return itemModels.stream()
		                 .flatMap(i -> ((Collection<ItemModel>) modelService.getAttributeValue(i, attribute)).stream())
		                 .collect(Collectors.toSet());
	}

	private CartData prepareCartData()
	{
		final CartModel cart = cartModelsCreator.createCart(UUID.randomUUID().toString(), 1.0);
		modelService.save(cart);

		final ItemModel promotionResult1 = modelService.create("PromotionResult");
		modelService.setAttributeValue(promotionResult1, "order", cart);

		final ItemModel promotionResult2 = modelService.create("PromotionResult");
		modelService.setAttributeValue(promotionResult2, "order", cart);

		modelService.saveAll(promotionResult1, promotionResult2);

		final ItemModel PR1Action1 = modelService.create("PromotionNullAction");
		modelService.setAttributeValue(PR1Action1, "promotionResult", promotionResult1);
		final ItemModel PR1Action2 = modelService.create("PromotionNullAction");
		modelService.setAttributeValue(PR1Action2, "promotionResult", promotionResult1);

		final ItemModel PR2Action1 = modelService.create("PromotionNullAction");
		modelService.setAttributeValue(PR2Action1, "promotionResult", promotionResult2);
		final ItemModel PR2Action2 = modelService.create("PromotionNullAction");
		modelService.setAttributeValue(PR2Action2, "promotionResult", promotionResult2);

		modelService.saveAll(PR1Action1, PR1Action2, PR2Action1, PR2Action2);

		assertThat(getRepository(promotionResult1).isFullySupported()).isTrue();
		assertThat(getRepository(PR1Action2).isFullySupported()).isTrue();

		final CartData cartData = new CartData();
		cartData.cart = cart.getPk();
		cartData.promotionActions.put(promotionResult1.getPk(), PR1Action1.getPk());
		cartData.promotionActions.put(promotionResult1.getPk(), PR1Action2.getPk());
		cartData.promotionActions.put(promotionResult2.getPk(), PR2Action1.getPk());
		cartData.promotionActions.put(promotionResult2.getPk(), PR2Action2.getPk());

		return cartData;
	}

	private PK prepareData()
	{
		final CartModel cart = cartModelsCreator.createCart(UUID.randomUUID().toString(), 1.0);
		modelService.save(cart);

		final ItemModel promotionResult = modelService.create("PromotionResult");
		modelService.setAttributeValue(promotionResult, "order", cart);

		modelService.save(promotionResult);

		final ItemModel promotionAction = modelService.create("PromotionNullAction");
		modelService.setAttributeValue(promotionAction, "promotionResult", promotionResult);

		modelService.save(promotionAction);

		final RepositoryResult relItem1Repo = getRepository(promotionResult);
		final RepositoryResult relItem2Repo = getRepository(promotionAction);

		assertThat(relItem1Repo.isFullySupported()).isTrue();
		assertThat(relItem2Repo.isFullySupported()).isTrue();

		return cart.getPk();
	}

	private RepositoryResult getRepository(final ItemModel relItem1)
	{
		final Class<? extends Item> jaloclass = typeService.getComposedTypeForCode(relItem1.getItemtype()).getJaloclass();
		return PolyglotPersistence.getRepository(getTypeInfo(jaloclass.cast(modelService.getSource(relItem1))));
	}

	private String getTableName(final String typename)
	{
		return Registry.getCurrentTenant()
		               .getPersistenceManager()
		               .getPersistenceInfo(typename)
		               .getItemTableName();
	}

	private static class CartData
	{
		PK cart;
		Multimap<PK, PK> promotionActions = ArrayListMultimap.create();
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.data.json;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.VariantsService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.hybris.backoffice.cockpitng.dataaccess.facades.common.PlatformFacadeStrategyHandleCache;
import com.hybris.backoffice.cockpitng.dataaccess.facades.type.DefaultPlatformTypeFacadeStrategy;
import com.hybris.backoffice.cockpitng.dataaccess.facades.type.DefaultTypeSystemLocalizationHelper;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.impl.DefaultTypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.impl.TypeFacadeStrategyRegistry;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.json.impl.DefaultJSONMapper;
import com.hybris.cockpitng.json.impl.DefaultObjectMapperConfiguration;


@IntegrationTest
public class JsonableItemModelTest extends ServicelayerTransactionalTest
{
	@Resource
	private TypeService typeService;
	@Resource
	private ModelService modelService;
	@Resource
	private I18NService i18NService;
	@Resource
	private VariantsService variantsService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	private DefaultJSONMapper defaultJSONMapper;


	@Before
	public void before()
	{
		defaultJSONMapper = new DefaultJSONMapper();
		defaultJSONMapper.setConfigurations(new ArrayList<>());
		defaultJSONMapper.getConfigurations().add(new DefaultObjectMapperConfiguration());


		final ItemObjectMapperConfiguration itemObjectMapperConfiguration = new ItemObjectMapperConfiguration();
		itemObjectMapperConfiguration.setTypeFacade(createTypeFacade());
		itemObjectMapperConfiguration.setModelService(modelService);
		defaultJSONMapper.getConfigurations().add(itemObjectMapperConfiguration);
	}

	protected TypeFacade createTypeFacade()
	{
		final DefaultTypeSystemLocalizationHelper typeSystemLocalizationHelper = new DefaultTypeSystemLocalizationHelper();
		final CockpitLocaleService cockpitLocaleService = mock(CockpitLocaleService.class);
		doReturn(List.of(Locale.ENGLISH)).when(cockpitLocaleService).getAllUILocales();

		typeSystemLocalizationHelper.setCockpitLocaleService(cockpitLocaleService);
		typeSystemLocalizationHelper.setFlexibleSearchService(flexibleSearchService);

		final DefaultTypeFacade typeFacade = new DefaultTypeFacade();
		final TypeFacadeStrategyRegistry registry = new TypeFacadeStrategyRegistry();
		final DefaultPlatformTypeFacadeStrategy strategy = new DefaultPlatformTypeFacadeStrategy();
		strategy.setTypeService(typeService);
		strategy.setI18nService(i18NService);
		strategy.setCockpitLocaleService(cockpitLocaleService);
		strategy.setModelService(modelService);
		strategy.setVariantsService(variantsService);
		strategy.setTypeSystemLocalizationHelper(typeSystemLocalizationHelper);
		final PlatformFacadeStrategyHandleCache platformFacadeStrategyHandleCache = new PlatformFacadeStrategyHandleCache();
		platformFacadeStrategyHandleCache.setTypeService(typeService);
		strategy.setPlatformFacadeStrategyHandleCache(platformFacadeStrategyHandleCache);
		registry.setDefaultStrategy(strategy);
		typeFacade.setStrategyRegistry(registry);
		return typeFacade;
	}

	private CatalogModel createCatalog(final String name)
	{
		final CatalogModel catalog = modelService.create(CatalogModel.class);
		catalog.setId(name);
		catalog.setId(name);
		modelService.save(catalog);
		return catalog;
	}

	private CatalogVersionModel createCatalogVersion(final CatalogModel catalog)
	{
		final CatalogVersionModel cvm = modelService.create(CatalogVersionModel.class);
		cvm.setVersion("staged");
		cvm.setCatalog(catalog);
		modelService.save(cvm);
		return cvm;
	}

	private ProductModel createProduct(final String code, final CatalogVersionModel cvm)
	{
		final ProductModel productModel = modelService.create(ProductModel.class);
		productModel.setCode(code);
		productModel.setCatalogVersion(cvm);

		productModel.setName("englishName");
		modelService.save(productModel);
		return productModel;
	}

	@Test
	public void test() throws Exception
	{
		final CatalogModel myCatalog = createCatalog("myCatalog");
		final CatalogVersionModel catalogVersion = createCatalogVersion(myCatalog);
		final ProductModel one = createProduct("one", catalogVersion);

		final String productString = defaultJSONMapper.toJSONString(one);
		Assertions.assertThat(productString).isNotEmpty();

		final ProductModel receivedProduct = defaultJSONMapper.fromJSONString(productString, ProductModel.class);
		Assertions.assertThat(receivedProduct).isNotNull();
		Assertions.assertThat(receivedProduct.getCode()).isEqualTo("one");
	}
}

/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import static java.util.Map.entry;
import static org.mockito.Mockito.lenient;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.searchservices.document.data.SnDocument;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;

import java.util.Locale;
import java.util.Map;

import org.mockito.Mock;

import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSnDocumentContainer;
import com.hybris.merchandising.model.MerchSnSynchContext;
import com.hybris.merchandising.model.Product;


public class AbstractMerchSnPopulatorTest
{
	protected static String EN = "en";
	protected static String DE = "de";
	protected static String CODE_FIELD = "code";
	protected static String NAME_FIELD = "name";
	protected static String URL_FIELD = "url";
	protected static String PRICE_FIELD = "price";
	protected static String SUMMARY_FIELD = "summary";
	protected static String DESCRIPTION_FIELD = "description";
	protected static String CATEGORIES_FIELD = "allCategories";
	protected static String IMG65_FIELD = "img-65Wx65H";
	protected static String IMG515_FIELD = "img-515Wx515H";
	protected static String STOCK_LEVEL_STATUS = "stockLevelStatus";
	protected static String CATEGORY_NAMES = "categoryNames";
	protected static String BASE_PRODUCT = "baseProduct";
	protected static String DEFAULT_LANGUAGE_ISO = "en";
	protected static String DEFAULT_CURRENCY_ISO = "USD";
	protected static Double PRICE_VALUE = Double.valueOf(25.5);
	protected static Map PRICE_MAP_VALUE = Map.of(DEFAULT_CURRENCY_ISO, PRICE_VALUE, "JPY", Double.valueOf(10));
	protected static String STOCK_LEVEL_VALUE = "inStock";

	protected Map<String, String> merchPropertiesMapping = Map.ofEntries(
			entry(Product.ID, CODE_FIELD),
			entry(Product.NAME, NAME_FIELD),
			entry(Product.PAGE_URL, URL_FIELD),
			entry(Product.PRICE, PRICE_FIELD),
			entry(Product.SUMMARY, SUMMARY_FIELD),
			entry(Product.DESCRIPTION, DESCRIPTION_FIELD),
			entry(Product.CATEGORIES_FIELD, CATEGORIES_FIELD),
			entry(Product.THUMBNAIL_IMAGE, IMG65_FIELD),
			entry(Product.MAIN_IMAGE, IMG515_FIELD),
			entry(BASE_PRODUCT, BASE_PRODUCT),
			entry(STOCK_LEVEL_STATUS, STOCK_LEVEL_STATUS),
			entry(CATEGORY_NAMES, CATEGORY_NAMES));

	protected Map<String, String> productDetailsFields = Map.of(Product.ID, CODE_FIELD, STOCK_LEVEL_STATUS, STOCK_LEVEL_STATUS);

	@Mock
	protected MerchSnDocumentContainer source;
	@Mock
	protected MerchSnConfigModel merchConfig;
	@Mock
	protected MerchSnSynchContext merchContext;
	@Mock
	protected SnIndexerContext indexerContext;
	@Mock
	protected SnDocument snDocument;
	@Mock
	protected CurrencyModel currencyModel;
	@Mock
	protected LanguageModel languageModel;

	protected Product target;

	public void setUp()
	{
		target = new Product();

		lenient().when(source.getMerchContext()).thenReturn(merchContext);
		lenient().when(merchContext.getMerchPropertiesMapping()).thenReturn(merchPropertiesMapping);
		lenient().when(merchContext.getProductDetailsFields()).thenReturn(productDetailsFields);
		lenient().when(merchContext.getDefaultLocale()).thenReturn(Locale.US);
		lenient().when(source.getMerchConfig()).thenReturn(merchConfig);
		lenient().when(merchConfig.getRollUpStrategyField()).thenReturn(CODE_FIELD);
		lenient().when(merchConfig.getCurrency()).thenReturn(currencyModel);
		lenient().when(currencyModel.getIsocode()).thenReturn(DEFAULT_CURRENCY_ISO);
		lenient().when(merchConfig.getDefaultLanguage()).thenReturn(languageModel);
		lenient().when(languageModel.getIsocode()).thenReturn(DEFAULT_LANGUAGE_ISO);
		lenient().when(source.getIndexerContext()).thenReturn(indexerContext);
		lenient().when(source.getInputDocument()).thenReturn(snDocument);
	}
}

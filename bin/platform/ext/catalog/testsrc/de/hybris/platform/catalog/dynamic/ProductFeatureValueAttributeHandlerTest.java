/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.catalog.dynamic;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.ModelDBUtilsService;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.FormatFactory;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ProductFeatureValueAttributeHandlerTest extends ServicelayerTest
{

	private final ProductFeatureValueAttributeHandler handler = new ProductFeatureValueAttributeHandler();

	@Resource
	private ModelDBUtilsService modelDBUtilsService;
	@Resource
	private FormatFactory formatFactory;
	@Resource
	private TypeService typeService;
	@Resource
	private ModelService modelService;
	ProductFeatureModel productFeatureModel;


	@Before
	public void setUp() throws Exception
	{
		productFeatureModel = modelService.create(ProductFeatureModel.class);
		handler.setModelDBUtilsService(modelDBUtilsService);
		handler.setTypeService(typeService);
		handler.setFormatFactory(formatFactory);
	}

	@Test
	public void shouldReturnNull()
	{
		assertThat(handler.get(productFeatureModel)).isNull();
	}

	@Test
	public void shouldReturnStringValue()
	{
		final String value = "StringValue";
		checkSettingValue(value);
		assertThat((String)productFeatureModel.getProperty(ProductFeatureModel.STRINGVALUE)).isEqualTo(value);
		assertThat((Boolean)productFeatureModel.getProperty(ProductFeatureModel.BOOLEANVALUE)).isFalse();
	}

	@Test
	public void shouldReturnBooleanTrueWhenStringValueIsTrue()
	{
		final String value = "true";
		checkSettingValue(value);
		assertThat((String)productFeatureModel.getProperty(ProductFeatureModel.STRINGVALUE)).isEqualTo(value);
		assertThat((Boolean)productFeatureModel.getProperty(ProductFeatureModel.BOOLEANVALUE)).isTrue();
	}

	@Test
	public void shouldReturnBooleanValue()
	{
		checkSettingValue(false);
		assertThat((Boolean)productFeatureModel.getProperty(ProductFeatureModel.BOOLEANVALUE)).isFalse();

		checkSettingValue(true);
		assertThat((Boolean)productFeatureModel.getProperty(ProductFeatureModel.BOOLEANVALUE)).isTrue();
	}

	@Test
	public void shouldReturnNumberFromRawValue()
	{
		BigDecimal value = BigDecimal.valueOf(1234.0);
		checkSettingValue(value);

		final BigDecimal rawvalue = productFeatureModel.getProperty(ProductFeatureModel.RAWVALUE);
		assertThat(rawvalue).isNotNull();
		assertThat(value).isEqualTo(rawvalue);

		value = BigDecimal.valueOf(0);
		checkSettingValue(value);
		assertThat((BigDecimal) productFeatureModel.getProperty(ProductFeatureModel.NUMBERVALUE)).isEqualTo(value);

		value = BigDecimal.valueOf(-10);
		checkSettingValue(value);
		assertThat((BigDecimal) productFeatureModel.getProperty(ProductFeatureModel.NUMBERVALUE)).isEqualTo(value);
	}

	@Test
	public void shouldReturnDateFromRawValue()
	{
		Date value = new Date();
		checkSettingValue(value);
		final Date rawvalue = productFeatureModel.getProperty(ProductFeatureModel.RAWVALUE);
		assertThat(rawvalue).isNotNull();
		assertThat(value).isEqualTo(rawvalue);

		value = new DateTime().withMillisOfSecond(0).toDate();
		checkSettingValue(value);
	}

	@Test
	public void shouldReturnReferenceValue()
	{
		final ComposedTypeModel type = typeService.getComposedTypeForClass(ProductModel.class);
		checkSettingValue(type);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionForObjectValue()
	{
		final Object value = new Object();
		handler.set(productFeatureModel, value);
	}

	@Test
	public void shouldReturnRawValueForTooLongString()
	{
		final ModelDBUtilsServiceTestImpl serviceTest = new ModelDBUtilsServiceTestImpl();
		final String value = "Text longer than 10 chars";

		handler.setModelDBUtilsService(serviceTest);
		checkSettingValue(value);

		final String rawvalue = productFeatureModel.getProperty(ProductFeatureModel.RAWVALUE);
		assertThat(rawvalue).isNotNull();
		assertThat(value).isEqualToIgnoringCase(rawvalue);
		assertThat(rawvalue).isEqualToIgnoringCase(productFeatureModel.getProperty(ProductFeatureModel.STRINGVALUE));

		handler.setModelDBUtilsService(modelDBUtilsService);
	}

	private void checkSettingValue(final Object value)
	{
		handler.set(productFeatureModel, value);

		assertThat(handler.get(productFeatureModel)).isNotNull();
		assertThat(handler.get(productFeatureModel)).isEqualTo(value);
	}

	class ModelDBUtilsServiceTestImpl implements ModelDBUtilsService
	{
		@Override
		public long findColumnSize(final String composedTypeCode, final String qualifier)
		{
			return 10L;
		}
	}
}
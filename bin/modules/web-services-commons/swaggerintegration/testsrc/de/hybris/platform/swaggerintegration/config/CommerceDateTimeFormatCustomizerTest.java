/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;


@UnitTest
public class CommerceDateTimeFormatCustomizerTest
{
	private CommerceDateTimeFormatCustomizer commerceDateTimeFormatCustomizer;

	private AnnotatedType irrelevant;

	@Before
	public void setup()
	{
		commerceDateTimeFormatCustomizer = new CommerceDateTimeFormatCustomizer();
		irrelevant = new AnnotatedType();
	}

	@Test
	public void shouldAddCurrentDateTimeWhenMissing()
	{
		//given
		Schema schema = createDateTimeSchema(null);

		//when
		commerceDateTimeFormatCustomizer.customize(schema, irrelevant);

		//then
		Assert.assertTrue(isValidDateTimeFormat(schema.getExample().toString()));
	}

	@Test
	public void shouldKeepTheOriginalExampleWhenExists()
	{
		//given
		String dateTimeExample = "2022-12-20T17:15:02+08:00";
		Schema schema = createDateTimeSchema(dateTimeExample);

		//when
		commerceDateTimeFormatCustomizer.customize(schema, irrelevant);

		//then
		Assert.assertEquals("should keep the original example when it exists", dateTimeExample, schema.getExample().toString());
	}

	@Test
	public void shouldKeepTheSameWhenSchemaIsNotDateTimeType()
	{
		//given
		Schema dateSchema = new DateSchema();
		Schema stringSchema = new StringSchema();


		//when
		commerceDateTimeFormatCustomizer.customize(dateSchema, irrelevant);
		commerceDateTimeFormatCustomizer.customize(stringSchema, irrelevant);

		//then
		Assert.assertNull(dateSchema.getExample());
		Assert.assertNull(stringSchema.getExample());
	}

	private Schema createDateTimeSchema(String example)
	{
		return new DateTimeSchema().example(example);
	}

	private boolean isValidDateTimeFormat(String dateTimeString)
	{
		Schema schema = new DateTimeSchema();
		schema.example(dateTimeString);
		return schema.getExample() != null;
	}

}

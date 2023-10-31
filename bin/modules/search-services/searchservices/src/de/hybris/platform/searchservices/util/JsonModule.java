/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.util;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;


/**
 * Module that provides additional serializers and deserializers for JSON.
 */
public final class JsonModule extends SimpleModule
{
	private static final JsonDeserializer<String> JSON_RAW_DESERIALIZER_INSTANCE = new JsonRawValueDeserializer();

	public JsonModule()
	{
		setDeserializerModifier(new JsonRawValueDeserializerModifier());
		addSerializer(Locale.class, new LocaleJsonSerializer());
		addDeserializer(Locale.class, new LocaleJsonDeserializer());
		addKeySerializer(Locale.class, new LocaleKeySerializer());
		addKeyDeserializer(Locale.class, new LocaleKeyDeserializer());
	}

	private static class JsonRawValueDeserializerModifier extends BeanDeserializerModifier
	{
		@Override
		public BeanDeserializerBuilder updateBuilder(final DeserializationConfig config, final BeanDescription beanDescription,
				final BeanDeserializerBuilder beanDeserializerBuilder)
		{
			beanDeserializerBuilder.getProperties().forEachRemaining(property -> {
				if (property.getAnnotation(JsonRawValue.class) != null)
				{
					beanDeserializerBuilder.addOrReplaceProperty(property.withValueDeserializer(JSON_RAW_DESERIALIZER_INSTANCE), true);
				}
			});

			return beanDeserializerBuilder;
		}
	}

	private static class JsonRawValueDeserializer extends JsonDeserializer<String>
	{
		@Override
		public String deserialize(final JsonParser parser, final DeserializationContext context) throws IOException
		{
			return parser.readValueAsTree().toString();
		}
	}

	private static class LocaleJsonSerializer extends JsonSerializer<Locale>
	{
		@Override
		public void serialize(final Locale source, final JsonGenerator generator, final SerializerProvider serializerProvider)
				throws IOException
		{
			final String languageTag = source.toLanguageTag();
			generator.writeString(languageTag);
		}
	}

	private static class LocaleJsonDeserializer extends JsonDeserializer<Locale>
	{
		@Override
		public Locale deserialize(final JsonParser parser, final DeserializationContext context) throws IOException
		{
			final String languageTag = parser.getText();
			return Locale.forLanguageTag(languageTag);
		}
	}

	private static class LocaleKeySerializer extends JsonSerializer<Locale>
	{
		@Override
		public void serialize(final Locale source, final JsonGenerator generator, final SerializerProvider serializerProvider)
				throws IOException
		{
			final String languageTag = source.toLanguageTag();
			generator.writeFieldName(languageTag);
		}
	}

	private static class LocaleKeyDeserializer extends KeyDeserializer
	{
		@Override
		public Object deserializeKey(final String source, final DeserializationContext context) throws IOException
		{
			return Locale.forLanguageTag(source);
		}
	}
}

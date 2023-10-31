/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.Test;

@UnitTest
public class ConfigurableRelationAttributesProviderUnitTest
{
	private final AtomicInteger pkCounter = new AtomicInteger();

	@Test
	public void shouldBeEnabledGloballyByDefaultButSpecificRelationShouldBeDisabledByDefault()
	{
		final TypeId typeId = TypeId.fromTypePk(anyPk());
		final Function<TypeId, Stream<RelationAttributes>> typeIdToAttributes = mock(Function.class);
		final ConfigurableRelationAttributesProvider provider = givenProvider(typeIdToAttributes, config(null));

		when(typeIdToAttributes.apply(typeId)).thenReturn(Stream.of(testAttributes("test")));

		final Stream<RelationAttributes> attributes = provider.getRelationAttributes(typeId);
		assertThat(attributes).isEmpty();
		verify(typeIdToAttributes).apply(typeId);
	}

	@Test
	public void shouldReturnEmptyAttributesWhenRelationCachingIsDisabledGlobally()
	{
		final Function<TypeId, Stream<RelationAttributes>> typeIdToAttributes = mock(Function.class);

		final ConfigurableRelationAttributesProvider provider = givenProvider(typeIdToAttributes, config(false));

		final Stream<RelationAttributes> attributesByPk = provider.getRelationAttributes(TypeId.fromTypePk(anyPk()));
		assertThat(attributesByPk).isEmpty();

		final Stream<RelationAttributes> attributesByTypeCode = provider.getRelationAttributes(TypeId.fromTypeCode(123));
		assertThat(attributesByTypeCode).isEmpty();

		final Stream<RelationAttributes> attributesByTypeName = provider.getRelationAttributes(
				TypeId.fromTypeName(uniqueString()));
		assertThat(attributesByTypeName).isEmpty();
	}

	@Test
	public void shouldReturnEmptyAttributesWhenRelationCachingIsEnabledGloballyButDisabledForSpecificType()
	{
		final String specificTypeName = uniqueString();
		final PK specificTypePk = anyPk();
		final int specificTypeCode = 123;
		final RelationAttributes specificAttributes = testAttributes(specificTypeName);
		final Function<TypeId, Stream<RelationAttributes>> typeIdToAttributes = mock(Function.class);

		when(typeIdToAttributes.apply(TypeId.fromTypePk(specificTypePk))).thenReturn(Stream.of(specificAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypeName(specificTypeName))).thenReturn(Stream.of(specificAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypeCode(specificTypeCode))).thenReturn(Stream.of(specificAttributes));

		final ConfigurableRelationAttributesProvider provider = givenProvider(typeIdToAttributes,
				config(true, specificTypeName, false));

		final Stream<RelationAttributes> attributesByPk = provider.getRelationAttributes(TypeId.fromTypePk(specificTypePk));
		assertThat(attributesByPk).isEmpty();

		final Stream<RelationAttributes> attributesByTypeCode = provider.getRelationAttributes(
				TypeId.fromTypeCode(specificTypeCode));
		assertThat(attributesByTypeCode).isEmpty();

		final Stream<RelationAttributes> attributesByTypeName = provider.getRelationAttributes(
				TypeId.fromTypeName(specificTypeName));
		assertThat(attributesByTypeName).isEmpty();

	}

	@Test
	public void shouldReturnRelationAttributesWhenRelationCachingIsEnabledGloballyAndEnabledForSpecificType()
	{
		final String specificTypeName = uniqueString();
		final PK specificTypePk = anyPk();
		final int specificTypeCode = 123;
		final RelationAttributes specificAttributes = testAttributes(specificTypeName);
		final Function<TypeId, Stream<RelationAttributes>> typeIdToAttributes = mock(Function.class);

		when(typeIdToAttributes.apply(TypeId.fromTypePk(specificTypePk))).thenReturn(Stream.of(specificAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypeName(specificTypeName))).thenReturn(Stream.of(specificAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypeCode(specificTypeCode))).thenReturn(Stream.of(specificAttributes));

		final ConfigurableRelationAttributesProvider provider = givenProvider(typeIdToAttributes,
				config(true, specificTypeName, true));

		final Stream<RelationAttributes> attributesByPk = provider.getRelationAttributes(TypeId.fromTypePk(specificTypePk));
		assertThat(attributesByPk).containsExactly(specificAttributes);

		final Stream<RelationAttributes> attributesByTypeCode = provider.getRelationAttributes(
				TypeId.fromTypeCode(specificTypeCode));
		assertThat(attributesByTypeCode).containsExactly(specificAttributes);

		final Stream<RelationAttributes> attributesByTypeName = provider.getRelationAttributes(
				TypeId.fromTypeName(specificTypeName));
		assertThat(attributesByTypeName).containsExactly(specificAttributes);
	}

	@Test
	public void shouldReturnOnlyEnabledTypes()
	{
		final int typeCode = 123;
		final String enabledTypeName = uniqueString();
		final PK enabledTypePk = anyPk();
		final String disabledTypeName = uniqueString();
		final PK disabledTypePk = anyPk();

		final RelationAttributes enabledAttributes = testAttributes(enabledTypeName);
		final RelationAttributes disabledAttributes = testAttributes(disabledTypeName);

		final Function<TypeId, Stream<RelationAttributes>> typeIdToAttributes = mock(Function.class);
		when(typeIdToAttributes.apply(TypeId.fromTypePk(enabledTypePk))).thenReturn(Stream.of(enabledAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypeName(enabledTypeName))).thenReturn(Stream.of(enabledAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypePk(disabledTypePk))).thenReturn(Stream.of(disabledAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypeName(disabledTypeName))).thenReturn(Stream.of(disabledAttributes));
		when(typeIdToAttributes.apply(TypeId.fromTypeCode(typeCode))).thenReturn(
				Stream.of(enabledAttributes, disabledAttributes));

		final ConfigurableRelationAttributesProvider provider = givenProvider(typeIdToAttributes,
				config(true, enabledTypeName, true));

		final Stream<RelationAttributes> enabledByPk = provider.getRelationAttributes(TypeId.fromTypePk(enabledTypePk));
		assertThat(enabledByPk).containsExactly(enabledAttributes);

		final Stream<RelationAttributes> disabledByPk = provider.getRelationAttributes(TypeId.fromTypePk(disabledTypePk));
		assertThat(disabledByPk).isEmpty();

		final Stream<RelationAttributes> enabledByTypeName = provider.getRelationAttributes(TypeId.fromTypeName(enabledTypeName));
		assertThat(enabledByTypeName).containsExactly(enabledAttributes);

		final Stream<RelationAttributes> disabledByTypeName = provider.getRelationAttributes(
				TypeId.fromTypeName(disabledTypeName));
		assertThat(disabledByTypeName).isEmpty();

		final Stream<RelationAttributes> attributesByTypeCode = provider.getRelationAttributes(TypeId.fromTypeCode(typeCode));
		assertThat(attributesByTypeCode).containsExactly(enabledAttributes);
	}

	private BiPredicate<String, Boolean> config(final Boolean global)
	{
		return config(global, uniqueString(), null);
	}

	private BiPredicate<String, Boolean> config(final Boolean global, final String specificName, final Boolean specificValue)
	{
		return (String flag, Boolean def) -> {
			if (ConfigurableRelationAttributesProvider.GLOBAL_ENABLE_FLAG.equals(flag))
			{
				return global == null ? def : global;
			}
			if (ConfigurableRelationAttributesProvider.typeEnableFlag(specificName).equals(flag))
			{
				return specificValue == null ? def : specificValue;
			}
			return def;
		};
	}

	private RelationAttributes testAttributes(final String specificTypeName)
	{
		return new RelationAttributes(specificTypeName)
		{
			@Override
			public boolean containsAnyAttribute()
			{
				return true;
			}

			@Override
			void forEachAttribute(final BiConsumer<? super String, ? super String> consumer)
			{
				consumer.accept("test-fk", "test-many");
			}
		};
	}

	private ConfigurableRelationAttributesProvider givenProvider(
			final Function<TypeId, Stream<RelationAttributes>> typeIdToAttributes,
			final BiPredicate<String, Boolean> config)
	{
		return new ConfigurableRelationAttributesProvider(typeIdToAttributes, config);
	}

	private PK anyPk()
	{
		return PK.createFixedCounterPK(123, pkCounter.incrementAndGet());
	}

	private String uniqueString()
	{
		return UUID.randomUUID().toString();
	}
}
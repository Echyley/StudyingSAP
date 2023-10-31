/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cache.relation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Test;

@UnitTest
public class CachingRelationAttributesProviderUnitTest
{
	@Test
	public void shouldCacheResultForSinglePkWithoutUsingExtractor()
	{
		final PK givenPk = PK.createFixedCounterPK(123, 1);
		final RelationAttributes givenAttributes = mock(RelationAttributes.class);
		final Function<PK, RelationAttributes> pkToAttributes = mock(Function.class);
		final Function<TypeId, Collection<PK>> typeIdToPKs = mock(Function.class);

		when(pkToAttributes.apply(givenPk)).thenReturn(givenAttributes);
		final CachingRelationAttributesProvider provider = givenProvider(pkToAttributes, typeIdToPKs);

		final List<RelationAttributes> attributes = provider.getRelationAttributes(TypeId.fromTypePk(givenPk))
		                                                    .collect(Collectors.toUnmodifiableList());
		assertThat(attributes).isNotEmpty().containsExactly(givenAttributes);
		verify(pkToAttributes, times(1)).apply(givenPk);

		final List<RelationAttributes> attributes2 = provider.getRelationAttributes(TypeId.fromTypePk(givenPk))
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes2).isNotEmpty().containsExactly(givenAttributes);
		verifyNoMoreInteractions(pkToAttributes);

		verifyZeroInteractions(typeIdToPKs);
	}

	@Test
	public void shouldCacheResultForSinglePkExtractedFromTypeName()
	{
		final PK givenPk = PK.createFixedCounterPK(123, 1);
		final TypeId givenTypeId = TypeId.fromTypeName("test");
		final RelationAttributes givenAttributes = mock(RelationAttributes.class);
		final Function<PK, RelationAttributes> pkToAttributes = mock(Function.class);
		final Function<TypeId, Collection<PK>> typeIdToPKs = mock(Function.class);

		when(pkToAttributes.apply(givenPk)).thenReturn(givenAttributes);
		when(typeIdToPKs.apply(givenTypeId)).thenReturn(Set.of(givenPk));
		final CachingRelationAttributesProvider provider = givenProvider(pkToAttributes, typeIdToPKs);

		final List<RelationAttributes> attributes = provider.getRelationAttributes(givenTypeId)
		                                                    .collect(Collectors.toUnmodifiableList());
		assertThat(attributes).isNotEmpty().containsExactly(givenAttributes);
		verify(pkToAttributes, times(1)).apply(givenPk);
		verify(typeIdToPKs, times(1)).apply(givenTypeId);

		final List<RelationAttributes> attributes2 = provider.getRelationAttributes(givenTypeId)
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes2).isNotEmpty().containsExactly(givenAttributes);
		verifyNoMoreInteractions(pkToAttributes);
		verifyNoMoreInteractions(typeIdToPKs);

		final List<RelationAttributes> attributes3 = provider.getRelationAttributes(TypeId.fromTypePk(givenPk))
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes3).isNotEmpty().containsExactly(givenAttributes);
		verifyNoMoreInteractions(pkToAttributes);
		verifyNoMoreInteractions(typeIdToPKs);
	}

	@Test
	public void shouldCacheResultForSinglePkExtractedFromTypeCode()
	{
		final PK givenPk = PK.createFixedCounterPK(123, 1);
		final TypeId givenTypeId = TypeId.fromTypeCode(123);
		final RelationAttributes givenAttributes = mock(RelationAttributes.class);
		final Function<PK, RelationAttributes> pkToAttributes = mock(Function.class);
		final Function<TypeId, Collection<PK>> typeIdToPKs = mock(Function.class);

		when(pkToAttributes.apply(givenPk)).thenReturn(givenAttributes);
		when(typeIdToPKs.apply(givenTypeId)).thenReturn(Set.of(givenPk));
		final CachingRelationAttributesProvider provider = givenProvider(pkToAttributes, typeIdToPKs);

		final List<RelationAttributes> attributes = provider.getRelationAttributes(givenTypeId)
		                                                    .collect(Collectors.toUnmodifiableList());
		assertThat(attributes).isNotEmpty().containsExactly(givenAttributes);
		verify(pkToAttributes, times(1)).apply(givenPk);
		verify(typeIdToPKs, times(1)).apply(givenTypeId);

		final List<RelationAttributes> attributes2 = provider.getRelationAttributes(givenTypeId)
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes2).isNotEmpty().containsExactly(givenAttributes);
		verifyNoMoreInteractions(pkToAttributes);
		verifyNoMoreInteractions(typeIdToPKs);

		final List<RelationAttributes> attributes3 = provider.getRelationAttributes(TypeId.fromTypePk(givenPk))
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes3).isNotEmpty().containsExactly(givenAttributes);
		verifyNoMoreInteractions(pkToAttributes);
		verifyNoMoreInteractions(typeIdToPKs);
	}

	@Test
	public void shouldCacheResultsForMultiplePKsExtractedFromTypeCode()
	{
		final PK givenPk1 = PK.createFixedCounterPK(123, 1);
		final PK givenPk2 = PK.createFixedCounterPK(123, 2);
		final TypeId givenTypeId = TypeId.fromTypeCode(123);
		final RelationAttributes givenAttributes1 = mock(RelationAttributes.class);
		final RelationAttributes givenAttributes2 = mock(RelationAttributes.class);
		final Function<PK, RelationAttributes> pkToAttributes = mock(Function.class);
		final Function<TypeId, Collection<PK>> typeIdToPKs = mock(Function.class);

		when(pkToAttributes.apply(givenPk1)).thenReturn(givenAttributes1);
		when(pkToAttributes.apply(givenPk2)).thenReturn(givenAttributes2);
		when(typeIdToPKs.apply(givenTypeId)).thenReturn(Set.of(givenPk1, givenPk2));
		final CachingRelationAttributesProvider provider = givenProvider(pkToAttributes, typeIdToPKs);

		final List<RelationAttributes> attributes = provider.getRelationAttributes(givenTypeId)
		                                                    .collect(Collectors.toUnmodifiableList());
		assertThat(attributes).isNotEmpty().containsExactlyInAnyOrder(givenAttributes1, givenAttributes2);
		verify(pkToAttributes, times(1)).apply(givenPk1);
		verify(pkToAttributes, times(1)).apply(givenPk2);
		verify(typeIdToPKs, times(1)).apply(givenTypeId);

		final List<RelationAttributes> attributes2 = provider.getRelationAttributes(givenTypeId)
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes2).isNotEmpty().containsExactlyInAnyOrder(givenAttributes1, givenAttributes2);
		verifyNoMoreInteractions(pkToAttributes);
		verifyNoMoreInteractions(typeIdToPKs);

		final List<RelationAttributes> attributes3 = provider.getRelationAttributes(TypeId.fromTypePk(givenPk1))
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes3).isNotEmpty().containsExactly(givenAttributes1);
		verifyNoMoreInteractions(pkToAttributes);
		verifyNoMoreInteractions(typeIdToPKs);
	}

	@Test
	public void shouldCacheEmptyAttributesWhenEmptyAttributesAreProvided()
	{
		final PK givenPk = PK.createFixedCounterPK(123, 1);
		final RelationAttributes givenAttributes = RelationAttributes.empty();
		final Function<PK, RelationAttributes> pkToAttributes = mock(Function.class);
		final Function<TypeId, Collection<PK>> typeIdToPKs = mock(Function.class);

		when(pkToAttributes.apply(givenPk)).thenReturn(givenAttributes);
		final CachingRelationAttributesProvider provider = givenProvider(pkToAttributes, typeIdToPKs);

		final List<RelationAttributes> attributes = provider.getRelationAttributes(TypeId.fromTypePk(givenPk))
		                                                    .collect(Collectors.toUnmodifiableList());
		assertThat(attributes).isNotEmpty().containsExactly(givenAttributes);
		verify(pkToAttributes, times(1)).apply(givenPk);

		final List<RelationAttributes> attributes2 = provider.getRelationAttributes(TypeId.fromTypePk(givenPk))
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes2).isNotEmpty().containsExactly(givenAttributes);
		verifyNoMoreInteractions(pkToAttributes);

		verifyZeroInteractions(typeIdToPKs);
	}

	@Test
	public void shouldCacheEmptyStreamWhenNoPKsAreExtracted()
	{
		final TypeId givenTypeId = TypeId.fromTypeCode(123);
		final Function<PK, RelationAttributes> pkToAttributes = mock(Function.class);
		final Function<TypeId, Collection<PK>> typeIdToPKs = mock(Function.class);

		when(typeIdToPKs.apply(givenTypeId)).thenReturn(Set.of());
		final CachingRelationAttributesProvider provider = givenProvider(pkToAttributes, typeIdToPKs);

		final List<RelationAttributes> attributes = provider.getRelationAttributes(givenTypeId)
		                                                    .collect(Collectors.toUnmodifiableList());
		assertThat(attributes).isEmpty();
		verify(typeIdToPKs, times(1)).apply(givenTypeId);

		final List<RelationAttributes> attributes2 = provider.getRelationAttributes(givenTypeId)
		                                                     .collect(Collectors.toUnmodifiableList());
		assertThat(attributes2).isEmpty();
		verifyNoMoreInteractions(typeIdToPKs);

		verifyZeroInteractions(pkToAttributes);
	}

	private CachingRelationAttributesProvider givenProvider(final Function<PK, RelationAttributes> pkToAttributes,
	                                                        final Function<TypeId, Collection<PK>> typeIdToPKs)
	{
		return new CachingRelationAttributesProvider(pkToAttributes, typeIdToPKs);
	}

}
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.persistence.polyglot.search.criteria;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.persistence.polyglot.PolyglotPersistence;
import de.hybris.platform.persistence.polyglot.model.Identity;
import de.hybris.platform.persistence.polyglot.model.ItemState;
import de.hybris.platform.persistence.polyglot.model.SingleAttributeKey;
import de.hybris.platform.persistence.polyglot.view.ItemStateView;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link MatchingPredicateBuilder}
 */
@UnitTest
public class MatchingPredicateBuilderTest
{
	private static final SingleAttributeKey TYPE_KEY = PolyglotPersistence.getNonlocalizedKey(Item.TYPE);

	private SingleAttributeKey attr1;
	private Identity typeId;
	private ItemState validItem;
	private ItemState invalidItem;

	@Before
	public void setUp()
	{
		attr1 = PolyglotPersistence.getNonlocalizedKey("attr1");
		typeId = PolyglotPersistence.identityFromLong(100);

		validItem = mock(ItemState.class);
		when(validItem.get(TYPE_KEY)).thenReturn(PolyglotPersistence.getReferenceTo(typeId));

		invalidItem = mock(ItemState.class);
		when(invalidItem.get(TYPE_KEY)).thenReturn(PolyglotPersistence.getReferenceTo(typeId));
	}

	@Test
	public void shouldFilterItemStatesWithIdentityAttributeAndIdentityParam()
	{
		testComparisonWithIdentityAttribute(PolyglotPersistence.identityFromLong(1));
	}

	@Test
	public void shouldFilterItemStatesWithIdentityAttributeAndReferenceParam()
	{
		testComparisonWithIdentityAttribute(PolyglotPersistence.getReferenceTo(PolyglotPersistence.identityFromLong(1)));
	}

	@Test
	public void shouldFilterItemStatesWithIdentityAttributeAndLongParam()
	{
		testComparisonWithIdentityAttribute(1L);
	}

	@Test
	public void shouldFilterItemStatesWithIdentityAttributeAndStringParam()
	{
		testComparisonWithIdentityAttribute("1");
	}

	@Test
	public void shouldReturnEmptyResultWithIdentityAttributeAndNullParam()
	{
		testComparisonReturnNoItemStates(null, PolyglotPersistence::identityFromLong);
	}

	@Test
	public void shouldReturnEmptyResultWithNullAttributeAndIdentityParam()
	{
		testComparisonReturnNoItemStates(PolyglotPersistence.identityFromLong(1L), id -> null);
	}

	@Test
	public void shouldFilterItemStatesWithReferenceAttributeAndIdentityParam()
	{
		testComparisonWithReferenceAttribute(PolyglotPersistence.identityFromLong(1));
	}

	@Test
	public void shouldFilterItemStatesWithReferenceAttributeAndReferenceParam()
	{
		testComparisonWithReferenceAttribute(PolyglotPersistence.getReferenceTo(PolyglotPersistence.identityFromLong(1)));
	}

	@Test
	public void shouldFilterItemStatesWithReferenceAttributeAndLongParam()
	{
		testComparisonWithReferenceAttribute(1L);
	}

	@Test
	public void shouldFilterItemStatesWithReferenceAttributeAndStringParam()
	{
		testComparisonWithReferenceAttribute("1");
	}

	@Test
	public void shouldReturnEmptyResultWithReferenceAttributeAndNullParam()
	{
		testComparisonReturnNoItemStates(null,
				id -> PolyglotPersistence.getReferenceTo(PolyglotPersistence.identityFromLong(id)));
	}

	@Test
	public void shouldReturnEmptyResultWithNullAttributeAndReferenceParam()
	{
		testComparisonReturnNoItemStates(PolyglotPersistence.getReferenceTo(PolyglotPersistence.identityFromLong(1L)),
				id -> null);
	}

	@Test
	public void shouldReturnEmptyResultWithIdentityAttributeAndLongParam()
	{
		testComparisonReturnNoItemStates(PolyglotPersistence.identityFromLong(1L), id -> id);
	}

	@Test
	public void shouldReturnAllItemStatesWithNullAttributeAndNullParam()
	{
		testComparisonReturnAllItemStates(null, id -> null);
	}

	@Test
	public void shouldReturnNoItemStatesWithLongAttributeAndNullParam()
	{
		testComparisonReturnNoItemStates(1L, id -> null);
	}

	@Test
	public void shouldFilterItemStateWithCharAttributeAndCharParam()
	{
		testComparisonReturnValidItemState('1', id -> id.toString().charAt(0));
	}

	@Test
	public void shouldFilterItemStateWithCharAttributeAndStringParam()
	{
		testComparisonReturnValidItemState("1", id -> id.toString().charAt(0));
	}

	@Test
	public void shouldFilterItemStateWithStringAttributeAndStringParam()
	{
		testComparisonReturnValidItemState("1", Object::toString);
	}

	@Test
	public void shouldFilterItemStateWithLongAttributeAndLongParam()
	{
		testComparisonReturnValidItemState(1L, id -> id);
	}

	@Test
	public void shouldFilterItemStateWithDoubleAttributeAndLongParam()
	{
		testComparisonReturnValidItemState(1.0, id -> id);
	}

	private void testComparisonWithIdentityAttribute(final Object param)
	{
		testComparisonReturnValidItemState(param, PolyglotPersistence::identityFromLong);
	}

	private void testComparisonWithReferenceAttribute(final Object param)
	{
		testComparisonReturnValidItemState(param,
				id -> PolyglotPersistence.getReferenceTo(PolyglotPersistence.identityFromLong(id)));
	}

	private void prepareItemStates(final Function<Long, Object> attrProvider)
	{
		when(validItem.get(attr1)).thenReturn(attrProvider.apply(1L));
		when(invalidItem.get(attr1)).thenReturn(attrProvider.apply(2L));
	}

	private MatchingPredicateBuilder createMatchingPredicateBuilder(final Object param)
	{
		final Map<String, Object> paramValuesMap = param != null ? Map.of("param1",
				param) : Collections.emptyMap();
		return new MatchingPredicateBuilder(Set.of(typeId),
				Conditions.cmp(attr1, Conditions.ComparisonCondition.CmpOperator.EQUAL, "param1"),
				paramValuesMap);
	}

	private Stream<ItemState> testComparison(final Object param, final Function<Long, Object> attrProvider)
	{
		prepareItemStates(attrProvider);
		final MatchingPredicateBuilder predicateBuilder = createMatchingPredicateBuilder(param);

		final Predicate<ItemStateView> predicate = predicateBuilder.getPredicate();
		return Stream.of(validItem, invalidItem).filter(predicate);
	}

	private void testComparisonReturnValidItemState(final Object param, final Function<Long, Object> attrProvider)
	{
		final Stream<ItemState> stream = testComparison(param, attrProvider);
		assertThat(stream).isNotEmpty().hasSize(1).containsExactly(validItem);
	}

	private void testComparisonReturnNoItemStates(final Object param, final Function<Long, Object> attrProvider)
	{
		final Stream<ItemState> stream = testComparison(param, attrProvider);
		assertThat(stream).isEmpty();
	}

	private void testComparisonReturnAllItemStates(final Object param, final Function<Long, Object> attrProvider)
	{
		final Stream<ItemState> stream = testComparison(param, attrProvider);
		assertThat(stream).hasSize(2).containsExactly(validItem, invalidItem);
	}
}

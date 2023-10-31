/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.indexer.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource;
import de.hybris.platform.searchservices.indexer.service.impl.PksSnIndexerItemSource;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PksSnIndexerItemSourceTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private SnIndexerContext indexerContext;

	@Test
	public void createItemSource() throws Exception
	{
		// given
		final PK pk1 = PK.fromLong(1);
		final PK pk2 = PK.fromLong(2);
		final PK pk3 = PK.fromLong(3);

		// when
		final SnIndexerItemSource itemSource = new PksSnIndexerItemSource(List.of(pk1, pk2, pk3));
		final List<PK> pks = itemSource.getPks(indexerContext);

		// then
		assertThat(pks).isNotNull().hasSize(3).containsExactly(pk1, pk2, pk3);
	}

	@Test
	public void createEmptyItemSource() throws Exception
	{
		// when
		final SnIndexerItemSource itemSource = new PksSnIndexerItemSource(null);
		final List<PK> pks = itemSource.getPks(indexerContext);

		// then
		assertThat(pks).isNotNull().isEmpty();
	}
}

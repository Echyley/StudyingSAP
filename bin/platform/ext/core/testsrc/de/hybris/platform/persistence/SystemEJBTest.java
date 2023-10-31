/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.List;

import org.junit.Test;

/**
 * Unit test for {@link SystemEJB}
 */
@UnitTest
public class SystemEJBTest
{
	private static final String ELEMENT = "test";

	@Test
	public void getWriteOnInitializationListTest()
	{
		//when
		final List<String> results = SystemEJB.getWriteOnInitializationList();

		//then
		assertThat(results).hasSize(4);
	}

	@Test
	public void listShouldBeUnmodifiableTest()
	{
		//when
		final List<String> results = SystemEJB.getWriteOnInitializationList();

		//then
		assertThatThrownBy(() -> results.set(0, ELEMENT)).isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> results.add(ELEMENT)).isInstanceOf(UnsupportedOperationException.class);
		assertThatThrownBy(() -> results.remove(0)).isInstanceOf(UnsupportedOperationException.class);
	}
}

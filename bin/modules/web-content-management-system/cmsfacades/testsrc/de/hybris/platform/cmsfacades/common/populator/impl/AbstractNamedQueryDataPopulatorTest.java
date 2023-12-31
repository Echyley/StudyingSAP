/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.common.populator.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Map;

import org.junit.Test;


@UnitTest
public class AbstractNamedQueryDataPopulatorTest
{
	private final AbstractNamedQueryDataPopulator populator = new AbstractNamedQueryDataPopulator()
	{
		@Override
		public Map<String, Object> convertParameters(final String params)
		{
			return null;
		}
	};

	@Test
	public void shouldBuildParametersStingMap()
	{
		final String params = "code:banner,name:help,title:sales";
		final Map<String, String> map = populator.buildParameterStringMap(params);

		assertThat(map.values().size(), equalTo(3));
		assertThat(map.get("code"), equalTo("banner"));
		assertThat(map.get("name"), equalTo("help"));
		assertThat(map.get("title"), equalTo("sales"));
	}

	@Test
	public void shouldBuildParametersStingMapWithEmptyValue()
	{
		final String params = "code:,name:help,title:sales";
		final Map<String, String> map = populator.buildParameterStringMap(params);

		assertThat(map.values().size(), equalTo(2));
		assertThat(map.get("code"), nullValue());
	}

	@Test
	public void shouldBuildParametersStingMapWithNoColon()
	{
		final String params = "code:banner,name,title:sales";
		final Map<String, String> map = populator.buildParameterStringMap(params);

		assertThat(map.values().size(), equalTo(2));
		assertThat(map.get("name"), nullValue());
	}

	@Test
	public void shouldBuildParametersStingMapWithEmptyKey()
	{
		final String params = "code:banner,name:help,:sales";
		final Map<String, String> map = populator.buildParameterStringMap(params);

		assertThat(map.values().size(), equalTo(3));
		assertThat(map.get("title"), nullValue());
		assertThat(map.get(""), equalTo("sales"));
	}

}

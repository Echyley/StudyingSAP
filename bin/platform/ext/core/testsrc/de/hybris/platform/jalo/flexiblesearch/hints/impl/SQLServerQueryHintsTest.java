/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.jalo.flexiblesearch.hints.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;

@UnitTest
public class SQLServerQueryHintsTest
{

	private final String FS_QUERY = "SELECT {PK} FROM {Product}";

	@Test
	public void shouldApplyHintValueToTheQueryWhileSQLServerIsUsed()
	{
		//given
		final SQLServerQueryHints maxDopHint = new SQLServerQueryHints(true);
		maxDopHint.addMaxDOPHint(5);

		//when
		final String result = maxDopHint.apply(FS_QUERY);

		//then
		assertThat(result).contains("OPTION ( MAXDOP 5 )");
	}

	@Test
	public void shouldNotApplyHintValueToTheQueryForNegativeValue()
	{
		//given
		final SQLServerQueryHints maxDopHintNeg = new SQLServerQueryHints(true);
		maxDopHintNeg.addMaxDOPHint(-1);

		//when
		final String result = maxDopHintNeg.apply(FS_QUERY);

		//then
		assertThat(result).doesNotContain("OPTION ( MAXDOP");
	}

	@Test
	public void shouldNotApplyHintValueToTheQueryWhileSQLServerIsUsed()
	{
		//given
		final SQLServerQueryHints maxDopHint = new SQLServerQueryHints(false);
		maxDopHint.addMaxDOPHint(5);

		//when
		final String result = maxDopHint.apply(FS_QUERY);

		//then
		assertThat(result).doesNotContain("OPTION ( MAXDOP 5 )");
	}

	@Test
	public void shouldApplyOnlyOneHintValueToTheQueryWhileSQLServerIsUsed()
	{
		//given
		final SQLServerQueryHints maxDopHint = new SQLServerQueryHints(true);
		maxDopHint.addMaxDOPHint(5);
		maxDopHint.addMaxDOPHint(6);

		//when
		final String result = maxDopHint.apply(FS_QUERY);

		//then
		assertThat(result).contains("MAXDOP 6").doesNotContain("MAXDOP 5");
	}

}
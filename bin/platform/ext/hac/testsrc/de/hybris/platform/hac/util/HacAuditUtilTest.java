/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.util;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Optional;

import org.junit.Test;

@UnitTest
public class HacAuditUtilTest
{
	@Test
	public void shouldNotNormalizeEOLIfNullValue()
	{
		//when
		final String normalized = HacAuditUtil.normalizeEOL(null);

		//then
		assertThat(normalized).isNull();
	}

	@Test
	public void shouldNormalizeEOL()
	{
		//given
		final String notNormalized = "INSERT_UPDATE Title; code[unique=true];\r\n;test_title1\r;test_title2\n;test_title3";
		final String correctlyNormalized = "INSERT_UPDATE Title; code[unique=true];\n;test_title1\n;test_title2\n;test_title3";

		//when
		final String normalized = HacAuditUtil.normalizeEOL(notNormalized);

		//then
		assertThat(normalized).isEqualTo(correctlyNormalized);
	}

	@Test
	public void shouldNotGenerateSignature()
	{
		//when
		final Optional<String> optionalSignature = HacAuditUtil.generateSHA256Signature(null);

		//then
		assertThat(optionalSignature).isEmpty();
	}

	@Test
	public void shouldGenerateSHA256Signature()
	{
		//given
		final String string = "INSERT_UPDATE Title; code[unique=true];\n;test_title";
		final String correctSignature = "f4ff96d901ba63bc8b6538ecc579733d06d63f8ba03e4630e90f7e63bd1a9657";

		//when
		final Optional<String> optionalSignature = HacAuditUtil.generateSHA256Signature(string);

		//then
		assertThat(optionalSignature).isNotEmpty();
		assertThat(optionalSignature.get()).hasSize(64);
		assertThat(optionalSignature.get()).contains(correctSignature);
	}
}

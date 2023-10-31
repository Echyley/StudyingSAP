/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.util;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.util.RegexParser;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test for {@link SequenceIdParser}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SequenceIdParserTest
{
	private SequenceIdParser parser;
	@Mock
	private File file;
	@Mock
	private RegexParser regexParser;


	@Before
	public void setUp()
	{
		parser = new SequenceIdParser();
		parser.setParser(regexParser);
	}


	@Test(expected = IllegalArgumentException.class)
	public void testSyntax()
	{
		given(file.getName()).willReturn("err.csv");
		parser.getSequenceId(file);
	}

	@Test
	public void testSuccess()
	{
		given(file.getName()).willReturn("test-1234.csv");
		given(regexParser.parse("test-1234.csv", 1)).willReturn("1234");
		Assert.assertEquals(Long.valueOf(1234L), parser.getSequenceId(file));
	}


}

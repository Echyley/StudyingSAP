/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sap.custdev.projects.fbs.slc.cfg.client.ITextDescription;


@UnitTest
public class TextConverterImplTest
{
	private static String textWoTags = "Huhu";
	private static String textAdditional = "Hello";
	private static String textWExplanation = textWoTags + SapproductconfigruntimesscConstants.EXPLANATION + textAdditional;
	private static String textWDocumentation = textWoTags + SapproductconfigruntimesscConstants.DOCUMENTATION + textAdditional;
	@Mock
	private ITextDescription textDescriptionEmpty;
	@Mock
	private ITextDescription textDescription;

	public TextConverterImpl classUnderTest = new TextConverterImpl();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		when(textDescriptionEmpty.getTextLine()).thenReturn(SapproductconfigruntimesscConstants.EXPLANATION);
		when(textDescription.getTextLine()).thenReturn(textWExplanation);
		when(textDescription.getTextLineId()).thenReturn(Integer.valueOf(7));
		when(textDescription.getTextFormat()).thenReturn("X");
	}

	@Test
	public void testConvertLongText()
	{

		final String input = "<(>&<)>DESCRIPTION&\n" + "<H>Text in bold</>, <U>text underlined.</> Lorem ipsum dolor sit amet,"
				+ "consectetur adipiscing elit. Nullam euismod tristique orci sed faucibus."
				+ "Curabitur id felis et sem ultricies posuere. Maecenas laoreet quis nibh"
				+ "vitae iaculis. Pellentesque eros lorem, hendrerit ut aliquet quis, porta"
				+ "eget augue. In gravida non metus nec porta. Curabitur ac mattis dui."
				+ "Donec auctor, magna non laoreet tempus, ante purus ultricies libero, vel"
				+ "laoreet ex nibh eget dolor. Curabitur quis molestie neque. Praesent"
				+ "tempor dui orci, et vehicula est dignissim ut. Nulla risus dolor,"
				+ "molestie in nisl sit amet, porta sagittis neque. Aliquam erat volutpat.\"&>\"" + "\n" + "<(>&<)>EXPLANATION&"
				+ "\n" + "Donec massa quam, luctus eget ultrices ut, euismod eu sapien. Duis"
				+ "maximus, urna ut suscipit vestibulum, nunc lectus egestas metus, pretium"
				+ "auctor quam lacus a libero. Donec neque risus, tincidunt eget luctus"
				+ "sed, maximus sed magna. Nulla porttitor orci in facilisis pretium. Nunc"
				+ "malesuada, nulla sit amet hendrerit vehicula, dolor arcu elementum leo";

		final String expected = "\nText in bold, text underlined. Lorem ipsum dolor sit amet,"
				+ "consectetur adipiscing elit. Nullam euismod tristique orci sed faucibus."
				+ "Curabitur id felis et sem ultricies posuere. Maecenas laoreet quis nibh"
				+ "vitae iaculis. Pellentesque eros lorem, hendrerit ut aliquet quis, porta"
				+ "eget augue. In gravida non metus nec porta. Curabitur ac mattis dui."
				+ "Donec auctor, magna non laoreet tempus, ante purus ultricies libero, vel"
				+ "laoreet ex nibh eget dolor. Curabitur quis molestie neque. Praesent"
				+ "tempor dui orci, et vehicula est dignissim ut. Nulla risus dolor,"
				+ "molestie in nisl sit amet, porta sagittis neque. Aliquam erat volutpat.'&>'" + "\n";
		final String output = classUnderTest.convertLongText(input);

		assertEquals(expected, output);
	}

	@Test
	public void testConvertMarkup()
	{
		final String input = "ABC<U:WWW.HYBRIS.DE>abc<U:HTTPS://WWW.SAP.COM>ABC</U>";
		final String expected = "ABCabcABC";

		final String output = classUnderTest.convertLongText(input);

		assertEquals(expected, output);
	}

	@Test
	public void testConvertLongTextEmpty()
	{
		final String EMPTY_TEXT = "";
		final String output = classUnderTest.convertLongText(EMPTY_TEXT);

		assertSame(EMPTY_TEXT, output);
	}

	@Test
	public void testConvertDependencyTextNull()
	{
		assertNull(classUnderTest.convertDependencyText(null));
	}

	@Test
	public void testConvertDependencyTextEmpty()
	{
		assertNull(classUnderTest.convertDependencyText(new ITextDescription[0]));
	}

	@Test
	public void testConvertDependencyTextNoExplanation()
	{

		assertEquals("", classUnderTest.convertDependencyText(new ITextDescription[]
		{ textDescriptionEmpty }));
	}


	@Test
	public void testGetExplanationNoExplanationText()
	{
		final String explanation = classUnderTest.getExplanationForDependency(textWoTags);
		assertEquals(textWoTags, explanation);

	}

	@Test
	public void testGetExplanationExplanationText()
	{
		final String explanation = classUnderTest.getExplanationForDependency(textWExplanation);
		assertEquals(textAdditional, explanation);
	}

	@Test
	public void testGetExplanationDocumentationText()
	{
		final String explanation = classUnderTest.getExplanationForDependency(textWDocumentation);
		assertEquals(textWoTags, explanation);
	}

	@Test
	public void testGetExplanationNull()
	{
		final String explanation = classUnderTest.getExplanationForDependency(null);
		assertEquals("", explanation);
	}

	@Test
	public void testConvert()
	{
		final ITextDescription[] textDescriptionArray =
		{ textDescription };
		final String convertedText = classUnderTest.convertDependencyText(textDescriptionArray);
		assertEquals(textAdditional, convertedText);
	}

}

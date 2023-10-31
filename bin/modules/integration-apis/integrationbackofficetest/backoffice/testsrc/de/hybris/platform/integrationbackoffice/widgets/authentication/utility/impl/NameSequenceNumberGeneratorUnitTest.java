/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.authentication.utility.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NameSequenceNumberGeneratorUnitTest
{
	private static final String IO_CODE = "iocode";
	private static final String IO_CODE2 = "ioCode500";

	private final FlexibleSearchService flexSearch = mock(FlexibleSearchService.class);
	private final SearchResult endpointResult = mock(SearchResult.class);
	private final SearchResult exposedDestinationResult = mock(SearchResult.class);
	private final NameSequenceNumberGenerator occNamingConvention = new NameSequenceNumberGenerator(flexSearch);

	@Before
	public void setup()
	{
		when(flexSearch.search(any(FlexibleSearchQuery.class))).thenReturn(endpointResult, exposedDestinationResult);
	}

	@Test
	public void getNumberWhenEndpointAndDestinationAreEmpty()
	{
		getSearchResultEndpoint(Collections.emptyList());
		getSearchResultExposedDestination(Collections.emptyList());
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.ZERO;

		assertEquals("No results present means expected generated number is '0'", expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointHasNoDigitAndDestinationIsEmpty()
	{
		getSearchResultEndpoint(List.of("cc-iocode-metadata"));
		getSearchResultExposedDestination(Collections.emptyList());
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.ONE;

		assertEquals("Existing entry (without numbers) means increment should give '1'", expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointIsEmptyAndDestinationHasNoDigit()
	{
		getSearchResultEndpoint(Collections.emptyList());
		getSearchResultExposedDestination(List.of("cc-iocode"));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.ONE;

		assertEquals("Existing entry (without numbers) means increment should give '1'", expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointAndDestinationHaveNoDigit()
	{
		getSearchResultEndpoint(List.of("cc-iocode-metadata"));
		getSearchResultExposedDestination(List.of("cc-iocode"));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.ONE;

		assertEquals("Existing entries (without numbers) means increment should give '1'", expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointHasDigitAndDestinationHasNoDigit()
	{
		final int seqNum = 1;
		getSearchResultEndpoint(List.of("cc-iocode-" + seqNum + "-metadata"));
		getSearchResultExposedDestination(List.of("cc-iocode"));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.valueOf(seqNum + 1);

		assertEquals("Existing entries, one with a number, means increment should give 1 more than the highest, in this case '2'",
				expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointHasNoDigitAndDestinationHasDigit()
	{
		final int seqNum = 1;
		getSearchResultEndpoint(List.of("cc-iocode-metadata"));
		getSearchResultExposedDestination(List.of("cc-iocode-" + seqNum));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.valueOf(seqNum + 1);

		assertEquals("Existing entries, one with a number, means increment should give 1 more than the highest, in this case '2'",
				expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointAndDestinationHaveDigits()
	{
		final int seqNumLow = 1;
		final int seqNumHigh = 5;
		getSearchResultEndpoint(List.of("cc-iocode-" + seqNumLow + "-metadata"));
		getSearchResultExposedDestination(List.of("cc-iocode-" + seqNumHigh));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.valueOf(seqNumHigh + 1);

		assertEquals("Existing entries each with a number means increment should give 1 more than the highest, in this case '6'",
				expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenMultipleEndpointsExistWithDigitAndDestinationIsEmpty()
	{
		final int seqNum = 5;
		getSearchResultEndpoint(List.of("cc-iocode-metadata", "cc-iocode-" + seqNum + "-metadata"));
		getSearchResultExposedDestination(Collections.emptyList());
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.valueOf(seqNum + 1);

		assertEquals("Existing entries with a number means increment should give 1 more than the highest, in this case '6'",
				expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointIsEmptyAndMultipleDestinationsExistWithDigit()
	{
		final int seqNum = 5;
		getSearchResultEndpoint(Collections.emptyList());
		getSearchResultExposedDestination(List.of("cc-iocode", "cc-iocode-" + seqNum));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.valueOf(seqNum + 1);

		assertEquals("Existing entries with a number means increment should give 1 more than the highest, in this case '6'",
				expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenMultipleEndpointsAndDestinationsExistWithDigits()
	{
		final int seqNumLow = 5;
		final int seqNumHigh = 55;
		getSearchResultEndpoint(List.of("cc-iocode-metadata", "cc-iocode-" + seqNumLow + "-metadata"));
		getSearchResultExposedDestination(List.of("cc-iocode-" + seqNumHigh));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = BigInteger.valueOf(seqNumHigh + 1);

		assertEquals("Existing entries with a number means increment should give 1 more than the highest, in this case '56'",
				expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointAndDestinationHaveDigitInName()
	{
		final int seqNum = 5;
		getSearchResultEndpoint(List.of("cc-iocode500-metadata", "cc-iocode500-" + seqNum + "-metadata"));
		getSearchResultExposedDestination(List.of("cc-iocode500"));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE2);
		final BigInteger expectedNo = BigInteger.valueOf(seqNum + 1);

		assertEquals(
				"Existing entries with a number (those in IO name do not count) means increment should give 1 more than the highest, in this case '6'",
				expectedNo, actualNo);
	}


	@Test
	public void getNumberWhenExistingEntityContainsVeryLargeNumber()
	{
		final int seqNumLow = 5;
		final String bigNum = "100000000000000000000";
		final BigInteger bigNumPlus1 = new BigInteger(bigNum).add(BigInteger.ONE);
		getSearchResultEndpoint(List.of("cc-iocode-metadata", "cc-iocode-" + seqNumLow + "-metadata"));
		getSearchResultExposedDestination(List.of("cc-iocode-" + bigNum));
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(IO_CODE);
		final BigInteger expectedNo = bigNumPlus1;

		assertEquals("Existing entry with a very large number will not cause overflow", expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenIONameContainsDigits()
	{
		getSearchResultEndpoint(List.of("cc-1234-10-metadata"));
		getSearchResultExposedDestination(List.of("cc-1234-10"));
		final String ioName = "1234-10";
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(ioName);
		final BigInteger expectedNo = BigInteger.ONE;

		assertEquals(
				"IO names that contain digits should not have those digits considered in the endpoint and destination number extrapolation." +
						"Therefore with no other digits in the entries the result should be '1'",
				expectedNo, actualNo);
	}

	@Test
	public void getNumberWhenEndpointContainsDigitsNotInIOName()
	{
		final int seqNum = 9;
		getSearchResultEndpoint(List.of("cc-price-test4-" + seqNum + "-metadata"));
		getSearchResultExposedDestination(Collections.emptyList());
		final String ioName = "Price-Test4";
		final BigInteger actualNo = occNamingConvention.getGeneratedSequencedNumber(ioName);
		final BigInteger expectedNo = BigInteger.valueOf(seqNum + 1);
		;

		assertEquals(
				"IO names that contain digits should not have those digits considered in the endpoint and destination number extrapolation." +
						"Therefore with no other digits in the entries the result should be '10'",
				expectedNo, actualNo);
	}

	private void getSearchResultEndpoint(final List<String> listOfID)
	{
		when(endpointResult.getResult()).thenReturn(listOfID);
	}

	private void getSearchResultExposedDestination(final List<String> listOfID)
	{
		when(exposedDestinationResult.getResult()).thenReturn(listOfID);
	}
}

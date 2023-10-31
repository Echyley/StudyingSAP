/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.labelproviders;

import com.hybris.backoffice.cockpitng.classification.labels.impl.RangeLabelProvider;
import com.hybris.cockpitng.util.Range;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.util.StandardDateRange;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DateRangeLabelProviderTest
{
	private static final String STANDARD_DATE_RANGE_CLASS_NAME = StandardDateRange.class.getName();
	private static final String INCORRECT_CLASS_NAME = Integer.class.getName();
	private static final String DATE_RANGE_RESULT_LABEL = "resultLabel";
	private static final String LABEL = "label";

	@InjectMocks
	private DateRangeLabelProvider testSubject;

	@Mock
	private RangeLabelProvider rangeLabelProvider;

	@Mock
	private StandardDateRange dateRange;

	@Test
	public void shouldNotHandleEditorTypeWhenDifferentThanStandardDateRangeClassNameWasPassed()
	{
		// when
		final boolean canHandle = testSubject.canHandle(INCORRECT_CLASS_NAME);

		// then
		assertThat(canHandle).isFalse();
	}

	@Test
	public void shouldHandleEditorTypeWhenStandardDateRangeClassNameWasPassed()
	{
		// when
		final boolean canHandle = testSubject.canHandle(STANDARD_DATE_RANGE_CLASS_NAME);

		// then
		assertThat(canHandle).isTrue();
	}

	@Test
	public void shouldProvideEmptyLabelWhenDifferentThanStandardDateRangeTypeWasPassed()
	{
		// when
		final String resultLabel = testSubject.getLabel(StringUtils.EMPTY, LABEL);

		// then
		assertThat(resultLabel).isEmpty();
	}

	@Test
	public void shouldProvideEmptyLabelWhenValueIsNull()
	{
		// when
		final String resultLabel = testSubject.getLabel(StringUtils.EMPTY, null);

		// then
		assertThat(resultLabel).isEmpty();
	}


	@Test
	public void shouldProvideResultLabelWhenRangeIsNotNull()
	{
		// given
		when(rangeLabelProvider.getLabel(any(Range.class))).thenReturn(DATE_RANGE_RESULT_LABEL);

		// when
		final String resultLabel = testSubject.getLabel(StringUtils.EMPTY, dateRange);

		// then
		assertThat(resultLabel).isEqualTo(DATE_RANGE_RESULT_LABEL);
	}

}

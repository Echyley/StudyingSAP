/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.standarddaterange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.util.StandardDateRange;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.WrongValueException;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;
import com.hybris.cockpitng.util.Range;


@RunWith(MockitoJUnitRunner.class)
public class DefaultStandardDateRangeEditorTest extends AbstractCockpitEditorRendererUnitTest<StandardDateRange, DefaultStandardDateRangeEditor>
{
	@Mock
	private EditorListener<StandardDateRange> editorListener;

	@Mock
	private EditorContext<StandardDateRange> editorContext;

	@Mock
	private Editor editor;

	@InjectMocks
	private final DefaultStandardDateRangeEditor dateRangeEditor = new DefaultStandardDateRangeEditor();

	private final Date beginDate = new Date(123456);
	private final Date endDate = new Date(234567);

	@Override
	public DefaultStandardDateRangeEditor getEditorInstance()
	{
		return dateRangeEditor;
	}

	@Test
	public void checkListenerNotifiedWhenRangeIsCorrect()
	{
		//given
		when(editor.getValue()).thenReturn(new Range<>(beginDate, endDate));

		//when
		dateRangeEditor.processValueChange(editorListener, editorContext);

		//then
		final ArgumentCaptor<StandardDateRange> standardDateRangeCaptor = ArgumentCaptor.forClass(StandardDateRange.class);
		verify(editorListener).onValueChanged(standardDateRangeCaptor.capture());
		assertThat(standardDateRangeCaptor.getValue().getStart()).isEqualTo(beginDate);
		assertThat(standardDateRangeCaptor.getValue().getEnd()).isEqualTo(endDate);
	}

	@Test
	public void checkListenerNotifiedWhenRangeIsEmpty()
	{
		//given
		when(editor.getValue()).thenReturn(new Range<>(null, null));

		//when
		dateRangeEditor.processValueChange(editorListener, editorContext);

		//then
		verify(editorListener).onValueChanged(null);
	}

	@Test
	public void checkExceptionThrownWhenBeginDateIsAfterEnd()
	{
		//given
		when(editor.getValue()).thenReturn(new Range<>(endDate, beginDate));

		//when
		WrongValueException exception = null;
		try
		{
			dateRangeEditor.processValueChange(editorListener, editorContext);
		} catch (final WrongValueException e)
		{
			exception = e;
		}

		//then
		verify(editorListener).onValueChanged(null);
		assertThat(exception).isNotNull();
	}

	@Test
	public void checkExceptionThrownWhenNullData()
	{
		//given
		when(editor.getValue()).thenReturn(null);

		//when
		WrongValueException exception = null;
		try
		{
			dateRangeEditor.processValueChange(editorListener, editorContext);
		} catch (final WrongValueException e)
		{
			exception = e;
		}

		//then
		verify(editorListener).onValueChanged(null);
		assertThat(exception).isNotNull();
	}
}
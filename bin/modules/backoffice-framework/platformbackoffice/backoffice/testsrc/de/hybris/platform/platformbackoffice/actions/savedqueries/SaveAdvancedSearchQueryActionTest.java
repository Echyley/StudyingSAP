/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions.savedqueries;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


public class SaveAdvancedSearchQueryActionTest extends AbstractActionUnitTest<SaveAdvancedSearchQueryAction>
{

	@Mock
	private SaveQueryActionChecker saveQueryActionChecker;
	@Spy
	@InjectMocks
	private SaveAdvancedSearchQueryAction saveAdvancedSearchQueryAction;

	@Override
	public SaveAdvancedSearchQueryAction getActionInstance()
	{
		return saveAdvancedSearchQueryAction;
	}

	@Before
	public void initialize()
	{
		CockpitTestUtil.mockZkEnvironment();
		willDoNothing().given(saveAdvancedSearchQueryAction).displayWarning(any(), any());
	}

	@Test
	public void shouldContextNotBeSentWhenInputDataIsNotCorrect() throws TypeNotFoundException
	{
		// given
		final ActionContext ctx = mock(ActionContext.class);
		given(ctx.getLabel(any())).willReturn("any");
		given(ctx.getLabel(any(), any())).willReturn("any");
		given(saveQueryActionChecker.check(any())).willReturn(Lists.newArrayList(new SaveQueryInvalidCondition("articleStatus")));

		// when
		saveAdvancedSearchQueryAction.perform(ctx);

		// then
		then(componentWidgetAdapter).should(never()).sendOutput(any(), any(), any());
		then(saveAdvancedSearchQueryAction).should().displayWarning(any(), any());
	}

	@Test
	public void shouldContextBeSentWhenInputDataIsCorrect() throws TypeNotFoundException
	{
		// given
		final ActionContext ctx = mock(ActionContext.class);
		given(ctx.getLabel(any())).willReturn("any");
		given(ctx.getLabel(any(), any())).willReturn("any");
		given(saveQueryActionChecker.check(any())).willReturn(Lists.emptyList());

		// when
		saveAdvancedSearchQueryAction.perform(ctx);

		// then
		then(componentWidgetAdapter).should().sendOutput(any(), any(), any());
		then(saveAdvancedSearchQueryAction).should(never()).displayWarning(any(), any());
	}

	@Test
	public void shouldContextNotBeSentWhenTypeWasNotFound() throws TypeNotFoundException
	{
		// given
		final ActionContext ctx = mock(ActionContext.class);
		given(ctx.getLabel(any())).willReturn("any");
		given(ctx.getLabel(any(), any())).willReturn("any");
		given(saveQueryActionChecker.check(any())).willThrow(TypeNotFoundException.class);

		// when
		saveAdvancedSearchQueryAction.perform(ctx);

		// then
		then(componentWidgetAdapter).should(never()).sendOutput(any(), any(), any());
		then(saveAdvancedSearchQueryAction).should().displayWarning(any(), any());
	}

}

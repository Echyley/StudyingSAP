/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.google.common.collect.ImmutableMap;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;
import com.hybris.cockpitng.testing.util.BeanLookup;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


public class ScriptingActionTest extends AbstractActionUnitTest<ScriptingAction>
{

	private static final String PARAMETER_SCRIPT_URI = "scriptUri";
	private static final String SCRIPT_1_URI = "script1";
	private static final String SCRIPT_2_URI = "script2";

	@InjectMocks
	private ScriptingAction action;

	@Mock
	private ScriptingLanguagesService scriptingLanguagesService;
	@Mock
	private ScriptExecutable script1Executable;
	@Mock
	private ScriptExecutable script2Executable;
	@Mock
	private CockpitAction script1Action;
	@Mock
	private CockpitAction script2Action;


	@Override
	public ScriptingAction getActionInstance()
	{
		return action;
	}

	@Before
	public void setUp()
	{
		final BeanLookup beanLookup = BeanLookupFactory.createBeanLookup()
				.registerBean("scriptingLanguagesService", scriptingLanguagesService).getLookup();
		CockpitTestUtil.mockBeanLookup(beanLookup);
	}

	@Test
	public void shouldAlwaysResolveActionScript()
	{
		final ActionContext<Object> script1Context = new ActionContext<>(null, null, ImmutableMap.of(PARAMETER_SCRIPT_URI,
				SCRIPT_1_URI), null);
		when(scriptingLanguagesService.getExecutableByURI(SCRIPT_1_URI)).thenReturn(script1Executable);
		when(script1Executable.getAsInterface(CockpitAction.class)).thenReturn(script1Action);

		getActionInstance().perform(script1Context);

		verify(script1Action).perform(script1Context);

		final ActionContext<Object> script2Context = new ActionContext<>(null, null, ImmutableMap.of(PARAMETER_SCRIPT_URI,
				SCRIPT_2_URI), null);
		when(scriptingLanguagesService.getExecutableByURI(SCRIPT_2_URI)).thenReturn(script2Executable);
		when(script2Executable.getAsInterface(CockpitAction.class)).thenReturn(script2Action);

		getActionInstance().perform(script2Context);

		verify(script2Action).perform(script2Context);
	}

}

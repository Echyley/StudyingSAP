/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.enums.ImpExValidationModeEnum;
import de.hybris.platform.servicelayer.impex.ExportService;
import de.hybris.platform.servicelayer.impex.impl.DefaultImpExValidationResult;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@UnitTest
@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class BasicScriptEditorTest extends AbstractCockpitngUnitTest<BasicScriptEditor>
{

	@InjectMocks
	@Spy
	private BasicScriptEditor scriptEditor;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private EditorContext<String> context;

	@Mock
	private Component component;

	@Mock
	private WidgetModel widgetModel;

	@Mock
	private ExportService exportService;

	@Mock
	private Button saveBtn;

	@Mock
	private Button verifyBtn;


	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		Mockito.when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		Mockito.doReturn(new Textbox()).when(scriptEditor).getScriptTextbox();
	}

	@Test
	public void testHandleValidateWithNullScript()
	{
		final DefaultImpExValidationResult validationResult = new DefaultImpExValidationResult(true);
		Mockito.when(exportService.validateExportScript(StringUtils.EMPTY, ImpExValidationModeEnum.EXPORT_ONLY))
				.thenReturn(validationResult);
		Mockito.doNothing().when(scriptEditor).showValidationResult(context, validationResult);


		scriptEditor.handleValidateEvent(context);

		Mockito.verify(scriptEditor, Mockito.times(1)).showValidationResult(Mockito.any(), Mockito.any());
	}

	@Test
	public void activateContentButtonsTest()
	{
		scriptEditor.activateContentButtons("content");

		Mockito.verify(saveBtn).setDisabled(false);
		Mockito.verify(verifyBtn).setDisabled(false);

		Mockito.reset(saveBtn);
		Mockito.reset(verifyBtn);

		scriptEditor.activateContentButtons(null);

		Mockito.verify(saveBtn).setDisabled(true);
		Mockito.verify(verifyBtn).setDisabled(true);


		Mockito.reset(saveBtn);
		Mockito.reset(verifyBtn);

		scriptEditor.activateContentButtons(StringUtils.EMPTY);

		Mockito.verify(saveBtn).setDisabled(true);
		Mockito.verify(verifyBtn).setDisabled(true);
	}


	@Override
	protected Class getWidgetType()
	{
		return BasicScriptEditor.class;
	}
}

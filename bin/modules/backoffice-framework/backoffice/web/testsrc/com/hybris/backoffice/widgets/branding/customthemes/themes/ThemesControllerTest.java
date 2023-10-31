package com.hybris.backoffice.widgets.branding.customthemes.themes;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import com.hybris.backoffice.model.CustomThemeModel;
import com.hybris.backoffice.model.ThemeModel;
import com.hybris.backoffice.theme.BackofficeThemeService;
import com.hybris.backoffice.theme.impl.ThemeStyleUtil;
import com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.ViewMode;
import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;

import static com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.BACK_BUTTON;
import static com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.CANCEL_BUTTON;
import static com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.CREATE_BUTTON;
import static com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.NOTIFICATION_AREA;
import static com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.NOTIFICATION_TYPE_CUSTOM_THEMES_CHANGED;
import static com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.NOTIFICATION_TYPE_CUSTOM_THEMES_CREATED;
import static com.hybris.backoffice.widgets.branding.customthemes.themes.ThemesController.SOCKET_OUTPUT_CUSTOM_THEME_CHANGED;
import static com.hybris.cockpitng.testing.util.CockpitTestUtil.mockZkEnvironment;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = CREATE_BUTTON)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = BACK_BUTTON)
@DeclaredViewEvent(eventName = Events.ON_CLICK, componentID = CANCEL_BUTTON)
@ExtensibleWidget(level = ExtensibleWidget.CONSTRUCTORS)
public class ThemesControllerTest extends AbstractWidgetUnitTest<ThemesController>
{
	@Mock
	private ModelService modelService;
	@Mock
	private transient BackofficeThemeService backofficeThemeService;
	@Mock
	private transient NotificationService notificationService;
	@Mock
	private transient ThemeStyleUtil customThemeUtil;
	@Mock
	private transient ThemeSaveHelper themeSaveHelper;
	@Mock
	private transient ThemeValidationHelper themeValidationHelper;
	@Mock
	private AppearanceItemRenderer appearanceItemRenderer;
	@Mock
	private ThemeModel currentBaseTheme;
	@Mock
	private CustomThemeModel currentEditTheme;
	@Mock
	private Combobox baseThemeCombox;
	@Mock
	private Div themeEditView;
	@Mock
	private Div themeDisplayView;
	@Mock
	private Div emptyListView;
	@Mock
	private Listbox themeListBox;
	@Mock
	private Label titleLabel;
	@Mock
	private Button backButton;
	@Mock
	private Button createButton;
	@Mock
	private Button saveButton;
	@Mock
	private Button cancelButton;
	@Mock
	private ListModelList<CustomThemeModel> customThemesModel;
	@Mock
	protected ListModelList<ThemeModel> baseThemesModel;

	@Spy
	@InjectMocks
	private ThemesController controller;

	@Before
	public void setUp()
	{
		mockZkEnvironment();
		doNothing().when(controller).createCodeEditor();
		doNothing().when(controller).createNameEditor();
		when(appearanceItemRenderer.resetAppearanceItemColor(any(MediaModel.class))).thenReturn(true);
		when(modelService.create(CustomThemeModel._TYPECODE)).thenReturn(mock(CustomThemeModel.class));
		when(currentBaseTheme.getStyle()).thenReturn(mock(MediaModel.class));
		when(backofficeThemeService.getDefaultTheme()).thenReturn(currentBaseTheme);
	}

	@Test
	public void shouldCreateNewNameAndCodeEditorWhenInitDataForCreateView()
	{
		controller.initDataForCreateView();
		verify(controller).createCodeEditor();
		verify(controller).createNameEditor();
		verify(controller).setCurrentObject(any(CustomThemeModel.class));
		verify(controller).clearCodeAndNameEditor();
	}

	@Test
	public void shouldClearNameAndCodeEditorWhenInitDataForCreateView()
	{
		controller.codeContainer = new Div();
		controller.nameEditorContainer = new Div();
		controller.codeTextbox = new Editor();
		controller.nameEditor = new Editor();
		controller.codeContainer.appendChild(controller.codeTextbox);
		controller.nameEditorContainer.appendChild(controller.nameEditor);

		controller.initDataForCreateView();

		verify(controller).clearCodeAndNameEditor();
		assertThat(controller.codeContainer.getChildren().size()).isEqualTo(0);
		assertThat(controller.nameEditor.getChildren().size()).isEqualTo(0);
	}

	@Test
	public void shouldChangeBaseThemeWhenSelectBaseTheme()
	{
		when(appearanceItemRenderer.isColorChanged()).thenReturn(false);
		doNothing().when(controller).baseThemeChange();

		controller.onBaseThemeSelect();

		verify(controller, never()).showMessagebox(any(), any(), any(), any(), any());
		verify(controller).baseThemeChange();
	}

	@Test
	public void shouldShowMessageToastWhenSelectBaseThemeWithError()
	{
		when(appearanceItemRenderer.isColorChanged()).thenReturn(true);
		doNothing().when(controller).showMessagebox(any(), any(), any(), any(), any());

		controller.onBaseThemeSelect();

		verify(controller).showMessagebox(any(), any(), any(), any(), any());
		verify(controller, never()).baseThemeChange();
	}

	@Test
	public void shouldShowDisplayViewWhenInit()
	{
		controller.initialize(mock(Div.class));
		verify(controller).switchView(ViewMode.DISPLAY);
	}

	@Test
	public void shouldRefreshThemeListAndShowCreateBtnWhenSwitchToDisplayView()
	{
		doNothing().when(controller).refreshThemeList();

		controller.switchView(ViewMode.DISPLAY);

		assertThat(controller.currentViewMode).isEqualTo(ViewMode.DISPLAY);
		verify(controller).refreshThemeList();
		verify(themeDisplayView).setVisible(true);
		verify(createButton).setVisible(true);
		verify(themeEditView).setVisible(false);
		verify(backButton).setVisible(false);
		verify(saveButton).setVisible(false);
		verify(cancelButton).setVisible(false);
	}

	@Test
	public void shouldResetEditViewDataAndShowCorrectBtnsWhenSwitchToEditView()
	{
		doNothing().when(controller).initDataForEditView();

		controller.switchView(ViewMode.EDIT);

		assertThat(controller.currentViewMode).isEqualTo(ViewMode.EDIT);
		verify(controller).initDataForEditView();
		verify(themeDisplayView).setVisible(false);
		verify(createButton).setVisible(false);
		verify(themeEditView).setVisible(true);
		verify(backButton).setVisible(true);
		verify(saveButton).setVisible(true);
		verify(cancelButton).setVisible(true);
		verify(baseThemeCombox).setDisabled(true);
	}

	@Test
	public void shouldResetCreateViewDataAndShowCorrectBtnsWhenSwitchToEditView()
	{
		doNothing().when(controller).initDataForCreateView();

		controller.switchView(ViewMode.CREATE);

		assertThat(controller.currentViewMode).isEqualTo(ViewMode.CREATE);
		verify(controller).initDataForCreateView();
		verify(themeDisplayView).setVisible(false);
		verify(createButton).setVisible(false);
		verify(themeEditView).setVisible(true);
		verify(backButton).setVisible(true);
		verify(saveButton).setVisible(true);
		verify(cancelButton).setVisible(true);
		verify(baseThemeCombox).setDisabled(false);
	}

	@Test
	public void shouldRefreshThemeListWhenInitDataForDisplayView()
	{
		doNothing().when(controller).refreshThemeList();
		controller.currentViewMode = ViewMode.DISPLAY;

		controller.initViewData();

		verify(controller).refreshThemeList();
		verify(controller, never()).initDataForCreateView();
		verify(controller, never()).initDataForEditView();
	}

	@Test
	public void shouldInitDataForEditViewIfCurrentViewIsEdit()
	{
		doNothing().when(controller).initDataForEditView();
		controller.currentViewMode = ViewMode.EDIT;

		controller.initViewData();

		verify(controller).initDataForEditView();
		verify(controller, never()).refreshThemeList();
		verify(controller, never()).initDataForCreateView();
	}

	@Test
	public void shouldInitDataForCreateViewIfCurrentViewIsCreate()
	{
		doNothing().when(controller).initDataForCreateView();
		controller.currentViewMode = ViewMode.CREATE;

		controller.initViewData();

		verify(controller).initDataForCreateView();
		verify(controller, never()).refreshThemeList();
		verify(controller, never()).initDataForEditView();
	}

	@Test
	public void shouldShowEmptyListViewIfNoThemeExist()
	{
		final List<CustomThemeModel> customThemes = Collections.emptyList();
		when(backofficeThemeService.getCustomThemes()).thenReturn(customThemes);
		when(customThemesModel.isEmpty()).thenReturn(true);

		controller.refreshThemeList();

		verify(customThemesModel).clear();
		verify(customThemesModel).addAll(customThemes);
		verify(emptyListView).setVisible(true);
	}

	@Test
	public void shouldNotShowEmptyListViewIfCustomThemeExist()
	{
		final List<CustomThemeModel> customThemes = Arrays.asList(mock(CustomThemeModel.class));
		when(backofficeThemeService.getCustomThemes()).thenReturn(customThemes);
		when(customThemesModel.isEmpty()).thenReturn(false);

		controller.refreshThemeList();

		verify(customThemesModel).clear();
		verify(customThemesModel).addAll(customThemes);
		verify(emptyListView).setVisible(false);
	}

	@Test
	public void shouldInitCorrectDataWhenInitDataForCreateView()
	{
		final var defaultTheme = mock(CustomThemeModel.class);
		final var style = mock(MediaModel.class);
		when(defaultTheme.getStyle()).thenReturn(style);
		when(backofficeThemeService.getDefaultTheme()).thenReturn(defaultTheme);
		when(appearanceItemRenderer.resetAppearanceItemColor(style)).thenReturn(true);

		controller.initDataForCreateView();

		verify(baseThemesModel, times(1)).clearSelection();
		verify(baseThemesModel).addToSelection(defaultTheme);
		verify(controller).enableSave(false);
	}

	@Test
	public void shouldShowMessageBoxIfBaseThemeStyleMissing()
	{
		final var defaultTheme = mock(CustomThemeModel.class);
		final var style = mock(MediaModel.class);
		when(defaultTheme.getStyle()).thenReturn(style);
		when(backofficeThemeService.getDefaultTheme()).thenReturn(defaultTheme);
		when(appearanceItemRenderer.resetAppearanceItemColor(style)).thenReturn(false);

		try (final MockedStatic<Messagebox> messageboxMock = mockStatic(Messagebox.class))
		{
			controller.initDataForCreateView();
			messageboxMock.verify(() -> Messagebox.show(any(), any(), eq(Messagebox.OK), eq(Messagebox.EXCLAMATION)));
			verify(baseThemesModel, times(2)).clearSelection();
			verify(baseThemesModel).addToSelection(defaultTheme);
			verify(controller).enableSave(false);
		}
	}

	@Test
	public void shouldInitCorrectDataWhenInitDataForEditView()
	{
		final var style = mock(MediaModel.class);
		final var mockBaseTheme = mock(ThemeModel.class);
		when(currentEditTheme.getStyle()).thenReturn(style);
		when(currentEditTheme.getBase()).thenReturn(mockBaseTheme);
		when(appearanceItemRenderer.resetAppearanceItemColor(style)).thenReturn(true);

		controller.initDataForEditView();

		verify(baseThemesModel, times(1)).clearSelection();
		verify(baseThemesModel).addToSelection(mockBaseTheme);
		verify(controller).enableSave(false);
	}

	@Test
	public void shouldShowMessageBoxIfThemeNotExistWhenInitDataForEditView()
	{
		when(modelService.isRemoved(currentEditTheme)).thenReturn(true);
		doNothing().when(controller).showMessagebox(any(), any(), any(), any());

		controller.initDataForEditView();

		verify(controller).showMessagebox(any(), any(), any(), any());
	}

	@Test
	public void shouldShowMessageBoxIfEditThemeStyleMissing()
	{
		final var style = mock(MediaModel.class);
		final var mockBaseTheme = mock(ThemeModel.class);
		when(currentEditTheme.getStyle()).thenReturn(style);
		when(currentEditTheme.getBase()).thenReturn(mockBaseTheme);
		when(appearanceItemRenderer.resetAppearanceItemColor(style)).thenReturn(false);
		doNothing().when(controller).showMessagebox(any(), any(), any(), any());

		try (final MockedStatic<Messagebox> messageboxMock = mockStatic(Messagebox.class))
		{
			controller.initDataForEditView();
			verify(controller).showMessagebox(any(), any(), any(), any());
			verify(baseThemesModel).clearSelection();
			verify(baseThemesModel).addToSelection(mockBaseTheme);
			verify(controller).enableSave(false);
		}
	}

	@Test
	public void shouldEnableSaveAndCancelBtns()
	{
		controller.enableSave(true);

		verify(saveButton).setDisabled(false);
		verify(cancelButton).setDisabled(false);
	}

	@Test
	public void shouldDisableSaveAndCancelBtns()
	{
		controller.enableSave(false);

		verify(saveButton).setDisabled(true);
		verify(cancelButton).setDisabled(true);
	}

	@Test
	public void shoulEnableSaveWhenGeneralDataChanged()
	{
		controller.onGeneralDataChange();

		verify(controller).enableSave(true);
	}

	@Test
	public void shouldShowMsgBoxIfColorChangedWhenSwitchBaseTheme()
	{
		doNothing().when(controller).showMessagebox(any(), any(), any(), any(), any());
		when(appearanceItemRenderer.isColorChanged()).thenReturn(true);
		controller.onBaseThemeSelect();
		verify(controller).showMessagebox(any(), any(), any(), any(), any());
	}

	@Test
	public void shouldShowMsgBoxIfBaseThemeStyleMissingWhenSwitchBaseTheme()
	{
		final var selectedBaseTheme = mock(ThemeModel.class);
		final var style = mock(MediaModel.class);
		final var selectedItem = mock(Comboitem.class);
		when(selectedBaseTheme.getStyle()).thenReturn(style);
		when(selectedItem.getValue()).thenReturn(selectedBaseTheme);
		when(baseThemeCombox.getSelectedItem()).thenReturn(selectedItem);
		when(appearanceItemRenderer.resetAppearanceItemColor(style)).thenReturn(false);
		try (final MockedStatic<Messagebox> messageboxMock = mockStatic(Messagebox.class))
		{
			controller.baseThemeChange();
			messageboxMock.verify(() -> Messagebox.show(any(), any(), eq(Messagebox.OK), eq(Messagebox.EXCLAMATION)));
			verify(baseThemesModel).clearSelection();
		}
	}

	@Test
	public void shouldEnableSaveWhenSwitchBaseTheme()
	{
		final var selectedBaseTheme = mock(ThemeModel.class);
		final var style = mock(MediaModel.class);
		final var selectedItem = mock(Comboitem.class);
		when(selectedBaseTheme.getStyle()).thenReturn(style);
		when(selectedItem.getValue()).thenReturn(selectedBaseTheme);
		when(baseThemeCombox.getSelectedItem()).thenReturn(selectedItem);
		when(appearanceItemRenderer.resetAppearanceItemColor(style)).thenReturn(true);

		controller.baseThemeChange();

		verify(controller).enableSave(true);
	}

	@Test
	public void shouldShowMsgBoxIfExceedMaximumWhenClickCreateBtn()
	{
		final var themelist = mock(List.class);
		when(themelist.size()).thenReturn(10);
		when(backofficeThemeService.getCustomThemes()).thenReturn(themelist);
		when(backofficeThemeService.getMaximumOfCustomTheme()).thenReturn(10);
		doNothing().when(controller).showMessagebox(any(), any(), any(), any(), any());

		executeViewEvent(CREATE_BUTTON, Events.ON_CLICK);

		verify(controller).showMessagebox(any(), any(), any(), any(), any());
	}

	@Test
	public void shouldSwitchToCreateViewWhenClickCreateBtnAndNotExceedMaximum()
	{
		final var themelist = mock(List.class);
		when(themelist.size()).thenReturn(9);
		when(backofficeThemeService.getCustomThemes()).thenReturn(themelist);
		when(backofficeThemeService.getMaximumOfCustomTheme()).thenReturn(10);

		executeViewEvent(CREATE_BUTTON, Events.ON_CLICK);

		verify(controller).switchView(ViewMode.CREATE);
	}

	@Test
	public void shouldShowConfirmMsgIfDataChangedWhenClickBackBtn()
	{
		when(saveButton.isDisabled()).thenReturn(false);
		doNothing().when(controller).showMessagebox(any(), any(), any(), any());
		executeViewEvent(BACK_BUTTON, Events.ON_CLICK);

		verify(controller).showMessagebox(any(), any(), any(), any());
		verify(controller, never()).switchView(any());
	}

	@Test
	public void shouldSwitchToDisplayViewIfNoDataChangedWhenClickBackBtn()
	{
		doNothing().when(controller).switchView(any());
		when(saveButton.isDisabled()).thenReturn(true);
		executeViewEvent(BACK_BUTTON, Events.ON_CLICK);

		verify(controller, never()).showMessagebox(any(), any(), any(), any());
		verify(controller).switchView(ViewMode.DISPLAY);
	}

	@Test
	public void shouldShowMsgBoxWhenClickCancelBtn()
	{
		doNothing().when(controller).showMessagebox(any(), any(), any(), any());
		executeViewEvent(CANCEL_BUTTON, Events.ON_CLICK);

		verify(controller).showMessagebox(any(), any(), any(), any());
	}

	@Test
	public void shouldShowMsgBoxIfThemeNotExistWhenClickEditBtn()
	{
		final var theme = mock(CustomThemeModel.class);
		when(modelService.isRemoved(theme)).thenReturn(true);
		doNothing().when(controller).showMessagebox(any(), any(), any(), any());

		controller.onThemeEdit(theme);

		verify(controller).showMessagebox(any(), any(), any(), any());
		verify(controller, never()).switchView(any());
	}

	@Test
	public void shouldShowMsgBoxIfStyleAndBaseThemeStyleMissingWhenClickEditBtn()
	{
		final var theme = mock(CustomThemeModel.class);
		final var baseTheme = mock(CustomThemeModel.class);
		final var style = mock(MediaModel.class);
		final var baseStyle = mock(MediaModel.class);

		when(theme.getStyle()).thenReturn(style);
		when(theme.getBase()).thenReturn(baseTheme);
		when(baseTheme.getStyle()).thenReturn(baseStyle);
		when(modelService.isRemoved(theme)).thenReturn(false);
		doReturn(false).when(controller).isValidThemeStyle(style);
		doReturn(false).when(controller).isValidThemeStyle(baseStyle);

		try (final MockedStatic<Messagebox> messageboxMock = mockStatic(Messagebox.class))
		{
			controller.onThemeEdit(theme);
			messageboxMock.verify(() -> Messagebox.show(any(), any(), eq(Messagebox.OK), eq(Messagebox.EXCLAMATION)));
			verify(controller, never()).switchView(any());
		}
	}

	@Test
	public void shouldSwitchEditViewWhenClickEditBtn()
	{
		final var theme = mock(CustomThemeModel.class);
		final var baseTheme = mock(CustomThemeModel.class);
		final var style = mock(MediaModel.class);
		final var baseStyle = mock(MediaModel.class);
		when(theme.getStyle()).thenReturn(style);
		when(theme.getBase()).thenReturn(baseTheme);
		when(modelService.isRemoved(theme)).thenReturn(false);
		doReturn(true).when(controller).isValidThemeStyle(style);

		controller.onThemeEdit(theme);

		verify(controller).switchView(ViewMode.EDIT);
	}

	@Test
	public void shouldShowMsgBoxWhenClickDeleteBtn()
	{
		doNothing().when(controller).showMessagebox(any(), any(), any(), any());

		controller.onThemeDelete(mock(CustomThemeModel.class));

		verify(controller).showMessagebox(any(), any(), any(), any());
	}

	@Test
	public void shouldNotCallServiceToRemoveThemeIfThemeNotExist()
	{
		final var theme = mock(CustomThemeModel.class);
		when(modelService.isRemoved(theme)).thenReturn(true);

		controller.deleteTheme(theme);

		verify(modelService, never()).remove(theme);
		verify(controller).sendOutput(SOCKET_OUTPUT_CUSTOM_THEME_CHANGED, null);
		verify(notificationService).notifyUser(any(String.class), any(String.class), eq(NotificationEvent.Level.SUCCESS));
		verify(controller).refreshThemeList();
	}

	@Test
	public void shouldCallServiceToRemoveThemeIfThemeExist()
	{
		final var theme = mock(CustomThemeModel.class);
		when(modelService.isRemoved(theme)).thenReturn(false);

		controller.deleteTheme(theme);

		verify(modelService).remove(theme);
		verify(controller).sendOutput(SOCKET_OUTPUT_CUSTOM_THEME_CHANGED, null);
		verify(notificationService).notifyUser(any(String.class), any(String.class), eq(NotificationEvent.Level.SUCCESS));
		verify(controller).refreshThemeList();
	}

	@Test
	public void shouldNotCreateThemeIfCodeIsEmpty()
	{
		final var codeEditor = mock(Editor.class);
		when(codeEditor.getValue()).thenReturn("");
		controller.currentViewMode = ViewMode.CREATE;
		controller.codeTextbox = codeEditor;
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			controller.onSave(mock(Event.class));
			verify(notificationService).notifyUser(NOTIFICATION_AREA, "customThemeCreateInValid", NotificationEvent.Level.FAILURE);
			clientsMock.verify(Clients::clearBusy);
			verify(controller, never()).createTheme(any(), any(), any(), any());
		}
	}

	@Test
	public void shouldNotCreateThemeIfBaseThemeEmpty()
	{
		final var codeEditor = mock(Editor.class);
		controller.codeTextbox = codeEditor;
		when(codeEditor.getValue()).thenReturn("myTheme");
		controller.currentViewMode = ViewMode.CREATE;
		when(appearanceItemRenderer.getStyleVariableMap()).thenReturn(Collections.emptyMap());
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			controller.onSave(mock(Event.class));
			verify(notificationService).notifyUser(NOTIFICATION_AREA, "customThemeCreateInValid", NotificationEvent.Level.FAILURE);
			clientsMock.verify(Clients::clearBusy);
			verify(controller, never()).createTheme(any(), any(), any(), any());
		}
	}

	@Test
	public void shouldNotUpdateThemeIfStyleMissing()
	{
		final var codeEditor = mock(Editor.class);
		controller.codeTextbox = codeEditor;
		when(codeEditor.getValue()).thenReturn("myTheme");
		controller.currentViewMode = ViewMode.EDIT;
		when(appearanceItemRenderer.getStyleVariableMap()).thenReturn(Collections.emptyMap());
		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			controller.onSave(mock(Event.class));
			verify(notificationService).notifyUser(NOTIFICATION_AREA, "customThemeEditInValid", NotificationEvent.Level.FAILURE);
			clientsMock.verify(Clients::clearBusy);
			verify(controller, never()).updateTheme(any(), any(), any());
		}
	}

	@Test
	public void shouldSwitchDisplayIfCreateThemeSuccess()
	{
		final var codeEditor = mock(Editor.class);
		final var nameEditor = mock(Editor.class);
		final var nameValue = mock(HashMap.class);
		final var styleMap = mock(Map.class);
		final var event = mock(Event.class);
		final var styleInputStream = mock(InputStream.class);
		final var previewImgData = "previewImgData";
		final var code = "myThemeCode";
		controller.codeTextbox = codeEditor;
		controller.nameEditor = nameEditor;
		when(event.getData()).thenReturn(previewImgData);
		when(appearanceItemRenderer.getStyleVariableMap()).thenReturn(styleMap);
		when(customThemeUtil.mapToStyleInputStream(styleMap)).thenReturn(styleInputStream);
		when(styleMap.isEmpty()).thenReturn(false);
		when(codeEditor.getValue()).thenReturn(code);
		when(nameEditor.getValue()).thenReturn(nameValue);
		when(appearanceItemRenderer.getStyleVariableMap()).thenReturn(styleMap);
		doReturn(true).when(controller).createTheme(any(), any(), any(), any());
		when(controller.createTheme(any(), any(), any(), any())).thenReturn(true);
		controller.currentViewMode = ViewMode.CREATE;

		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			controller.onSave(event);

			verify(controller).createTheme(code, nameValue, styleInputStream, previewImgData);
			verify(notificationService).notifyUser(NOTIFICATION_AREA, NOTIFICATION_TYPE_CUSTOM_THEMES_CREATED,
					NotificationEvent.Level.SUCCESS);
			verify(controller).sendOutput(SOCKET_OUTPUT_CUSTOM_THEME_CHANGED, null);
			verify(controller).switchView(ViewMode.DISPLAY);
			clientsMock.verify(Clients::clearBusy);
		}
	}

	@Test
	public void shouldSwitchDisplayIfUpdateThemeSuccess()
	{
		final var codeEditor = mock(Editor.class);
		final var nameEditor = mock(Editor.class);
		final var nameValue = mock(HashMap.class);
		final var styleMap = mock(Map.class);
		final var event = mock(Event.class);
		final var styleInputStream = mock(InputStream.class);
		final var previewImgData = "previewImgData";
		final var code = "myThemeCode";
		controller.codeTextbox = codeEditor;
		controller.nameEditor = nameEditor;
		when(event.getData()).thenReturn(previewImgData);
		when(appearanceItemRenderer.getStyleVariableMap()).thenReturn(styleMap);
		when(customThemeUtil.mapToStyleInputStream(styleMap)).thenReturn(styleInputStream);
		when(styleMap.isEmpty()).thenReturn(false);
		when(codeEditor.getValue()).thenReturn(code);
		when(nameEditor.getValue()).thenReturn(nameValue);
		when(appearanceItemRenderer.getStyleVariableMap()).thenReturn(styleMap);
		doReturn(true).when(controller).updateTheme(any(), any(), any());
		controller.currentViewMode = ViewMode.EDIT;

		try (final MockedStatic<Clients> clientsMock = mockStatic(Clients.class))
		{
			controller.onSave(event);

			verify(controller).updateTheme(nameValue, styleInputStream, previewImgData);
			verify(notificationService).notifyUser(NOTIFICATION_AREA, NOTIFICATION_TYPE_CUSTOM_THEMES_CHANGED,
					NotificationEvent.Level.SUCCESS);
			verify(controller).sendOutput(SOCKET_OUTPUT_CUSTOM_THEME_CHANGED, null);
			verify(controller).switchView(ViewMode.DISPLAY);
			clientsMock.verify(Clients::clearBusy);
		}
	}

	@Test
	public void shouldReturnFalseAndShowErrorMsgIfCreateThemeFailure()
	{
		final var mockThemeSaver = mock(ThemeSaveHelper.ThemeSaver.class);
		when(themeSaveHelper.setTheme(null)).thenReturn(null);

		final var isSuccess = controller.createTheme("code", mock(HashMap.class), mock(InputStream.class), "previewImage");

		assertThat(isSuccess).isFalse();
		verify(notificationService).notifyUser(eq(NOTIFICATION_AREA), eq(NOTIFICATION_TYPE_CUSTOM_THEMES_CREATED),
				eq(NotificationEvent.Level.FAILURE), any(Exception.class));
	}

	@Test
	public void shouldReturnTrueIfCreateThemeSuccess()
	{
		final var mockThemeSaver = mock(ThemeSaveHelper.ThemeSaver.class);
		when(themeSaveHelper.setTheme(null)).thenReturn(mockThemeSaver);
		when(mockThemeSaver.init(any())).thenReturn(mockThemeSaver);
		when(mockThemeSaver.initStyle(any())).thenReturn(mockThemeSaver);
		when(mockThemeSaver.initThumbnail(any())).thenReturn(mockThemeSaver);

		final var isSuccess = controller.createTheme("code", mock(HashMap.class), mock(InputStream.class), "previewImage");

		assertThat(isSuccess).isTrue();
		verify(notificationService, never()).notifyUser(eq(NOTIFICATION_AREA), eq(NOTIFICATION_TYPE_CUSTOM_THEMES_CREATED),
				eq(NotificationEvent.Level.FAILURE), any(Exception.class));
	}

	@Test
	public void shouldReturnFalseAndShowErrorMsgIfEditThemeFailure()
	{
		when(modelService.isRemoved(currentEditTheme)).thenReturn(false);

		final var isSuccess = controller.updateTheme(mock(HashMap.class), mock(InputStream.class), "previewImage");

		assertThat(isSuccess).isFalse();
		verify(notificationService).notifyUser(eq(NOTIFICATION_AREA), eq(NOTIFICATION_TYPE_CUSTOM_THEMES_CHANGED),
				eq(NotificationEvent.Level.FAILURE), any(Exception.class));
	}

	@Test
	public void shouldReturnFalseAndShowMsgBoxIfThemeNotExistWhenEditTheme()
	{
		doNothing().when(controller).showMessagebox(any(), any(), any(), any());
		when(modelService.isRemoved(currentEditTheme)).thenReturn(true);

		final var isSuccess = controller.updateTheme(mock(HashMap.class), mock(InputStream.class), "previewImage");

		assertThat(isSuccess).isFalse();
		verify(controller).showMessagebox(any(), any(), any(), any());
		verify(notificationService, never()).notifyUser(eq(NOTIFICATION_AREA), eq(NOTIFICATION_TYPE_CUSTOM_THEMES_CHANGED),
				eq(NotificationEvent.Level.FAILURE), any(Exception.class));
	}

	@Test
	public void shouldReturnTrueIfEditThemeSuccess()
	{
		final var mockThemeSaver = mock(ThemeSaveHelper.ThemeSaver.class);
		when(themeSaveHelper.setTheme(currentEditTheme)).thenReturn(mockThemeSaver);
		when(mockThemeSaver.init(any())).thenReturn(mockThemeSaver);
		when(mockThemeSaver.initStyle(any())).thenReturn(mockThemeSaver);
		when(mockThemeSaver.initThumbnail(any())).thenReturn(mockThemeSaver);

		final var isSuccess = controller.updateTheme(mock(HashMap.class), mock(InputStream.class), "previewImage");

		assertThat(isSuccess).isTrue();
		verify(notificationService, never()).notifyUser(eq(NOTIFICATION_AREA), eq(NOTIFICATION_TYPE_CUSTOM_THEMES_CHANGED),
				eq(NotificationEvent.Level.FAILURE), any(Exception.class));
	}

	@Override
	protected ThemesController getWidgetController()
	{
		return controller;
	}
}

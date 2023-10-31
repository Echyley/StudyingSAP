package com.hybris.backoffice.personalization.listview;

import static com.hybris.backoffice.personalization.PersonalizationConstants.COMPONENT;
import static com.hybris.backoffice.personalization.PersonalizationConstants.DATA_TYPE_CODE;
import static com.hybris.backoffice.personalization.PersonalizationConstants.LIST_VIEW_MOLD_NAME;
import static com.hybris.backoffice.personalization.PersonalizationConstants.MERGE_MODE_REMOVE;
import static com.hybris.backoffice.personalization.listview.ListViewFlowActionHandler.CTX_TYPE_CODE;
import static com.hybris.backoffice.personalization.listview.ListViewFlowActionHandler.PARAM_WIZARD_CURRENT_CONTEXT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.backoffice.personalization.PersonalizationConstants;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.core.config.UserConfigurationService;
import com.hybris.cockpitng.core.config.impl.DefaultConfigContext;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


@RunWith(MockitoJUnitRunner.class)
public class ListViewFlowActionHandlerTest
{
	@Mock
	private NotificationService notificationService;
	@Mock
	private UserConfigurationService userConfigurationService;
	@Mock
	private FlowActionHandlerAdapter adapter;

	@InjectMocks
	@Spy
	private ListViewFlowActionHandler handler;

	@Spy
	private WidgetInstanceManager wim;
	private AttributeChooserForm form;
	private Map<String, Object> context;

	private ListView savedColumnConfig;
	private ListView columnConfig;
	private ListColumn codeColumn;
	private ListColumn nameColumn;

	private Attribute pk;
	private Attribute code;
	private Attribute name;
	private Attribute des;

	@Before
	public void setUp()
	{
		pk = createAttribute(ProductModel.PK);
		code = createAttribute(ProductModel.CODE);
		name = createAttribute(ProductModel.NAME);
		des = createAttribute(ProductModel.DESCRIPTION);

		savedColumnConfig = new ListView();
		columnConfig = new ListView();
		codeColumn = createListColumn(ProductModel.CODE);
		nameColumn = createListColumn(ProductModel.NAME);

		context = new HashMap<>();
		context.put(CTX_TYPE_CODE, ProductModel._TYPECODE);
		context.put(COMPONENT, LIST_VIEW_MOLD_NAME);
		context.put(PersonalizationConstants.LIST_VIEW_COLUMN_CONFIG, columnConfig);

		form = new AttributeChooserForm();
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		wim.getModel().setValue(PersonalizationConstants.PARAM_ATTRIBUTES_FORM_PROPERTY, form);
		wim.getModel().setValue(PARAM_WIZARD_CURRENT_CONTEXT, context);
		when(adapter.getWidgetInstanceManager()).thenReturn(wim);
		when(userConfigurationService.loadUserCurrentTypeConfiguration(
				new DefaultConfigContext(LIST_VIEW_MOLD_NAME, ProductModel._TYPECODE), ListView.class)).thenReturn(savedColumnConfig);
	}

	@Test
	public void shouldNotifyUserAboutMissingForm()
	{
		// given
		wim.getModel().setValue(PersonalizationConstants.PARAM_ATTRIBUTES_FORM_PROPERTY, null);

		// when
		handler.perform(null, adapter, null);

		// then
		verify(notificationService).notifyUser(PersonalizationConstants.NOTIFICATION_SOURCE_PERSONALIZATION,
				PersonalizationConstants.NOTIFICATION_EVENT_TYPE_CONFIGURATION_ERROR, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldNotifyUserIfNoAttributesSelected()
	{
		// when
		handler.perform(null, adapter, null);

		// then
		verify(notificationService).notifyUser(PersonalizationConstants.NOTIFICATION_SOURCE_PERSONALIZATION,
				PersonalizationConstants.NOTIFICATION_EVENT_TYPE_MISSING_ATTRIBUTES, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldSaveNewlySelectedAttributesWithCorrectOrderForUserConfiguration()
	{
		// given
		final var chosenAttributes = Arrays.asList(pk, code, name, des);
		form.setChosenAttributes(new LinkedHashSet<>(chosenAttributes));

		// when
		handler.perform(null, adapter, null);

		// then
		verify(userConfigurationService).storeUserConfiguration(any(), argThat(listView -> {
			final var columns = ((ListView) listView).getColumn();
			return columns.size() == 4 && isAttributesMatched(chosenAttributes, ((ListView) listView).getColumn());
		}));
	}

	@Test
	public void shouldSaveRemovedAttributesWithRemoveMergeModeForUserConfiguration()
	{
		// given
		columnConfig.getColumn().addAll(Arrays.asList(codeColumn, nameColumn));
		final var chosenAttributes = Arrays.asList(des, pk, name);
		form.setChosenAttributes(new LinkedHashSet<>(chosenAttributes));

		// when
		handler.perform(null, adapter, null);

		// then
		verify(userConfigurationService).storeUserConfiguration(any(), argThat(listView -> {
			final var removedColumn = ((ListView) listView).getColumn().stream()
					.filter(column -> MERGE_MODE_REMOVE.equals(column.getMergeMode())).map(column -> column.getQualifier())
					.collect(Collectors.toSet());
			return removedColumn.size() == 1 && removedColumn.contains(ProductModel.CODE);
		}));
	}

	@Test
	public void shouldKeepSavedRemovedAttributesWithRemoveMergeModeForUserConfiguration()
	{
		// given
		final var removedApprovelColumn = createListColumn(ProductModel.APPROVALSTATUS);
		when(removedApprovelColumn.getMergeMode()).thenReturn(MERGE_MODE_REMOVE);
		savedColumnConfig.getColumn().add(removedApprovelColumn);
		columnConfig.getColumn().addAll(Arrays.asList(codeColumn, nameColumn));
		final var chosenAttributes = Arrays.asList(des, pk, name);
		form.setChosenAttributes(new LinkedHashSet<>(chosenAttributes));

		// when
		handler.perform(null, adapter, null);

		// then
		verify(userConfigurationService).storeUserConfiguration(any(), argThat(listView -> {
			final var removedColumn = ((ListView) listView).getColumn().stream()
					.filter(column -> MERGE_MODE_REMOVE.equals(column.getMergeMode())).map(column -> column.getQualifier())
					.collect(Collectors.toSet());

			return removedColumn.size() == 2
					&& removedColumn.containsAll(Arrays.asList(ProductModel.APPROVALSTATUS, ProductModel.CODE));
		}));
	}

	@Test
	public void shouldSaveCustomAttributesWithCorrectDataWhenSaveUserConfiguration()
	{
		// given
		final String preview = "productPreview";
		final String status = "productStatus";
		final var previewColumn = createListColumn(preview);
		final var statusColumn = createListColumn(status);
		final var previewAttr = new ListViewAttribute(preview, preview, true, previewColumn);
		final var statusAttr = new ListViewAttribute(status, status, true, statusColumn);
		columnConfig.getColumn().addAll(Arrays.asList(previewColumn, statusColumn));

		final var chosenAttributes = Arrays.asList(code, statusAttr, previewAttr, name);
		form.setChosenAttributes(new LinkedHashSet<>(chosenAttributes));

		// when
		handler.perform(null, adapter, null);

		// then
		verify(userConfigurationService).storeUserConfiguration(any(), argThat(listView -> {
			final var columns = ((ListView) listView).getColumn();

			return columns.size() == 4 && isAttributesMatched(chosenAttributes, columns);
		}));
	}

	@Test
	public void shouldNotifyUserIfSavedUserConfigurationSuccess()
	{
		// given
		final var chosenAttributes = Arrays.asList(des, pk, name);
		form.setChosenAttributes(new LinkedHashSet<>(chosenAttributes));

		// when
		handler.perform(null, adapter, null);

		// then
		verify(adapter).done();
		verify(notificationService).notifyUser(PersonalizationConstants.NOTIFICATION_SOURCE_PERSONALIZATION,
				PersonalizationConstants.NOTIFICATION_EVENT_TYPE_PERSONALIZATION_SAVED, NotificationEvent.Level.SUCCESS);
	}

	@Test
	public void shouldNotifyUserIfSavedUserConfigurationFailure()
	{
		// given
		final var chosenAttributes = Arrays.asList(des, pk, name);
		form.setChosenAttributes(new LinkedHashSet<>(chosenAttributes));
		doThrow(ModelSavingException.class).when(userConfigurationService).storeUserConfiguration(any(), any());

		// when
		handler.perform(null, adapter, null);

		// then
		verify(notificationService).notifyUser(PersonalizationConstants.NOTIFICATION_SOURCE_PERSONALIZATION,
				PersonalizationConstants.NOTIFICATION_EVENT_TYPE_PERSONALIZATION_SAVED, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldNotifyListViewToUpdateColumnConfigAfterSaveSuccess()
	{
		// given
		columnConfig.getColumn().addAll(Arrays.asList(codeColumn, nameColumn));
		final var chosenAttributes = Arrays.asList(des, pk, name);
		form.setChosenAttributes(new LinkedHashSet<>(chosenAttributes));

		// when
		handler.perform(null, adapter, null);

		// then
		verify(wim).sendOutput(eq(PersonalizationConstants.SOCKET_OUT_WIZARD_RESULT), argThat(map -> {
			final var updatedColumnConfig = (ListView) ((Map) map).get(PersonalizationConstants.LIST_VIEW_COLUMN_CONFIG);
			return ((Map) map).get(COMPONENT).equals(LIST_VIEW_MOLD_NAME)
					&& ((Map) map).get(DATA_TYPE_CODE).equals(ProductModel._TYPECODE)
					&& isAttributesMatched(chosenAttributes, updatedColumnConfig.getColumn());
		}));
	}

	private boolean isAttributesMatched(final List<Attribute> attributes, final List<ListColumn> columns)
	{
		int pos = 0;
		for (final var column : columns)
		{
			if (!column.getPosition().equals(BigInteger.valueOf(pos + 1))
					|| !column.getQualifier().equals(attributes.get(pos).getQualifier()))
			{
				return false;
			}
			if (attributes.get(pos) instanceof ListViewAttribute)
			{
				if (column != ((ListViewAttribute) attributes.get(pos)).getListColumn())
				{
					return false;
				}
			}
			pos++;
		}
		return true;
	}

	private Attribute createAttribute(final String qualifier)
	{
		return new Attribute(qualifier, qualifier, false);
	}

	private ListColumn createListColumn(final String qualifier)
	{
		final var column = spy(new ListColumn());
		column.setQualifier(qualifier);
		return column;
	}

}

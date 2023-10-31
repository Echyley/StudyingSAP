package com.hybris.backoffice.personalization.listview.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

import com.hybris.backoffice.attributechooser.Attribute;
import com.hybris.backoffice.attributechooser.AttributeChooserForm;
import com.hybris.backoffice.attributechooser.AttributesChooserConfig;
import com.hybris.backoffice.personalization.PersonalizationConstants;
import com.hybris.backoffice.personalization.listview.ListViewAttribute;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListColumn;
import com.hybris.cockpitng.core.config.impl.jaxb.listview.ListView;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;


@RunWith(MockitoJUnitRunner.class)
public class FlowCustomViewRendererTest
{
	@Mock
	private WidgetComponentRenderer<Component, AttributesChooserConfig, AttributeChooserForm> attributeChooserRenderer;
	@Mock
	private CockpitLocaleService localeService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private NotificationService notificationService;

	@InjectMocks
	@Spy
	private FlowCustomViewRenderer renderer;

	private Component component = new Div();;
	private AttributeChooserForm form;
	private WidgetInstanceManager wim;
	private Locale currentLocale = Locale.forLanguageTag("en");

	private DataAttribute pk;
	private DataAttribute code;
	private DataAttribute name;
	private DataAttribute des;

	private ListView columnConfig;
	private ListColumn codeColumn;
	private ListColumn nameColumn;
	private ListColumn previewColumn;
	private ListColumn statusColumn;
	private String preview = "productPreview";
	private String status = "productStatus";

	@Before
	public void setUp()
	{
		pk = mockDataAttribute(ProductModel.PK);
		code = mockDataAttribute(ProductModel.CODE);
		name = mockDataAttribute(ProductModel.NAME);
		des = mockDataAttribute(ProductModel.DESCRIPTION);

		columnConfig = new ListView();
		codeColumn = mockListColumn(ProductModel.CODE);
		nameColumn = mockListColumn(ProductModel.NAME);
		previewColumn = mockListColumn(preview);
		statusColumn = mockListColumn(status);

		form = new AttributeChooserForm();
		wim = CockpitTestUtil.mockWidgetInstanceManager();
		wim.getModel().setValue(PersonalizationConstants.PARAM_ATTRIBUTES_FORM_PROPERTY, form);
		wim.getModel().setValue(PersonalizationConstants.LIST_VIEW_COLUMN_CONFIG, columnConfig);

		when(localeService.getCurrentLocale()).thenReturn(currentLocale);
		when(permissionFacade.canReadProperty(eq(ProductModel._TYPECODE), any())).thenReturn(true);
	}

	@Test
	public void shouldNotifyUserAboutMissingForm()
	{
		// given
		wim.getModel().setValue(PersonalizationConstants.PARAM_ATTRIBUTES_FORM_PROPERTY, null);

		// when
		renderer.render(null, null, new HashMap<>(), null, wim);

		// then
		verify(notificationService).notifyUser(PersonalizationConstants.NOTIFICATION_SOURCE_PERSONALIZATION,
				PersonalizationConstants.NOTIFICATION_EVENT_TYPE_CONFIGURATION_ERROR, NotificationEvent.Level.FAILURE);
	}

	@Test
	public void shouldRenderAttributeChooserWithCorrectParams()
	{
		// given
		final var dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Arrays.asList(name, pk, code));

		// when
		renderer.render(component, null, new HashMap<>(), dataType, wim);

		// then
		verify(attributeChooserRenderer).render(eq(component), argThat(config -> !config.isIncludeAllSupported()),
				argThat(form -> form.getAvailableAttributes().size() == 3 && form.getChosenAttributes().isEmpty()), eq(dataType),
				eq(wim));
	}

	@Test
	public void shouldSetCorrectAvailableAttributesAndSelectedAttributesToForm()
	{
		// given
		final var dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Arrays.asList(name, pk, code, des));
		columnConfig.getColumn().addAll(Arrays.asList(nameColumn, codeColumn));

		// when
		renderer.render(component, null, new HashMap<>(), dataType, wim);

		// then
		final var availableSet = form.getAvailableAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		final var selectedSet = form.getSelectedAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		assertThat(availableSet.size()).isEqualTo(2);
		assertThat(selectedSet.size()).isEqualTo(2);
		assertThat(availableSet.containsAll(Arrays.asList(ProductModel.PK, ProductModel.DESCRIPTION))).isTrue();
		assertThat(selectedSet.containsAll(Arrays.asList(ProductModel.CODE, ProductModel.NAME))).isTrue();
	}

	@Test
	public void shouldFilterNoReadPermissionAttributesInAvailableAttributes()
	{
		// given
		final var dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Arrays.asList(name, pk, code, des));
		when(permissionFacade.canReadProperty(ProductModel._TYPECODE, ProductModel.PK)).thenReturn(false);
		when(permissionFacade.canReadProperty(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(false);

		// when
		renderer.render(component, null, new HashMap<>(), dataType, wim);

		// then
		final var availableSet = form.getAvailableAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		final var selectedSet = form.getSelectedAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		assertThat(availableSet.size()).isEqualTo(2);
		assertThat(selectedSet.size()).isZero();
		assertThat(availableSet.containsAll(Arrays.asList(ProductModel.NAME, ProductModel.DESCRIPTION))).isTrue();
	}

	@Test
	public void shouldFilterNotSearchableAttributesInAvailableAttributes()
	{
		// given
		final var dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Arrays.asList(name, pk, code, des));
		when(pk.isSearchable()).thenReturn(false);
		when(code.isSearchable()).thenReturn(false);

		// when
		renderer.render(component, null, new HashMap<>(), dataType, wim);

		// then
		final var availableSet = form.getAvailableAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		assertThat(availableSet.size()).isEqualTo(2);
		assertThat(availableSet.containsAll(Arrays.asList(ProductModel.NAME, ProductModel.DESCRIPTION))).isTrue();
	}

	@Test
	public void shouldFilterLocalizedAttributesIfNoReadableLocalesForCurrentUser()
	{
		// given
		final var dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Arrays.asList(name, pk, code, des));
		when(name.isLocalized()).thenReturn(true);
		when(permissionFacade.getEnabledReadableLocalesForCurrentUser()).thenReturn(null);

		// when
		renderer.render(component, null, new HashMap<>(), dataType, wim);

		// then
		final var availableSet = form.getAvailableAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		assertThat(availableSet.size()).isEqualTo(3);
		assertThat(availableSet.containsAll(Arrays.asList(ProductModel.PK, ProductModel.CODE, ProductModel.DESCRIPTION))).isTrue();
	}

	@Test
	public void shouldAddMandatoryAttributesInSelectedAttributesIfIsCustomAttributes()
	{
		// given
		final var dataType = mockDataTypeWithAttributes(ProductModel._TYPECODE, Arrays.asList(name, code));
		columnConfig.getColumn().addAll(Arrays.asList(previewColumn, statusColumn));

		// when
		renderer.render(component, null, new HashMap<>(), dataType, wim);

		// then
		final var availableSet = form.getAvailableAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		final var selectedSet = form.getSelectedAttributes().stream().map(Attribute::getQualifier).collect(Collectors.toSet());
		assertThat(availableSet.size()).isEqualTo(2);
		assertThat(selectedSet.size()).isEqualTo(2);
		assertThat(availableSet.containsAll(Arrays.asList(ProductModel.CODE, ProductModel.NAME))).isTrue();
		assertThat(selectedSet.containsAll(Arrays.asList(preview, status))).isTrue();
		form.getSelectedAttributes().stream().forEach(attribute -> {
			final var column = attribute.getQualifier().equals(preview) ? previewColumn : statusColumn;
			assertThat(attribute).isInstanceOf(ListViewAttribute.class);
			assertThat(attribute.getQualifier()).isEqualTo(column.getQualifier());
			assertThat(attribute.isMandatory()).isTrue();
			assertThat(((ListViewAttribute) attribute).getListColumn()).isEqualTo(column);
		});
	}

	private DataType mockDataTypeWithAttributes(final String typeCode, final Collection<DataAttribute> attributes)
	{
		final DataType dataType = mock(DataType.class);
		when(dataType.getCode()).thenReturn(typeCode);
		when(dataType.getAttributes()).thenReturn(attributes);
		attributes.forEach(attr -> when(dataType.getAttribute(attr.getQualifier())).thenReturn(attr));
		return dataType;
	}

	private DataAttribute mockDataAttribute(final String attribute)
	{
		final var da = mock(DataAttribute.class);
		when(da.getQualifier()).thenReturn(attribute);
		when(da.isSearchable()).thenReturn(true);
		when(da.getLabel(currentLocale)).thenReturn(attribute);
		return da;
	}

	private ListColumn mockListColumn(final String attribute)
	{
		final var column = mock(ListColumn.class);
		when(column.getQualifier()).thenReturn(attribute);
		when(column.getLabel()).thenReturn(attribute);
		return column;
	}

}

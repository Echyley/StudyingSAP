/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.classification.comparator.ClassificationClassComparator;
import de.hybris.platform.platformbackoffice.classification.comparator.FeatureComparator;
import de.hybris.platform.platformbackoffice.classification.provider.ClassificationSectionNameProvider;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Div;
import org.zkoss.zul.Tabpanel;

import com.google.common.collect.Lists;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.AbstractTab;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomTab;
import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.Parameter;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.i18n.CockpitLocaleService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.widgets.baseeditorarea.DefaultEditorAreaController;
import com.hybris.cockpitng.widgets.editorarea.renderer.EditorAreaRendererUtils;
import com.hybris.cockpitng.widgets.editorarea.renderer.impl.DefaultEditorAreaRenderer;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationTabEditorAreaRendererTest
{
	@Mock
	private ClassificationService classificationService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private UserService userService;
	@Mock
	private ClassificationClassComparator classificationClassComparator;
	@Mock
	private FeatureComparator featureComparator;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private FeaturePeristanceHandler featurePeristanceHandler;
	@Mock
	private CockpitLocaleService cockpitLocaleService;
	@Mock
	private transient PermissionFacade permissionFacade;
	@InjectMocks
	@Spy
	private ClassificationTabEditorAreaRendererUnderTest renderer;

	private AbstractTab abstractTab;
	private DataType dataType;
	private WidgetInstanceManager wim;
	private ProductModel product;
	private Tabpanel tabpanel;
	private final ArgumentMatcher<Event> tabSelectedEventMatcher = new ArgumentMatcher()
	{
		@Override
		public boolean matches(final Object o)
		{
			final Event event = (Event) o;
			return event != null && DefaultEditorAreaRenderer.ON_TAB_SELECTED_EVENT.equals(event.getName())
					&& event.getTarget().equals(tabpanel);
		}

	};
	private Component child;

	protected void callListeners(final Map<String, EventListener<Event>> listenerMap)
	{
		listenerMap.forEach((s, eventEventListener) -> {
			try
			{
				eventEventListener.onEvent(new Event("save"));
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	private void createClassificationTabEditor(final int callerComponentDepth, final AbstractTab abstractTab)
	{
		dataType = mock(DataType.class);
		wim = CockpitTestUtil.mockWidgetInstanceManager();

		product = mock(ProductModel.class);
		wim.getModel().setValue(DefaultEditorAreaController.MODEL_CURRENT_OBJECT, product);
		when(classificationService.getFeatures(product)).thenReturn(new FeatureList());

		tabpanel = new Tabpanel();
		child = createXRecursiveChildrenAndGetLastOne(tabpanel, callerComponentDepth);

		renderer.render(child, abstractTab, product, dataType, wim);
		// add new classes to a product

	}

	private void createClassificationTabEditor(final int callerComponentDepth)
	{
		abstractTab = mock(AbstractTab.class);
		createClassificationTabEditor(callerComponentDepth, abstractTab);
	}

	protected Component createXRecursiveChildrenAndGetLastOne(final Component component, final int childCount)
	{
		Component currentLast = component;

		for (int i = 0; i < childCount; i++)
		{
			final Div child = new Div();
			child.setParent(currentLast);
			currentLast = child;
		}
		return currentLast;
	}


	@Before
	public void setUp()
	{
		doNothing().when(renderer).postEvent(any());
	}

	@Test
	public void testTabPanelEventCallAfterChangeAndRefresh()
	{
		// given
		createClassificationTabEditor(0);

		// when
		// add new classes to a product
		// simulate save
		callListeners(EditorAreaRendererUtils.getAfterCancelListeners(wim.getModel()));

		// then
		verify(renderer).postEvent(argThat(tabSelectedEventMatcher));
	}

	@Test
	public void testTabPanelEventCallAfterChangeAndSave()
	{
		// given
		createClassificationTabEditor(0);

		// when
		// add new classes to a product
		when(product.getClassificationClasses()).thenReturn(Lists.newArrayList(new ClassificationClassModel()));
		// simulate save
		callListeners(EditorAreaRendererUtils.getAfterSaveListeners(wim.getModel()));

		// then
		verify(renderer).postEvent(argThat(tabSelectedEventMatcher));
	}

	@Test
	public void testTabPanelEventCallFromDeepComponent()
	{
		// given
		createClassificationTabEditor(10);

		// when
		// add new classes to a product
		when(product.getClassificationClasses()).thenReturn(Lists.newArrayList(new ClassificationClassModel()));
		// simulate save
		callListeners(EditorAreaRendererUtils.getAfterSaveListeners(wim.getModel()));

		// then
		verify(renderer).postEvent(argThat(tabSelectedEventMatcher));
	}

	@Test
	public void testNoReadAccessSectionWhenUserHasNoPermissions()
	{
		// given
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(false);

		// when
		createClassificationTabEditor(0);

		// then
		then(renderer).should().renderEmptyTab(any(), eq(ClassificationTabEditorAreaRenderer.LABEL_NO_READ_ACCESS));
		then(renderer).should(never()).renderSection(any(), any(), any(), any());
	}

	@Test
	public void testNoReadAccessSectionWhenUserHasNoPermissionToClassificationAttribute()
	{
		// given
		given(permissionFacade.canReadType(ClassificationAttributeModel._TYPECODE)).willReturn(false);
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassAttributeAssignmentModel._TYPECODE)).willReturn(true);

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		final Feature feature = createFeatureMock(0, "a", "a");
		final ClassificationSectionNameProvider classificationSectionNameProvider = mock(ClassificationSectionNameProvider.class);
		given(classificationSectionNameProvider.provide(eq(classificationClassModel))).willReturn("SectionName");
		renderer.setClassificationSectionNameProvider(classificationSectionNameProvider);
		final Map<ClassificationClassModel, List<Feature>> testData = new HashMap<>();
		final List<Feature> featureList = Lists.newArrayList(feature);
		testData.put(classificationClassModel, featureList);
		doReturn(testData).when(renderer).getFeaturesGroupedByClassificationClassMap(any(), any());

		// when
		createClassificationTabEditor(0);

		// then
		then(renderer).should(never()).renderEmptyTab(any(), any());
		then(renderer).should().createSectionNameLabelSuffix(any());
		then(renderer).should(never()).renderAttributes(any(), any(), any());
	}

	@Test
	public void testRenderSectionWhenUserHasPermissionsAndTheInitiallyOpenedIsTrue()
	{
		// given
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassAttributeAssignmentModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassificationAttributeModel._TYPECODE)).willReturn(true);
		mockPrepareData();

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		final ClassificationSectionNameProvider classificationSectionNameProvider = mock(ClassificationSectionNameProvider.class);
		renderer.setClassificationSectionNameProvider(classificationSectionNameProvider);

		doNothing().when(renderer).renderAttributes(any(), any(), any());

		// when
		createClassificationTabEditor(0);

		// then
		then(renderer).should(never()).renderEmptyTab(any(), eq(ClassificationTabEditorAreaRenderer.LABEL_NO_READ_ACCESS));
		then(renderer).should().renderSection(any(), any(), any(), any());
		then(renderer).should().renderAttributes(any(), any(), any());
	}

	@Test
	public void testRenderSectionWhenTheInitiallyOpenedIsFalse()
	{
		// given
		given(permissionFacade.canReadType(ClassificationClassModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassAttributeAssignmentModel._TYPECODE)).willReturn(true);
		given(permissionFacade.canReadType(ClassificationAttributeModel._TYPECODE)).willReturn(true);
		mockPrepareData();

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		final ClassificationSectionNameProvider classificationSectionNameProvider = mock(ClassificationSectionNameProvider.class);
		renderer.setClassificationSectionNameProvider(classificationSectionNameProvider);

		final CustomTab customTab = new CustomTab();
		final Parameter parameter = new Parameter();
		parameter.setName("initiallyOpened");
		parameter.setValue("false");
		customTab.getRenderParameter().add(parameter);

		// when
		createClassificationTabEditor(0, (AbstractTab) customTab);

		// then
		then(renderer).should(never()).renderEmptyTab(any(), eq(ClassificationTabEditorAreaRenderer.LABEL_NO_READ_ACCESS));
		then(renderer).should().renderSection(any(), any(), any(), any());
		then(renderer).should(never()).renderAttributes(any(), any(), any());
	}

	@Test
	public void getFeatureNameOrCodeShouldReturnSessionSpecificLocalizedName()
	{
		//given
		final String expectedLocalizedName = "It works!";

		final Feature feature = mock(Feature.class);
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeModel classAttribute = mock(ClassificationAttributeModel.class);

		doReturn(classAttribute).when(assignment).getClassificationAttribute();
		doReturn(assignment).when(feature).getClassAttributeAssignment();

		doReturn(Locale.JAPAN).when(cockpitLocaleService).getCurrentLocale();
		doReturn(expectedLocalizedName).when(classAttribute).getName(Locale.JAPAN);

		//when
		final String result = renderer.getFeatureNameOrCode(feature);

		//then
		verify(classAttribute).getName(Locale.JAPAN);
		assertThat(result).isEqualTo(expectedLocalizedName);
	}

	private Feature createFeatureMock(final Integer position, final String name, final String code)
	{
		final Feature feature = mock(Feature.class);
		final ClassAttributeAssignmentModel assignment = mock(ClassAttributeAssignmentModel.class);
		when(assignment.getPosition()).thenReturn(position);
		final ClassificationAttributeModel attribute = mock(ClassificationAttributeModel.class);
		when(assignment.getClassificationAttribute()).thenReturn(attribute);
		when(attribute.getCode()).thenReturn(code);

		when(feature.getName()).thenReturn(name);
		when(feature.getClassAttributeAssignment()).thenReturn(assignment);

		return feature;
	}

	@Test
	public void testOrderOfRenderedFeatures()
	{
		//given
		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);

		final Feature feature0 = createFeatureMock(0, "a", "a");
		final Feature feature1 = createFeatureMock(1, null, "b");
		final Feature feature2 = createFeatureMock(1, "c", "c");
		final Feature feature3 = createFeatureMock(null, "a", "a");
		final Feature feature4 = createFeatureMock(null, "z", "z");


		renderer.setFeatureComparator(new FeatureComparator());


		final Map<ClassificationClassModel, List<Feature>> testData = new HashMap<>();
		final List<Feature> featureList = Lists.newArrayList(feature2, feature1, feature0, feature4, feature3);
		testData.put(classificationClassModel, featureList);

		when(classificationService.getFeatures(product)).thenReturn(new FeatureList(featureList));

		final Set<ClassificationClassModel> filteredClassificationClasses = new HashSet<>();
		filteredClassificationClasses.add(classificationClassModel);

		final Map<ClassificationClassModel, List<Feature>> sortedData = new HashMap<>();
		sortedData.put(classificationClassModel, Lists.newArrayList(feature0, feature1, feature2, feature3, feature4));


		doReturn(testData).when(renderer).getFeaturesGroupedByClassificationClassMap(any(), any());
		doReturn(filteredClassificationClasses).when(renderer).getFilteredClassificationClasses(any());

		//when
		final Map<ClassificationClassModel, List<Feature>> returnedData = renderer.prepareDataForRendering(product);

		//then
		assertEquals(returnedData, sortedData);
	}

	@Test
	public void refreshCurrentTabShouldBeTriggeredIrrespectivelyOfWhetherTheObjectWasChanged() throws Exception
	{
		//given
		createClassificationTabEditor(0);

		//when
		final Map<String, EventListener<Event>> listeners = EditorAreaRendererUtils.getAfterCancelListeners(wim.getModel());
		for (final EventListener<Event> listener : listeners.values())
		{
			listener.onEvent(null);
		}

		//then
		verify(renderer).refreshCurrentTab(any());

	}

	private void mockPrepareData()
	{
		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		final Feature feature = mock(Feature.class);
		final Map<ClassificationClassModel, List<Feature>> data = new HashMap<>();
		data.put(classificationClassModel, Lists.newArrayList(feature));
		doReturn(data).when(renderer).prepareDataForRendering(any());
	}

	public static class ClassificationTabEditorAreaRendererUnderTest extends ClassificationTabEditorAreaRenderer
	{
		@Override
		public void refreshCurrentTab(final Component component)
		{
			super.refreshCurrentTab(component);
		}
	}
}

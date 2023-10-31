/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.widgets.compare.renderers;

import static com.hybris.cockpitng.widgets.compare.CompareViewController.MODEL_VALUE_CHANGED_MAP;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.classification.ClassificationInfo;
import de.hybris.platform.platformbackoffice.widgets.compare.model.FeatureDescriptor;
import de.hybris.platform.platformbackoffice.widgets.compare.model.impl.BackofficePartialRendererData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.EventListener;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.components.table.TableCell;
import com.hybris.cockpitng.components.table.TableRow;
import com.hybris.cockpitng.config.compareview.jaxb.Attribute;
import com.hybris.cockpitng.core.model.StandardModelKeys;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.widgets.compare.model.PartialRendererData;
import com.hybris.cockpitng.widgets.compare.renderer.AbstractCompareViewElementRenderer;
import com.hybris.cockpitng.widgets.compare.renderer.CompareViewElementAttributeValueCache;
import com.hybris.cockpitng.widgets.compare.renderer.DefaultCompareViewAttributeRenderer;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationCompareViewFeatureRendererTest
{

	private static final String FEATURE_CODE = "featureCode";
	private static final String CLASSIFICATION_ATTRIBUTE_CODE = "classificationAttributeCode";

	@Spy
	@InjectMocks
	private ClassificationCompareViewFeatureRenderer testSubject;

	@Mock
	private BackofficePartialRendererData<Collection> data;

	@Mock
	private DataType dataType;

	@Mock
	private WidgetInstanceManager widgetInstanceManager;

	@Mock
	private FeatureDescriptor featureDescriptor;

	@Mock
	private PermissionFacade permissionFacade;

	@Mock
	private ClassificationService classificationService;

	@Test
	public void attributeNameShouldReturnFeatureCodeWhenItExist()
	{
		//given
		when(featureDescriptor.getName()).thenReturn(FEATURE_CODE);

		//when
		final String attributeName = testSubject.getAttributeName(featureDescriptor, data, dataType, widgetInstanceManager);

		//then
		assertThat(attributeName).isEqualTo(FEATURE_CODE);
	}

	@Test
	public void attributeNameShouldReturnClassificationAttributeCodeWhenFeatureCodeDoNotExist()
	{
		//given
		when(featureDescriptor.getClassificationAttributeCode()).thenReturn(CLASSIFICATION_ATTRIBUTE_CODE);

		//when
		final String attributeName = testSubject.getAttributeName(featureDescriptor, data, dataType, widgetInstanceManager);

		//then
		assertThat(attributeName).isEqualTo(CLASSIFICATION_ATTRIBUTE_CODE);
	}

	@Test
	public void shouldReturnIncorrectStateTrueWhenFeatureDoesNotExistsForItem()
	{
		//given
		final Map<FeatureDescriptor, ClassificationInfo> emptyFeatureValue = new HashMap<>();
		final Map<String, Map<FeatureDescriptor, ClassificationInfo>> featureValuesByClassificationCode = new HashMap<>();
		featureValuesByClassificationCode.put("", emptyFeatureValue);
		final ProductModel item = Mockito.mock(ProductModel.class);
		final Map<ProductModel, Map<String, Map<FeatureDescriptor, ClassificationInfo>>> featureValuesByClassificationCodeByProduct = new HashMap<>();
		featureValuesByClassificationCodeByProduct.put(item, featureValuesByClassificationCode);

		when(data.getFeatureValues()).thenReturn(featureValuesByClassificationCodeByProduct);

		//when
		final boolean incorrectState = testSubject.isValueIncorrect(featureDescriptor, dataType, data, item);

		//then
		assertThat(incorrectState).isTrue();

	}

	@Test
	public void shouldReturnIncorrectStateFalseWhenFeatureExistsForItem()
	{
		//given
		final String classificationCode = "code";

		final Map<FeatureDescriptor, ClassificationInfo> featureValues = new HashMap<>();
		final ClassificationInfo info = Mockito.mock(ClassificationInfo.class);
		featureValues.put(featureDescriptor, info);

		final Map<String, Map<FeatureDescriptor, ClassificationInfo>> featureValuesByClassificationCode = new HashMap<>();
		featureValuesByClassificationCode.put(classificationCode, featureValues);

		final Map<ProductModel, Map<String, Map<FeatureDescriptor, ClassificationInfo>>> featureValuesByClassificationCodeByItem = new HashMap<>();
		final ProductModel item = Mockito.mock(ProductModel.class);
		featureValuesByClassificationCodeByItem.put(item, featureValuesByClassificationCode);

		when(data.getFeatureValues()).thenReturn(featureValuesByClassificationCodeByItem);
		when(featureDescriptor.getClassificationCode()).thenReturn(classificationCode);

		//when
		final boolean incorrectState = testSubject.isValueIncorrect(featureDescriptor, dataType, data, item);

		//then
		assertThat(incorrectState).isFalse();

	}

	@Test
	public void shouldPermissionToReadDependOnlyOnFeatureDescriptorCanReadValue()
	{
		when(featureDescriptor.canRead()).thenReturn(true).thenReturn(false);
		final Object item = new Object();

		final boolean permissionsToReadWhenFeatureDescriptorAllowsRead = testSubject.hasPermissionsToRead(featureDescriptor, item);
		final boolean permissionsToReadWhenFeatureDescriptorForbidsRead = testSubject.hasPermissionsToRead(featureDescriptor, item);

		assertThat(permissionsToReadWhenFeatureDescriptorAllowsRead).isTrue();
		assertThat(permissionsToReadWhenFeatureDescriptorForbidsRead).isFalse();
	}

	@Test
	public void shouldPermissionToWriteDependOnlyOnFeatureDescriptorCanReadValue()
	{
		when(featureDescriptor.canWrite()).thenReturn(true).thenReturn(false);
		final Object item = new Object();

		final boolean permissionsToWriteWhenFeatureDescriptorAllowsWrite = testSubject.hasPermissionsToWrite(featureDescriptor,
				item);
		final boolean permissionsToWriteWhenFeatureDescriptorForbidsWrite = testSubject.hasPermissionsToWrite(featureDescriptor,
				item);

		assertThat(permissionsToWriteWhenFeatureDescriptorAllowsWrite).isTrue();
		assertThat(permissionsToWriteWhenFeatureDescriptorForbidsWrite).isFalse();
	}

	@Test
	public void shouldNotAllowRenderValueOfClassificationAttributeWhenUserHasNotPermissionToReadFeatureValuesFromFeatureValueListAndValueIsEnum()
	{
		//given
		when(featureDescriptor.canRead()).thenReturn(true);
		final Object item = new Object();
		when(permissionFacade.canReadType(ClassificationAttributeValueModel._TYPECODE)).thenReturn(false);
		final WidgetModel widgetModel = mock(WidgetModel.class);
		when(widgetModel.getValue(StandardModelKeys.CONTEXT_OBJECT, Object.class)).thenReturn(item);
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		final Map featureValuesMock = mock(Map.class);
		final Map valuesForItemMock = mock(Map.class);
		final Map valuesForClassificationCode = mock(Map.class);
		final ClassificationInfo classificationInfoMock = mock(ClassificationInfo.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModelMock = mock(ClassAttributeAssignmentModel.class);
		when(classAttributeAssignmentModelMock.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.ENUM);
		when(classificationInfoMock.getAssignment()).thenReturn(classAttributeAssignmentModelMock);
		when(valuesForClassificationCode.get(any())).thenReturn(classificationInfoMock);
		when(valuesForItemMock.get(any())).thenReturn(valuesForClassificationCode);
		when(featureValuesMock.get(item)).thenReturn(valuesForItemMock);
		when(data.getFeatureValues()).thenReturn(featureValuesMock);

		//when
		final boolean permissionsToRead = testSubject.hasPermissionsToRead(featureDescriptor, data, item, dataType,
				widgetInstanceManager);

		//then
		assertThat(permissionsToRead).isFalse();
	}


	@Test
	public void shouldNotAllowRenderValueOfClassificationAttributeWhenUserHasNotPermissionToWriteFeatureValuesFromFeatureValueListAndValueIsEnum()
	{
		//given
		when(featureDescriptor.canWrite()).thenReturn(true);
		final Object item = new Object();
		when(permissionFacade.canChangeType(ClassificationAttributeValueModel._TYPECODE)).thenReturn(false);
		final WidgetModel widgetModel = mock(WidgetModel.class);
		when(widgetModel.getValue(StandardModelKeys.CONTEXT_OBJECT, Object.class)).thenReturn(item);
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		final Map featureValuesMock = mock(Map.class);
		final Map valuesForItemMock = mock(Map.class);
		final Map valuesForClassificationCode = mock(Map.class);
		final ClassificationInfo classificationInfoMock = mock(ClassificationInfo.class);
		final ClassAttributeAssignmentModel classAttributeAssignmentModelMock = mock(ClassAttributeAssignmentModel.class);
		when(classAttributeAssignmentModelMock.getAttributeType()).thenReturn(ClassificationAttributeTypeEnum.ENUM);
		when(classificationInfoMock.getAssignment()).thenReturn(classAttributeAssignmentModelMock);
		when(valuesForClassificationCode.get(any())).thenReturn(classificationInfoMock);
		when(valuesForItemMock.get(any())).thenReturn(valuesForClassificationCode);
		when(featureValuesMock.get(item)).thenReturn(valuesForItemMock);
		when(data.getFeatureValues()).thenReturn(featureValuesMock);

		//when
		final boolean permissionsToWrite = testSubject.hasPermissionsToWrite(featureDescriptor, data, item, dataType,
				widgetInstanceManager);

		//then
		assertThat(permissionsToWrite).isFalse();
	}

	@Test
	public void shouldUpdateProductFeaturesWithFeatureList() throws Exception
	{
		//given
		final String attributeValueCacheKey = "attribute-value-cache";
		final TableCell tableCell = mock(TableCell.class);
		final ProductModel productModel = mock(ProductModel.class);

		final Component draggedComponent = mock(Component.class);
		final DropEvent testOndropEvent = mock(DropEvent.class);
		when(testOndropEvent.getDragged()).thenReturn(draggedComponent);
		final CompareViewElementAttributeValueCache testAttributeValueCache = mock(CompareViewElementAttributeValueCache.class);
		final CompareViewElementAttributeValueCache droppedAttributeValueCache = mock(CompareViewElementAttributeValueCache.class);
		when(draggedComponent.getAttribute(attributeValueCacheKey)).thenReturn(testAttributeValueCache);
		when(tableCell.getAttribute(attributeValueCacheKey)).thenReturn(droppedAttributeValueCache);

		final List<FeatureValue> featureValueList = new ArrayList<>();
		final FeatureValue featureValue1 = mock(FeatureValue.class);
		final FeatureValue featureValue2 = mock(FeatureValue.class);
		featureValueList.add(featureValue1);
		featureValueList.add(featureValue2);

		final DataType dataType = mock(DataType.class);
		when(testAttributeValueCache.getValue()).thenReturn(featureValueList);
		when(droppedAttributeValueCache.getQualifier()).thenReturn("test");
		when(droppedAttributeValueCache.getDataType()).thenReturn(dataType);

		final List<Component> cellChildren = new ArrayList<>();
		final Component cellChild = mock(Component.class);
		cellChildren.add(cellChild);
		when(tableCell.getChildren()).thenReturn(cellChildren);
		final List<Component> cellContainerChildren = new ArrayList<>();
		final Editor cellContainerChild = mock(Editor.class);
		cellContainerChildren.add(cellContainerChild);
		when(cellChild.getChildren()).thenReturn(cellContainerChildren);

		when(featureDescriptor.getClassificationCode()).thenReturn("test");
		final Map<ProductModel, Map<String, Map<FeatureDescriptor, ClassificationInfo>>> featureValueMapForProduct = new HashMap<>();
		final Map<String, Map<FeatureDescriptor, ClassificationInfo>> featureValueMapForClassificationCode = new HashMap<>();
		final Map<FeatureDescriptor, ClassificationInfo> featureValueMapForFeatureDescriptor = new HashMap<>();

		final ClassificationInfo classificationInfo = mock(ClassificationInfo.class);
		featureValueMapForFeatureDescriptor.put(featureDescriptor, classificationInfo);
		featureValueMapForClassificationCode.put("test", featureValueMapForFeatureDescriptor);
		featureValueMapForProduct.put(productModel, featureValueMapForClassificationCode);

		when(data.getFeatureValues()).thenReturn(featureValueMapForProduct);
		when(classificationInfo.isLocalized()).thenReturn(false);

		final String comparisonObjectFeatureListMap = "comparison-object-featureList-map";
		final WidgetModel widgetModel = mock(WidgetModel.class);
		when(widgetModel.getValue(comparisonObjectFeatureListMap, Map.class)).thenReturn(new HashMap<>());
		when(widgetModel.getValue(MODEL_VALUE_CHANGED_MAP, Map.class)).thenReturn(new HashMap<>());
		doNothing().when(widgetModel).setValue(any(), any());
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);

		final FeatureList featureList = mock(FeatureList.class);
		when(classificationService.getFeatures(productModel)).thenReturn(featureList);
		final List<Feature> testProductFeatureList = new ArrayList<>();
		final Feature testProductFeature = mock(Feature.class);
		when(testProductFeature.getCode()).thenReturn("test");
		when(featureDescriptor.getCode()).thenReturn("test");
		testProductFeatureList.add(testProductFeature);
		when(featureList.getFeatures()).thenReturn(testProductFeatureList);
		doNothing().when(testProductFeature).removeAllValues();

		//when
		final EventListener<Event> eventEventListener = testSubject.createCompareViewElementOnDropEvent(tableCell,
				featureDescriptor, data, productModel, dataType, widgetInstanceManager);
		eventEventListener.onEvent(testOndropEvent);

		//then
		verify(cellContainerChild).setValue(classificationInfo);
		verify(cellContainerChild).reload();
		verify(testProductFeature).setValues(featureValueList);

	}
}


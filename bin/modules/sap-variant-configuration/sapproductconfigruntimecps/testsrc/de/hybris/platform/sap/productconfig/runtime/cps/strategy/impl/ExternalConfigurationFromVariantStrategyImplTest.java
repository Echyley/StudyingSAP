/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.strategy.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.MasterDataCacheAccessService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalObjectKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link ExternalConfigurationFromVariantStrategyImpl}
 */
@UnitTest
public class ExternalConfigurationFromVariantStrategyImplTest
{
	private static final String variantCode = "DRAGON_CAR_V16";
	private static final String baseProductCode = "DRAGON_CAR";
	private static final String kbId = "123";
	private static final String csticKey = "COLOUR";
	private static final String classificationAttributeNonCPQ = "NON CPQ";
	private static final String csticValue = "BK";
	private static final String csticValueInClassificationSystem = csticKey + "_" + csticValue;
	private static final String classificationValueIdentifier = "8796093809248";
	private static final String productPk = "8796183429121";
	private static final String searchString = "select {pk} from {productfeature} where {stringvalue}='"
			+ classificationValueIdentifier + "'" + "and {product}='" + productPk + "'";
	private static final String AUTHOR_CONSTRAINT = "4";
	private static final String UNIT = "PCE";

	private static final Double NUMBER_VALUE = Double.valueOf(3);
	private static final String STRING_VALUE = "XXX";

	private final ExternalConfigurationFromVariantStrategyImpl classUnderTest = new ExternalConfigurationFromVariantStrategyImpl();

	@Mock
	private ClassificationService mockClassificationService;
	@Mock
	private MasterDataCacheAccessService mockMasterDataCacheAccessService;
	@Mock
	private I18NService mockI18NService;
	@Mock
	private FlexibleSearchService mockFlexibleSearchService;
	@Mock
	private VariantProductModel mockVariantModel;
	@Mock
	private ProductModel mockBaseProductModel;
	@Mock
	private FeatureList mpckFeatureList;
	@Mock
	private Feature mockFeatureRelated;
	@Mock
	private Feature mockFeatureNotRelated;
	@Mock
	private ClassAttributeAssignmentModel mockClassAttributeAssignment;
	@Mock
	private ClassAttributeAssignmentModel mockClassAttributeAssignmentNotRelated;
	@Mock
	private ClassificationAttributeModel mockClassificationAttribute;
	@Mock
	private ClassificationAttributeModel mockClassificationAttributeNotRelated;
	@Mock
	private FeatureValue mockFeatureValue;
	@Mock
	private ClassificationAttributeValueModel mockClassificationAttributeValue;
	@Mock
	private SearchResult<Object> mockSearchResult;
	@Mock
	private ProductFeatureModel mockProductFeature;
	@Mock
	private UnitModel mockUnitModel;
	@Mock
	private ConfigurationProductUtil mockConfigProductUtil;
	@Mock
	private ProductFeatureModel mockedProductFeatureModelWithClassificationAttributeValueModel;
	@Mock
	private ProductFeatureModel mockedProductFeatureModelWithDoubleValueModel;
	@Mock
	private ProductFeatureModel mockedProductFeatureModelWithStringValueModel;
	@Mock
	private ProductFeatureModel mockedProductFeatureModelWithWrongTypeOfValueModel;


	private final PK pkClassificationAttribute = PK.parse(classificationValueIdentifier);
	private final PK pkProduct = PK.parse(productPk);

	private final List<Feature> features = new ArrayList();
	private final CPSMasterDataKnowledgeBaseContainer kbContainer = new CPSMasterDataKnowledgeBaseContainer();
	private final Map<String, CPSMasterDataCharacteristicContainer> characteristics = new HashMap<>();
	private final CPSMasterDataCharacteristicContainer csticContainer = new CPSMasterDataCharacteristicContainer();
	private final List<FeatureValue> featureValueList = new ArrayList<>();
	private final List<Object> featureModelList = new ArrayList<>();

	private final List<ProductFeatureModel> productFeatureList = new ArrayList<>();


	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);

		when(mockConfigProductUtil.getProductForCurrentCatalog(variantCode)).thenReturn(mockVariantModel);
		when(mockConfigProductUtil.getProductForCurrentCatalog(baseProductCode)).thenReturn(mockBaseProductModel);
		when(mockVariantModel.getBaseProduct()).thenReturn(mockBaseProductModel);
		when(mockVariantModel.getPk()).thenReturn(pkProduct);
		when(mockVariantModel.getFeatures()).thenReturn(productFeatureList);
		when(mockBaseProductModel.getCode()).thenReturn(baseProductCode);
		when(mockBaseProductModel.getUnit()).thenReturn(mockUnitModel);
		when(mockUnitModel.getCode()).thenReturn(UNIT);
		when(mockClassificationService.getFeatures(mockVariantModel)).thenReturn(mpckFeatureList);
		when(mockI18NService.getCurrentLocale()).thenReturn(Locale.US);
		when(mockMasterDataCacheAccessService.getKbContainer(kbId, Locale.US.toLanguageTag())).thenReturn(kbContainer);
		when(mpckFeatureList.getFeatures()).thenReturn(features);
		when(mockFeatureRelated.getClassAttributeAssignment()).thenReturn(mockClassAttributeAssignment);
		when(mockFeatureRelated.getValues()).thenReturn(featureValueList);
		when(mockFeatureNotRelated.getClassAttributeAssignment()).thenReturn(mockClassAttributeAssignmentNotRelated);
		when(mockFeatureValue.getValue()).thenReturn(mockClassificationAttributeValue);
		when(mockClassificationAttributeValue.getCode()).thenReturn(csticValueInClassificationSystem);
		when(mockClassificationAttributeValue.getPk()).thenReturn(pkClassificationAttribute);
		when(mockClassAttributeAssignment.getClassificationAttribute()).thenReturn(mockClassificationAttribute);
		when(mockClassAttributeAssignmentNotRelated.getClassificationAttribute()).thenReturn(mockClassificationAttributeNotRelated);
		when(mockClassificationAttribute.getCode()).thenReturn(csticKey);
		when(mockClassificationAttributeNotRelated.getCode()).thenReturn(classificationAttributeNonCPQ);
		when(mockFlexibleSearchService.search(searchString)).thenReturn(mockSearchResult);
		when(mockSearchResult.getResult()).thenReturn(featureModelList);
		when(mockProductFeature.getAuthor()).thenReturn(AUTHOR_CONSTRAINT);

		features.add(mockFeatureRelated);
		features.add(mockFeatureNotRelated);
		featureValueList.add(mockFeatureValue);
		featureModelList.add(mockProductFeature);

		kbContainer.setCharacteristics(characteristics);
		characteristics.put(csticKey, csticContainer);
		classUnderTest.setClassificationService(mockClassificationService);
		classUnderTest.setMasterDataCacheAccessService(mockMasterDataCacheAccessService);
		classUnderTest.setI18NService(mockI18NService);
		classUnderTest.setFlexibleSearchService(mockFlexibleSearchService);
		classUnderTest.setConfigurationProductUtil(mockConfigProductUtil);
	}

	@Test
	public void testClassificationService()
	{
		assertEquals(mockClassificationService, classUnderTest.getClassificationService());
	}

	@Test
	public void testFlexibleSearchService()
	{
		assertEquals(mockFlexibleSearchService, classUnderTest.getFlexibleSearchService());
	}

	@Test
	public void testCreateExternalConfiguration()
	{
		productFeatureList.add(prepareMockedProductFeatureModelWithClassificationAttributeValueModel());
		final CPSExternalConfiguration configuration = classUnderTest.createExternalConfiguration(variantCode, kbId);
		assertNotNull(configuration);
		assertEquals(kbId, configuration.getKbId());
		assertTrue(configuration.isComplete());
		assertTrue(configuration.isConsistent());
		final CPSExternalItem rootItem = configuration.getRootItem();
		assertNotNull(rootItem);
		assertNull(rootItem.getSubItems());
		final List<CPSExternalCharacteristic> rootItemCstics = rootItem.getCharacteristics();
		assertNotNull(rootItemCstics);
		assertEquals(1, rootItemCstics.size());
		final CPSExternalCharacteristic cpsExternalCharacteristic = rootItemCstics.get(0);
		assertEquals(csticKey, cpsExternalCharacteristic.getId());
		final List<CPSExternalValue> values = cpsExternalCharacteristic.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());
		final CPSExternalValue cpsExternalValue = values.get(0);
		assertEquals(csticValue, cpsExternalValue.getValue());
	}

	@Test
	public void testCreateRootInstanceFromVariant()
	{
		final CPSExternalItem rootItem = classUnderTest.createExternalRootItem(variantCode);
		assertNotNull(rootItem);
		final CPSQuantity quantity = rootItem.getQuantity();
		assertNotNull(quantity);
		assertEquals(Double.valueOf(1), quantity.getValue());
		assertEquals(UNIT, quantity.getUnit());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.INSTANCE_ID_ROOT, rootItem.getId());
		final CPSExternalObjectKey objectKey = rootItem.getObjectKey();
		assertNotNull(objectKey);
		assertEquals(SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA, objectKey.getType());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.DEFAULT_CLASS_TYPE, objectKey.getClassType());
		assertEquals(baseProductCode, objectKey.getId());
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.AUTHOR_USER, rootItem.getObjectKeyAuthor());
		assertTrue(rootItem.isComplete());
		assertTrue(rootItem.isConsistent());
	}

	@Test
	public void testDetermineBaseProduct()
	{
		final ProductModel base = classUnderTest.determineBaseProduct(variantCode);
		assertEquals(mockBaseProductModel, base);
	}

	@Test(expected = IllegalStateException.class)
	public void testDetermineBaseProductNoVariant()
	{
		classUnderTest.determineBaseProduct(baseProductCode);
	}

	@Test
	public void testGetMasterDataCacheAccessService()
	{
		assertEquals(mockMasterDataCacheAccessService, classUnderTest.getMasterDataCacheAccessService());
	}

	@Test
	public void testIsCharacteristicRelated()
	{
		assertTrue(classUnderTest.isCharacteristicRelatedToCurrentProduct(csticKey, kbId));
	}

	@Test
	public void testIsCharacteristicRelatedNotRelated()
	{
		assertFalse(classUnderTest.isCharacteristicRelatedToCurrentProduct("CSTIC_KEY_NOT_RELATED", kbId));
	}

	@Test
	public void testGetI18NService()
	{
		assertEquals(mockI18NService, classUnderTest.getI18NService());
	}

	@Test
	public void testRetrieveCharacteristicId()
	{
		assertEquals(csticKey,
				classUnderTest.retrieveCharacteristicId(prepareMockedProductFeatureModelWithClassificationAttributeValueModel()));
	}

	@Test(expected = NullPointerException.class)
	public void testRetrieveCharacteristicIdNoClassAttributeAssignment()
	{
		when(mockedProductFeatureModelWithClassificationAttributeValueModel.getClassificationAttributeAssignment())
				.thenReturn(null);
		classUnderTest.retrieveCharacteristicId(mockedProductFeatureModelWithClassificationAttributeValueModel);
	}

	@Test(expected = NullPointerException.class)
	public void testRetrieveCharacteristicIdNoClassificationAttribute()
	{
		when(mockedProductFeatureModelWithClassificationAttributeValueModel.getClassificationAttributeAssignment())
				.thenReturn(mockClassAttributeAssignment);
		classUnderTest.retrieveCharacteristicId(mockedProductFeatureModelWithClassificationAttributeValueModel);
		when(mockClassAttributeAssignment.getClassificationAttribute()).thenReturn(null);
		classUnderTest.retrieveCharacteristicId(mockedProductFeatureModelWithClassificationAttributeValueModel);
	}

	@Test
	public void testFindAuthor()
	{
		final String author = classUnderTest.findAuthor(classificationValueIdentifier, productPk);
		assertEquals(AUTHOR_CONSTRAINT, author);
	}

	@Test
	public void testFindAuthorDontReturnNull()
	{
		when(mockProductFeature.getAuthor()).thenReturn(null);
		final String author = classUnderTest.findAuthor(classificationValueIdentifier, productPk);
		assertEquals(ExternalConfigurationFromVariantStrategyImpl.AUTHOR_USER, author);
	}


	@Test(expected = IllegalStateException.class)
	public void testFindAuthorNoProductFeatures()
	{
		featureModelList.clear();
		classUnderTest.findAuthor(classificationValueIdentifier, productPk);
	}

	@Test
	public void testDetermineCharacteristics()
	{
		productFeatureList.add(prepareMockedProductFeatureModelWithClassificationAttributeValueModel());
		final List<CPSExternalCharacteristic> cpsCharacteristics = classUnderTest.determineCharacteristics(kbId, mockVariantModel);
		assertNotNull(cpsCharacteristics);
		assertEquals(1, cpsCharacteristics.size());
	}

	@Test
	public void testDetermineCharacteristicsNoValues()
	{
		final List<CPSExternalCharacteristic> cpsCharacteristics = classUnderTest.determineCharacteristics(kbId, mockVariantModel);
		assertNotNull(cpsCharacteristics);
		assertTrue(cpsCharacteristics.isEmpty());
	}

	@Test
	public void testPrepareCpsExternalValue()
	{
		final CPSExternalValue cpsExternalValue = classUnderTest
				.prepareCpsExternalValue(prepareMockedProductFeatureModelWithClassificationAttributeValueModel());
		assertNotNull(cpsExternalValue);
		assertEquals(csticValue, cpsExternalValue.getValue());
		assertEquals(" ", cpsExternalValue.getAuthor());
	}

	@Test
	public void testPrepareCpsExternalValueDouble()
	{
		final CPSExternalValue cpsExternalValue = classUnderTest
				.prepareCpsExternalValue(prepareMockedProductFeatureModelWithDoubleValueModel());
		assertNotNull(cpsExternalValue);
		assertEquals(NUMBER_VALUE.toString(), cpsExternalValue.getValue());
		assertEquals(" ", cpsExternalValue.getAuthor());
	}

	@Test
	public void testPrepareCpsExternalValueString()
	{
		final CPSExternalValue cpsExternalValue = classUnderTest
				.prepareCpsExternalValue(prepareMockedProductFeatureModelWithStringValueModel());
		assertNotNull(cpsExternalValue);
		assertEquals(STRING_VALUE.toString(), cpsExternalValue.getValue());
		assertEquals(" ", cpsExternalValue.getAuthor());
	}

	@Test(expected = IllegalStateException.class)
	public void testPrepareCpsExternalValueWrongType()
	{
		final CPSExternalValue cpsExternalValue = classUnderTest
				.prepareCpsExternalValue(prepareMockedProductFeatureModelWithWrongTypeOfValueModel());
	}

	@Test(expected = IllegalStateException.class)
	public void testPrepareCpsExternalValueCsticValueDoesNotContainCstic()
	{
		final ProductFeatureModel productFeatureModel = prepareMockedProductFeatureModelWithClassificationAttributeValueModel();
		final ClassificationAttributeValueModel valueModel = new ClassificationAttributeValueModel();
		valueModel.setCode("wrong_value_code");
		when(mockedProductFeatureModelWithClassificationAttributeValueModel.getValue()).thenReturn(valueModel);
		final CPSExternalValue cpsExternalValue = classUnderTest.prepareCpsExternalValue(productFeatureModel);
	}

	@Test
	public void testPrepareCpsExternalCaracteristicNewCsticId()
	{
		final List<CPSExternalCharacteristic> cpsExternalCharacteristicList = prepareCpsExternalCharacteristicList();
		assertEquals(1, cpsExternalCharacteristicList.size());
		final String characteristicId = "NewCsticId";
		classUnderTest.prepareCpsExternalCaracteristic(cpsExternalCharacteristicList, characteristicId);
		assertEquals(2, cpsExternalCharacteristicList.size());
		assertEquals(characteristicId, cpsExternalCharacteristicList.get(1).getId());
		assertEquals(0, cpsExternalCharacteristicList.get(1).getValues().size());
	}

	@Test
	public void testPrepareCpsExternalCaracteristicExistingCsticId()
	{
		final List<CPSExternalCharacteristic> cpsExternalCharacteristicList = prepareCpsExternalCharacteristicList();
		assertEquals(1, cpsExternalCharacteristicList.size());
		classUnderTest.prepareCpsExternalCaracteristic(cpsExternalCharacteristicList, csticKey);
		assertEquals(1, cpsExternalCharacteristicList.size());
		assertEquals(csticKey, cpsExternalCharacteristicList.get(0).getId());
		assertEquals(1, cpsExternalCharacteristicList.get(0).getValues().size());
	}

	@Test
	public void testMapProductFeatureToCpsExternalCharacteristic()
	{
		final ProductFeatureModel productFeature = prepareMockedProductFeatureModelWithClassificationAttributeValueModel();
		final List<CPSExternalCharacteristic> cpsExternalCharacteristicList = new ArrayList<>();
		classUnderTest.mapProductFeatureToCpsExternalCharacteristic(productFeature, cpsExternalCharacteristicList, kbId);
		assertEquals(1, cpsExternalCharacteristicList.size());
		assertEquals(csticKey, cpsExternalCharacteristicList.get(0).getId());
		assertEquals(csticValue, cpsExternalCharacteristicList.get(0).getValues().get(0).getValue());
	}

	@Test
	public void testMapProductFeatureToCpsExternalCharacteristicWrongCsticId()
	{
		final ProductFeatureModel productFeature = prepareMockedProductFeatureModelWithClassificationAttributeValueModel();
		final ClassAttributeAssignmentModel classAttributeAssignment = prepareClassificationAssignment("wrongCstic");
		when(mockedProductFeatureModelWithClassificationAttributeValueModel.getClassificationAttributeAssignment())
				.thenReturn(classAttributeAssignment);
		final List<CPSExternalCharacteristic> cpsExternalCharacteristicList = new ArrayList<>();
		classUnderTest.mapProductFeatureToCpsExternalCharacteristic(mockedProductFeatureModelWithClassificationAttributeValueModel,
				cpsExternalCharacteristicList, kbId);
		assertEquals(0, cpsExternalCharacteristicList.size());
	}

	protected ProductFeatureModel prepareMockedProductFeatureModelWithClassificationAttributeValueModel()
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = prepareClassificationAssignment(csticKey);
		when(mockedProductFeatureModelWithClassificationAttributeValueModel.getClassificationAttributeAssignment())
				.thenReturn(classAttributeAssignment);
		final ClassificationAttributeValueModel valueModel = new ClassificationAttributeValueModel();
		valueModel.setCode(csticValueInClassificationSystem);
		when(mockedProductFeatureModelWithClassificationAttributeValueModel.getValue()).thenReturn(valueModel);
		when(mockedProductFeatureModelWithClassificationAttributeValueModel.getAuthor()).thenReturn(" ");
		return mockedProductFeatureModelWithClassificationAttributeValueModel;
	}

	protected ProductFeatureModel prepareMockedProductFeatureModelWithDoubleValueModel()
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = prepareClassificationAssignment(csticKey);
		when(mockedProductFeatureModelWithDoubleValueModel.getClassificationAttributeAssignment())
				.thenReturn(classAttributeAssignment);
		when(mockedProductFeatureModelWithDoubleValueModel.getValue()).thenReturn(NUMBER_VALUE);
		when(mockedProductFeatureModelWithDoubleValueModel.getAuthor()).thenReturn(" ");
		return mockedProductFeatureModelWithDoubleValueModel;
	}

	protected ProductFeatureModel prepareMockedProductFeatureModelWithStringValueModel()
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = prepareClassificationAssignment(csticKey);
		when(mockedProductFeatureModelWithStringValueModel.getClassificationAttributeAssignment())
				.thenReturn(classAttributeAssignment);
		when(mockedProductFeatureModelWithStringValueModel.getValue()).thenReturn(STRING_VALUE);
		when(mockedProductFeatureModelWithStringValueModel.getAuthor()).thenReturn(" ");
		return mockedProductFeatureModelWithStringValueModel;
	}

	protected ProductFeatureModel prepareMockedProductFeatureModelWithWrongTypeOfValueModel()
	{
		final ClassAttributeAssignmentModel classAttributeAssignment = prepareClassificationAssignment(csticKey);
		when(mockedProductFeatureModelWithWrongTypeOfValueModel.getClassificationAttributeAssignment())
				.thenReturn(classAttributeAssignment);
		when(mockedProductFeatureModelWithWrongTypeOfValueModel.getValue()).thenReturn(new Object());
		return mockedProductFeatureModelWithWrongTypeOfValueModel;
	}

	protected ClassAttributeAssignmentModel prepareClassificationAssignment(final String code)
	{
		final ClassificationAttributeModel classificationAttribute = new ClassificationAttributeModel();
		classificationAttribute.setCode(code);
		final ClassAttributeAssignmentModel classAttributeAssignment = new ClassAttributeAssignmentModel();
		classAttributeAssignment.setClassificationAttribute(classificationAttribute);
		return classAttributeAssignment;
	}

	protected List<CPSExternalCharacteristic> prepareCpsExternalCharacteristicList()
	{
		final CPSExternalCharacteristic characteristic = new CPSExternalCharacteristic();
		characteristic.setId(csticKey);
		final CPSExternalValue value = new CPSExternalValue();
		value.setValue(csticValue);
		final List<CPSExternalValue> values = new ArrayList<>();
		values.add(value);
		characteristic.setValues(values);
		final List<CPSExternalCharacteristic> cpsExternalCharacteristicList = new ArrayList<>();
		cpsExternalCharacteristicList.add(characteristic);
		return cpsExternalCharacteristicList;
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.hybris.cockpitng.search.data.SortData;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.platformbackoffice.dao.BackofficeSavedQueryDAO;
import de.hybris.platform.platformbackoffice.model.BackofficeSavedQueryModel;
import de.hybris.platform.platformbackoffice.model.BackofficeSearchConditionModel;
import de.hybris.platform.platformbackoffice.services.converters.BackofficeSavedQueryValueConverter;
import de.hybris.platform.platformbackoffice.services.converters.impl.AtomicValueConverter;
import de.hybris.platform.platformbackoffice.services.converters.impl.HybrisEnumValueConverter;
import de.hybris.platform.platformbackoffice.services.converters.impl.HybrisItemTypeValueConverter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hybris.backoffice.widgets.advancedsearch.AdvancedSearchMode;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.backoffice.widgets.advancedsearch.impl.SearchConditionData;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldType;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.Parameter;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.search.data.ValueComparisonOperator;


public class BackofficeSavedQueriesServiceImplTest
{
	public static final String TEST_TYPE_CODE = "testTypeCode";
	public static final String TEST_SORT_ATTRIBUTE = "testSortAttribute";
	public static final String TEST_VALUE = "testValue";
	public static final String TEST_ATTRIBUTE = "testAttribute";
	public static final Locale CANADA_FRENCH = Locale.CANADA_FRENCH;
	public static final Integer TEST_INT_VALUE = Integer.valueOf(123123);
	public static final String TEST_EDITOR = "TEST_EDITOR";
	public static final String TEST_PARAM_NAME = "TEST_PARAM_NAME";
	public static final String TEST_PARAM_VALUE = "TEST_PARAM_VALUE";
	public static final String TEST_QUERY_NAME = "TEST_QUERY_NAME";
	public static final String TEST_USER = "testUser";
	public static final String TEST_GROUP = "testGroup";
	public static final AdvancedSearchMode SEARCH_MODE = AdvancedSearchMode.ADVANCED;

	@InjectMocks
	private BackofficeSavedQueriesServiceImpl backofficeSavedQueriesServiceImpl;
	@Mock
	private BackofficeSavedQueryDAO backofficeSavedQueryDAO;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private ModelService modelService;
	@Mock
	private DataType dataType;
	@Mock
	private DataAttribute dataAttribute;
	private UserModel principalModel;
	private UserGroupModel userGroupModel;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		principalModel = new UserModel();
		principalModel.setUid(TEST_USER);
		userGroupModel = new UserGroupModel();
		userGroupModel.setUid(TEST_GROUP);
		Mockito.when(modelService.create(BackofficeSavedQueryModel.class)).thenReturn(new BackofficeSavedQueryModel());
		Mockito.when(modelService.create(BackofficeSearchConditionModel.class)).thenReturn(new BackofficeSearchConditionModel());
		Mockito.doReturn(Boolean.FALSE).when(modelService).isRemoved(Mockito.any());
		Mockito.when(typeFacade.load(TEST_TYPE_CODE)).thenReturn(dataType);
		Mockito.when(dataType.getAttribute(TEST_ATTRIBUTE)).thenReturn(dataAttribute);
		Mockito.when(dataAttribute.getValueType()).thenReturn(dataType);
		backofficeSavedQueriesServiceImpl.setSavedQueryValueConverters(getConverters());
	}

	private List<BackofficeSavedQueryValueConverter> getConverters()
	{
		final List<BackofficeSavedQueryValueConverter> converters = Lists.newArrayList();
		final HybrisEnumValueConverter hybrisEnumValueConverter = new HybrisEnumValueConverter();
		hybrisEnumValueConverter.setEnumerationService(enumerationService);
		converters.add(hybrisEnumValueConverter);
		final HybrisItemTypeValueConverter hybrisItemTypeValueConverter = new HybrisItemTypeValueConverter();
		hybrisItemTypeValueConverter.setModelService(modelService);
		converters.add(hybrisItemTypeValueConverter);
		converters.add(new AtomicValueConverter());
		return converters;
	}

	@Test
	public void testTwoWaysConversionWithIntValueCondition() throws TypeNotFoundException
	{
		Mockito.when(dataType.getType()).thenReturn(DataType.Type.ATOMIC);
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.INTEGER);

		final BackofficeSavedQueryModel savedQuery = createSavedQuery();
		final BackofficeSearchConditionModel condition = createConditionWithIntValue();
		savedQuery.setConditions(Lists.newArrayList(condition));

		final AdvancedSearchInitContext initCtx = backofficeSavedQueriesServiceImpl.getAdvancedSearchInitContext(savedQuery);
		assertNotNull(initCtx);
		assertEquals(1, initCtx.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE).size());

		final SearchConditionData scd = initCtx.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE).iterator().next();
		assertEquals(TEST_INT_VALUE, scd.getValue());
		assertTrue(scd.getValue() instanceof Integer);
		assertEquals(ValueComparisonOperator.EQUALS.getOperatorCode(), scd.getOperator().getOperatorCode());
		final FieldType ft = scd.getFieldType();
		assertTrue(ft.isDisabled());
		assertTrue(ft.isMandatory());
		assertTrue(ft.isSelected());
		assertTrue(ft.isSortable());
		assertEquals(TEST_ATTRIBUTE, ft.getName());
		assertEquals(condition.getOperatorCode(), ft.getOperator());
		assertEquals(condition.getEditor(), ft.getEditor());
		final Parameter parameter = ft.getEditorParameter().iterator().next();
		assertEquals(TEST_PARAM_NAME, parameter.getName());
		assertEquals(TEST_PARAM_VALUE, parameter.getValue());

		final AdvancedSearchData asd = initCtx.getAdvancedSearchData();
		assertEquals(TEST_TYPE_CODE, asd.getTypeCode());
		assertEquals(ValueComparisonOperator.AND.getOperatorCode(), asd.getGlobalOperator().getOperatorCode());
		assertEquals(Boolean.TRUE, asd.getIncludeSubtypes());
		assertTrue(asd.getSortData().isAscending());
		assertEquals(TEST_SORT_ATTRIBUTE, asd.getSortData().getSortAttribute());
		assertEquals(SEARCH_MODE, asd.getAdvancedSearchMode());


		// convert it back
		final Map<Locale, String> queryName = new HashMap<>();
		queryName.put(Locale.ENGLISH, TEST_QUERY_NAME);
		final BackofficeSavedQueryModel convertedSavedQuery = backofficeSavedQueriesServiceImpl.createSavedQuery(queryName, asd,
				principalModel, Collections.singletonList(userGroupModel));

		assertEquals(TEST_QUERY_NAME, convertedSavedQuery.getName(Locale.ENGLISH));
		assertEquals(asd.getGlobalOperator().getOperatorCode(), convertedSavedQuery.getGlobalOperatorCode());
		assertEquals(asd.getIncludeSubtypes(), convertedSavedQuery.getIncludeSubtypes());
		assertEquals(asd.getTypeCode(), convertedSavedQuery.getTypeCode());
		assertEquals(asd.getSortData().getSortAttribute(), convertedSavedQuery.getSortAttribute());
		assertEquals(Boolean.valueOf(asd.getSortData().isAscending()), convertedSavedQuery.getSortAsc());
		assertTrue(CollectionUtils.isEqualCollection(savedQuery.getUserGroups(), convertedSavedQuery.getUserGroups()));
		assertEquals(savedQuery.getQueryOwner(), convertedSavedQuery.getQueryOwner());
		assertEquals(asd.getAdvancedSearchMode(), AdvancedSearchMode.valueOf(convertedSavedQuery.getSearchMode()));

		final BackofficeSearchConditionModel convertedCondition = convertedSavedQuery.getConditions().iterator().next();
		assertEquals(scd.getOperator(), ValueComparisonOperator.getOperatorByCode(convertedCondition.getOperatorCode()));
		assertEquals(String.valueOf(scd.getValue()), convertedCondition.getValue());
		assertEquals(Boolean.valueOf(ft.isSelected()), convertedCondition.getSelected());
		assertEquals(Boolean.valueOf(ft.isSortable()), convertedCondition.getSortable());
		assertEquals(Boolean.valueOf(ft.isMandatory()), convertedCondition.getMandatory());
		assertEquals(Boolean.valueOf(ft.isDisabled()), convertedCondition.getDisabled());
		assertEquals(ft.getName(), convertedCondition.getAttribute());
		assertEquals(ft.getEditor(), convertedCondition.getEditor());
		assertEquals(ft.getOperator(), convertedCondition.getOperatorCode());
		assertEquals(parameter.getName(), convertedCondition.getEditorParameters().entrySet().iterator().next().getKey());
		assertEquals(parameter.getValue(), convertedCondition.getEditorParameters().entrySet().iterator().next().getValue());
	}

	@Test
	public void shouldCreateSavedQueryWithEmptyAdvancedSearchMode()
	{
		// convert it back
		final Map<Locale, String> queryName = new HashMap<>();
		queryName.put(Locale.ENGLISH, TEST_QUERY_NAME);

		final AdvancedSearchData asd = new AdvancedSearchData();
		asd.setTypeCode(TEST_TYPE_CODE);
		asd.setGlobalOperator(ValueComparisonOperator.AND);
		asd.setIncludeSubtypes(true);
		asd.setSortData(new SortData(TEST_SORT_ATTRIBUTE, true));

		final BackofficeSavedQueryModel convertedSavedQuery = backofficeSavedQueriesServiceImpl.createSavedQuery(queryName, asd,
				principalModel, Collections.singletonList(userGroupModel));


		assertThat(convertedSavedQuery).isNotNull();
	}

	@Test
	public void testReferenceValueCondition() throws TypeNotFoundException
	{
		Mockito.when(dataType.getType()).thenReturn(DataType.Type.COMPOUND);
		Mockito.when(dataType.getClazz()).thenReturn(ProductModel.class);
		Mockito.when(dataAttribute.getValueType()).thenReturn(dataType);
		final ProductModel productModel = new ProductModel();
		productModel.setCode("testCode");

		final BackofficeSavedQueryModel savedQuery = createSavedQuery();
		final BackofficeSearchConditionModel condition = createConditionTemplate();
		condition.setValueReference(productModel);

		savedQuery.setConditions(Lists.newArrayList(condition));

		final AdvancedSearchInitContext advancedSearchInitContext = backofficeSavedQueriesServiceImpl
				.getAdvancedSearchInitContext(savedQuery);
		assertNotNull(advancedSearchInitContext);
		assertEquals(1, advancedSearchInitContext.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE).size());

		final SearchConditionData searchConditionData = advancedSearchInitContext.getAdvancedSearchData()
				.getConditions(TEST_ATTRIBUTE).iterator().next();
		assertEquals(productModel.getCode(), ((ProductModel) searchConditionData.getValue()).getCode());
	}

	@Test
	public void testEnumValueCondition()
	{
		Mockito.when(dataType.getType()).thenReturn(DataType.Type.ENUM);
		Mockito.when(dataType.getClazz()).thenReturn(ArticleApprovalStatus.class);
		Mockito.when(enumerationService.getEnumerationValue(ArticleApprovalStatus.APPROVED.getType(),
				ArticleApprovalStatus.APPROVED.getCode())).thenReturn(ArticleApprovalStatus.APPROVED);

		final BackofficeSavedQueryModel savedQuery = createSavedQuery();
		savedQuery.setConditions(Lists.newArrayList(createConditionWithEnumValue()));

		final AdvancedSearchInitContext advancedSearchInitContext = backofficeSavedQueriesServiceImpl
				.getAdvancedSearchInitContext(savedQuery);
		assertNotNull(advancedSearchInitContext);
		assertEquals(1, advancedSearchInitContext.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE).size());

		final SearchConditionData searchConditionData = advancedSearchInitContext.getAdvancedSearchData()
				.getConditions(TEST_ATTRIBUTE).iterator().next();
		assertEquals(ArticleApprovalStatus.APPROVED, searchConditionData.getValue());
	}

	@Test
	public void testLocalizedStringCondition()
	{
		Mockito.when(dataType.getType()).thenReturn(DataType.Type.ATOMIC);
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.STRING);
		final BackofficeSavedQueryModel savedQuery = createSavedQuery();
		savedQuery.setConditions(Lists.newArrayList(createConditionWithLocalizedValue(DataType.STRING, TEST_VALUE.toString())));

		final AdvancedSearchInitContext advancedSearchInitContext = backofficeSavedQueriesServiceImpl
				.getAdvancedSearchInitContext(savedQuery);

		assertNotNull(advancedSearchInitContext);
		assertEquals(1, advancedSearchInitContext.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE).size());

		final SearchConditionData searchCondition = advancedSearchInitContext.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE)
				.iterator().next();
		assertEquals(CANADA_FRENCH, ((Map<Locale, Object>) searchCondition.getValue()).entrySet().iterator().next().getKey());
		final Object value = ((Map<Locale, Object>) searchCondition.getValue()).entrySet().iterator().next().getValue();
		assertEquals(TEST_VALUE, ((Map<Locale, Object>) searchCondition.getValue()).entrySet().iterator().next().getValue());
		assertTrue(value instanceof String);
	}

	@Test
	public void testLocalizedIntegerCondition()
	{
		Mockito.when(dataType.getType()).thenReturn(DataType.Type.ATOMIC);
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.INTEGER);
		final BackofficeSavedQueryModel savedQuery = createSavedQuery();
		savedQuery
				.setConditions(Lists.newArrayList(createConditionWithLocalizedValue(DataType.INTEGER, TEST_INT_VALUE.toString())));

		final AdvancedSearchInitContext advancedSearchInitContext = backofficeSavedQueriesServiceImpl
				.getAdvancedSearchInitContext(savedQuery);

		assertNotNull(advancedSearchInitContext);
		assertEquals(1, advancedSearchInitContext.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE).size());

		final SearchConditionData searchCondition = advancedSearchInitContext.getAdvancedSearchData().getConditions(TEST_ATTRIBUTE)
				.iterator().next();
		assertEquals(CANADA_FRENCH, ((Map<Locale, Object>) searchCondition.getValue()).entrySet().iterator().next().getKey());
		final Object value = ((Map<Locale, Object>) searchCondition.getValue()).entrySet().iterator().next().getValue();
		assertEquals(TEST_INT_VALUE, ((Map<Locale, Object>) searchCondition.getValue()).entrySet().iterator().next().getValue());
		assertTrue(value instanceof Integer);
	}

	private BackofficeSavedQueryModel createSavedQuery()
	{
		final BackofficeSavedQueryModel savedQueryModel = new BackofficeSavedQueryModel();
		savedQueryModel.setSortAsc(Boolean.TRUE);
		savedQueryModel.setTokenizable(Boolean.TRUE);
		savedQueryModel.setIncludeSubtypes(Boolean.TRUE);
		savedQueryModel.setName(TEST_QUERY_NAME, Locale.ENGLISH);
		savedQueryModel.setSortAttribute(TEST_SORT_ATTRIBUTE);
		savedQueryModel.setGlobalOperatorCode(ValueComparisonOperator.AND.getOperatorCode());
		savedQueryModel.setQueryOwner(principalModel);
		savedQueryModel.setTypeCode(TEST_TYPE_CODE);
		savedQueryModel.setUserGroups(Collections.singletonList(userGroupModel));
		savedQueryModel.setSearchMode(SEARCH_MODE.name());
		return savedQueryModel;
	}

	private BackofficeSearchConditionModel createConditionWithLocalizedValue(final DataType dataType, final String value)
	{
		final BackofficeSearchConditionModel condition = createConditionTemplate();
		condition.setValue(value);
		condition.setLanguageCode(CANADA_FRENCH.toLanguageTag());
		return condition;
	}

	private BackofficeSearchConditionModel createConditionWithEnumValue()
	{
		final BackofficeSearchConditionModel condition = createConditionTemplate();
		condition
				.setValue(String.format("%s#%s", ArticleApprovalStatus.APPROVED.getType(), ArticleApprovalStatus.APPROVED.getCode()));
		return condition;
	}

	private BackofficeSearchConditionModel createConditionWithIntValue()
	{
		final BackofficeSearchConditionModel condition = createConditionTemplate();
		condition.setValue(String.valueOf(TEST_INT_VALUE));
		return condition;
	}

	private BackofficeSearchConditionModel createConditionTemplate()
	{
		final BackofficeSearchConditionModel condition = new BackofficeSearchConditionModel();
		condition.setAttribute(TEST_ATTRIBUTE);
		condition.setDisabled(Boolean.TRUE);
		condition.setMandatory(Boolean.TRUE);
		condition.setOperatorCode(ValueComparisonOperator.EQUALS.getOperatorCode());
		condition.setSortable(Boolean.TRUE);
		condition.setSelected(Boolean.TRUE);
		condition.setEditor(TEST_EDITOR);
		final Map<String, String> params = Maps.newHashMap();
		params.put(TEST_PARAM_NAME, TEST_PARAM_VALUE);
		condition.setEditorParameters(params);
		return condition;
	}
}

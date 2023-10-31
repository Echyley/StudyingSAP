/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.lookup;

import static de.hybris.platform.integrationservices.model.BaseMockAttributeDescriptorModelBuilder.attributeDescriptor;
import static de.hybris.platform.integrationservices.model.MockIntegrationObjectItemModelBuilder.itemModelBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.integrationservices.item.IntegrationItem;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.search.NonUniqueItemFoundException;
import de.hybris.platform.integrationservices.search.WhereClauseCondition;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.odata2services.odata.persistence.DefaultItemLookupRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.exception.MissingKeyNavigationPropertyException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.odata2.api.edm.EdmAnnotationAttribute;
import org.apache.olingo.odata2.api.edm.EdmAnnotations;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmProperty;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.core.ep.entry.ODataEntryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultItemLookupStrategyUnitTest
{
	private static final String IS_UNIQUE = "s:IsUnique";
	private static final String NULLABLE = "Nullable";
	private static final Date NOW = new Date();
	private static final String INTEGRATION_OBJECT_CODE = "IntegrationObject";
	private static final String ITEM_TYPE = "MyType";
	private static final String NAV_ITEM_TYPE = "MyNavType";

	@Mock(lenient = true)
	private FlexibleSearchService flexibleSearchService;
	@Mock(lenient = true)
	private IntegrationObjectService integrationObjectService;
	@Mock(lenient = true)
	private DefaultItemLookupRequestFactory itemLookupRequestFactory;
	@Captor
	private ArgumentCaptor<FlexibleSearchQuery> searchQueryCaptor;
	@InjectMocks
	private final DefaultItemLookupStrategy lookupStrategy = new DefaultItemLookupStrategy();

	private EdmEntitySet entitySet;
	private EdmEntityType entityType;
	private ODataEntry entity;

	private EdmEntitySet navEntitySet;
	private EdmEntityType entityTypeNav;
	private ItemLookupRequest lookupRequest;
	private final Map<String, Object> properties = Maps.newHashMap();
	private final Map<String, Object> navProperties = Maps.newHashMap();

	@Before
	public void setUp() throws EdmException
	{
		entity = mock(ODataEntryImpl.class);
		lenient().when(entity.getProperties()).thenReturn(properties);

		entityType = entityType(ITEM_TYPE);
		entitySet = entitySet(entityType);

		entityTypeNav = entityType(NAV_ITEM_TYPE);
		navEntitySet = entitySet(entityTypeNav);
		lookupRequest = lookupRequest();

		lenient().when(itemLookupRequestFactory.createFrom(any(), any(), any())).thenCallRealMethod();
		lenient().when(integrationObjectService.findItemAttributeName(anyString(), anyString(), anyString()))
				.thenAnswer(i -> i.getArgument(2));
	}


	@Test
	public void testExistingItem_NoParam() throws EdmException
	{
		givenNavigationProperties();
		givenItemsDefined(item(ITEM_TYPE), item(NAV_ITEM_TYPE, "d1", "d2"));

		final PK pk = PK.fromLong(1);
		final ItemModel itemNav = mock(ItemModel.class);
		lenient().when(itemNav.getPk()).thenReturn(pk);

		givenItemsFound(itemNav);

		lookupStrategy.lookup(lookupRequest);

		verify(flexibleSearchService, times(2)).search(searchQueryCaptor.capture());

		final Collection<String> queries = searchQueryCaptor.getAllValues().stream()
				.map(FlexibleSearchQuery::getQuery)
				.collect(Collectors.toList());
		assertThat(queries)
				.containsExactly("SELECT DISTINCT {mynavtype:pk} FROM {MyNavType* AS mynavtype} WHERE {mynavtype:d1} = ?d1 AND {mynavtype:d2} = ?d2",
						"SELECT DISTINCT {mytype:pk} FROM {MyType* AS mytype} WHERE {mytype:d} = ?d");

		final List<Map<String, Object>> params = searchQueryCaptor.getAllValues().stream()
				.map(FlexibleSearchQuery::getQueryParameters)
				.collect(Collectors.toList());
		assertThat(params).hasSize(2);
		assertThat(params.get(0)).hasSize(2)
				.containsEntry("d1", 10)
				.containsEntry("d2", 20);
		assertThat(params.get(1)).hasSize(1)
				.containsEntry("d", pk);
	}

	@Test
	public void testExistingItem_WithNavParam() throws EdmException
	{
		givenSimpleKeyProperties();
		givenNavigationProperties();
		givenItemsDefined(item(ITEM_TYPE, "a", "b"), item(NAV_ITEM_TYPE, "d1", "d2"));

		final PK pk = PK.fromLong(2);
		final ItemModel itemNav = mock(ItemModel.class);
		lenient().when(itemNav.getPk()).thenReturn(pk);

		givenItemsFound(itemNav);

		lookupStrategy.lookup(lookupRequest);

		verify(flexibleSearchService, atLeast(1)).search(searchQueryCaptor.capture());
		final List<String> queries = searchQueryCaptor.getAllValues().stream()
				.map(FlexibleSearchQuery::getQuery)
				.collect(Collectors.toList());
		assertThat(queries).hasSize(2);

		assertThat(queries.get(0))
				.contains("SELECT DISTINCT {mynavtype:pk} FROM {MyNavType* AS mynavtype} WHERE")
				.contains("{mynavtype:d1} = ?d1")
				.contains("{mynavtype:d2} = ?d2")
				.matches(query -> StringUtils.countMatches(query, "AND") == 1);
		assertThat(queries.get(1))
				.contains("SELECT DISTINCT {mytype:pk} FROM {MyType* AS mytype} WHERE")
				.contains("{mytype:a} = ?a")
				.contains("{mytype:b} = ?b")
				.contains("{mytype:d} = ?d")
				.matches(query -> StringUtils.countMatches(query, "AND") == 2);

		final List<Map<String, Object>> params = searchQueryCaptor.getAllValues().stream()
				.map(FlexibleSearchQuery::getQueryParameters)
				.collect(Collectors.toList());
		assertThat(params).hasSize(2);
		assertThat(params.get(0)).hasSize(2)
				.containsEntry("d1", 10)
				.containsEntry("d2", 20);
		assertThat(params.get(1)).hasSize(3)
				.containsEntry("a", 1)
				.containsEntry("b", NOW)
				.containsEntry("d", pk);
	}

	@Test
	public void testExistingItem_WithNavParam_ButNoNavItemFound() throws EdmException
	{
		givenSimpleKeyProperties();
		givenNavigationProperties();
		givenItemsDefined(item(ITEM_TYPE, "a", "b"), item(NAV_ITEM_TYPE, "d1", "d2"));
		givenItemsFound();

		assertThat(lookupStrategy.lookup(lookupRequest)).isNull();
	}

	@Test
	public void testExistingItem_NoNavParam() throws EdmException
	{
		final ItemModel item = mock(ItemModel.class);
		givenSimpleKeyProperties();
		givenItemsDefined(item(ITEM_TYPE, "a", "b"), item(NAV_ITEM_TYPE));
		givenItemsFound(item);

		final ItemModel itemModel = lookupStrategy.lookup(lookupRequest);

		verify(flexibleSearchService).search(searchQueryCaptor.capture());

		assertThat(itemModel).isSameAs(item);
		assertThat(searchQueryCaptor.getValue().getQuery()).isEqualTo("SELECT DISTINCT {mytype:pk} FROM {MyType* AS mytype} WHERE {mytype:a} = ?a AND {mytype:b} = ?b");
		assertThat(searchQueryCaptor.getValue().getQueryParameters()).hasSize(2)
				.containsEntry("a", 1)
				.containsEntry("b", NOW);
	}

	@Test
	public void testDuplicatedExistingItem_NoNavParam() throws EdmException
	{
		givenSimpleKeyProperties();
		givenItemsDefined(item(ITEM_TYPE, "a", "b"));
		givenItemsFound(mock(ItemModel.class), mock(ItemModel.class));

		assertThatThrownBy(() -> lookupStrategy.lookup(lookupRequest))
				.isInstanceOf(NonUniqueItemFoundException.class)
				.hasMessage("No unique item found for MyType: {a=1, b=" + NOW + "}");

		verify(flexibleSearchService).search(searchQueryCaptor.capture());

		assertThat(searchQueryCaptor.getValue().getQuery()).isEqualTo("SELECT DISTINCT {mytype:pk} FROM {MyType* AS mytype} WHERE {mytype:a} = ?a AND {mytype:b} = ?b");
		assertThat(searchQueryCaptor.getValue().getQueryParameters()).hasSize(2)
				.containsEntry("a", 1)
				.containsEntry("b", NOW);
	}

	@Test
	public void testMultipleExistingItem_NoKeyParam_NoNavParam()
	{
		givenItemsDefined(item(ITEM_TYPE), item(NAV_ITEM_TYPE));

		assertThatThrownBy(() -> lookupStrategy.lookup(lookupRequest))
				.isInstanceOf(NonUniqueItemFoundException.class)
				.hasMessage("No key properties defined for type [MyType]");

		verify(flexibleSearchService, never()).search(any(FlexibleSearchQuery.class));
	}

	@Test
	public void testNotExistingItem_NoNavParam() throws EdmException
	{
		givenSimpleKeyProperties();
		givenItemsDefined(item(ITEM_TYPE, "a", "b"));
		givenItemsFound();

		final ItemModel itemModel = lookupStrategy.lookup(lookupRequest);

		verify(flexibleSearchService).search(searchQueryCaptor.capture());

		assertThat(itemModel).isNull();
		assertThat(searchQueryCaptor.getValue().getQuery()).isEqualTo("SELECT DISTINCT {mytype:pk} FROM {MyType* AS mytype} WHERE {mytype:a} = ?a AND {mytype:b} = ?b");
		assertThat(searchQueryCaptor.getValue().getQueryParameters()).hasSize(2)
				.containsEntry("a", 1)
				.containsEntry("b", NOW);
	}

	@Test
	public void testEntityWithoutKeyNavProperty() throws EdmException
	{
		givenItemsDefined(item(ITEM_TYPE), item(NAV_ITEM_TYPE));

		final EdmTyped edmTyped = createNavigationProperty("unit", uniqueAnnotation(), nullableAnnotation(false));

		lenient().when(entityType.getProperty("unit")).thenReturn(edmTyped);
		lenient().when(entityType.getKeyPropertyNames()).thenReturn(Collections.emptyList());
		lenient().when(entityType.getNavigationPropertyNames()).thenReturn(Collections.singletonList("unit"));

		assertThatExceptionOfType(MissingKeyNavigationPropertyException.class)
				.isThrownBy(() -> lookupStrategy.lookup(lookupRequest()))
				.withMessageContaining("NavigationProperty [unit] is required for EntityType [MyType].");
	}

	@Test
	public void testEntityWithUniqueAndNullableNavProperty() throws EdmException
	{
		givenItemsDefined(item(ITEM_TYPE), item(NAV_ITEM_TYPE));
		givenItemsFound();

		final EdmTyped edmTyped = createNavigationProperty("unit", uniqueAnnotation(), nullableAnnotation(true));

		lenient().when(entityType.getProperty("unit")).thenReturn(edmTyped);
		lenient().when(entityType.getKeyPropertyNames()).thenReturn(Collections.emptyList());
		lenient().when(entityType.getNavigationPropertyNames()).thenReturn(Collections.singletonList("unit"));

		final ItemLookupRequest request = lookupRequest();
		lenient().when(request.getAttribute()).thenReturn(Pair.of("preventNonUniqueItemFoundExceptionParam", "doesNotMatter"));

		assertThat(lookupStrategy.lookup(request)).isNull();
	}

	@Test
	public void testLookupItems() throws EdmException
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound(item1, item2);

		final ItemLookupResult<ItemModel> searchResult = lookupStrategy.lookupItems(collectionLookupRequest());

		assertThat(searchResult.getEntries()).hasSize(2);
		assertThat(searchResult.getEntries()).containsExactly(item1, item2);
	}

	@Test
	public void testLookupItemsQuerySetsTotalCountToTrue() throws EdmException
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound(item1, item2);

		lookupStrategy.lookupItems(collectionLookupRequest());

		verify(flexibleSearchService).search(searchQueryCaptor.capture());
		assertThat(searchQueryCaptor.getValue().isNeedTotal()).isTrue();
	}

	@Test
	public void testLookupItemsQueryWhenInlineCountIsPresent() throws EdmException
	{
		final ItemModel item1 = mock(ItemModel.class);
		final ItemModel item2 = mock(ItemModel.class);
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound(item1, item2);

		lenient().when(lookupRequest.includeTotalCount()).thenReturn(true);
		lookupStrategy.lookupItems(lookupRequest);

		verify(flexibleSearchService).search(searchQueryCaptor.capture());
		assertThat(searchQueryCaptor.getValue().isNeedTotal()).isTrue();
	}

	@Test
	public void testLookupItemsQueryWhenODataEntryIsNotInItemLookupRequestNoWhereClauseInQuery() throws EdmException
	{
		givenSimpleKeyProperties();
		givenItemsDefined(item(ITEM_TYPE, "a", "b"));
		givenItemsFound();

		lenient().when(lookupRequest.getODataEntry()).thenReturn(null);
		lookupStrategy.lookupItems(lookupRequest);

		verify(flexibleSearchService).search(searchQueryCaptor.capture());
		assertThat(searchQueryCaptor.getValue().getQuery().toUpperCase()).doesNotContain("WHERE");
	}

	@Test
	public void testFlexibleSearchQueryForCollectionNeedsTotalCount() throws EdmException
	{
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound();

		lookupStrategy.lookupItems(collectionLookupRequest());

		verify(flexibleSearchService).search(searchQueryCaptor.capture());
		assertThat(searchQueryCaptor.getValue().getQuery()).contains("SELECT DISTINCT {mytype:pk} FROM {MyType* AS mytype}");
		assertThat(searchQueryCaptor.getValue().getQueryParameters()).hasSize(0);
		assertThat(searchQueryCaptor.getValue().isNeedTotal()).isTrue();
	}

	@Test
	public void testFlexibleSearchQueryForCollectionDifferentPageRequest() throws EdmException
	{
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound();

		lookupStrategy.lookupItems(pagedLookupRequest());

		verify(flexibleSearchService).search(searchQueryCaptor.capture());
		assertThat(searchQueryCaptor.getValue().getQuery()).contains("SELECT DISTINCT {mytype:pk} FROM {MyType* AS mytype}");
		assertThat(searchQueryCaptor.getValue().getCount()).isEqualTo(500);
		assertThat(searchQueryCaptor.getValue().getStart()).isEqualTo(100);
	}

	@Test
	public void testFlexibleSearchQueryForCollectionIsOrderedByPK() throws EdmException
	{
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound();

		lookupStrategy.lookupItems(collectionLookupRequest());

		verify(flexibleSearchService).search(searchQueryCaptor.capture());
		assertThat(searchQueryCaptor.getValue().getQuery()).contains("ORDER BY {mytype:pk}");
	}

	@Test
	public void testCountAllItemsInEntitySet() throws EdmException
	{
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound(10, mock(ItemModel.class), mock(ItemModel.class));

		final int count = lookupStrategy.count(lookupRequest());

		assertThat(count).isEqualTo(10);
	}

	@Test
	public void testCountQuery() throws EdmException
	{
		givenItemsDefined(item(ITEM_TYPE));
		givenItemsFound();

		lenient().when(lookupRequest.includeTotalCount()).thenReturn(true);
		lookupStrategy.count(lookupRequest);

		verify(flexibleSearchService).search(searchQueryCaptor.capture());
		assertThat(searchQueryCaptor.getValue().isNeedTotal()).isTrue();
	}

	private EdmEntitySet entitySet(final EdmEntityType type) throws EdmException
	{
		final EdmEntitySet set = mock(EdmEntitySet.class);
		lenient().when(set.getEntityType()).thenReturn(type);
		return set;
	}

	private EdmEntityType entityType(final String type) throws EdmException
	{
		final EdmEntityType edmType = mock(EdmEntityType.class);
		lenient().when(edmType.getName()).thenReturn(type);
		return edmType;
	}

	private void givenSimpleKeyProperties() throws EdmException
	{
		properties.put("a", 1);
		properties.put("b", DateUtils.toCalendar(NOW));

		lenient().when(entityType.getPropertyNames()).thenReturn(Arrays.asList("a", "b"));
		final EdmProperty a = uniqueProperty("a");
		final EdmProperty b = uniqueProperty("b");

		lenient().when(entityType.getProperty("a")).thenReturn(a);
		lenient().when(entityType.getProperty("b")).thenReturn(b);
	}

	private EdmProperty uniqueProperty(final String name) throws EdmException
	{
		final EdmAnnotations annotations = edmAnnotations(uniqueAnnotation());
		final EdmProperty prop = mock(EdmProperty.class);
		lenient().when(prop.getName()).thenReturn(name);
		lenient().when(prop.getAnnotations()).thenReturn(annotations);
		return prop;
	}

	private EdmAnnotationAttribute uniqueAnnotation()
	{
		final EdmAnnotationAttribute annotationsAttribute = mock(EdmAnnotationAttribute.class);
		lenient().when(annotationsAttribute.getName()).thenReturn(DefaultItemLookupStrategyUnitTest.IS_UNIQUE);
		lenient().when(annotationsAttribute.getText()).thenReturn("true");
		return annotationsAttribute;
	}

	private EdmAnnotationAttribute nullableAnnotation(final boolean value)
	{
		final EdmAnnotationAttribute annotationsAttribute = mock(EdmAnnotationAttribute.class);
		lenient().when(annotationsAttribute.getName()).thenReturn(DefaultItemLookupStrategyUnitTest.NULLABLE);
		lenient().when(annotationsAttribute.getText()).thenReturn(String.valueOf(value));
		return annotationsAttribute;
	}

	private EdmAnnotations edmAnnotations(final EdmAnnotationAttribute... annotationAttributes)
	{
		final EdmAnnotations annotations = mock(EdmAnnotations.class);
		lenient().when(annotations.getAnnotationAttributes()).thenReturn(Arrays.asList(annotationAttributes));
		return annotations;
	}

	private void givenNavigationProperties() throws EdmException
	{
		lenient().when(entityType.getNavigationPropertyNames()).thenReturn(Arrays.asList("c", "d"));
		final EdmNavigationProperty c = createNavigationProperty("c");
		lenient().when(entityType.getProperty("c")).thenReturn(c);
		final EdmNavigationProperty d = createNavigationProperty("d", uniqueAnnotation());
		lenient().when(entityType.getProperty("d")).thenReturn(d);

		lenient().when(entitySet.getRelatedEntitySet(d)).thenReturn(navEntitySet);

		// Configuring inner-lookup
		final ODataEntry navPropertyEntry = mock(ODataEntryImpl.class);
		properties.put("d", navPropertyEntry);

		navProperties.put("d1", 10);
		navProperties.put("d2", 20);

		lenient().when(entityTypeNav.getPropertyNames()).thenReturn(Arrays.asList("d1", "d2"));
		final EdmProperty a = uniqueProperty("d1");
		final EdmProperty b = uniqueProperty("d2");

		lenient().when(entityTypeNav.getProperty("d1")).thenReturn(a);
		lenient().when(entityTypeNav.getProperty("d2")).thenReturn(b);
		lenient().when(navPropertyEntry.getProperties()).thenReturn(navProperties);
	}

	private EdmNavigationProperty createNavigationProperty(final String name, final EdmAnnotationAttribute... annotationAttributes) throws EdmException
	{
		final EdmAnnotations annotations = edmAnnotations(annotationAttributes);

		final EdmNavigationProperty navigationProperty = mock(EdmNavigationProperty.class);
		lenient().when(navigationProperty.getName()).thenReturn(name);
		lenient().when(navigationProperty.getAnnotations()).thenReturn(annotations);
		return navigationProperty;
	}

	private EdmNavigationProperty createNavigationProperty(final String name) throws EdmException
	{
		return createNavigationProperty(name, new EdmAnnotationAttribute[0]);
	}

	private ItemLookupRequest lookupRequest() throws EdmException
	{
		final IntegrationItem item = mock(IntegrationItem.class);
		final ItemLookupRequest request = mock(ItemLookupRequest.class);
		final TypeDescriptor itemTypeDescriptor = mock(TypeDescriptor.class);
		lenient().when(request.getTypeDescriptor()).thenReturn(itemTypeDescriptor);
		lenient().when(request.getPaginationParameters()).thenReturn(Optional.empty());
		lenient().when(request.getEntitySet()).thenReturn(entitySet);
		lenient().doReturn(entitySet.getEntityType()).when(request).getEntityType();
		lenient().when(request.getODataEntry()).thenReturn(entity);
		lenient().when(request.getIntegrationItem()).thenReturn(item);
		lenient().when(request.getIntegrationObjectCode()).thenReturn(INTEGRATION_OBJECT_CODE);
		lenient().when(request.getAcceptLocale()).thenReturn(Locale.ENGLISH);
		return request;
	}

	private void givenItemsFound(final ItemModel... itemModels)
	{
		givenItemsFound(itemModels.length, itemModels);
	}

	private void givenItemsFound(final int totalCount, final ItemModel... itemModels)
	{
		final SearchResult result = mock(SearchResult.class);
		lenient().when(result.getCount()).thenReturn(itemModels.length);
		lenient().when(result.getTotalCount()).thenReturn(totalCount);
		lenient().when(result.getResult()).thenReturn(Arrays.asList(itemModels));
		lenient().doReturn(result).when(flexibleSearchService).search(any(FlexibleSearchQuery.class));
	}

	private void givenItemsDefined(final IntegrationObjectItemModel... items)
	{
		final Set<IntegrationObjectItemModel> models = new HashSet<>(Arrays.asList(items));
		lenient().doReturn(models).when(integrationObjectService).findAllIntegrationObjectItems(INTEGRATION_OBJECT_CODE);
	}

	private ItemLookupRequest collectionLookupRequest() throws EdmException
	{
		final ItemLookupRequest request = mock(ItemLookupRequest.class);
		lenient().when(request.getEntitySet()).thenReturn(entitySet);
		lenient().when(request.getIntegrationObjectCode()).thenReturn(INTEGRATION_OBJECT_CODE);
		lenient().when(request.getAcceptLocale()).thenReturn(Locale.ENGLISH);
		lenient().doReturn(entitySet.getEntityType()).when(request).getEntityType();
		return request;
	}

	private ItemLookupRequest pagedLookupRequest() throws EdmException
	{
		final ItemLookupRequest request = collectionLookupRequest();
		lenient().when(request.getSkip()).thenReturn(100);
		lenient().when(request.getTop()).thenReturn(500);
		return request;
	}

	private IntegrationObjectItemModel item(final String code, final String... keyNames)
	{
		final IntegrationObjectItemModel item = itemModelBuilder().withCode(code).build();
		final Set<IntegrationObjectItemAttributeModel> attributes = Stream.of(keyNames)
				.map(name -> attribute(item, name))
				.collect(Collectors.toSet());
		lenient().doReturn(attributes).when(item).getUniqueAttributes();
		return item;
	}

	private IntegrationObjectItemAttributeModel attribute(final IntegrationObjectItemModel item, final String name)
	{
		final AttributeDescriptorModel descriptor = attributeDescriptor().withQualifier(name).build();
		final IntegrationObjectItemAttributeModel attr = mock(IntegrationObjectItemAttributeModel.class);
		lenient().doReturn(name).when(attr).getAttributeName();
		lenient().doReturn(descriptor).when(attr).getAttributeDescriptor();
		lenient().doReturn(item).when(attr).getIntegrationObjectItem();
		return attr;
	}
}

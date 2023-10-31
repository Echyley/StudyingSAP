/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.pcmbackoffice.classificationgroupsbackoffice.facades;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.search.data.SearchQueryCondition;
import com.hybris.cockpitng.search.data.SearchQueryConditionList;
import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupAssignmentModel;
import com.hybris.classificationgroupsservices.model.ClassFeatureGroupModel;
import com.hybris.classificationgroupsservices.services.ClassFeatureGroupAssignmentService;


@RunWith(MockitoJUnitRunner.class)
public class ClassFeatureGroupAssignmentSearchFacadeTest
{
	@Mock
	private ClassFeatureGroupAssignmentService classFeatureGroupAssignmentService;
	@InjectMocks
	private ClassFeatureGroupAssignmentSearchFacade facade;

	@Test
	public void shouldReturnAllClassFeatureGroupAssignmentsIfParentClassificationClassIsNotDefined()
	{
		//given
		final ClassificationClassModel classificationClass = null;
		final ClassFeatureGroupAssignmentModel featureGroupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final SearchQueryData searchQueryData = mockSearchQueryData(classificationClass);

		given(classFeatureGroupAssignmentService.findAllFeatureGroupAssignments()).willReturn(List.of(featureGroupAssignment));

		//when
		final Pageable<ClassFeatureGroupAssignmentModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).containsExactlyInAnyOrder(featureGroupAssignment);
	}

	@Test
	public void shouldReturnEmptyListIfClassFeatureGroupAssignmentsHasDifferentClassificationClass()
	{
		//given
		final ClassFeatureGroupAssignmentModel featureGroupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		final SearchQueryData searchQueryData = mockSearchQueryData(classificationClass);

		given(classFeatureGroupAssignmentService.findAllFeatureGroupAssignments()).willReturn(List.of(featureGroupAssignment));
		given(featureGroupAssignment.getClassificationClass()).willReturn(mock(ClassificationClassModel.class));

		//when
		final Pageable<ClassFeatureGroupAssignmentModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).isEmpty();
	}

	@Test
	public void shouldReturnListOfClassFeatureGroupAssignmentForMatchingClassificationClass()
	{
		//given
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		final ClassFeatureGroupAssignmentModel featureGroupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final SearchQueryData searchQueryData = mockSearchQueryData(classificationClass);

		given(classFeatureGroupAssignmentService.findAllFeatureGroupAssignments()).willReturn(List.of(featureGroupAssignment));
		given(featureGroupAssignment.getClassificationClass()).willReturn(classificationClass);
		given(featureGroupAssignment.getClassFeatureGroup()).willReturn(null);

		//when
		final Pageable<ClassFeatureGroupAssignmentModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).containsExactlyInAnyOrder(featureGroupAssignment);
	}

	@Test
	public void shouldFilterOutClassFeatureGroupAssignmentIfGroupIsAlreadyAssigned()
	{
		//given
		final ClassFeatureGroupAssignmentModel featureGroupAssignment = mock(ClassFeatureGroupAssignmentModel.class);
		final ClassificationClassModel classificationClass = mock(ClassificationClassModel.class);
		final SearchQueryData searchQueryData = mockSearchQueryData(classificationClass);

		given(classFeatureGroupAssignmentService.findAllFeatureGroupAssignments()).willReturn(List.of(featureGroupAssignment));
		given(featureGroupAssignment.getClassificationClass()).willReturn(classificationClass);
		given(featureGroupAssignment.getClassFeatureGroup()).willReturn(mock(ClassFeatureGroupModel.class));

		//when
		final Pageable<ClassFeatureGroupAssignmentModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).isEmpty();
	}

	private SearchQueryData mockSearchQueryData(final ClassificationClassModel classificationClass)
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final SearchQueryConditionList queryConditionList = mock(SearchQueryConditionList.class);
		final SearchQueryCondition classificationClassCondition = mock(SearchQueryCondition.class);
		final List list = Lists.newArrayList(queryConditionList);

		given(queryConditionList.getConditions()).willReturn(List.of(classificationClassCondition));
		given(searchQueryData.getConditions()).willReturn(list);
		given(classificationClassCondition.getValue()).willReturn(classificationClass);
		given(searchQueryData.getPageSize()).willReturn(1);
		return searchQueryData;
	}
}

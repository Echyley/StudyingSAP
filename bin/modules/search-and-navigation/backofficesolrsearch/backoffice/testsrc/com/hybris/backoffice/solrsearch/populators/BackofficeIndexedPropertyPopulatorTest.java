/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import org.assertj.core.api.BDDAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.then;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class BackofficeIndexedPropertyPopulatorTest
{

	private BackofficeIndexedPropertyPopulator testSubject;

	@Mock
	private SolrIndexedPropertyModel solrIndexedPropertyModelMock;

	@Mock
	private IndexedProperty indexedPropertyMock;

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	@Captor
	private ArgumentCaptor<ClassAttributeAssignmentModel> classAttributeAssignmentModelArgumentCaptor;

	@Before
	public void setUp()
	{
		testSubject = new BackofficeIndexedPropertyPopulator();
	}

	@Test
	public void shouldPopulateBackofficeDisplayName()
	{
		//when
		testSubject.populate(solrIndexedPropertyModelMock, indexedPropertyMock);

		//then
		then(indexedPropertyMock).should().setBackofficeDisplayName(stringCaptor.capture());
		BDDAssertions.then(stringCaptor.getValue()).isEqualTo(solrIndexedPropertyModelMock.getBackofficeDisplayName());
	}

	@Test
	public void shouldClassAttributeAssignment()
	{
		//when
		testSubject.populate(solrIndexedPropertyModelMock, indexedPropertyMock);

		//then
		then(indexedPropertyMock).should().setClassAttributeAssignment(classAttributeAssignmentModelArgumentCaptor.capture());
		BDDAssertions.then(classAttributeAssignmentModelArgumentCaptor.getValue())
				.isEqualTo(solrIndexedPropertyModelMock.getClassAttributeAssignment());
	}

}

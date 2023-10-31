/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlesearch.provider;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests the units of class
 * {@link StructuredArticleComponentNamesValueResolver}.
 */
@UnitTest
@SuppressWarnings("javadoc")
public class StructuredArticleComponentNamesValueResolverTest extends BaseStructuredArticleComponentValueResolverTest {
	protected static final String NAME_1 = "name1";
	protected static final String NAME_2 = "name2";
	protected static final String NAME_1_DE = "name1_de";
	protected static final String NAME_2_DE = "name2_de";
	protected static final String NAME_1_EN = "name1_en";
	protected static final String NAME_2_EN = "name2_en";

	private StructuredArticleComponentNamesValueResolver classUnderTest;

	@Before
	public void setUp() {
		if (componentProduct1 != null) {
			when(componentProduct1.getName()).thenReturn(NAME_1);
			when(componentProduct1.getName(getLocaleEN())).thenReturn(NAME_1_EN);
			when(componentProduct1.getName(getLocaleDE())).thenReturn(NAME_1_DE);

			when(componentProduct2.getName()).thenReturn(NAME_2);
			when(componentProduct2.getName(getLocaleEN())).thenReturn(NAME_2_EN);
			when(componentProduct2.getName(getLocaleDE())).thenReturn(NAME_2_DE);

			classUnderTest = new StructuredArticleComponentNamesValueResolver();
			classUnderTest.setCommonI18NService(getCommonI18NService());
		}
	}

	@Test
	public void resolveNoName() throws FieldValueProviderException {
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties();

		// when
		if (product != null) {
			classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

			// then
			verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any());
			verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any(), any(String.class));
		}
	}

	@Test
	public void resolveNoNameForNotStructuredArticle() throws FieldValueProviderException {
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentNames"),
				true, false);
		if (product != null) {
			when(product.getStructuredArticleType()).thenReturn(null);

			// when
			classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

			// then
			verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any());
			verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any(), any(String.class));
		}
	}

	@Test
	public void resolveNamesMultivalueNoLocale() throws FieldValueProviderException {
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentNames"),
				true, false);

		// when
		if (product != null) {
			classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

			// then
			final IndexedProperty[] indexedPropertiesArray = indexedProperties
					.toArray(new IndexedProperty[indexedProperties.size()]);
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_1, null);
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_2, null);
		}
	}

	@Test
	public void resolveNamesSinglevalueNoLocale() throws FieldValueProviderException {
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentNames"),
				false, false);

		// when
		if (product != null) {
			classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

			// then
			final IndexedProperty[] indexedPropertiesArray = indexedProperties
					.toArray(new IndexedProperty[indexedProperties.size()]);
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_1 + " " + NAME_2, null);
		}
	}

	@Test
	public void resolveNamesMultivalueLocale() throws FieldValueProviderException {
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentNames"),
				true, true);

		// when
		if (product != null) {
			classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

			// then
			final IndexedProperty[] indexedPropertiesArray = indexedProperties
					.toArray(new IndexedProperty[indexedProperties.size()]);
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_1_EN,
					getLanguageModelEN().getIsocode());
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_2_EN,
					getLanguageModelDE().getIsocode());
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_1_DE,
					getLanguageModelEN().getIsocode());
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_2_DE,
					getLanguageModelDE().getIsocode());
		}
	}

	@Test
	public void resolveNamesSinglevalueLocale() throws FieldValueProviderException {
		// given
		final Collection<IndexedProperty> indexedProperties = getIndexedProperties(Arrays.asList("componentNames"),
				false, true);

		// when
		if (product != null) {
			classUnderTest.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

			// then
			final IndexedProperty[] indexedPropertiesArray = indexedProperties
					.toArray(new IndexedProperty[indexedProperties.size()]);
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_1_EN + " " + NAME_2_EN,
					getLanguageModelEN().getIsocode());
			verify(getInputDocument()).addField(indexedPropertiesArray[0], NAME_1_DE + " " + NAME_2_DE,
					getLanguageModelDE().getIsocode());
		}
	}

}

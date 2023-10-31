/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.pk.reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.util.type.BackofficeTypeUtils;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
@RunWith(MockitoJUnitRunner.class)
public class DefaultReferencePKEditorTest extends AbstractCockpitEditorRendererUnitTest<Object, DefaultReferencePKEditor>
{

	@InjectMocks
	private final DefaultReferencePKEditor editor = new DefaultReferencePKEditor();

	@Mock
	private LabelService labelService;

	@Mock
	private ModelService modelService;

	@Mock
	private BackofficeTypeUtils backofficeTypeUtils;

	@Mock
	private EditorContext<Object> context;

	@Override
	public DefaultReferencePKEditor getEditorInstance()
	{
		return editor;
	}

	@Test
	public void getSupportedTypesNoTypeSet()
	{
		//given
		when(context.getParameterAs("supportedTypes")).thenReturn(null);

		//when
		final String[] types = getEditorInstance().getSupportedTypes(context);

		//then
		assertThat(types).containsOnly(ItemModel._TYPECODE);
	}

	@Test
	public void getSupportedTypesOneTypeSet()
	{
		//given
		when(context.getParameterAs("supportedTypes")).thenReturn(ProductModel._TYPECODE);

		//when
		final String[] types = getEditorInstance().getSupportedTypes(context);

		//then
		assertThat(types).containsOnly(ProductModel._TYPECODE);
	}

	@Test
	public void getSupportedTypesManyTypesSet()
	{
		//given
		when(context.getParameterAs("supportedTypes"))
				.thenReturn(String.format("%s,%s", CategoryModel._TYPECODE, ProductModel._TYPECODE));

		//when
		final String[] types = getEditorInstance().getSupportedTypes(context);

		//then
		assertThat(types).containsOnly(CategoryModel._TYPECODE, ProductModel._TYPECODE);
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.variant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.testing.AbstractCockpitngUnitTest;


@RunWith(MockitoJUnitRunner.class)
public class VariantAttributesSectionRendererTest extends AbstractCockpitngUnitTest<VariantAttributesSectionRenderer>
{
	public static final String CURRENT_TYPE = "currentType";
	@InjectMocks
	@Spy
	private VariantAttributesSectionRenderer renderer;
	@Mock
	private WidgetInstanceManager wim;
	@Mock
	private DefaultWidgetModel widgetModel;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private DataType dataType;

	@Before
	public void setUp()
	{
		doReturn(widgetModel).when(wim).getModel();

	}

	@Test
	public void shouldTakeCurrentObjectForUnboundSection()
	{
		doReturn(Collections.singletonList("attr1")).when(renderer).getRenderedQualifiers(any(DataType.class));
		doReturn(dataType).when(widgetModel).getValue(eq(CURRENT_TYPE), eq(DataType.class));

		final Collection<String> qualifiers = renderer.getRenderedQualifiers(wim);

		assertThat(qualifiers).containsExactly("attr1");
		verify(widgetModel).getValue(eq(CURRENT_TYPE), any());
		verify(renderer).getRenderedQualifiers(dataType);
	}


	@Test
	public void shouldTakeEditedVariantProductWhenCurrentTypeIsNull() throws TypeNotFoundException
	{
		final Object editedObject = new Object();
		doReturn(null).when(widgetModel).getValue(eq(CURRENT_TYPE), eq(DataType.class));
		doReturn(editedObject).when(widgetModel).getValue(eq(VariantAttributesSectionRenderer.EDITED_VARIANT_PRODUCT), any());
		doReturn(Object.class.getName()).when(typeFacade).getType(editedObject);
		doReturn(Collections.singletonList("attr1")).when(renderer).getRenderedQualifiers(any(DataType.class));
		doReturn(dataType).when(typeFacade).load(Object.class.getName());

		final Collection<String> qualifiers = renderer.getRenderedQualifiers(wim);

		assertThat(qualifiers).containsExactly("attr1");
		verify(widgetModel).getValue(eq(CURRENT_TYPE), any());
		verify(widgetModel).getValue(eq(VariantAttributesSectionRenderer.EDITED_VARIANT_PRODUCT), any());
	}

	@Test
	public void shouldReturnEmptyListOnEmptyObject()
	{
		doReturn(null).when(widgetModel).getValue(eq(CURRENT_TYPE), eq(DataType.class));
		doReturn(null).when(widgetModel).getValue(eq(VariantAttributesSectionRenderer.EDITED_VARIANT_PRODUCT), any());

		final Collection<String> qualifiers = renderer.getRenderedQualifiers(wim);

		assertThat(qualifiers).isEmpty();
	}

	@Test
	public void shouldReturnEmptyListOnUnknownObjectType() throws TypeNotFoundException
	{
		doReturn(null).when(widgetModel).getValue(eq(CURRENT_TYPE), eq(DataType.class));
		doReturn("test").when(widgetModel).getValue(eq(VariantAttributesSectionRenderer.EDITED_VARIANT_PRODUCT), any());
		doReturn(String.class.getName()).when(typeFacade).getType("test");
		doThrow(TypeNotFoundException.class).when(typeFacade).load(String.class.getName());

		final Collection<String> qualifiers = renderer.getRenderedQualifiers(wim);

		assertThat(qualifiers).isEmpty();
	}
}

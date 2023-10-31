/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.accessors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.user.TitleModel;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.core.model.impl.DefaultModelValueHandler;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;


@RunWith(MockitoJUnitRunner.class)
public class RuntimeAttributesPropertyAccessorTest
{
	@Mock
	private TypeFacade typeFacade;
	@InjectMocks
	private RuntimeAttributesPropertyAccessor propertyAccessor;

	@Test
	public void testCanReadAndWriteForRuntimeAttribute() throws TypeNotFoundException, AccessException
	{
		//given
		final TitleModel title = mock(TitleModel.class);
		final String qualifier = "runtimeAttribute";
		final DataAttribute dataAttribute = mock(DataAttribute.class);
		final DataType dataType = Mockito.mock(DataType.class);
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isRuntimeAttribute();
		Mockito.when(dataType.getAttribute(Mockito.any())).thenReturn(dataAttribute);
		Mockito.when(typeFacade.load(Mockito.any())).thenReturn(dataType);

		//when
		final boolean canReadRuntimeAttr = propertyAccessor.canRead(getDefaultEvaluationContext(), title, qualifier);
		final boolean canWriteRuntimeAttr = propertyAccessor.canWrite(getDefaultEvaluationContext(), title, qualifier);

		//then
		assertThat(canReadRuntimeAttr).isTrue();
		assertThat(canWriteRuntimeAttr).isTrue();
	}

	@Test
	public void testCantReadAndWriteForNotRuntimeAttribute() throws TypeNotFoundException, AccessException
	{
		//given
		final TitleModel title = mock(TitleModel.class);
		final String qualifier = "name";
		final DataAttribute dataAttribute = mock(DataAttribute.class);
		final DataType dataType = Mockito.mock(DataType.class);
		Mockito.doReturn(Boolean.FALSE).when(dataAttribute).isRuntimeAttribute();
		Mockito.when(dataType.getAttribute(Mockito.any())).thenReturn(dataAttribute);
		Mockito.when(typeFacade.load(Mockito.any())).thenReturn(dataType);

		//when
		final boolean canReadNonRuntimeAttr = propertyAccessor.canRead(getDefaultEvaluationContext(), title, qualifier);
		final boolean canWriteNonRuntimeAttr = propertyAccessor.canWrite(getDefaultEvaluationContext(), title, qualifier);

		//then
		assertThat(canReadNonRuntimeAttr).isFalse();
		assertThat(canWriteNonRuntimeAttr).isFalse();
	}


	@Test
	public void shouldReadRuntimeProperty() throws TypeNotFoundException, AccessException
	{
		//given
		final TitleModel title = mock(TitleModel.class);
		final String qualifier = "testQualifier";
		final String qualifierValue = "testValue";
		Mockito.when(title.getProperty(qualifier)).thenReturn(qualifierValue);
		//when
		final TypedValue value = propertyAccessor.read(getDefaultEvaluationContext(), title, qualifier);
		//then
		assertThat(value.getValue()).isEqualTo(qualifierValue);
	}

	private EvaluationContext getDefaultEvaluationContext()
	{
		final WidgetModel widgetModel = new DefaultWidgetModel(new HashMap<String, Object>(), new DefaultModelValueHandler());
		return new StandardEvaluationContext(widgetModel);
	}
}

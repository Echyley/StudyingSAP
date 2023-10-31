/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 *
 */
package de.hybris.platform.warehousingbackoffice.renderers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hybris.cockpitng.core.config.impl.jaxb.editorarea.CustomPanel;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.labels.LabelService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.warehousing.model.AtpFormulaModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;

import java.util.Map;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AtpFormulaPanelRendererTest
{
	@InjectMocks
	private final AtpFormulaPanelRenderer renderer = new AtpFormulaPanelRenderer();
	@Mock
	private ObjectFacade objectFacade;
	@Mock
	private TypeFacade typeFacade;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private AtpFormulaModel atpFormulaModel;
	@Mock
	private WidgetModel widgetModel;
	@Mock
	private WidgetInstanceManager widgetInstanceManager;
	@Mock
	private Div attributeContainer;
	@Mock
	private CustomPanel customPanel;
	@Mock
	private DataType dataType;
	@Mock
	private LabelService labelService;

	@Before
	public void setUp()
	{
		when(widgetInstanceManager.getModel()).thenReturn(widgetModel);
		renderer.setAtpFormulaVar2ArithmeticOperatorMap(Map.of());
	}

	@Test
	public void testRendererSuccessful() throws TypeNotFoundException
	{
		//Given
		when(typeFacade.load(AtpFormulaModel._TYPECODE)).thenReturn(dataType);
		when(dataType.getClazz()).thenReturn(anyObject());
		when(permissionFacade.canReadInstanceProperty(any(),AtpFormulaModel.AVAILABILITY)).thenReturn(Boolean.TRUE);

		// render
		final Div parent = new Div();
		renderer.render(parent, customPanel, atpFormulaModel, dataType, widgetInstanceManager);

		// verify
		verify(typeFacade).load(anyString());
		assertEquals(10,parent.getChildren().get(0).getChildren().get(0).getChildren().size());
	}

	@Test
	public void testRendererNotAtpFormula() throws TypeNotFoundException
	{
		//Given

		// render
		final Div parent = new Div();
		renderer.render(parent, customPanel, null, dataType, widgetInstanceManager);

		// verify
		verify(typeFacade,never()).load(anyString());
	}

	@Test
	public void testRendererDisabledNoAccess() throws TypeNotFoundException
	{
		//Given
		when(typeFacade.load(AtpFormulaModel._TYPECODE)).thenReturn(dataType);
		when(dataType.getClazz()).thenReturn(anyObject());
		when(permissionFacade.canReadInstanceProperty(any(),AtpFormulaModel.AVAILABILITY)).thenReturn(Boolean.FALSE);

		// render
		final Div parent = new Div();
		renderer.render(parent, customPanel, atpFormulaModel, dataType, widgetInstanceManager);
		final Label label = (Label) parent.getChildren().get(0);

		// verify
		assertNotNull(label);
	}
}

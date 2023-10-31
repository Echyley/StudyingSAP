/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modals.controllers;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.DeclaredInput;
import com.hybris.cockpitng.testing.annotation.DeclaredViewEvent;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;
import com.hybris.cockpitng.testing.annotation.NullSafeWidget;

@DeclaredInput(value = "openVirtualAttributeModal", socketType = List.class)
@DeclaredViewEvent(componentID = "createButton", eventName = Events.ON_CLICK)
@NullSafeWidget(false)
@ExtensibleWidget(false)
public class CreateVirtualAttributeModalControllerUnitTest extends AbstractWidgetUnitTest<CreateVirtualAttributeModalController>
{
	@Mock(lenient = true)
	protected Combobox scriptLocationComboBox;
	@Mock(lenient = true)
	protected Combobox virtualAttributeDescriptorComboBox;
	@Mock(lenient = true)
	protected Combobox descriptorTypeComboBox;
	@Mock(lenient = true)
	protected Textbox virtualAttributeAlias;
	@Mock(lenient = true)
	protected Textbox virtualAttributeDescriptorCode;
	@Mock(lenient = true)
	protected Button createButton;
	@Mock(lenient = true)
	protected Button clearButton;
	@Mock(lenient = true)
	protected ReadService readService;
	@Spy
	@InjectMocks
	private CreateVirtualAttributeModalController containerController;

	@Override
	protected CreateVirtualAttributeModalController getWidgetController()
	{
		return containerController;
	}

	public void setup()
	{
		lenient().when(readService.getScriptModels()).thenReturn(Collections.emptyList());
	}

	@Test
	public void setCurrentListboxAttributesHandlesNull()
	{
		containerController.loadCreateIntegrationObjectModal(null);
		assertEquals("Set an empty list if it's set null", Collections.emptyList(),
				containerController.currentListboxAttributes);
	}

	@Test
	public void setCurrentListboxAttributesDoesNotStoreReference()
	{
		final List<AbstractListItemDTO> list = new ArrayList<>(List.of(mock(AbstractListItemDTO.class)));
		containerController.loadCreateIntegrationObjectModal(list);
		list.add(mock(AbstractListItemDTO.class));
		assertNotEquals("Two lists contain different elements.", list, containerController.currentListboxAttributes);
		assertNotSame("The references are different.", list, containerController.currentListboxAttributes);
	}
}

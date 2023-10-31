/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.hybris.cockpitng.editors.EditorListener;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfigParameterOnOpenEventListenerTest
{
	@Mock
	private Combobox comboBox;
	@Mock
	private EditorListener<Object> listener;
	@Mock
	private OpenEvent event;
	@Mock
	private Comboitem comboItem ;

	private final Object selectedObject = new Object();

	@InjectMocks
	private ProductConfigParameterOnOpenEventListener classUnderTest;


	@Test
	public void testOnEventWithoutSelectedValue()
	{
		when(event.isOpen()).thenReturn(false);
		classUnderTest.onEvent(event);
	}

	@Test
	public void testOnEventWithSelectedValue()
	{
		when(event.isOpen()).thenReturn(false);
		when(comboBox.getSelectedItem()).thenReturn(comboItem);
		when(comboItem.getValue()).thenReturn(selectedObject);

		classUnderTest.onEvent(event);

		verify(listener, times(1)).onValueChanged(selectedObject);
	}

	@Test
	public void testOnEventWithSelectedValueCalledTwice()
	{
		when(event.isOpen()).thenReturn(true);
		when(comboBox.getSelectedItem()).thenReturn(comboItem);
		when(comboItem.getValue()).thenReturn(selectedObject);

		classUnderTest.onEvent(event);

		when(event.isOpen()).thenReturn(true);
		classUnderTest.onEvent(event);

		verify(listener, times(0)).onValueChanged(selectedObject);
	}
}

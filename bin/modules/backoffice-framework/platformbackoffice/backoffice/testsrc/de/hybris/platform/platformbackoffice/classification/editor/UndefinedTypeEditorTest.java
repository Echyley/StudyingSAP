/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.classification.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorRegistry;
import com.hybris.cockpitng.testing.util.BeanLookup;
import com.hybris.cockpitng.testing.util.BeanLookupFactory;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class UndefinedTypeEditorTest
{
	@Mock
	private Component parent;

	@Mock
	private EditorContext<Object> context;

	@Mock
	private EditorRegistry editorRegistry;

	@Test
	public void render()
	{
		// given
		final BeanLookup beanLookup = BeanLookupFactory.createBeanLookup().registerBean("editorRegistry", editorRegistry)
				.getLookup();
		CockpitTestUtil.mockBeanLookup(beanLookup);
		final ArgumentCaptor<Component> argCaptor = ArgumentCaptor.forClass(Component.class);

		final UndefinedTypeEditor undefinedTypeEditor = new UndefinedTypeEditor();

		// when
		undefinedTypeEditor.render(parent, context, null);

		// then
		Mockito.verify(parent).appendChild(argCaptor.capture());
		final Editor editor = (Editor) argCaptor.getValue();
		assertThat(editor.isReadOnly()).isTrue();
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.pk;

import com.hybris.cockpitng.testing.AbstractCockpitEditorRendererUnitTest;


public class DefaultPKEditorTest extends AbstractCockpitEditorRendererUnitTest<Object, DefaultPKEditor>
{
	private final DefaultPKEditor editor = new DefaultPKEditor();

	@Override
	public DefaultPKEditor getEditorInstance()
	{
		return editor;
	}
}

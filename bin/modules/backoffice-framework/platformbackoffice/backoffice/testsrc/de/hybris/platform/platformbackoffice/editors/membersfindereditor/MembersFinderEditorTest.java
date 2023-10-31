/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.membersfindereditor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchData;
import com.hybris.backoffice.widgets.advancedsearch.impl.AdvancedSearchInitContext;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.AdvancedSearch;
import com.hybris.cockpitng.core.config.impl.jaxb.hybris.advancedsearch.FieldListType;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.testing.AbstractWidgetUnitTest;
import com.hybris.cockpitng.testing.annotation.ExtensibleWidget;


@ExtensibleWidget(level = ExtensibleWidget.ALL)
public class MembersFinderEditorTest extends AbstractWidgetUnitTest<MembersFinderEditor>
{

	@Spy
	private MembersFinderEditor membersFinderEditor;

	@Mock
	private AdvancedSearch advancedSearch;
	@Mock
	private FieldListType emptyFieldList;

	@Before
	public void setUp()
	{
		when(emptyFieldList.getField()).thenReturn(Collections.emptyList());
	}

	@Test
	public void checkSearchConfigCloneIsUsed()
	{
		// given
		when(membersFinderEditor.loadAdvancedConfiguration(widgetInstanceManager, MembersFinderEditor.PRINCIPAL_SEARCH_EDITOR_NAME))
				.thenReturn(advancedSearch);
		when(advancedSearch.getFieldList()).thenReturn(emptyFieldList);

		final EditorContext<Object> context = mock(EditorContext.class);
		final AdvancedSearchData advancedSearchData = mock(AdvancedSearchData.class);

		// when
		final AdvancedSearchInitContext initContext = membersFinderEditor.createSearchContext(advancedSearchData,
				widgetInstanceManager, context);

		// then
		assertThat(initContext.getAdvancedSearchConfig()).isNotSameAs(advancedSearch);
	}

	@Override
	protected MembersFinderEditor getWidgetController()
	{
		return membersFinderEditor;
	}
}

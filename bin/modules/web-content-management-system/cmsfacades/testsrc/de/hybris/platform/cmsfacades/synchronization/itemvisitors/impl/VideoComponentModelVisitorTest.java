/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.itemvisitors.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.VideoComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.VISITORS_CTX_LOCALES;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class VideoComponentModelVisitorTest
{
	@Mock
	private VideoComponentModel component;
	@Mock
	private AbstractRestrictionModel restriction1;
	@Mock
	private AbstractRestrictionModel restriction2;
	@Mock
	private MediaContainerModel media;

	@InjectMocks
	private VideoComponentModelVisitor visitor;

	@Before
	public void setUp()
	{
		when(component.getRestrictions()).thenReturn(asList(restriction1, restriction2));
		when(component.getThumbnail(Locale.ENGLISH)).thenReturn(media);
	}

	@Test
	public void willCollectRestrictionsAndMediaForVideoComponent()
	{
		final Map<String, Object> context = new HashMap<>();
		context.put(VISITORS_CTX_LOCALES, Arrays.asList(Locale.ENGLISH));

		final List<ItemModel> visit = visitor.visit(component, null, context);

		assertThat(visit, containsInAnyOrder(restriction1, restriction2, media));
	}
}

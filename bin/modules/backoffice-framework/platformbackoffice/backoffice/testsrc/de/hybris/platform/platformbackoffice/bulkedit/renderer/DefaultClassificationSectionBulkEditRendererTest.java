/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.bulkedit.renderer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.platformbackoffice.classification.provider.ClassificationSectionNameProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;

import com.hybris.cockpitng.testing.util.CockpitTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class DefaultClassificationSectionBulkEditRendererTest
{

	@Mock
	private ClassificationSectionNameProvider classificationSectionNameProvider;

	@Spy
	@InjectMocks
	private DefaultClassificationSectionBulkEditRenderer renderer;

	@Before
	public void setUp()
	{
		CockpitTestUtil.mockZkEnvironment();
	}

	@Test
	public void shouldRenderClassificationSectionWithCorrectNameForGivenClassificationClass()
	{
		// given
		final Div parent = new Div();
		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);

		given(classificationSectionNameProvider.provide(classificationClassModel))
				.willReturn("Default - Online: First Class : Class001");

		// when
		final Component section = renderer.render(parent, classificationClassModel);

		// then
		assertThat(section).isNotNull();
		assertThat(section.getParent()).isEqualTo(parent);
		final Caption caption = (Caption) section.query(".yw-bulkedit-groupbox-caption");
		assertThat(caption.getLabel()).isEqualTo("Default - Online: First Class : Class001");
	}

	@Test
	public void shouldSectionBeOpenedByDefault()
	{
		// given
		final Div parent = new Div();
		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);

		// when
		final Groupbox section = (Groupbox) renderer.render(parent, classificationClassModel);

		// then
		assertThat(section.isOpen()).isEqualTo(true);
	}

	@Test
	public void shouldSectionBeClosedAfterClickEvent()
	{
		// given
		final Div parent = new Div();
		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		final Groupbox section = (Groupbox) renderer.render(parent, classificationClassModel);
		final Button expandButton = (Button) section.query(".yw-expandCollapse");

		// when
		CockpitTestUtil.simulateEvent(expandButton, new Event(Events.ON_CLICK));

		// then
		assertThat(section.isOpen()).isEqualTo(false);
	}

}

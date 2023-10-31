/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.labelproviders;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.labels.LabelService;
import com.hybris.cockpitng.testing.util.CockpitTestUtil;
import com.hybris.cockpitng.testing.util.LabelLookup;
import com.hybris.cockpitng.testing.util.LabelLookupFactory;


@RunWith(MockitoJUnitRunner.class)
public class WorkflowItemAttachmentLabelProviderTest
{

	private static final String LABEL_KEY_NOT_SAVED = "labelprovider.notsaved";

	@InjectMocks
	private WorkflowItemAttachmentLabelProvider labelProvider;

	@Mock
	private ModelService modelService;

	@Mock
	private LabelService labelService;

	@Test
	public void shouldIncludeNameIfSet()
	{
		// given
		final String NAME = "name";
		final WorkflowItemAttachmentModel attachmentModel = mock(WorkflowItemAttachmentModel.class);
		when(attachmentModel.getName()).thenReturn(NAME);

		// when
		final String label = labelProvider.getLabel(attachmentModel);

		// then
		assertThat(label).startsWith(NAME);
	}

	@Test
	public void shouldIncludeItemLabelIfSet()
	{
		// given
		final String LABEL = "label";
		final WorkflowItemAttachmentModel attachmentModel = mock(WorkflowItemAttachmentModel.class);
		when(attachmentModel.getItem()).thenReturn(mock(ItemModel.class));
		when(labelService.getObjectLabel(any())).thenReturn(LABEL);

		// when
		final String label = labelProvider.getLabel(attachmentModel);

		// then
		assertThat(label).contains(LABEL);
	}

	@Test
	public void shouldIncludeNameAndItemLabelIfSet()
	{
		// given
		final String LABEL = "label";
		final String NAME = "name";
		final WorkflowItemAttachmentModel attachmentModel = mock(WorkflowItemAttachmentModel.class);
		when(attachmentModel.getName()).thenReturn(NAME);
		when(attachmentModel.getItem()).thenReturn(mock(ItemModel.class));
		when(labelService.getObjectLabel(any())).thenReturn(LABEL);

		// when
		final String label = labelProvider.getLabel(attachmentModel);

		// then
		assertThat(label).startsWith(NAME);
		assertThat(label).contains(LABEL);
	}

	@Test
	public void shouldIncludeNewItemInformationIfValid()
	{
		// given
		final String LABEL = "not saved";
		final WorkflowItemAttachmentModel attachmentModel = mock(WorkflowItemAttachmentModel.class);
		when(attachmentModel.getItem()).thenReturn(mock(ItemModel.class));
		when(labelService.getObjectLabel(any())).thenReturn("Object label");
		final LabelLookup labelLookup = LabelLookupFactory.createLabelLookup().registerLabel(LABEL_KEY_NOT_SAVED, LABEL)
				.getLookup();
		CockpitTestUtil.mockLabelLookup(labelLookup);
		when(modelService.isNew(any())).thenReturn(Boolean.TRUE);

		// when
		final String label = labelProvider.getLabel(attachmentModel);

		// then
		assertThat(label).contains(LABEL);
	}
}

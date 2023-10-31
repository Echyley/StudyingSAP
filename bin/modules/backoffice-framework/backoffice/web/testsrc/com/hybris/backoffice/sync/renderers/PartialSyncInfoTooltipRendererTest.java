/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.sync.renderers;

import de.hybris.platform.catalog.model.SyncItemJobModel;

import java.util.HashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.sync.PartialSyncInfo;

import static org.mockito.Mockito.doReturn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.zkoss.zul.Div;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;


@RunWith(MockitoJUnitRunner.class)
public class PartialSyncInfoTooltipRendererTest
{
	@Mock
	private PartialSyncInfo partialSync;

	@Spy
	@InjectMocks
	private PartialSyncInfoTooltipRenderer renderer;

	@Test
	public void shouldCreatePartialSyncInfoWhenHaveInboundSyncStatus()
	{
		//given
		final var inboundSyncStatus = new HashMap<SyncItemJobModel, Boolean>();
		inboundSyncStatus.put(mock(SyncItemJobModel.class), false);
		doReturn(new Div()).when(renderer).createJobSyncInfo(any(), any());
		doReturn(inboundSyncStatus).when(partialSync).getInboundSyncStatus();

		//when
		final Component container = renderer.createPartialSyncInfo(partialSync);

		//then
		verify(renderer).createJobSyncInfo(any(), any());
		assertThat(container.getClass().getName()).doesNotContain(renderer.SCLASS_PARTIAL_SYNC_INFO);
		assertThat(container.getChildren()).hasSize(1);
	}

	@Test
	public void shouldCreatePartialSyncInfoWhenHaveOutboundSyncStatus()
	{
		//given
		final var inboundSyncStatus = new HashMap<SyncItemJobModel, Boolean>();
		inboundSyncStatus.put(mock(SyncItemJobModel.class), false);
		doReturn(new Div()).when(renderer).createJobSyncInfo(any(), any());
		doReturn(inboundSyncStatus).when(partialSync).getOutboundSyncStatus();

		//when
		final Component container = renderer.createPartialSyncInfo(partialSync);

		//then
		verify(renderer).createJobSyncInfo(any(), any());
		assertThat(container.getClass().getName()).doesNotContain(renderer.SCLASS_PARTIAL_SYNC_INFO);
		assertThat(container.getChildren()).hasSize(1);
	}

	@Test
	public void shouldReturnNoSyncJobDefinedWhenNotHavaSyncStatus()
	{
		//when
		final Component container = renderer.createPartialSyncInfo(partialSync);

		//then
		verify(renderer, never()).createJobSyncInfo(any(), any());
		assertThat(container).isInstanceOf(Div.class);
		assertThat(((Div)container).getSclass()).contains(renderer.SCLASS_PARTIAL_SYNC_INFO);
		assertThat(container.getChildren()).hasSize(1);
		final var label = container.getChildren().get(0);
		assertThat(label).isInstanceOf(Label.class);
		assertThat(((Label)label).getSclass()).contains(renderer.SCLASS_PARTIAL_SYNC_INFO_JOB);
	}
}

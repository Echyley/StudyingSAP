/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions.catalogsynchronization;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.synchronization.CatalogVersionSyncJobModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.actions.ActionContext;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteCatalogSynchronizationJobActionTest
{
	@InjectMocks
	private ExecuteCatalogSynchronizationJobAction action = new ExecuteCatalogSynchronizationJobAction();

	@Mock
	private SynchronizationFacade synchronizationFacade;

	@Test
	public void shouldReturnFalseWhenSyncFacadeCantSync()
	{
		doReturn(false).when(synchronizationFacade).canSync(any());

		final CatalogVersionSyncJobModel job = mock(CatalogVersionSyncJobModel.class);
		final ActionContext<CatalogVersionSyncJobModel> ctx = new ActionContext(job, null, null, null);

		final boolean canPerform = action.canPerform(ctx);

		verify(synchronizationFacade).canSync(job);
		assertThat(canPerform).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenSyncFacadeCanSync()
	{
		doReturn(true).when(synchronizationFacade).canSync(any());

		final CatalogVersionSyncJobModel job = mock(CatalogVersionSyncJobModel.class);
		final ActionContext<CatalogVersionSyncJobModel> ctx = new ActionContext(job, null, null, null);

		final boolean canPerform = action.canPerform(ctx);

		verify(synchronizationFacade).canSync(job);
		assertThat(canPerform).isTrue();
	}
}
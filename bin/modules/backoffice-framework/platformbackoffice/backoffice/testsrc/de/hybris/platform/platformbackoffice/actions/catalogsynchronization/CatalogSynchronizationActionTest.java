/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.actions.catalogsynchronization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.sync.facades.SynchronizationFacade;
import com.hybris.cockpitng.actions.ActionContext;


@RunWith(MockitoJUnitRunner.class)
public class CatalogSynchronizationActionTest
{

	@InjectMocks
	private final CatalogSynchronizationAction action = new CatalogSynchronizationAction();

	@Mock
	private SynchronizationFacade synchronizationFacade;

	@Test
	public void canPerformShouldReturnFalseForNullCatalogVersion()
	{
		//given
		final ActionContext<CatalogVersionModel> ctx = mock(ActionContext.class);
		doReturn(null).when(ctx).getData();

		//when
		final boolean result = action.canPerform(ctx);

		//then
		assertThat(result).isFalse();
	}

	@Test
	public void canPerformShouldReturnTrueIfExecutableSyncJobExists()
	{
		//given
		final ActionContext<CatalogVersionModel> ctx = mock(ActionContext.class);
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		doReturn(catalogVersion).when(ctx).getData();

		final SyncItemJobModel sync = mock(SyncItemJobModel.class);
		doReturn(Arrays.asList(mock(SyncItemJobModel.class), sync)).when(catalogVersion).getSynchronizations();
		doReturn(Boolean.TRUE).when(synchronizationFacade).canSync(sync);

		//when
		final boolean result = action.canPerform(ctx);

		//then
		assertThat(result).isTrue();
	}
}

/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.setup;

import com.hybris.backoffice.search.events.AfterInitializationEndBackofficeSearchListener;
import com.hybris.backoffice.search.services.BackofficeFacetSearchConfigService;
import com.hybris.backoffice.searchservices.model.BackofficeIndexedTypeToSearchservicesIndexConfigModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.*;
import de.hybris.platform.searchservices.model.*;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeSearchservicesIndexInitializerTest
{
	@Mock
	BackofficeFacetSearchConfigService backofficeFacetSearchConfigService;
	@Mock
	private SnIndexerService snIndexerService;

	@Mock
	private SnIndexerItemSourceFactory snIndexerItemSourceFactory;

	@Mock
	private ModelService modelService;

	@InjectMocks
	private BackofficeSearchservicesIndexInitializer initializer = new BackofficeSearchservicesIndexInitializer()
	{
		@Override
		protected boolean shouldInitializeIndexes()
		{
			return isIndexAutoinitEnabled();
		}
	};

	private static final String TYPE_CONFIG_ID = "typeId";
	private final BackofficeIndexedTypeToSearchservicesIndexConfigModel configModel = mock(
			BackofficeIndexedTypeToSearchservicesIndexConfigModel.class);;
	private final SnIndexConfigurationModel indexConfig = mock(SnIndexConfigurationModel.class);
	private final SnIndexTypeModel typeConfig = mock(SnIndexTypeModel.class);
	private final AfterInitializationEndBackofficeSearchListener searchListener = mock(
			AfterInitializationEndBackofficeSearchListener.class);
	private boolean indexAutoinitEnabled;

	@Before
	public void setup() throws SnIndexerException
	{
		initializer.setAfterInitializationEndBackofficeListener(searchListener);
		Mockito.lenient().when(searchListener.isCallbackRegistered(any())).thenReturn(false);
		when(backofficeFacetSearchConfigService.getAllMappedFacetSearchConfigs()).thenReturn(Arrays.asList(configModel));
		when(configModel.getSnIndexConfiguration()).thenReturn(indexConfig);
		when(configModel.getSnIndexType()).thenReturn(typeConfig);

	}

	@Test
	public void shouldDoNotingWhenDoNotNeedInitialize()
	{
		indexAutoinitEnabled = false;

		initializer.initialize();

		verifyZeroInteractions(backofficeFacetSearchConfigService);
	}

	@Test
	public void shouldDoNothingWhenIndexHasBeenInitialized()
	{
		indexAutoinitEnabled = true;
		when(configModel.isActive()).thenReturn(true);

		initializer.initialize();

		verifyZeroInteractions(snIndexerService);
	}

	@Test
	public void shouldNotSaveAllCronJobsWhenCronJobAlreadySucceed()
	{
		indexAutoinitEnabled = true;
		when(configModel.isActive()).thenReturn(true);

		initializer.initialize();
		verify(modelService, never()).saveAll((Object) any());
	}

	@Test
	public void shouldSaveAllCronJobsAfterPerformFullIndex() throws SnIndexerException
	{
		indexAutoinitEnabled = true;
		when(configModel.isActive()).thenReturn(false);

		final FullSnIndexerCronJobModel cronJobModelUnknown = mock(FullSnIndexerCronJobModel.class);
		Mockito.lenient().when(cronJobModelUnknown.getResult()).thenReturn(CronJobResult.UNKNOWN);
		final AbstractSnIndexerItemSourceModel absItemSource = mock(AbstractSnIndexerItemSourceModel.class);
		when(cronJobModelUnknown.getIndexerItemSource()).thenReturn(absItemSource);
		final List<AbstractSnIndexerCronJobModel> cronJobs = Arrays.asList(cronJobModelUnknown);
		when(typeConfig.getIndexerCronJobs()).thenReturn(cronJobs);
		when(typeConfig.getId()).thenReturn(TYPE_CONFIG_ID);

		final SnIndexerItemSource itemSource = mock(SnIndexerItemSource.class);
		when(snIndexerItemSourceFactory.createItemSource(absItemSource, Collections.emptyMap())).thenReturn(itemSource);
		final SnIndexerRequest snIndexerRequest = mock(SnIndexerRequest.class);
		when(snIndexerService.createFullIndexerRequest(TYPE_CONFIG_ID, itemSource)).thenReturn(snIndexerRequest);
		final SnIndexerResponse snIndexerResponse = mock(SnIndexerResponse.class);
		when(snIndexerService.index(snIndexerRequest)).thenReturn(snIndexerResponse);

		initializer.initialize();
		verify(modelService).saveAll(Arrays.asList(cronJobModelUnknown, configModel));
	}

	private boolean isIndexAutoinitEnabled()
	{
		return indexAutoinitEnabled;
	}
}

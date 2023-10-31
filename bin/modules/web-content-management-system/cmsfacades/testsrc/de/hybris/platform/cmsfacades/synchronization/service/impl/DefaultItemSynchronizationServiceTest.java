/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.synchronization.service.impl;

import static de.hybris.platform.catalog.enums.SyncItemStatus.IN_SYNC;
import static de.hybris.platform.catalog.enums.SyncItemStatus.NOT_APPLICABLE;
import static de.hybris.platform.catalog.enums.SyncItemStatus.NOT_SYNC;
import static de.hybris.platform.cmsfacades.synchronization.service.impl.DefaultItemSynchronizationService.SYNCHRONIZATION_MAX_RECURSION_DEPTH;
import static de.hybris.platform.core.PK.fromLong;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ItemSyncTimestampModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.catalog.synchronization.CatalogSynchronizationService;
import de.hybris.platform.catalog.synchronization.SyncConfig;
import de.hybris.platform.catalog.synchronization.SyncItemInfo;
import de.hybris.platform.catalog.synchronization.SynchronizationStatusService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cmsfacades.data.SyncItemInfoJobStatusData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.data.SynchronizationItemDetailsData;
import de.hybris.platform.cmsfacades.synchronization.cache.SynchronizationCacheService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.model.collector.RelatedItemsCollector;
import de.hybris.platform.servicelayer.session.MockSessionService;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultItemSynchronizationServiceTest
{
	
	@Spy
	private final MockSessionService mockSessionService = new MockSessionService();
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private RelatedItemsCollector relatedItemsCollector;
	@Mock
	private SynchronizationStatusService platformSynchronizationStatusService;
	@Mock
	private CatalogSynchronizationService catalogSynchronizationService;
	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;
	@Mock
	private SynchronizationCacheService synchronizationCacheService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	
	@InjectMocks
	private DefaultItemSynchronizationService itemSynchronizationService;

	
	private 	List<ItemModel> relatedItems;

	@Mock
	private ItemModel item;
	private final PK itemPK =  fromLong(123);

	@Mock
	private ItemModel relatedItem1;
	private final PK relatedItem1PK =  fromLong(1234);

	@Mock
	private ItemModel relatedItem2;	
	private final PK relatedItem2PK =  fromLong(12345);

	private final String catalogId = "theCatalogId";
	
	@Mock
	private CatalogVersionModel sourceVersion;
	private final String sourceVersionId = "sourceVersionId";

	@Mock
	private CatalogVersionModel targetVersion;
	private final String targetVersionId = "targetVersionId";

	private SyncRequestData syncRequestData;

	@Mock
	private SyncItemJobModel targetToSourceJob;
	private final PK targetToTargetJobPK = fromLong(5678);

	@Mock
	private SyncItemJobModel sourceToTargetJob;
	private final PK sourceToTargetJobPK = fromLong(56789);

	@Mock
	private SyncItemJobModel wrongJob2;
	@Mock
	private SyncItemJobModel wrongJob3;
	
	@Mock
	private SyncItemInfo syncItemInfo;
	private final PK lastSyncTimePK = fromLong(123456);
	@Mock
	private ItemSyncTimestampModel timeStamp;
	@Mock
	private Date lastSyncTime;
	
	@Mock
	private SyncItemInfo syncRelatedItem1Info;
	@Mock
	private SyncItemInfo syncRelatedItem2Info;
	
	@Mock
	private SyncConfig config;
	
	@Captor
	private ArgumentCaptor<List<ItemModel>> itemListCaptor;

	@Before
	public void setUp(){
		
		syncRequestData = new SyncRequestData();
		syncRequestData.setCatalogId(catalogId);

		when(sourceVersion.getVersion()).thenReturn(sourceVersionId);
		when(targetVersion.getVersion()).thenReturn(targetVersionId);

		when(catalogVersionService.getCatalogVersion(catalogId, sourceVersionId)).thenReturn(sourceVersion);
		when(catalogVersionService.getCatalogVersion(catalogId, targetVersionId)).thenReturn(targetVersion);
		
		when(catalogTypeService.getCatalogVersionForCatalogVersionAwareModel(item)).thenReturn(sourceVersion);
		
		when(targetToSourceJob.getSourceVersion()).thenReturn(targetVersion);
		when(targetToSourceJob.getTargetVersion()).thenReturn(sourceVersion);

		when(sourceToTargetJob.getSourceVersion()).thenReturn(sourceVersion);
		when(sourceToTargetJob.getTargetVersion()).thenReturn(targetVersion);

		when(sourceToTargetJob.getPk()).thenReturn(sourceToTargetJobPK);
		when(targetToSourceJob.getPk()).thenReturn(targetToTargetJobPK);
		
		when(wrongJob2.getSourceVersion()).thenReturn(sourceVersion);
		when(wrongJob2.getTargetVersion()).thenReturn(sourceVersion);
		
		when(wrongJob3.getSourceVersion()).thenReturn(targetVersion);
		when(wrongJob3.getTargetVersion()).thenReturn(targetVersion);

		when(platformSynchronizationStatusService.getInboundSynchronizations(item)).thenReturn(asList(targetToSourceJob, sourceToTargetJob, wrongJob2, wrongJob3));
		when(platformSynchronizationStatusService.getOutboundSynchronizations(item)).thenReturn(asList(targetToSourceJob, sourceToTargetJob, wrongJob2, wrongJob3));
		
		when(item.getPk()).thenReturn(itemPK);
		when(relatedItem1.getPk()).thenReturn(relatedItem1PK);
		when(relatedItem2.getPk()).thenReturn(relatedItem2PK);
		relatedItems = asList(item, relatedItem1, relatedItem2);
		
		when(relatedItemsCollector.collect(eq(item), anyMapOf(String.class, Object.class))).thenReturn(relatedItems);
		when(sessionSearchRestrictionsDisabler.execute(any())).thenReturn(relatedItems);
	
		when(syncItemInfo.getSyncStatus()).thenReturn(NOT_SYNC);
		when(syncItemInfo.getSyncTimestampPk()).thenReturn(lastSyncTimePK);
		when(modelService.get(lastSyncTimePK)).thenReturn(timeStamp);
		when(timeStamp.getLastSyncTime()).thenReturn(lastSyncTime);;
		
		when(syncRelatedItem1Info.getSyncStatus()).thenReturn(IN_SYNC);
		when(syncRelatedItem1Info.getItemPk()).thenReturn(relatedItem1PK);
		
		when(syncRelatedItem2Info.getSyncStatus()).thenReturn(NOT_SYNC);
		when(syncRelatedItem2Info.getItemPk()).thenReturn(relatedItem2PK);

		when(platformSynchronizationStatusService.getSyncInfo(item, sourceToTargetJob)).thenReturn(syncItemInfo);
		when(platformSynchronizationStatusService.getSyncInfo(relatedItem1, sourceToTargetJob)).thenReturn(syncRelatedItem1Info);
		when(platformSynchronizationStatusService.getSyncInfo(relatedItem2, sourceToTargetJob)).thenReturn(syncRelatedItem2Info);
		when(platformSynchronizationStatusService.getSyncInfo(item, targetToSourceJob)).thenReturn(syncItemInfo);
		when(platformSynchronizationStatusService.getSyncInfo(relatedItem1, targetToSourceJob)).thenReturn(syncRelatedItem1Info);
		when(platformSynchronizationStatusService.getSyncInfo(relatedItem2, targetToSourceJob)).thenReturn(syncRelatedItem2Info);

		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getInteger(SYNCHRONIZATION_MAX_RECURSION_DEPTH, 5)).thenReturn(5);

		doAnswer((Answer<SyncItemInfo>) invocation -> {
			final Supplier<SyncItemInfo> supplier = (Supplier) invocation.getArguments()[0];
			return supplier.get();
		}).when(synchronizationCacheService).getOrSetItemCache(any(),anyString());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void willTrowExceptionIfSyncRequestDataNotProvided(){
		
		itemSynchronizationService.getSynchronizationItemStatus(null, item);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void willTrowExceptionIfItemNotProvided(){
		
		itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, null);
	}
		
	@Test
	public void whenSourceVersionIsItemVersion_getSyncStatus_Use_OutboundSynchronizations_and_returns_aggregated_status_of_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);
		//prepare
		syncRequestData.setSourceVersionId(sourceVersionId);
		syncRequestData.setTargetVersionId(targetVersionId);

		//execute
		final SynchronizationItemDetailsData synchronizationItemStatus = itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, item);
		
		//assert
		assertThat(synchronizationItemStatus.getItem(), is(item));
		assertThat(synchronizationItemStatus.getCatalogId(), is(catalogId));
		assertThat(synchronizationItemStatus.getSourceVersionId(), is(sourceVersionId));
		assertThat(synchronizationItemStatus.getTargetVersionId(), is(targetVersionId));
		assertThat(synchronizationItemStatus.getSyncStatus(), is(NOT_SYNC.name()));
		assertThat(synchronizationItemStatus.getLastSyncStatusDate(), is(lastSyncTime));
		
		final List<SyncItemInfoJobStatusData> relatedItemStatuses = synchronizationItemStatus.getRelatedItemStatuses();
		assertThat(relatedItemStatuses.size(), is(1));
		assertThat(relatedItemStatuses.get(0).getItem(), is(relatedItem2));
		assertThat(relatedItemStatuses.get(0).getSyncStatus(), is(NOT_SYNC.name()));
		
		
		//verify
		verify(platformSynchronizationStatusService, times(1)).getOutboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getInboundSynchronizations(any(ItemModel.class));

		verify(platformSynchronizationStatusService).getSyncInfo(item, sourceToTargetJob);
		verify(platformSynchronizationStatusService).getSyncInfo(relatedItem1, sourceToTargetJob);
		verify(platformSynchronizationStatusService).getSyncInfo(relatedItem2, sourceToTargetJob);
		
		verify(searchRestrictionService, times(1)).disableSearchRestrictions();
		verify(searchRestrictionService, times(1)).enableSearchRestrictions();
	}

	@Test
	public void shouldFilterNotApplicableStatus()
	{
		//prepare
		when(syncItemInfo.getSyncStatus()).thenReturn(IN_SYNC);
		when(syncRelatedItem1Info.getSyncStatus()).thenReturn(IN_SYNC);
		when(syncRelatedItem2Info.getSyncStatus()).thenReturn(NOT_APPLICABLE);
		syncRequestData.setSourceVersionId(sourceVersionId);
		syncRequestData.setTargetVersionId(targetVersionId);

		//execute
		final SynchronizationItemDetailsData synchronizationItemStatus = itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, item);

		assertThat(synchronizationItemStatus.getSyncStatus(), is(IN_SYNC.name()));
	}
	
	@Test
	public void whenSourceVersionIsNotItemVersion_getSyncStatus_Use_InboundSynchronizations_and_returns_aggregated_status_of_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(false);
		
		//prepare
		syncRequestData.setSourceVersionId(targetVersionId);
		syncRequestData.setTargetVersionId(sourceVersionId);
		
		//execute
		final SynchronizationItemDetailsData synchronizationItemStatus = itemSynchronizationService.getSynchronizationItemStatus(syncRequestData, item);
		
		//assert
		assertThat(synchronizationItemStatus.getItem(), is(item));
		assertThat(synchronizationItemStatus.getCatalogId(), is(catalogId));
		assertThat(synchronizationItemStatus.getSourceVersionId(), is(targetVersionId));
		assertThat(synchronizationItemStatus.getTargetVersionId(), is(sourceVersionId));
		assertThat(synchronizationItemStatus.getSyncStatus(), is(NOT_SYNC.name()));
		
		final List<SyncItemInfoJobStatusData> relatedItemStatuses = synchronizationItemStatus.getRelatedItemStatuses();
		assertThat(relatedItemStatuses.size(), is(1));
		assertThat(relatedItemStatuses.get(0).getItem(), is(relatedItem2));
		assertThat(relatedItemStatuses.get(0).getSyncStatus(), is(NOT_SYNC.name()));
		
		
		//verify
		verify(platformSynchronizationStatusService, times(1)).getInboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getOutboundSynchronizations(any(ItemModel.class));

		verify(platformSynchronizationStatusService).getSyncInfo(item, targetToSourceJob);
		verify(platformSynchronizationStatusService).getSyncInfo(relatedItem1, targetToSourceJob);
		verify(platformSynchronizationStatusService).getSyncInfo(relatedItem2, targetToSourceJob);
		
		verify(searchRestrictionService, never()).disableSearchRestrictions();
		verify(searchRestrictionService, never()).enableSearchRestrictions();
		
	}
	
	@Test
	public void whenSourceVersionIsItemVersion_performSync_Use_OutboundSynchronizations_and_performs_on_all_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(true);
		
		//prepare
		syncRequestData.setSourceVersionId(sourceVersionId);
		syncRequestData.setTargetVersionId(targetVersionId);
		
		//execute
		itemSynchronizationService.performItemSynchronization(syncRequestData, asList(item, relatedItem2), config);
		
		//assert
		verify(catalogSynchronizationService, times(1)).performSynchronization(itemListCaptor.capture(), eq(sourceToTargetJob), eq(config));
		
		assertThat(itemListCaptor.getValue(), containsInAnyOrder(item, relatedItem1, relatedItem2));
		
		//verify
		
		verify(platformSynchronizationStatusService, times(1)).getOutboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getInboundSynchronizations(any(ItemModel.class));

		verify(searchRestrictionService, times(1)).disableSearchRestrictions();
		verify(searchRestrictionService, times(1)).enableSearchRestrictions();

	}

	@Test
	public void whenSourceVersionIsNotItemVersion_performSync_Use_OutboundSynchronizations_and_performs_on_all_related_items(){
		
		when(searchRestrictionService.isSearchRestrictionsEnabled()).thenReturn(false);
		//prepare
		syncRequestData.setSourceVersionId(targetVersionId);
		syncRequestData.setTargetVersionId(sourceVersionId);
		
		//execute
		itemSynchronizationService.performItemSynchronization(syncRequestData, asList(item, relatedItem1), config);
		
		//assert
		verify(catalogSynchronizationService, times(1)).performSynchronization(itemListCaptor.capture(), eq(targetToSourceJob), eq(config));
		
		assertThat(itemListCaptor.getValue(), containsInAnyOrder(item, relatedItem1, relatedItem2));

		//verify
		
		verify(platformSynchronizationStatusService, times(1)).getInboundSynchronizations(item);
		verify(platformSynchronizationStatusService, never()).getOutboundSynchronizations(any(ItemModel.class));

		verify(searchRestrictionService, never()).disableSearchRestrictions();
		verify(searchRestrictionService, never()).enableSearchRestrictions();

	}

}

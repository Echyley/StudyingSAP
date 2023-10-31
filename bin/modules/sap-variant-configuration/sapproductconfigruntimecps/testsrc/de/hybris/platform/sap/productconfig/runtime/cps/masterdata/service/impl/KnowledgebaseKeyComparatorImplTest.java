/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.KnowledgebaseBuildSyncStatus;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link KnowledgebaseKeyComparatorImpl}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class KnowledgebaseKeyComparatorImplTest
{

	private static final String KB_ID = "99";
	private static final Integer KB_BUILD = Integer.valueOf(11);
	private static final Integer KB_BUILD_OUTDATED = Integer.valueOf(3);
	@Mock
	private ConfigurationMasterDataService masterDataService;
	private final CPSConfiguration configuration = new CPSConfiguration();

	@InjectMocks
	private KnowledgebaseKeyComparatorImpl classUnderTest;

	@Before
	public void setup()
	{
		configuration.setKbId(KB_ID);
		configuration.setKbBuild(KB_BUILD);
		when(masterDataService.getKbBuildNumber(KB_ID)).thenReturn(KB_BUILD);
	}

	@Test
	public void testRetrieveKnowledgebaseBuildSyncStatus()
	{
		final KnowledgebaseBuildSyncStatus result = classUnderTest.retrieveKnowledgebaseBuildSyncStatus(configuration);
		assertNotNull(result);
		assertEquals(KnowledgebaseBuildSyncStatus.IN_SYNC, result);
	}

	@Test
	public void testRetrieveKnowledgebaseBuildSyncStatusOutdatedMasterData()
	{
		when(masterDataService.getKbBuildNumber(KB_ID)).thenReturn(KB_BUILD_OUTDATED);
		final KnowledgebaseBuildSyncStatus result = classUnderTest.retrieveKnowledgebaseBuildSyncStatus(configuration);
		assertNotNull(result);
		assertEquals(KnowledgebaseBuildSyncStatus.OUTDATED_MASTER_DATA, result);
		verify(masterDataService).removeCachedKb(KB_ID);
	}

	@Test
	public void testRetrieveKnowledgebaseBuildSyncStatusOutdatedRuntime()
	{
		configuration.setKbBuild(KB_BUILD_OUTDATED);
		final KnowledgebaseBuildSyncStatus result = classUnderTest.retrieveKnowledgebaseBuildSyncStatus(configuration);
		assertNotNull(result);
		assertEquals(KnowledgebaseBuildSyncStatus.OUTDATED_RUNTIME, result);
	}
}

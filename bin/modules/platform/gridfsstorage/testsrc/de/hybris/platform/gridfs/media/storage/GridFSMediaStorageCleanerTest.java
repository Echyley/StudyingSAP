/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gridfs.media.storage;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.storage.MediaStorageConfigService;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoAdmin;
import org.springframework.data.mongodb.core.MongoOperations;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class GridFSMediaStorageCleanerTest
{
	private static final String GRID_FS_STORAGE_STRATEGY = "gridFsStorageStrategy";

	private GridFSMediaStorageCleaner gridFSCleaner;

	@Mock
	private MediaStorageConfigService config;

	@Mock
	private MongoOperations mongoOperations;

	@Mock
	private MongoAdmin mongoAdmin;


	@Before
	public void setUp()
	{
		gridFSCleaner = new GridFSMediaStorageCleaner(true, config, mongoOperations, mongoAdmin)
		{
			@Override
			public void setTenantPrefix()
			{
				tenantPrefix = "sys-master";
			}
		};

		gridFSCleaner.setTenantPrefix();
	}

	@Test
	public void shouldDropAllCollectionsPrefixedWithTenantIdDuringCleaningOnInit()
	{
		// given
		given(config.isStorageStrategyConfigured(GRID_FS_STORAGE_STRATEGY)).willReturn(Boolean.TRUE);
		given(mongoOperations.getCollectionNames()).willReturn(createCollectionNames());

		// when
		gridFSCleaner.onInitialize();

		// then
		verify(mongoOperations, times(1)).dropCollection("sys-master-foo");
		verify(mongoOperations, times(1)).dropCollection("sys-master-bar");
		verify(mongoOperations, times(0)).dropCollection("baz");
	}

	private Set<String> createCollectionNames()
	{
		return Set.of("sys-master-foo", "sys-master-bar", "baz");
	}
}

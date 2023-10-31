/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.catalog.jalo;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncCronJob;
import de.hybris.platform.catalog.jalo.synchronization.CatalogVersionSyncJob;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.media.Media;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

@IntegrationTest
public class CatalogManagerAssignMediasTest extends ServicelayerBaseTest
{
	private CatalogVersion source;
	private CatalogVersion target;
	private Media media1, media2, media3, media4;

	@Test
	public void shouldNotAssignDefaultCatalogVersionsToSyncScheduleMedias() throws Exception
	{
		//given
		UserManager.getInstance().createUserGroup("customergroup");
		final CatalogVersionSyncCronJob cronJob = createSyncCronJobWithMediasToSync();
		//when
		CatalogManager.getInstance().createDefaultCatalog(null);
		//then
		assertThat(CatalogManager.getInstance().getCatalog(CatalogManager.DEFAULT_CATALOG_ID)).isNotNull();
		assertThat(cronJob.getScheduleMedias().stream().map(d -> d.getProperty("catalogVersion"))).containsNull();
	}


	private CatalogVersionSyncCronJob createSyncCronJobWithMediasToSync()
	{
		createCatalogAndMedia();

		final List<PK> pks = new ArrayList<>();

		pks.add(media1.getPK());
		pks.add(media2.getPK());
		pks.add(media3.getPK());
		pks.add(media4.getPK());

		final String mediaTC = media1.getPK().getTypeCodeAsString();

		final List<String> typeCodes = new ArrayList<>();
		for (int i = 0; i < 4; i++)
		{
			typeCodes.add(mediaTC);
		}

		final Map args = new HashMap();
		args.put(CatalogVersionSyncJob.CODE, "test-job_" + new Date());
		args.put(CatalogVersionSyncJob.SOURCEVERSION, source);
		args.put(CatalogVersionSyncJob.TARGETVERSION, target);
		args.put(CatalogVersionSyncJob.MAXTHREADS, Integer.valueOf(2));
		final CatalogVersionSyncJob job = CatalogManager.getInstance().createCatalogVersionSyncJob(args);
		final CatalogVersionSyncCronJob cronJob = (CatalogVersionSyncCronJob) job.newExecution();
		job.configureFullVersionSync(cronJob);
		return cronJob;

	}

	private void createCatalogAndMedia()
	{
		final Catalog catalog  = CatalogManager.getInstance().createCatalog("test-catalog");

		source = CatalogManager.getInstance().createCatalogVersion(catalog, "test-source", null);
		source.setLanguages(Collections.singletonList(getOrCreateLanguage("de")));

		target = CatalogManager.getInstance().createCatalogVersion(catalog, "test-target", null);
		target.setLanguages(Collections.singletonList(getOrCreateLanguage("de")));

		media1 = MediaManager.getInstance().createMedia("media1");
		CatalogManager.getInstance().setCatalogVersion(media1, source);

		media2 = MediaManager.getInstance().createMedia("media2");
		CatalogManager.getInstance().setCatalogVersion(media2, source);

		media3 = MediaManager.getInstance().createMedia("media3");
		CatalogManager.getInstance().setCatalogVersion(media3, source);

		media4 = MediaManager.getInstance().createMedia("media4");
		CatalogManager.getInstance().setCatalogVersion(media4, source);
	}

}


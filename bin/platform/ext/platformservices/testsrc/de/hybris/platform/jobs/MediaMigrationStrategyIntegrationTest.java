/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.model.MediaFolderStructureMigrationCronJobModel;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

@IntegrationTest
public class MediaMigrationStrategyIntegrationTest extends ServicelayerTransactionalBaseTest
{
	@Resource(mappedName = "mediaFolderStructureMigration")
	private AbstractMaintenanceJobPerformable job;
	@Resource
	private ModelService modelService;
	@Resource
	private MediaService mediaService;

	private MediaFolderModel folder;

	private MockitoSession mockito;

	@Mock
	private MediaFolderStructureMigrationCronJobModel cronJob;

	private List<MediaModel> medias;
	private String storageStrategyForTestingFolder;
	private String hashingDepthForTestingFolder;

	@Before
	public void setUp() throws Exception
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();

		folder = createTestFolder();
		storageStrategyForTestingFolder = Config.getParameter("media.folder." + folder.getQualifier() + ".storage.strategy");
		hashingDepthForTestingFolder = Config.getParameter("media.folder." + folder.getQualifier() + ".hashing.depth");
		Config.setParameter("media.folder." + folder.getQualifier() + ".storage.strategy", "localFileMediaStorageStrategy");
		medias = createAndAddMediaToFolder(10, folder);
	}

	private MediaFolderModel createTestFolder()
	{
		final MediaFolderModel folder = modelService.create(MediaFolderModel.class);
		folder.setQualifier("test");
		folder.setPath("test");
		modelService.save(folder);
		setSubfoldersDepthForFolder(folder, 0);
		return folder;
	}

	private List<MediaModel> createAndAddMediaToFolder(final int numOfMedia, final MediaFolderModel folder)
	{
		final List<MediaModel> result = new ArrayList<>();

		for (int i = 0; i < numOfMedia; i++)
		{
			final String code = RandomStringUtils.randomAlphabetic(5);
			final MediaModel media = createEmptyMediaModelInFolder(code, folder);
			try (DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(code.getBytes())))
			{
				mediaService.setStreamForMedia(media, inputStream);
			}
			catch (final Exception e)
			{
				fail(e.getMessage());
			}
			modelService.save(media);
			assertThat(media.getDataPK()).overridingErrorMessage(
					"media PK: " + media.getPk() + ", index: " + i + " has no dataPK")
			                             .isNotNull();
			MediaDataAssert.assertThat(media).hasDirDepthEqualTo(0);
			result.add(media);
		}
		return result;
	}

	private MediaModel createEmptyMediaModelInFolder(final String code, final MediaFolderModel folder)
	{
		final MediaModel media = modelService.create(CatalogUnawareMediaModel.class);
		media.setCode(code);
		media.setFolder(folder);
		modelService.save(media);
		return media;
	}

	@After
	public void tearDown() throws Exception
	{
		mockito.finishMocking();

		cleanUpMediaDir();
		Config.setParameter("media.folder." + folder.getQualifier() + ".storage.strategy", storageStrategyForTestingFolder);
		Config.setParameter("media.folder." + folder.getQualifier() + ".hashing.depth", hashingDepthForTestingFolder);
	}

	private void cleanUpMediaDir()
	{
		for (final MediaModel media : medias)
		{
			MediaManager.getInstance().deleteMedia(media.getFolder().getQualifier(), media.getLocation());
		}
	}


	@Test
	public void shouldThrowIllegalArgumentExceptionWhenCronJobIsNull()
	{
		// given
		final MediaFolderStructureMigrationCronJobModel cronJob = null;

		try
		{
			// when
			job.perform(cronJob);
			fail("Should throw IllegalArgumentException");
		}
		catch (final IllegalArgumentException e)
		{
			// then
			assertThat(e).hasMessage("MediaFolderStructureMigrationCronJobModel is required!");
		}
	}

	@Test
	public void shouldMoveFlatStructureOfTheFolderToThreeDirectoriesDepthHashedStructure()
	{
		// given
		given(cronJob.getMediaFolder()).willReturn(folder);
		setSubfoldersDepthForFolder(folder, 3);

		// when
		final PerformResult performResult = job.perform(cronJob);

		// then
		assertThat(performResult).isNotNull();
		assertThat(performResult.getResult()).isEqualTo(CronJobResult.SUCCESS);
		for (final MediaModel media : medias)
		{
			modelService.refresh(media);
			MediaDataAssert.assertThat(media).hasDirDepthEqualTo(3);
		}
	}

	private void setSubfoldersDepthForFolder(final MediaFolderModel folder, final Integer hashingDepth)
	{
		Config.setParameter("media.folder." + folder.getQualifier() + ".hashing.depth",
				hashingDepth == null ? null : hashingDepth.toString());
	}

	private static class MediaDataAssert extends AbstractAssert<MediaDataAssert, MediaModel>
	{
		public MediaDataAssert(final MediaModel actual)
		{
			super(actual, MediaDataAssert.class);
		}

		public static MediaDataAssert assertThat(final MediaModel actual)
		{
			return new MediaDataAssert(actual);
		}

		public MediaDataAssert hasDirDepthEqualTo(final Integer depth)
		{
			Assertions.assertThat(StringUtils.countMatches(getLocation(actual), "/")).isEqualTo(depth);
			return this;
		}

		private String getLocation(final MediaModel actual)
		{
			final String path = actual.getFolder().getPath();
			final String location = actual.getLocation();
			final String result;
			if (path != null && location.contains(path))
			{
				result = location.substring(path.length() + 1);
			}
			else
			{
				result = location;
			}
			return result;
		}
	}
}

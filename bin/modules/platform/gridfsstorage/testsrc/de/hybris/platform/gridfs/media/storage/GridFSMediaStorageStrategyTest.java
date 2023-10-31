/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gridfs.media.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.gridfs.GridFsCriteria.whereFilename;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.media.services.MediaLocationHashService;
import de.hybris.platform.media.storage.MediaMetaData;
import de.hybris.platform.media.storage.MediaStorageConfigService;
import de.hybris.platform.media.storage.MediaStorageConfigService.MediaFolderConfig;
import de.hybris.platform.media.storage.impl.StoredMediaData;
import de.hybris.platform.util.MediaUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.client.gridfs.model.GridFSFile;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GridFSMediaStorageStrategyTest
{
	private static final String TENANT_CONTAINER_PREFIX = "sys-master";
	private static final String MEDIA_ID = "123456";
	private static final String REAL_FILENAME = "foo.jpg";
	private static final String MEDIA_LOCATION = MEDIA_ID + MediaUtil.FILE_SEP + REAL_FILENAME;
	private static final String MIME = "image/jpeg";
	private static final String FOLDER_QUALIFIER = "fooBar";
	private static final long FILE_SIZE = 12345;

	@Mock
	private MediaLocationHashService mediaLocationHashService;

	@Mock
	private MediaStorageConfigService storageConfig;

	@Mock
	private MediaFolderConfig folderConfig;

	@Mock
	private InputStream dataStream;

	@Mock
	private GridFSFile gridFSFile;

	@Mock
	private GridFsResource gridFsResource;

	@Mock
	private MappingMongoConverter mongoConverter;

	@Mock
	private MongoDatabaseFactory mongoDatabaseFactory;

	@Mock
	private GridFsTemplate gridFsTemplate;

	private GridFSMediaStorageStrategy gridFSStrategy;

	@Before
	public void setUp()
	{
		gridFSStrategy = new GridFSMediaStorageStrategy(mediaLocationHashService, mongoConverter, mongoDatabaseFactory)
		{
			@Override
			public void setTenantPrefix()
			{
				tenantPrefix = TENANT_CONTAINER_PREFIX;
			}

			@Override
			protected GridFsTemplate getGridFsTemplate(final MediaFolderConfig config)
			{
				return gridFsTemplate;
			}
		};

		gridFSStrategy.setTenantPrefix();

		lenient().when(storageConfig.getConfigForFolder(FOLDER_QUALIFIER)).thenReturn(folderConfig);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNullOnStoringMedia()
	{
		//given
		final MediaFolderConfig nullFolderConfig = null;


		//when, then
		assertThatThrownBy(
				() -> gridFSStrategy.store(nullFolderConfig, MEDIA_ID, Collections.EMPTY_MAP, dataStream)).isInstanceOf(
				IllegalArgumentException.class).hasMessageContaining("config is required!");
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMediaIdIsNullOnStoringMedia()
	{
		//given
		final String mediaId = null;

		//when, then
		assertThatThrownBy(() -> gridFSStrategy.store(folderConfig, mediaId, Collections.EMPTY_MAP, dataStream)).isInstanceOf(
				IllegalArgumentException.class).hasMessageContaining("mediaId is required!");
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenMetaDataIsNullOnStoringMedia()
	{
		//given
		final Map<String, Object> metaData = null;

		//when, then
		assertThatThrownBy(() -> gridFSStrategy.store(folderConfig, MEDIA_ID, metaData, dataStream)).isInstanceOf(
				IllegalArgumentException.class).hasMessageContaining("metaData is required!");
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenDataStreamIsNullOnStoringMedia()
	{
		//given
		final InputStream nullDataStream = null;

		//when, then
		assertThatThrownBy(
				() -> gridFSStrategy.store(folderConfig, MEDIA_ID, Collections.EMPTY_MAP, nullDataStream)).isInstanceOf(
				IllegalArgumentException.class).hasMessageContaining("dataStream is required!");
	}

	@Test
	public void shouldStoreMediaUsingGridFSStrategy()
	{
		//given
		final Map<String, Object> metadata = buildMediaMetaData(MIME, REAL_FILENAME, FILE_SIZE);

		//when
		final StoredMediaData storedMediaData = gridFSStrategy.store(folderConfig, MEDIA_ID, metadata, dataStream);

		// then
		assertThat(storedMediaData).isNotNull();
		assertThat(storedMediaData.getLocation()).isEqualTo(MEDIA_LOCATION);
		assertThat(storedMediaData.getSize()).isEqualTo(Long.valueOf(FILE_SIZE));
		assertThat(storedMediaData.getSize()).isEqualTo(FILE_SIZE);
		verify(gridFsTemplate).store(dataStream, MEDIA_LOCATION, MIME);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNullOnDeleteMedia()
	{
		//given
		final MediaFolderConfig nullFolderConfig = null;

		//when, then
		assertThatThrownBy(() -> gridFSStrategy.delete(nullFolderConfig, MEDIA_LOCATION)).isInstanceOf(
				IllegalArgumentException.class).hasMessageContaining("config is required!");
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenLocationIsNullOnDeleteMedia()
	{
		//given
		final String location = null;

		//when, then
		assertThatThrownBy(() -> gridFSStrategy.delete(folderConfig, location)).isInstanceOf(IllegalArgumentException.class)
		                                                                       .hasMessageContaining("location is required!");
	}

	@Test
	public void shouldDeleteMediaFromStorage()
	{
		//given
		final Query query = createFindByFileNameQuery(MEDIA_LOCATION);

		//when
		gridFSStrategy.delete(folderConfig, MEDIA_LOCATION);

		//then
		verify(gridFsTemplate).delete(query);
	}

	@Test
	public void shouldThrowIllegalArgumentExceptionWhenFolderConfigIsNullOnGettingMediaAsStream()
	{
		//given
		final MediaFolderConfig nullFolderConfig = null;

		assertThatThrownBy(() -> gridFSStrategy.getAsStream(nullFolderConfig, MEDIA_LOCATION)).isInstanceOf(
				IllegalArgumentException.class).hasMessageContaining("config is required!");
	}


	@Test
	public void shouldThrowIllegalArgumentExceptionWhenLocationIsNullOnGettingMediaAsStream()
	{
		//given
		final String location = null;

		assertThatThrownBy(() -> gridFSStrategy.getAsStream(folderConfig, location)).isInstanceOf(IllegalArgumentException.class)
		                                                                            .hasMessageContaining(
				                                                                            "location is required!");
	}

	@Test
	public void shouldGetMediaAsStream() throws IOException
	{
		//given
		final Query query = createFindByFileNameQuery(MEDIA_LOCATION);
		given(gridFsTemplate.findOne(query)).willReturn(gridFSFile);
		given(gridFsTemplate.getResource(gridFSFile)).willReturn(gridFsResource);
		given(gridFsResource.getInputStream()).willReturn(dataStream);

		//when
		final InputStream mediaAsStream = gridFSStrategy.getAsStream(folderConfig, MEDIA_LOCATION);

		//then
		assertThat(mediaAsStream).isNotNull().isEqualTo(dataStream);
	}

	@Test
	public void shouldThrowUnsupportedOperationExceptionWhenGettingMediaAsFile()
	{
		//when, then
		assertThatThrownBy(() -> gridFSStrategy.getAsFile(folderConfig, MEDIA_LOCATION)).isInstanceOf(
				                                                                                UnsupportedOperationException.class)
		                                                                                .hasMessageContaining(
				                                                                                "Obtaining media as file is not supported for GridFS storage. Use getMediaAsStream method.");
	}

	@Test
	public void shouldReturnSizeOfAnObjectInStorage()
	{
		//given
		final Long expectedSize = 12345L;
		final Query query = createFindByFileNameQuery(MEDIA_LOCATION);
		given(gridFsTemplate.findOne(query)).willReturn(gridFSFile);
		given(gridFSFile.getLength()).willReturn(expectedSize);

		//when
		final long size = gridFSStrategy.getSize(folderConfig, MEDIA_LOCATION);

		//then
		assertThat(size).isEqualTo(expectedSize);
	}

	@Test
	public void shouldThrowMediaNotFoundExceptionWhenAskingForSizeForNonExistentObject()
	{
		//given
		final String mediaLocation = "NON_EXISISTENT";

		//when, then
		assertThatThrownBy(() -> gridFSStrategy.getSize(folderConfig, mediaLocation)).isInstanceOf(MediaNotFoundException.class);
	}

	@Test
	public void shouldGetExistingGridFsTemplateFromPoolInsteadCreatingNewOne()
	{
		//given
		final GridFSMediaStorageStrategy storageStrategy = new GridFSMediaStorageStrategy(mediaLocationHashService, mongoConverter, mongoDatabaseFactory);
		final GridFsTemplate createdGridFsTemplate = storageStrategy.getGridFsTemplate(folderConfig);

		//when
		final GridFsTemplate takenFromPoolGridFsTemplate = storageStrategy.getGridFsTemplate(folderConfig);

		//then
		assertThat(createdGridFsTemplate).isSameAs(takenFromPoolGridFsTemplate);
	}

	private Map<String, Object> buildMediaMetaData(final String mime, final String originalName, final long fileSize)
	{
		final Map<String, Object> metaData = new HashMap<>();
		metaData.put(MediaMetaData.MIME, mime);
		metaData.put(MediaMetaData.FILE_NAME, originalName);
		metaData.put(MediaMetaData.SIZE, fileSize);
		return metaData;
	}

	private Query createFindByFileNameQuery(final String fileName)
	{
		return query(whereFilename().is(fileName));
	}
}

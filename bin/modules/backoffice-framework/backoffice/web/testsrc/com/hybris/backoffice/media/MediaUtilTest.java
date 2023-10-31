/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.media;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.tx.Transaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.MimeTypeUtils;
import org.zkoss.util.media.Media;


@RunWith(MockitoJUnitRunner.class)
public class MediaUtilTest
{
	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private SearchRestrictionService searchRestrictionService;
	@Mock
	private SessionService sessionService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private UserService userService;
	@Mock
	private CatalogVersionModel catalogVersion;

	@Spy
	@InjectMocks
	private MediaUtil mediaUtil;

	@Before
	public void setUp()
	{
		doAnswer(invocationOnMock -> {
			final SessionExecutionBody body = (SessionExecutionBody) invocationOnMock.getArguments()[0];
			return body.execute();
		}).when(sessionService).executeInLocalView(any(), any());
		when(catalogVersionService.getCatalogVersion(BackofficeMediaConstants.DEFAULT_MEDIA_CATALOG_ID,
				BackofficeMediaConstants.DEFAULT_MEDIA_CATALOG_VERSION)).thenReturn(catalogVersion);
	}

	@Test
	public void shouldReturnEmptyIfMediaNotExist()
	{
		final var code = "code";
		when(mediaService.getMedia(catalogVersion, code)).thenThrow(UnknownIdentifierException.class);

		final var mediaOpt = mediaUtil.getMedia(code);

		assertThat(mediaOpt).isEmpty();
		verify(searchRestrictionService).enableSearchRestrictions();
	}

	@Test
	public void shouldReturnCorrectMediaIfFindMediaByCodeExist()
	{
		final var code = "code";
		final var media = mock(MediaModel.class);
		when(mediaService.getMedia(catalogVersion, code)).thenReturn(media);

		final var mediaOpt = mediaUtil.getMedia(code);

		assertThat(mediaOpt).isNotEmpty();
		assertThat(mediaOpt.get()).isEqualTo(media);
		verify(searchRestrictionService).enableSearchRestrictions();
	}

	@Test
	public void shouldRemoveModelAndDataWhenDeleteMedia()
	{
		final var media = mock(MediaModel.class);

		mediaUtil.deleteMedia(media);

		verify(modelService).remove(media);
	}

	@Test
	public void shouldUpdateModelAndDataWhenUpdateMedia()
	{
		final var mediaModel = new MediaModel();
		final var zkMedia = mock(Media.class);
		final var byteData = new byte[1];
		final var fileName = "fileName";
		final var type = "type";
		when(zkMedia.getByteData()).thenReturn(byteData);
		when(zkMedia.getName()).thenReturn(fileName);
		when(zkMedia.getContentType()).thenReturn(type);

		mediaUtil.updateMedia(mediaModel, zkMedia);

		verify(modelService).save(argThat(new ArgumentMatcher<MediaModel>()
		{
			@Override
			public boolean matches(final MediaModel model)
			{
				return model.equals(mediaModel) && model.getRealFileName().equals(fileName) && model.getMime().equals(type);
			}
		}));

		verify(mediaService).setDataForMedia(mediaModel, byteData);
	}

	@Test(expected = RuntimeException.class)
	public void shouldRollBackTransactionIfUpdateMediaFailed()
	{
		final var mediaModel = new MediaModel();
		final var zkMedia = mock(Media.class);
		final var ex = mock(ModelSavingException.class);
		when(zkMedia.getName()).thenReturn("fileName");
		when(zkMedia.getContentType()).thenReturn("type");
		doThrow(ex).when(modelService).save(mediaModel);
		try (final MockedStatic<Transaction> transactionMock = mockStatic(Transaction.class))
		{
			final var tx = spy(Transaction.class);
			transactionMock.when(() -> Transaction.current()).thenReturn(tx);
			transactionMock.when(() -> Transaction.toException(ex, RuntimeException.class)).thenReturn(mock(RuntimeException.class));

			mediaUtil.updateMedia(mediaModel, zkMedia);

			verify(tx).rollback();
			verify(mediaService, never()).setDataForMedia(mediaModel, any());
		}
	}

	@Test
	public void shouldCreateMediaWithZKMediaData()
	{
		final var mediaModel = new MediaModel();
		final var zkMedia = mock(Media.class);
		final var byteData = new byte[1];
		final var fileName = "fileName";
		final var type = "type";
		final var mediaCode = "code";
		final var folderCode = "folderCode";
		final var folderModel = mock(MediaFolderModel.class);
		when(zkMedia.getByteData()).thenReturn(byteData);
		when(zkMedia.getName()).thenReturn(fileName);
		when(zkMedia.getContentType()).thenReturn(type);
		when(mediaService.getFolder(folderCode)).thenReturn(folderModel);
		when(modelService.create(MediaModel._TYPECODE)).thenReturn(mediaModel);

		final var savedMedia = mediaUtil.createMedia(mediaCode, folderCode, MediaModel._TYPECODE, zkMedia);

		assertThat(savedMedia).isEqualTo(mediaModel);
		verify(modelService).save(argThat(new ArgumentMatcher<MediaModel>()
		{
			@Override
			public boolean matches(final MediaModel model)
			{
				return model.getRealFileName().equals(fileName) && model.getMime().equals(type) && model.getCode().equals(mediaCode)
						&& model.getFolder().equals(folderModel) && model.getCatalogVersion().equals(catalogVersion);
			}
		}));
		verify(mediaService).setDataForMedia(mediaModel, byteData);
	}

	@Test(expected = RuntimeException.class)
	public void shouldRollBackTransactionIfCreateMediaFailed()
	{
		final var folderCode = "folderCode";
		final var ex = mock(ModelSavingException.class);
		when(mediaService.getFolder(folderCode)).thenReturn(mock(MediaFolderModel.class));
		doThrow(ex).when(modelService).create(MediaModel._TYPECODE);

		try (final MockedStatic<Transaction> transactionMock = mockStatic(Transaction.class))
		{
			final var tx = spy(Transaction.class);
			transactionMock.when(() -> Transaction.current()).thenReturn(tx);
			transactionMock.when(() -> Transaction.toException(ex, RuntimeException.class)).thenReturn(mock(RuntimeException.class));

			mediaUtil.createMedia("code", folderCode, MediaModel._TYPECODE, mock(Media.class));

			verify(tx).rollback();
			verify(mediaService, never()).setDataForMedia(any(), any());
		}
	}

	@Test
	public void shouldCreateMediaWithBasicData()
	{
		final var mediaModel = new MediaModel();
		final var fileName = "fileName";
		final var mediaCode = "code";
		final var folder = BackofficeMediaConstants.BACKOFFICE_THEMES_FOLDER;
		final var mimeType = MimeTypeUtils.IMAGE_PNG_VALUE;
		final var folderModel = mock(MediaFolderModel.class);
		when(mediaService.getFolder(folder)).thenReturn(folderModel);
		when(modelService.create(MediaModel._TYPECODE)).thenReturn(mediaModel);

		final var savedMedia = mediaUtil.createMedia(mediaCode, folder, MediaModel._TYPECODE, mimeType, fileName);

		assertThat(savedMedia).isEqualTo(mediaModel);
		verify(modelService).save(argThat(new ArgumentMatcher<MediaModel>()
		{
			@Override
			public boolean matches(final MediaModel model)
			{
				return model.getRealFileName().equals(fileName) && model.getMime().equals(mimeType) && model.getCode().equals(mediaCode)
						&& model.getFolder().equals(folderModel) && model.getCatalogVersion().equals(catalogVersion);
			}
		}));
	}

	@Test(expected = RuntimeException.class)
	public void shouldRollBackTransactionIfCreateMediaWithBasicData()
	{
		final var folderCode = "folderCode";
		final var ex = mock(ModelSavingException.class);
		when(mediaService.getFolder(folderCode)).thenReturn(mock(MediaFolderModel.class));
		doThrow(ex).when(modelService).create(MediaModel._TYPECODE);

		try (final MockedStatic<Transaction> transactionMock = mockStatic(Transaction.class))
		{
			final var tx = spy(Transaction.class);
			transactionMock.when(() -> Transaction.current()).thenReturn(tx);
			transactionMock.when(() -> Transaction.toException(ex, RuntimeException.class)).thenReturn(mock(RuntimeException.class));

			 mediaUtil.createMedia("code", folderCode, MediaModel._TYPECODE, "png", "fileName");

			verify(tx).rollback();
			verify(modelService, never()).save(any());
		}
	}
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.impl;

import static java.util.Optional.ofNullable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.exceptions.InvalidNamedQueryException;
import de.hybris.platform.cms2.exceptions.SearchExecutionNamedQueryException;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.service.NamedQueryService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.data.NamedQueryData;
import de.hybris.platform.cmsfacades.dto.MediaFileDto;
import de.hybris.platform.cmsfacades.exception.ValidationError;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.media.service.CMSMediaService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.media.exceptions.MediaNotFoundException;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Validator;

import jersey.repackaged.com.google.common.collect.Lists;



@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultMediaFacadeTest
{
	private static final String CODE = "media-code";
	private static final String UUID = "media-uuid";

	@Spy
	@InjectMocks
	private DefaultMediaFacade mediaFacade;

	@Mock
	private MediaService mediaService;
	@Mock
	private ModelService modelService;
	@Mock
	private CMSMediaService cmsMediaService;
	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private NamedQueryService namedQueryService;
	@Mock
	private Validator namedQueryDataValidator;
	@Mock
	private Converter<MediaModel, MediaData> mediaModelConverter;
	@Mock
	private Converter<NamedQueryData, NamedQuery> mediaNamedQueryConverter;
	@Mock
	private Validator createMediaValidator;
	@Mock
	private Validator createMediaFileValidator;
	@Mock
	private Populator<MediaData, MediaModel> createMediaPopulator;
	@Mock
	private Populator<MediaFileDto, MediaModel> createMediaFilePopulator;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Mock
	private CMSAdminSiteService adminSiteService;

	@Mock
	private MediaModel mediaModel1;
	@Mock
	private MediaData mediaData1;
	@Mock
	private MediaFileDto mediaFileDto1;
	@Mock
	private MediaModel mediaModel2;
	@Mock
	private MediaData mediaData2;
	@Mock
	private MediaModel mediaModel3;
	@Mock
	private MediaData mediaData3;
	@Mock
	private NamedQueryData namedQueryData;
	@Mock
	private NamedQuery namedQuery;
	@Mock
	private CatalogVersionModel catalogVersion;

	@Before
	public void setUp() throws InvalidNamedQueryException
	{
		when(mediaModelConverter.convert(mediaModel1)).thenReturn(mediaData1);
		when(mediaModelConverter.convert(mediaModel2)).thenReturn(mediaData2);
		when(mediaModelConverter.convert(mediaModel3)).thenReturn(mediaData3);
		when(adminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersion);
		when(mediaService.getMedia(catalogVersion, CODE)).thenReturn(mediaModel1);
		when(uniqueItemIdentifierService.getItemModel(UUID, MediaModel.class)).thenReturn(ofNullable(mediaModel1));

		when(modelService.create(MediaModel.class)).thenReturn(mediaModel1);
		when(mediaModel1.getItemtype()).thenReturn(MediaModel._TYPECODE);
		doNothing().when(createMediaPopulator).populate(mediaData1, mediaModel1);
		doNothing().when(createMediaFilePopulator).populate(mediaFileDto1, mediaModel1);
		doNothing().when(facadeValidationService).validate(createMediaValidator, mediaData1);
		doNothing().when(facadeValidationService).validate(createMediaFileValidator, mediaFileDto1);

		when(mediaNamedQueryConverter.convert(namedQueryData)).thenReturn(namedQuery);
		when(namedQueryService.search(namedQuery)).thenReturn(Lists.newArrayList(mediaModel1, mediaModel2, mediaModel3));
		doNothing().when(facadeValidationService).validate(namedQueryDataValidator, namedQueryData);

		doReturn(true).when(permissionCRUDService).canCreateTypeInstance(anyString());
		doReturn(true).when(permissionCRUDService).canReadType(anyString());
	}

	@Test
	public void shouldGetMediaCode()
	{
		final MediaData mediaData = mediaFacade.getMediaByCode(CODE);
		assertEquals(mediaData1, mediaData);
	}

	@Test(expected = MediaNotFoundException.class)
	public void shouldFailGetMediaByCode_MediaNotFound()
	{
		when(mediaService.getMedia(catalogVersion, CODE)).thenThrow(new UnknownIdentifierException("exception"));
		mediaFacade.getMediaByCode(CODE);
	}

	@Test
	public void shouldGetMediaByUUID()
	{
		final MediaData mediaData = mediaFacade.getMediaByUUID(UUID);
		assertEquals(mediaData1, mediaData);
	}

	@Test(expected = ValidationException.class)
	public void shouldFailGetMediaByNamedQuery_ValidationErrors()
	{
		doThrow(new ValidationException(new ValidationError("exception"))) //
				.when(facadeValidationService).validate(namedQueryDataValidator, namedQueryData);
		mediaFacade.getMediaByNamedQuery(namedQueryData);
	}

	@Test
	public void shouldGetMediaByNamedQuery_NoResults_ConversionException()
	{
		doThrow(new ConversionException("exception")).when(mediaNamedQueryConverter).convert(namedQueryData);
		final List<MediaData> mediaList = mediaFacade.getMediaByNamedQuery(namedQueryData);
		assertTrue(mediaList.isEmpty());
	}

	@Test
	public void shouldGetMediaByNamedQuery_NoResults_InvalidNamedQuery()
	{
		doThrow(new InvalidNamedQueryException("exception")).when(mediaNamedQueryConverter).convert(namedQueryData);
		final List<MediaData> mediaList = mediaFacade.getMediaByNamedQuery(namedQueryData);
		assertTrue(mediaList.isEmpty());
	}

	@Test
	public void shouldGetMediaByNamedQuery_NoResults_SearchException() throws InvalidNamedQueryException
	{
		Mockito.doThrow(new SearchExecutionNamedQueryException("exception")).when(namedQueryService).search(namedQuery);
		final List<MediaData> mediaList = mediaFacade.getMediaByNamedQuery(namedQueryData);
		assertTrue(mediaList.isEmpty());
	}

	@Test
	public void shouldGetMediaByNamedQuery()
	{
		final List<MediaData> mediaList = mediaFacade.getMediaByNamedQuery(namedQueryData);
		assertTrue(mediaList.contains(mediaData1));
		assertTrue(mediaList.contains(mediaData2));
		assertTrue(mediaList.contains(mediaData3));
	}

	@Test(expected = ValidationException.class)
	public void shouldFailAddMedia_ValidationErrors()
	{
		doThrow(new ValidationException(new ValidationError("exception"))).when(facadeValidationService)
				.validate(createMediaValidator, mediaData1);
		mediaFacade.addMedia(mediaData1, mediaFileDto1);
	}

	@Test(expected = ConversionException.class)
	public void shouldFailAddMedia_ConversionException()
	{
		doThrow(new ConversionException("exception")).when(createMediaPopulator).populate(mediaData1, mediaModel1);
		mediaFacade.addMedia(mediaData1, mediaFileDto1);
	}

	@Test
	public void shouldAddMedia()
	{
		final MediaData mediaData = mediaFacade.addMedia(mediaData1, mediaFileDto1);

		assertThat(mediaData, Matchers.sameInstance(mediaData1));
		verify(facadeValidationService).validate(createMediaValidator, mediaData1);
		verify(facadeValidationService).validate(createMediaFileValidator, mediaFileDto1);
		verify(createMediaPopulator).populate(any(MediaData.class), any(MediaModel.class));
		verify(createMediaFilePopulator).populate(any(MediaFileDto.class), any(MediaModel.class));
		verify(mediaService).setStreamForMedia(any(MediaModel.class), any());
		verify(modelService, times(2)).save(any(Object.class));
	}

	@Test
	public void shouldFindMedias()
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(5);

		MediaModel media = new MediaModel();
		media.setCode("test");
		final SearchResultImpl<MediaModel> mockResult = new SearchResultImpl<>(Arrays.asList(media), 1, 1, 0);
		doReturn(mockResult).when(cmsMediaService).findMediasForCatalogVersion("", "", catalogVersion, pageableData);
		when(mediaModelConverter.convert(media)).thenReturn(mediaData1);

		final SearchResult<MediaData> medias = mediaFacade.findMedias("", "", pageableData);
		assertTrue(medias.getResult().contains(mediaData1));
	}
}

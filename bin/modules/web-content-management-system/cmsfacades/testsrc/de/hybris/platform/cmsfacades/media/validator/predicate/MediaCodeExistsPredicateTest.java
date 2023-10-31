/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.validator.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cmsfacades.data.MediaData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class MediaCodeExistsPredicateTest
{
	private static final String VALID_CODE = "testMediaCode";
	private static final String MEDIA_CATALOG_ID = "test-catalog-id";
    private static final String MEDIA_CATALOG_VERSION = "test-version-id";
	private static final String INVALID_CODE = "invalid";

	@Mock
	private MediaService mediaService;
	@Mock
	private MediaModel media;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CatalogVersionService catalogVersionService;

	@InjectMocks
	private MediaCodeExistsPredicate predicate;

	private MediaData mediaData;

	@Before
    public void setUp()
    {
        mediaData = new MediaData();
       	when(catalogVersionService.getCatalogVersion(MEDIA_CATALOG_ID, MEDIA_CATALOG_VERSION)).thenReturn(catalogVersionModel);
    }

	@Test
	public void shouldFindMediaByCode()
	{
        mediaData.setCode(VALID_CODE);
		when(mediaService.getMedia(catalogVersionModel, VALID_CODE)).thenReturn(media);
		final boolean result = predicate.test(mediaData);
		assertTrue(result);
	}

	@Test
	public void shouldNotFindMediaByCode()
	{
        mediaData.setCode(INVALID_CODE);
		when(catalogVersionService.getCatalogVersion(MEDIA_CATALOG_ID, MEDIA_CATALOG_VERSION)).thenReturn(null);
		when(mediaService.getMedia(null, INVALID_CODE)).thenThrow(new UnknownIdentifierException("code is invalid"));
		final boolean result = predicate.test(mediaData);
		assertFalse(result);
	}

	@Test
	public void shouldFindMultipleMediaByCode()
	{
        mediaData.setCode(VALID_CODE);
		when(mediaService.getMedia(catalogVersionModel, VALID_CODE)).thenThrow(new AmbiguousIdentifierException("multiple entries found for code"));
		final boolean result = predicate.test(mediaData);
		assertTrue(result);
	}

}

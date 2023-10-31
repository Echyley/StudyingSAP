/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.model.ProductImage;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MerchSnProductImagePopulatorTest extends AbstractMerchSnPopulatorTest
{
	protected static String BASE_URL = "baseUrl/";
	protected static String THUMBNAIL_URL_VALUE = "thumbnailUrl";
	protected static String IMAGE_URL_VALUE = "imageUrl";

	protected MerchSnProductImagePopulator populator;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		populator = new MerchSnProductImagePopulator();
	}


	@Test
	public void testPopulateImages()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of(IMG65_FIELD, THUMBNAIL_URL_VALUE, IMG515_FIELD, IMAGE_URL_VALUE));

		//when
		populator.populate(source, target);

		//then
		final ProductImage images = target.getImages();
		assertEquals(THUMBNAIL_URL_VALUE, images.getThumbnailImage());
		assertEquals(IMAGE_URL_VALUE, images.getMainImage());
	}

	@Test
	public void testPopulateImagesWithBaseUrl()
	{
		//given
		when(merchConfig.getBaseImageUrl()).thenReturn(BASE_URL);
		when(snDocument.getFields()).thenReturn(Map.of(IMG65_FIELD, THUMBNAIL_URL_VALUE, IMG515_FIELD, IMAGE_URL_VALUE));

		//when
		populator.populate(source, target);

		//then
		final ProductImage images = target.getImages();
		final String thumbnail = images.getThumbnailImage();
		assertNotNull(thumbnail);
		assertTrue(thumbnail.startsWith(BASE_URL));
		assertTrue(thumbnail.endsWith(THUMBNAIL_URL_VALUE));
		final String mainImage = images.getMainImage();
		assertNotNull(mainImage);
		assertTrue(mainImage.startsWith(BASE_URL));
		assertTrue(mainImage.endsWith(IMAGE_URL_VALUE));
	}

	@Test
	public void testPopulateWhenNoValues()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of());

		//when
		populator.populate(source, target);

		//then
		final ProductImage images = target.getImages();
		assertTrue(StringUtils.isEmpty(images.getThumbnailImage()));
		assertTrue(StringUtils.isEmpty(images.getMainImage()));
	}
}

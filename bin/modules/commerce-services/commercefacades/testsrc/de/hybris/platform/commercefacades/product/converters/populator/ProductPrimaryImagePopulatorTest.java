/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.ImageFormatMapping;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaContainerService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


/**
 * Test suite for {@link ProductPrimaryImagePopulator}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductPrimaryImagePopulatorTest
{
	private static final String MEDIA_FORMAT_1 = "thumb";
	private static final String MEDIA_FORMAT_QUALIFIER_1 = "96x96";
	private static final String MEDIA_FORMAT_2 = "zoom";
	private static final String MEDIA_FORMAT_QUALIFIER_2 = "545x545";
	private static final String PRODUCT_NAME = "TestProduct";

	@Mock
	private MediaService mediaService;
	@Mock
	private MediaContainerService mediaContainerService;
	@Mock
	private ImageFormatMapping imageFormatMapping;
	@Mock
	private Converter<MediaModel, ImageData> imageConverter;
	@Mock
	private ModelService modelService;
	@Mock
	private ProductModel productModel;
	@Spy
	private ProductData productData;
	@Mock
	private MediaModel pictureMedia;
	@Mock
	private MediaContainerModel pictureMediaContainerModel;
	@Mock
	private MediaFormatModel thumbFormat;
	@Mock
	private MediaFormatModel zoomFormat;
	@Mock
	private MediaModel thumbMedia;
	@Mock
	private MediaModel zoomMedia;
	@Mock
	private ImageData thumbImage;
	@Mock
	private ImageData zoomImage;
	@Mock
	private ImageData existingPrimaryImage;
	@Mock
	private ImageData existingGalleryImage;

	private ProductPrimaryImagePopulator<ProductModel, ProductData> productPrimaryImagePopulator;

	@Before
	public void setUp()
	{
		final List<String> imageFormats = new ArrayList<>();
		imageFormats.add(MEDIA_FORMAT_1);
		imageFormats.add(MEDIA_FORMAT_2);
		productPrimaryImagePopulator = new ProductPrimaryImagePopulator<>();
		productPrimaryImagePopulator.setModelService(modelService);
		productPrimaryImagePopulator.setImageConverter(imageConverter);
		productPrimaryImagePopulator.setImageFormatMapping(imageFormatMapping);
		productPrimaryImagePopulator.setImageFormats(imageFormats);
		productPrimaryImagePopulator.setMediaContainerService(mediaContainerService);
		productPrimaryImagePopulator.setMediaService(mediaService);

		given(existingPrimaryImage.getImageType()).willReturn(ImageDataType.PRIMARY);
		given(existingGalleryImage.getImageType()).willReturn(ImageDataType.GALLERY);
		given(productModel.getName()).willReturn(PRODUCT_NAME);
		given(mediaService.getFormat(MEDIA_FORMAT_QUALIFIER_1)).willReturn(thumbFormat);
		given(mediaService.getFormat(MEDIA_FORMAT_QUALIFIER_2)).willReturn(zoomFormat);
		given(mediaContainerService.getMediaForFormat(pictureMediaContainerModel, thumbFormat)).willReturn(thumbMedia);
		given(mediaContainerService.getMediaForFormat(pictureMediaContainerModel, zoomFormat)).willReturn(zoomMedia);
		given(imageConverter.convert(thumbMedia)).willReturn(thumbImage);
		given(imageConverter.convert(zoomMedia)).willReturn(zoomImage);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(MEDIA_FORMAT_1)).willReturn(MEDIA_FORMAT_QUALIFIER_1);
		given(imageFormatMapping.getMediaFormatQualifierForImageFormat(MEDIA_FORMAT_2)).willReturn(MEDIA_FORMAT_QUALIFIER_2);
		given(pictureMedia.getMediaContainer()).willReturn(pictureMediaContainerModel);
		given(modelService.getAttributeValue(productModel, ProductModel.PICTURE)).willReturn(pictureMedia);
	}

	@Test
	public void testPopulateWithNewImagesOnly()
	{
		productPrimaryImagePopulator.populate(productModel, productData);

		assertThat(productData.getImages()).containsExactlyInAnyOrder(thumbImage, zoomImage);

		verifyPopulatedPrimaryImages();
	}

	@Test
	public void testPopulateWithExistingImagesOnly()
	{
		productPrimaryImagePopulator.setImageFormats(Collections.emptyList());
		productData.setImages(List.of(existingPrimaryImage, existingGalleryImage));

		productPrimaryImagePopulator.populate(productModel, productData);

		assertThat(productData.getImages()).containsExactly(existingGalleryImage);
	}

	@Test
	public void testPopulateWithExistingAndNewImages()
	{
		productData.setImages(List.of(existingPrimaryImage, existingGalleryImage));

		productPrimaryImagePopulator.populate(productModel, productData);

		assertThat(productData.getImages()).containsExactlyInAnyOrder(thumbImage, zoomImage, existingGalleryImage);

		verifyPopulatedPrimaryImages();
	}

	@Test
	public void testPopulateWhenNoMediaContainer()
	{
		given(modelService.getAttributeValue(productModel, ProductModel.PICTURE)).willReturn(null);

		productPrimaryImagePopulator.populate(productModel, productData);

		verify(productData, never()).getImages();
		verify(productData, never()).setImages(anyList());
		assertThat(productData.getImages()).isNull();
	}

	@Test
	public void testPopulateWhenEmptyImageFormatsList()
	{
		productPrimaryImagePopulator.setImageFormats(Collections.emptyList());

		productPrimaryImagePopulator.populate(productModel, productData);

		assertThat(productData.getImages()).isEmpty();
	}

	private void verifyPopulatedPrimaryImages()
	{
		verify(thumbImage).setAltText(PRODUCT_NAME);
		verify(zoomImage).setAltText(PRODUCT_NAME);
		verify(thumbImage).setImageType(ImageDataType.PRIMARY);
		verify(zoomImage).setImageType(ImageDataType.PRIMARY);
		verify(thumbImage).setFormat(MEDIA_FORMAT_1);
		verify(zoomImage).setFormat(MEDIA_FORMAT_2);
		verify(thumbImage, never()).setGalleryIndex(anyInt());
		verify(zoomImage, never()).setGalleryIndex(anyInt());
	}
}

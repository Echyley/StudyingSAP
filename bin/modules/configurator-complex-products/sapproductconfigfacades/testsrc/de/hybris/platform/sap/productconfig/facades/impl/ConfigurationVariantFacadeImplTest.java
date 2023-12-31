/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductImagePopulator;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationVariantData;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.data.VariantSearchResult;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationVariantSearchService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit test for {@link ConfigurationVariantFacadeImpl}
 */
@UnitTest
public class ConfigurationVariantFacadeImplTest
{
	private static final String CONFIG_ID = "configId_123";
	private static final String PRODUCT_CODE = "SAP_SIMPLE_POC";

	private ConfigurationVariantFacadeImpl classUnderTest;

	@Mock
	private ProductConfigurationVariantSearchService variantSearchServiceMock;
	@Mock
	private ProductConfigurationService productConfigurationServiceMock;	
	@Mock
	private ProductModel productMock;
	@Mock
	private ProductService productServiceMock;
	@Mock
	private AbstractProductImagePopulator<ProductModel, ProductData> imagePopulatorMock;
	@Mock
	private ProductPricePopulator<ProductModel, ProductData> pricePopulatorMock;

	private List<VariantSearchResult> variantSearchResult;
	private ImageData imageData;
	private Collection<ImageData> imageList;
	private PriceData priceData;
	private ConfigModel configModel;
	private KBKey kbKey;



	@Before
	public void setUp()
	{
		kbKey = new KBKeyImpl(PRODUCT_CODE);
		configModel = new ConfigModelImpl();
		configModel.setKbKey(kbKey);
		classUnderTest = new ConfigurationVariantFacadeImpl();
		MockitoAnnotations.initMocks(this);

		variantSearchResult = new ArrayList();
		given(variantSearchServiceMock.getVariantsForConfiguration(CONFIG_ID, PRODUCT_CODE)).willReturn(variantSearchResult);
		classUnderTest.setVariantSerachService(variantSearchServiceMock);


		given(productMock.getName()).willReturn("Test Product Name");
		given(productMock.getCode()).willReturn(PRODUCT_CODE);
		given(productServiceMock.getProductForCode(PRODUCT_CODE)).willReturn(productMock);
		given(productConfigurationServiceMock.retrieveConfigurationOverview(CONFIG_ID)).willReturn(configModel);
		classUnderTest.setProductService(productServiceMock);
		classUnderTest.setProductConfigurationService(productConfigurationServiceMock);

		mockImagePopulator();
		mockPricePopulator();
	}

	protected void mockPricePopulator()
	{
		priceData = new PriceData();
		final Answer answer = new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final ProductModel model = (ProductModel) args[0];
				final ProductData data = (ProductData) args[1];
				if (PRODUCT_CODE.equals(model.getCode()))
				{
					data.setPrice(priceData);
				}
				return null;
			}
		};
		Mockito.doAnswer(answer).when(pricePopulatorMock).populate(Mockito.any(ProductModel.class), Mockito.any(ProductData.class));
		classUnderTest.setPricePopulator(pricePopulatorMock);
	}

	protected void mockImagePopulator()
	{
		imageData = new ImageData();
		imageData.setImageType(ImageDataType.PRIMARY);
		imageList = new ArrayList();
		imageList.add(imageData);
		final Answer answer = new Answer<Object>()
		{
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				final Object[] args = invocation.getArguments();
				final ProductModel model = (ProductModel) args[0];
				final ProductData data = (ProductData) args[1];
				if (PRODUCT_CODE.equals(model.getCode()))
				{
					data.setImages(imageList);
				}
				return null;
			}
		};
		Mockito.doAnswer(answer).when(imagePopulatorMock).populate(Mockito.any(ProductModel.class), Mockito.any(ProductData.class));
		classUnderTest.setImagePopulator(imagePopulatorMock);
	}

	@Test
	public void testSearchForSimilarVariantsNoVariantsFound()
	{
		final List<ConfigurationVariantData> variants = classUnderTest.searchForSimilarVariants(CONFIG_ID, PRODUCT_CODE);
		assertTrue("No variants expected", variants.isEmpty());
	}

	@Test
	public void testSearchForSimilarVariantsSomeVariantsFound()
	{

		variantSearchResult.add(createSearchResult("123"));
		variantSearchResult.add(createSearchResult("567"));
		given(productServiceMock.getProductForCode(Mockito.anyString())).willReturn(productMock);

		final List<ConfigurationVariantData> variants = classUnderTest.searchForSimilarVariants(CONFIG_ID, PRODUCT_CODE);
		assertEquals("2 variants expected", 2, variants.size());
		for (final ConfigurationVariantData variantData : variants)
		{
			assertTrue("Unexpected variant matched", contains(variantSearchResult, variantData.getProductCode()));
		}
	}

	protected boolean contains(final List<VariantSearchResult> resultList, final String productCode)
	{
		for (final VariantSearchResult searchResult : resultList)
		{
			if (searchResult.getProductCode().equals(productCode))
			{
				return true;
			}
		}
		return false;
	}

	protected VariantSearchResult createSearchResult(final String string)
	{
		final VariantSearchResult result = new VariantSearchResult();
		result.setProductCode(string);
		return result;
	}

	@Test
	public void testCreateVariantData_ProductCode()
	{
		final ConfigurationVariantData variantData = classUnderTest.createVariantData(PRODUCT_CODE);
		assertNotNull("null object returned", variantData);
		assertEquals(PRODUCT_CODE, variantData.getProductCode());
	}

	@Test
	public void testCreateVariantData_ProductName()
	{
		final ConfigurationVariantData variantData = classUnderTest.createVariantData(PRODUCT_CODE);
		assertNotNull("null object returned", variantData);
		assertEquals("Test Product Name", variantData.getName());
	}

	@Test
	public void testCreateVariantData_ImageData()
	{
		final ConfigurationVariantData variantData = classUnderTest.createVariantData(PRODUCT_CODE);
		assertNotNull("null object returned", variantData);
		assertSame("wrong image returned", imageData, variantData.getImageData());
	}

	@Test
	public void testCreateVariantData_PriceData()
	{
		final ConfigurationVariantData variantData = classUnderTest.createVariantData(PRODUCT_CODE);
		assertNotNull("null object returned", variantData);
		assertSame("wrong price returned", priceData, variantData.getPrice());
	}

	@Test
	public void testAddPriceData_noPrice()
	{
		given(productMock.getCode()).willReturn("bla123");
		final ConfigurationVariantData variantData = new ConfigurationVariantData();
		classUnderTest.addPriceData(variantData, productMock);
		assertNull("images not expected", variantData.getPrice());
	}

	@Test
	public void testAddImageData_noImages()
	{
		given(productMock.getCode()).willReturn("bla123");
		final ConfigurationVariantData variantData = new ConfigurationVariantData();
		classUnderTest.addImageData(variantData, productMock);
		assertNull("images not expected", variantData.getImageData());
	}

	@Test
	public void testAddImageData_selectCorrectImageIndex()
	{
		imageData.setGalleryIndex(Integer.valueOf(0));
		imageData.setImageType(ImageDataType.PRIMARY);
		imageList.clear();
		final ImageData anotherGalleryImage = new ImageData();
		anotherGalleryImage.setGalleryIndex(Integer.valueOf(5));
		anotherGalleryImage.setImageType(ImageDataType.PRIMARY);
		imageList.add(anotherGalleryImage);
		imageList.add(imageData);
		imageList.add(anotherGalleryImage);
		final ConfigurationVariantData variantData = new ConfigurationVariantData();
		classUnderTest.addImageData(variantData, productMock);
		assertSame("wrong image returned", imageData, variantData.getImageData());
	}

	@Test
	public void testAddImageData_selectCorrectImageType()
	{
		imageData.setGalleryIndex(Integer.valueOf(3));
		imageData.setImageType(ImageDataType.PRIMARY);
		imageList.clear();
		final ImageData anotherGalleryImage = new ImageData();
		anotherGalleryImage.setGalleryIndex(Integer.valueOf(0));
		anotherGalleryImage.setImageType(ImageDataType.GALLERY);
		imageList.add(anotherGalleryImage);
		imageList.add(imageData);
		imageList.add(anotherGalleryImage);
		final ConfigurationVariantData variantData = new ConfigurationVariantData();
		classUnderTest.addImageData(variantData, productMock);
		assertSame("wrong image returned", imageData, variantData.getImageData());
	}

	@Test
	public void testUseImage_nullImage()
	{
		final boolean useImage = classUnderTest.useImage(null, null);
		assertFalse("nulll image may not be used", useImage);
	}

	@Test
	public void testUseImage_noGalleryIndex()
	{
		final ImageData image = new ImageData();
		image.setImageType(ImageDataType.PRIMARY);
		final boolean useImage = classUnderTest.useImage(image, image);
		assertFalse("nulll image may not be used", useImage);
	}
	
	@Test
	public void testSearchForSimilarVariantsWoProductSomeVariantsFound()
	{

		variantSearchResult.add(createSearchResult("123"));
		variantSearchResult.add(createSearchResult("567"));
		given(productServiceMock.getProductForCode(Mockito.anyString())).willReturn(productMock);

		final List<ConfigurationVariantData> variants = classUnderTest.searchForSimilarVariants(CONFIG_ID);
		assertEquals("2 variants expected", 2, variants.size());
		for (final ConfigurationVariantData variantData : variants)
		{
			assertTrue("Unexpected variant matched", contains(variantSearchResult, variantData.getProductCode()));
		}
	}

	@Test(expected=IllegalStateException.class)
	public void testSearchForSimilarVariantsWoNoConfigFound()
	{
		given(productConfigurationServiceMock.retrieveConfigurationOverview(CONFIG_ID)).willReturn(null);
		classUnderTest.searchForSimilarVariants(CONFIG_ID);
 	}

	@Test(expected=IllegalStateException.class)
	public void testSearchForSimilarVariantsWoNoKbKey()
	{
		given(productConfigurationServiceMock.retrieveConfigurationOverview(CONFIG_ID)).willReturn(new ConfigModelImpl());
		classUnderTest.searchForSimilarVariants(CONFIG_ID);
 	}	


}

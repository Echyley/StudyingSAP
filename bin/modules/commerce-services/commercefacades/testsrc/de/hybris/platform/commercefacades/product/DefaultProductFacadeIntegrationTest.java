/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.customerreview.setup.CustomerReviewSystemSetup;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.UserGroup;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Integration test suite for {@link DefaultProductFacade}
 */
@IntegrationTest
public class DefaultProductFacadeIntegrationTest extends ServicelayerTest
{

	private static final String LANG_EN = "en";
	private static final Double TEST_RATING = Double.valueOf(5d);
	private static final String TEST_COMMENT = "comment";
	private static final String TEST_HEADLINE = "headline";
	private static final String TEST_PRODUCT_CODE_3423 = "HW1210-3423";
	private static final String TEST_PRODUCT_CODE_3424 = "HW1210-3424";
	private static final String TEST_PRODUCT_CODE_3425 = "HW1210-3425";
	private static final String TEST_PRODUCT_CODE_3426 = "HW1210-3426";
	private static final String TEST_PRODUCT_CODE_2356 = "HW2300-2356";
	private static final String TEST_BASESITE_UID = "testSite";

	private static final Logger LOG = Logger.getLogger(DefaultProductFacadeIntegrationTest.class);

	@Resource
	private ProductFacade productFacade;

	@Resource
	private ModelService modelService;

	@Resource
	private ProductService productService;

	@Resource
	private UserService userService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private CustomerReviewSystemSetup customerReviewSystemSetup;

	@Resource
	private TypeService typeService;

	@Resource
	private BaseSiteService baseSiteService;


	@Before
	public void setUp() throws Exception
	{
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_EN));
		createUserGroups();
		final Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("customerreview.searchrestrictions.create", new String[] { "true" });
		final SystemSetupContext ctx = new SystemSetupContext(params, Type.ESSENTIAL, Process.ALL, "customerreview");
		customerReviewSystemSetup.createSearchRestrictions(ctx);

		// Create data for tests
		LOG.info("Creating data for product facade..");
		userService.setCurrentUser(userService.getAnonymousUser());
		final long startTime = System.currentTimeMillis();
		new CoreBasicDataCreator().createEssentialData(Collections.EMPTY_MAP, null);
		// importing test csv
		importCsv("/commercefacades/test/testProductFacade.csv", "utf-8");
		importCsv("/commercefacades/test/testProductReferences.csv", "utf-8");
		createCoreData();
		createHardwareCatalog();
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage(LANG_EN));

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID), false);

		LOG.info("Finished data for product facade " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Test
	public void testGetProductForCodeBasic()
	{
		//There is no variant type created for testing. Variants must be tested in proper extension, where any variant is available.
		final ProductData productData = productFacade
				.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3424, Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));

		assertNotNull(productData);
		assertEquals(TEST_PRODUCT_CODE_3424, productData.getCode());

		assertNotNull(productData.getPrice());
		assertEquals(BigDecimal.valueOf(32.95), productData.getPrice().getValue());

		assertEquals(Double.valueOf(3.5), productData.getAverageRating());
		assertEquals("testManufacturer", productData.getManufacturer());
		assertEquals(Boolean.TRUE, productData.getPurchasable());
	}

	@Test
	public void testGetProductForCodeVolumePricing()
	{
		//There is no variant type created for testing. Variants must be tested in proper extension, where any variant is available.
		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3426,
				Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.VOLUME_PRICES));

		assertNotNull(productData);
		assertEquals(TEST_PRODUCT_CODE_3426, productData.getCode());

		assertNotNull(productData.getVolumePrices());
		assertEquals(2, productData.getVolumePrices().size());

		assertEquals("EUR", productData.getVolumePrices().get(0).getCurrencyIso());
		assertEquals(Long.valueOf(1), productData.getVolumePrices().get(0).getMinQuantity());
		assertEquals(Long.valueOf(9), productData.getVolumePrices().get(0).getMaxQuantity());
		assertEquals(BigDecimal.valueOf(85.0), productData.getVolumePrices().get(0).getValue());

		assertEquals("EUR", productData.getVolumePrices().get(1).getCurrencyIso());
		assertEquals(Long.valueOf(10), productData.getVolumePrices().get(1).getMinQuantity());
		assertNull(productData.getVolumePrices().get(1).getMaxQuantity());
		assertEquals(BigDecimal.valueOf(80.0), productData.getVolumePrices().get(1).getValue());

	}

	@Test
	public void testGetProductForCodeImagesAndCategories()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.PRICE);
		options.add(ProductOption.GALLERY);
		options.add(ProductOption.CATEGORIES);
		//There is no variant type created for testing. Variants must be tested in proper extension, where any variant is available.
		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3424, options);

		assertNotNull(productData);
		assertEquals(TEST_PRODUCT_CODE_3424, productData.getCode());

		assertNotNull(productData.getPrice());
		assertEquals(BigDecimal.valueOf(32.95), productData.getPrice().getValue());

		assertNotNull(productData.getCategories());
		assertEquals("testCategory1", productData.getCategories().iterator().next().getCode());
		assertEquals(Double.valueOf(3.5), productData.getAverageRating());
		assertEquals("testManufacturer", productData.getManufacturer());
		assertEquals(Boolean.TRUE, productData.getPurchasable());
	}

	@Ignore("BIT-2420 (explicitly testing promotion functionality)")
	@Test
	public void testGetProductForCodePromotions()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.PROMOTIONS);
		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3425, options);

		assertNotNull(productData);
		assertEquals(TEST_PRODUCT_CODE_3425, productData.getCode());
		assertEquals(1, productData.getPotentialPromotions().size());
		assertEquals("Fixed Price for default", productData.getPotentialPromotions().iterator().next().getDescription());
	}

	@Test
	public void testGetProductForCodeStock()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.STOCK);

		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_3423, options);

		assertNotNull(productData);

		assertEquals(TEST_PRODUCT_CODE_3423, productData.getCode());
		assertEquals(Long.valueOf(122), productData.getStock().getStockLevel());
	}

	@Test
	public void testGetProductForCodeClassification()
	{
		final Set<ProductOption> options = new HashSet<ProductOption>();
		options.add(ProductOption.CLASSIFICATION); //no data for that found

		final ProductData productData = productFacade.getProductForCodeAndOptions(TEST_PRODUCT_CODE_2356, options);

		assertNotNull(productData);

		assertEquals(TEST_PRODUCT_CODE_2356, productData.getCode());
		assertNotNull(productData.getClassifications());
		assertEquals(3, productData.getClassifications().size());
		assertEquals(1,
				productData.getClassifications().iterator().next().getFeatures().iterator().next().getFeatureValues().size());
		assertEquals("PCI Express",
				productData.getClassifications().iterator().next().getFeatures().iterator().next().getFeatureValues().iterator()
						.next().getValue());

	}

	@Test
	public void testGetProductReferencesForCode()
	{
		final List<ProductOption> options = Arrays.asList(ProductOption.BASIC);

		final List<ProductReferenceData> productReferenceDatas = productFacade
				.getProductReferencesForCode("camera1", ProductReferenceTypeEnum.ACCESSORIES, options, null);

		assertNotNull(productReferenceDatas);

		assertEquals(1, productReferenceDatas.size());
		assertEquals("ACCESSORIES", productReferenceDatas.get(0).getReferenceType().getCode());
	}

	@Test
	public void testGetProductReferencesForCodeWithLimit()
	{
		final List<ProductOption> options = Arrays.asList(ProductOption.BASIC);

		final List<ProductReferenceData> productReferenceDatas = productFacade
				.getProductReferencesForCode("camera1", ProductReferenceTypeEnum.ACCESSORIES, options, Integer.valueOf(1));

		assertNotNull(productReferenceDatas);

		assertEquals(1, productReferenceDatas.size());
		assertEquals("ACCESSORIES", productReferenceDatas.get(0).getReferenceType().getCode());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testGetAllReviewsError()
	{
		productFacade.getReviews("unknown");
		Assert.fail("UnknownIdentifier expected");
	}

	@Test
	public void testPostReview()
	{
		final ReviewData reviewData = new ReviewData();
		reviewData.setComment(TEST_COMMENT);
		reviewData.setHeadline(TEST_HEADLINE);
		reviewData.setRating(TEST_RATING);
		final int currentReviewsCount = productFacade.getReviews(TEST_PRODUCT_CODE_3423).size();
		productFacade.postReview(TEST_PRODUCT_CODE_3423, reviewData);
		List<ReviewData> reviews = productFacade.getReviews(TEST_PRODUCT_CODE_3423);
		assertEquals(currentReviewsCount + 1, reviews.size());

		final ProductModel productModel = productService.getProductForCode(TEST_PRODUCT_CODE_3423);
		for (final CustomerReviewModel customerReviewModel : productModel.getProductReviews())
		{
			customerReviewModel.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
			modelService.save(customerReviewModel);
		}
		reviews = productFacade.getReviews(TEST_PRODUCT_CODE_3423);
		final ReviewData review = reviews.get(0);
		assertNotNull(review);
		Assert.assertNotSame(reviewData, review);
		assertEquals(reviewData.getRating(), review.getRating());
		assertEquals(reviewData.getComment(), review.getComment());
		assertEquals(reviewData.getHeadline(), review.getHeadline());
		assertEquals(userService.getAdminUser().getUid(), review.getPrincipal().getUid());
		assertEquals(userService.getAdminUser().getName(), review.getPrincipal().getName());
	}

	protected void createUserGroups()
	{
		UserGroup customerGroup = null;
		try
		{
			customerGroup = UserManager.getInstance().getUserGroupByGroupID(Constants.USER.CUSTOMER_USERGROUP);
		}
		catch (final JaloItemNotFoundException e)
		{
			try
			{
				customerGroup = UserManager.getInstance().createUserGroup(Constants.USER.CUSTOMER_USERGROUP);
			}
			catch (final ConsistencyCheckException ex)
			{
				LOG.error("Could not create a customer user group.");
			}
		}

		final Set<UserGroup> customerGroupList = new LinkedHashSet<UserGroup>();
		customerGroupList.add(customerGroup);
		typeService.getAttributeDescriptor(CustomerModel._TYPECODE, Customer.GROUPS).setDefaultValue(customerGroupList);
		UserManager.getInstance().getAnonymousCustomer().addToGroups(jaloSession.getSessionContext(), customerGroup);
	}

}

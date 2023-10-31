/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.ReviewData;
import de.hybris.platform.assistedservicefacades.customer360.populators.ReviewDataPopulator;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class AsmReviewPopulatorTest
{
	@InjectMocks
	private final ReviewDataPopulator populator = new ReviewDataPopulator();

	@Mock
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Test
	public void populateTest()
	{
		final String headline = "HEADLINE";
		final Double rate = Double.valueOf(3);
		final Date created = new Date();
		final Date update = new Date();

		final CustomerReviewModel reviewModel = new CustomerReviewModel();
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		Mockito.when(productModel.getName()).thenReturn("");
		Mockito.when(productModelUrlResolver.resolve(productModel)).thenReturn("");

		reviewModel.setProduct(productModel);
		reviewModel.setHeadline(headline);
		reviewModel.setApprovalStatus(CustomerReviewApprovalType.APPROVED);
		reviewModel.setRating(rate);
		reviewModel.setCreationtime(created);
		reviewModel.setModifiedtime(update);

		final ReviewData reviewData = new ReviewData();

		populator.populate(reviewModel, reviewData);

		Assert.assertEquals(CustomerReviewApprovalType.APPROVED.getCode(), reviewData.getReviewStatus());
		Assert.assertEquals(headline, reviewData.getReviewText());
		Assert.assertEquals(created, reviewData.getCreated());
		Assert.assertEquals(update, reviewData.getUpdated());
		Assert.assertEquals(rate, reviewData.getRating());
	}
}
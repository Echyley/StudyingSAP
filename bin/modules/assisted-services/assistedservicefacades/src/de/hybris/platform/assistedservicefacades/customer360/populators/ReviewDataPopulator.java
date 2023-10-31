/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.populators;

import de.hybris.platform.assistedservicefacades.customer360.ReviewData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

/**
 * CustomerReviewModel -> ReviewData populator
 */
public class ReviewDataPopulator implements Populator<CustomerReviewModel, ReviewData>
{
    private UrlResolver<ProductModel> productModelUrlResolver;

    @Override
    public void populate(CustomerReviewModel customerReviewModel, ReviewData reviewData)
    {
        reviewData.setProductName(customerReviewModel.getProduct().getName());
        reviewData.setReviewStatus(customerReviewModel.getApprovalStatus().getCode());
        reviewData.setReviewText(customerReviewModel.getHeadline());
        reviewData.setCreated(customerReviewModel.getCreationtime());
        reviewData.setUpdated(customerReviewModel.getModifiedtime());
        reviewData.setRating(customerReviewModel.getRating());
        reviewData.setSKUNumber(customerReviewModel.getProduct().getCode());
        reviewData.setProductUrl(getProductModelUrlResolver().resolve(customerReviewModel.getProduct()));
    }

    protected UrlResolver<ProductModel> getProductModelUrlResolver()
    {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
    {
        this.productModelUrlResolver = productModelUrlResolver;
    }
}

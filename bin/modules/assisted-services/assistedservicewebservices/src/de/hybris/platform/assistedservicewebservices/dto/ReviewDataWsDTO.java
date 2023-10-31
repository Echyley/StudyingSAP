/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
/**
 * Representation of a review
 */
@Schema(name="ReviewData", description="Representation of a review.")
public  class ReviewDataWsDTO  implements Serializable

{
    /** Default serialVersionUID value. */

    private static final long serialVersionUID = 1L;

    /** Name of the product<br/><br/><i>Generated property</i> for <code>ReviewDataWsDTO.productName</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="productName", description="Name of the product.", example = "Flagship tripod with remote control and pan handle")
    private String productName;

    /** Code of the product<br/><br/><i>Generated property</i> for <code>ReviewDataWsDTO.productCode</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="productCode", description="Code of the product.", example = "23355")
    private String productCode;

    /** Date the review was created<br/><br/><i>Generated property</i> for <code>ReviewDataWsDTO.createdAt</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="createdAt", description="Date the review was created.", example = "2022-09-12T12:56:57.624Z")
    private Date createdAt;

    /** Date the review was updated<br/><br/><i>Generated property</i> for <code>ReviewDataWsDTO.updatedAt</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="updatedAt", description="Date the review was updated.", example = "2022-09-12T12:56:57.624Z")
    private Date updatedAt;

    /** Rating of the product<br/><br/><i>Generated property</i> for <code>ReviewDataWsDTO.rating</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="rating", description="Rating of the product, which is configurable in the backend and the defaults are min=1 and max=5.", example = "4.5")
    private Double rating;

    /** Status of the review<br/><br/><i>Generated property</i> for <code>ReviewDataWsDTO.reviewStatus</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="reviewStatus", description="Status of the review, allowed values are 'approved', 'pending', 'rejected'.", example = "approved")
    private String reviewStatus;

    /** Text of the review<br/><br/><i>Generated property</i> for <code>ReviewDataWsDTO.reviewText</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="reviewText", description="Text of the review.", example = "This product is excellent")
    private String reviewText;

    public ReviewDataWsDTO()
    {
        // default constructor
    }

    public void setProductName(final String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductCode(final String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setCreatedAt(final Date createdAt)
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setUpdatedAt(final Date updatedAt)
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt()
    {
        return updatedAt;
    }

    public void setRating(final Double rating)
    {
        this.rating = rating;
    }

    public Double getRating()
    {
        return rating;
    }

    public void setReviewStatus(final String reviewStatus)
    {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewStatus()
    {
        return reviewStatus;
    }

    public void setReviewText(final String reviewText)
    {
        this.reviewText = reviewText;
    }

    public String getReviewText()
    {
        return reviewText;
    }
}


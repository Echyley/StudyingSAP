/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Representation of a list of reviews.
 */
@Schema(name="C360ReviewList", description="Representation of a list of reviews.")
public  class C360ReviewList extends Customer360DataWsDTO

{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360ReviewList.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360ReviewList")
    private String type;

    /** List of reviews the customer has provided<br/><br/><i>Generated property</i> for <code>C360ReviewList.reviews</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="reviews", description="List of reviews the customer has provided.")
    private List<ReviewDataWsDTO> reviews;

    public C360ReviewList()
    {
        // default constructor
    }

    public void setReviews(final List<ReviewDataWsDTO> reviews)
    {
        this.reviews = reviews;
    }

    public List<ReviewDataWsDTO> getReviews()
    {
        return reviews;
    }
}

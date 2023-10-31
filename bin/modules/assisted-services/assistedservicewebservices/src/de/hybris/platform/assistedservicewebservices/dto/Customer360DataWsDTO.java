/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representation of the Customer 360 data.
 */
@Schema(
        name="Customer360Data",
        description="Representation of the Customer 360 data.",
        oneOf = {
                C360ReviewList.class,
                C360StoreLocation.class,
                C360Overview.class,
                C360PromotionList.class,
                C360CouponList.class,
                C360CustomerCouponList.class,
                C360CustomerProfile.class,
                C360CustomerProductInterestList.class,
                C360ActivityList.class,
                C360Cart.class,
                C360SavedCart.class,
                C360TicketList.class
        }
)
public  class Customer360DataWsDTO implements Serializable

{

    /** Default serialVersionUID value. */

    private static final long serialVersionUID = 1L;

    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>Customer360DataWsDTO.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data, for example, 'c360StoreLocation', 'c360ReviewList', 'c360CustomerProductInterestList', 'c360PromotionList', 'c360CustomerCouponList', 'c360CouponList', 'c360Overview', 'c360CustomerProfile', 'c360ActivityList', 'c360Cart', 'c360SavedCart', 'c360TicketList'.", example = "c360StoreLocation")
    private String type;

    public Customer360DataWsDTO()
    {
        // default constructor
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
}

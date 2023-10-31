/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Representation of a list of coupons.
 */
@Schema(name="C360CouponList", description="Representation of a list of coupons.")
public  class C360CouponList extends Customer360DataWsDTO

{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360CouponList.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360CouponList")
    private String type;

    /** List of coupons <br/><br/><i>Generated property</i> for <code>C360CouponList.coupons</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="coupons", description="List of coupons.")
    private List<C360CouponWsDTO> coupons;

    public C360CouponList()
    {
        // default constructor
    }


    public List<C360CouponWsDTO> getCoupons()
    {
        return coupons;
    }

    public void setCoupons(List<C360CouponWsDTO> coupons)
    {
        this.coupons = coupons;
    }
}

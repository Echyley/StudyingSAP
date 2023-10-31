/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Representation of a list of customer coupons.
 */
@Schema(name="C360CustomerCouponList", description="Representation of a list of customer coupons")
public  class C360CustomerCouponList extends Customer360DataWsDTO
{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360CustomerCouponList.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360CustomerCouponList")
    private String type;

    /** List of customer coupons <br/><br/><i>Generated property</i> for <code>C360CustomerCouponList.customerCoupons</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="customerCoupons", description="List of customer coupons.")
    private List<C360CustomerCouponWsDTO> customerCoupons;

    public C360CustomerCouponList()
    {
        // default constructor
    }

    public List<C360CustomerCouponWsDTO> getCustomerCoupons()
    {
        return customerCoupons;
    }

    public void setCustomerCoupons(List<C360CustomerCouponWsDTO> customerCoupons)
    {
        this.customerCoupons = customerCoupons;
    }


}

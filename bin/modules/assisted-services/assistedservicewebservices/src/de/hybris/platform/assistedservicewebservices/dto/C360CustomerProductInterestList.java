/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Representation of a list of customer interests in products.
 */
@Schema(name="C360CustomerProductInterestList", description="Representation of a list of customer interests in products.")
public  class C360CustomerProductInterestList extends Customer360DataWsDTO

{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360CustomerProductInterestList.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360CustomerProductInterestList")
    private String type;

    /** List of customer interests in products<br/><br/><i>Generated property</i> for <code>C360CustomerProductInterestList.customerProductInterests</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="customerProductInterests", description="List of customer interests in products.")
    private List<C360CustomerProductInterestWsDTO> customerProductInterests;

    public C360CustomerProductInterestList()
    {
        // default constructor
    }

    public void setCustomerProductInterests(final List<C360CustomerProductInterestWsDTO> customerProductInterests)
    {
        this.customerProductInterests = customerProductInterests;
    }

    public List<C360CustomerProductInterestWsDTO> getCustomerProductInterests()
    {
        return customerProductInterests;
    }
}

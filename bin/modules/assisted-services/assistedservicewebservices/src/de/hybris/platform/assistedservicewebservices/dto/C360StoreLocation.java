/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representation of the store location data.
 */
@Schema(name="C360StoreLocation", description="Representation of the store location data.")
public  class C360StoreLocation extends Customer360DataWsDTO

{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360StoreLocation.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360StoreLocation")
    private String type;

    /** Address of the store the current agent is associated with<br/><br/><i>Generated property</i> for <code>C360StoreLocation.address</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="address", description="Address of the store for the current associated agent.", example = "New York United States 10001")
    private String address;

    public C360StoreLocation()
    {
        // default constructor
    }

    public void setAddress(final String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
}

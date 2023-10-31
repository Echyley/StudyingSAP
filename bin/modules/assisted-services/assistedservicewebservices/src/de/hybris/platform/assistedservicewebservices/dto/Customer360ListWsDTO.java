/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * List of the the Customer 360 data
 */
@Schema(
        name="Customer360List",
        description="List of the the Customer 360 data."
)
public  class Customer360ListWsDTO implements Serializable

{

    /** Default serialVersionUID value. */

    private static final long serialVersionUID = 1L;

    /** List of the customer 360 data<br/><br/><i>Generated property</i> for <code>Customer360DataWsDTO.value</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="value", description="List of the Customer 360 data")
    private List<Customer360DataWsDTO> value;

    public Customer360ListWsDTO()
    {
        // default constructor
    }

    public void setValue(final List<Customer360DataWsDTO> value)
    {
        this.value = value;
    }

    public List<Customer360DataWsDTO> getValue()
    {
        return value;
    }
}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representation of the cart and saved cart data.
 */
@Schema(name="C360Cart", description="Representation of the cart data.")
public class C360Cart extends Customer360DataWsDTO
{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360Cart.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360Cart")
    private String type;


    /** The cart of the returned Customer 360 data<br/><br/><i>Generated property</i> for <code>C360Cart.data</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="cart", description="The cart of the returned Customer 360 data.")
    private C360CartDataWsDTO cart;

    public C360Cart()
    {
        // default constructor
    }

    public C360CartDataWsDTO getCart()
    {
        return cart;
    }

    public void setCart(C360CartDataWsDTO cart)
    {
        this.cart = cart;
    }

}

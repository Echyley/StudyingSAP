/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Representation of the cart and saved cart data.
 */
@Schema(name="C360SavedCart", description="Representation of the saved cart data.")
public class C360SavedCart extends Customer360DataWsDTO
{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360Cart.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360SavedCart")
    private String type;

    /** The saved cart of the returned Customer 360 data<br/><br/><i>Generated property</i> for <code>C360Cart.data</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="savedCart", description="The saved cart of the returned Customer 360 data.")
    private C360CartDataWsDTO savedCart;
    public C360SavedCart()
    {
        // default constructor
    }

    public C360CartDataWsDTO getSavedCart()
    {
        return savedCart;
    }

    public void setSavedCart(C360CartDataWsDTO savedCart)
    {
        this.savedCart = savedCart;
    }

}

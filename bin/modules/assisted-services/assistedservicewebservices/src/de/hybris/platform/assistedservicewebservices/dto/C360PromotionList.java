/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Representation of a list of promotions.
 */
@Schema(name="C360PromotionList", description="Representation of a list of promotions.")
public  class C360PromotionList extends Customer360DataWsDTO

{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360CSAPromotionList.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360PromotionList")
    private String type;

    /** List of promotions<br/><br/><i>Generated property</i> for <code>C360PromotionList.promotions</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="promotions", description="List of promotions.")
    private List<C360PromoWsDTO> promotions;

    public C360PromotionList()
    {
        // default constructor
    }

    public List<C360PromoWsDTO> getPromotions()
    {
        return promotions;
    }

    public void setPromotions(List<C360PromoWsDTO> promotions)
    {
        this.promotions = promotions;
    }
}

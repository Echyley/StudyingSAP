/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Representation of a list of activities.
 */
@Schema(name="C360ActivityList", description="Representation of a list of activities.")
public  class C360ActivityList extends Customer360DataWsDTO

{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360ActivityList.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360ActivityList")
    private String type;

    /** List of activities <br/><br/><i>Generated property</i> for <code>C360ActivityList.activities</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="activities", description="List of activities.")
    private List<C360ActivityWsDTO> activities;

    public C360ActivityList()
    {
        // default constructor
    }


    public List<C360ActivityWsDTO> getActivities()
    {
        return activities;
    }

    public void setActivities(List<C360ActivityWsDTO> activities)
    {
        this.activities = activities;
    }
}

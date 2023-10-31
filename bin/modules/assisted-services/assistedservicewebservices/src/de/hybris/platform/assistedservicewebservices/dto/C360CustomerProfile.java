/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;



/**
 * Representation of a list of customer profile.
 */
@Schema(name="C360CustomerProfile", description="Representation of customer profile")
public class C360CustomerProfile extends Customer360DataWsDTO
{
    /** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360CustomerProfile.type</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360CustomerProfile")
    protected String type;


    /** The profile of the returned Customer 360 data.<br/><br/><i>Generated property</i> for <code>C360CustomerProfile.data</code> property defined at extension <code>assistedservicewebservices</code>. */
    @Schema(name="profile", description="The profile of the returned Customer 360 data.")
    protected C360CustomerProfileDataWsDTO profile;

    public C360CustomerProfile()
    {
        // default constructor
    }

    public C360CustomerProfileDataWsDTO getProfile()
    {
        return profile;
    }

    public void setProfile(C360CustomerProfileDataWsDTO profile)
    {
        this.profile = profile;
    }
}


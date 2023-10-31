/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicewebservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name="C360Overview", description="Representation of an overview of the customer's data.")
public class C360Overview extends Customer360DataWsDTO
{
	/** Type of the customer 360 data returned<br/><br/><i>Generated property</i> for <code>C360Overview.type</code> property defined at extension <code>assistedservicewebservices</code>. */
	@Schema(name="type", description="Type of the returned Customer 360 data.", example = "c360Overview")
	private String type;

	/** The overview of the returned Customer 360 overview<br/><br/><i>Generated property</i> for <code>C360Overview.overview</code> property defined at extension <code>assistedservicewebservices</code>. */
	@Schema(name="overview", description="The overview of the returned Customer 360 overview.")
	private C360OverviewDataWsDTO overview;

	public C360Overview() {
		//default constructor
	}

	public C360OverviewDataWsDTO getOverview()
	{
		return overview;
	}

	public void setOverview(C360OverviewDataWsDTO overview)
	{
		this.overview = overview;
	}
}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;


public class ArticleSourceResponse {

	private List<ArticleBySource> items = new ArrayList();


	/**
	 * @return the items
	 */
	@JsonGetter("items")
	public List<ArticleBySource> getItems()
	{
    return items;
  }

	/**
	 * @param items
	 *           the items to set
	 */
	@JsonSetter("items")
	public void setItems(final List<ArticleBySource> items)
	{
    this.items = items;
  }




}


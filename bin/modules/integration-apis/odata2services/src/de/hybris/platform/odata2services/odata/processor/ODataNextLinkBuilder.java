/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.processor;

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;

import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

public class ODataNextLinkBuilder
{
	private static final String SKIPTOKEN = "$skiptoken";
	private static final Pattern REGEX_REPLACE_STEP_1 = Pattern.compile("&?\\" + SKIPTOKEN + "=\\d+");
	private static final Pattern REGEX_REPLACE_STEP_2 = Pattern.compile("&?\\$skip=\\d+");
	private static final Pattern REGEX_REPLACE_STEP_3 = Pattern.compile("\\?&\\$");
	private ItemLookupRequest lookupRequest;
	private String url;
	private Integer totalCount;
	private String currentLink;
	private int skip;
	private int top;

	ODataNextLinkBuilder()
	{
	}

	public static ODataNextLinkBuilder nextLink()
	{
		return new ODataNextLinkBuilder();
	}

	public ODataNextLinkBuilder withLookupRequest(final ItemLookupRequest lookupRequest)
	{
		this.lookupRequest = lookupRequest;
		return this;
	}

	public ODataNextLinkBuilder withTotalCount(final Integer totalCount)
	{
		this.totalCount = totalCount;
		return this;
	}

	public String build()
	{
		assertAllRequiredFieldsAreSet();
		setSkip(lookupRequest.getSkip());
		setTop(lookupRequest.getTop());
		setTotalCount(totalCount);
		setCurrentLink(lookupRequest.getRequestUri().toString());
		return getUrl();
	}

	private String getLink()
	{
		url = prepareLinkForNextSkipToken();
		url += getNewSkipToken();
		return url;
	}

	private String getNewSkipToken()
	{
		return (url.contains("?") ? "&" : "?") + SKIPTOKEN + "=" + newSkipValue();
	}

	private String newSkipValue()
	{
		return String.valueOf(top + skip);
	}

	private boolean isLastPage()
	{
		return totalCount - top <= skip;
	}

	private String prepareLinkForNextSkipToken()
	{
		setCurrentLink(REGEX_REPLACE_STEP_1.matcher(currentLink).replaceFirst(""));
		setCurrentLink(REGEX_REPLACE_STEP_2.matcher(currentLink).replaceFirst(""));
		setCurrentLink(REGEX_REPLACE_STEP_3.matcher(currentLink).replaceFirst("?\\$"));
		return currentLink;
	}

	private String getUrl()
	{
		return !isLastPage() ? getLink() : null;
	}

	private void setCurrentLink(final String currentLink)
	{
		this.currentLink = currentLink;
	}

	private void setTotalCount(final int totalCount)
	{
		this.totalCount = totalCount;
	}

	private void setSkip(final int skip)
	{
		this.skip = skip;
	}

	private void setTop(final int top)
	{
		this.top = top;
	}

	private void assertAllRequiredFieldsAreSet()
	{
		Preconditions.checkArgument(lookupRequest != null, "itemLookupRequest must be provided");
		Preconditions.checkArgument(lookupRequest.getRequestUri() != null, "requestUri must be provided");
		Preconditions.checkArgument(lookupRequest.getSkip() != null, "skip must be provided");
		Preconditions.checkArgument(lookupRequest.getTop() != null, "top must be provided");
		Preconditions.checkArgument(totalCount != null && totalCount >= 0,
				"totalCount must be provided and have a value of 0 or greater");
	}
}

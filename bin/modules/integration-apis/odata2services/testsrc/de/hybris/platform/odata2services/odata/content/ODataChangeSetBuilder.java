/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.content;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;

public class ODataChangeSetBuilder
{
	private static final String CHANGE_SET_BOUNDARY = "changeSet-boundary";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String BOUNDARY = "__BOUNDARY__";
	private static final String PAYLOAD = "__PAYLOAD__";
	private static final String CHANGE_SET_TEMPLATE =
			CONTENT_TYPE + ": multipart/mixed; boundary=" + BOUNDARY + "\n"
					+ "\r\n" + PAYLOAD + "\n"
					+ "--" + BOUNDARY + "--\n";
	private static final String CONTENT_ID = "Content-ID";
	private static final String POST = "POST";
	private static final String APPLICATION_JSON = "application/json";
	private static final String ACCEPT = "Accept";
	private static final String HTTP_1_1 = "HTTP/1.1";

	public static ODataChangeSetBuilder changeSetBuilder()
	{
		return new ODataChangeSetBuilder();
	}

	private final List<String> parts = Lists.newArrayList();
	private String changeSetBoundary = CHANGE_SET_BOUNDARY;
	private String uri = "";

	public ODataChangeSetBuilder withBoundary(final String boundary)
	{
		this.changeSetBoundary = boundary;
		return this;
	}

	public ODataChangeSetBuilder withUri(final String uri)
	{
		this.uri = uri;
		return this;
	}

	public ODataChangeSetBuilder withPart(final Locale locale, final ChangeSetPartContentBuilder builder)
	{
		return withPart(locale, builder.build());
	}

	public ODataChangeSetBuilder withPart(final Locale locale, final String payload)
	{
		return withPart(ODataChangeSetPartBuilder.partBuilder()
		                                         .withContentLanguage(locale)
		                                         .withBody(payload));
	}

	public ODataChangeSetBuilder withPart(final ODataChangeSetPartBuilder builder)
	{
		final String part = builder
				.withPartHeaders(
						CONTENT_ID + ": " + parts.size() + "\n\r\n" +
								POST + " " + uri + " " + HTTP_1_1 + '\n' +
								CONTENT_TYPE + ": " + APPLICATION_JSON + '\n' +
								ACCEPT + ": " + APPLICATION_JSON + '\n'
				)
				.withUri(uri)
				.build();
		parts.add(part);
		return this;
	}

	public String build()
	{
		final String separator = "--" + changeSetBoundary + "\n";
		final String payload = separator + StringUtils.join(parts, separator);
		return CHANGE_SET_TEMPLATE
				.replace(BOUNDARY, changeSetBoundary)
				.replace(PAYLOAD, payload);
	}
}

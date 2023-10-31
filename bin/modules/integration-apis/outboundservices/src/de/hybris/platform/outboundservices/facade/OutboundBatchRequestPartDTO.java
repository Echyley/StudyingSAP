/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.facade;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.assertj.core.util.Preconditions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

/**
 * DTO that contains the information about an HTTP Request needed for generating a batch HTTP request.
 */
public final class OutboundBatchRequestPartDTO
{
	private final HttpEntity<Map<String, Object>> httpEntity;
	private final String itemType;
	private final HttpMethod requestType;
	private final String changeID;


	private OutboundBatchRequestPartDTO(@NotNull final HttpEntity<Map<String, Object>> httpEntity,
	                                    @NotNull final String itemType,
	                                    @NotNull final HttpMethod requestType,
	                                    @NotNull final String changeID)
	{
		Preconditions.checkArgument(httpEntity != null, "httpEntity cannot be null");
		Preconditions.checkArgument(itemType != null, "itemType cannot be null");
		Preconditions.checkArgument(requestType != null, "requestType cannot be null");
		Preconditions.checkArgument(changeID != null, "changeId cannot be null");
		this.httpEntity = httpEntity;
		this.itemType = itemType;
		this.requestType = requestType;
		this.changeID = changeID;
	}


	/**
	 * Returns the http entity that contains the body and headers of the request
	 *
	 * @return HttpEntity<Map < String, Object>>  containing headers and body
	 */
	public HttpEntity<Map<String, Object>> getHttpEntity()
	{
		return httpEntity;
	}

	/**
	 * Returns the item type that the request is for
	 *
	 * @return String with the item type
	 */
	public String getItemType()
	{
		return itemType;
	}

	/**
	 * Returns the request type
	 *
	 * @return HttpMethod with the request type
	 */
	public HttpMethod getRequestType()
	{
		return requestType;
	}

	/**
	 * Returns the change Id responsible for linking the SyncParameters to the corresponding OutboundItemDTOGroup.
	 *
	 * @return Change Id
	 */
	public String getChangeID()
	{
		return changeID;
	}

	@Override
	public String toString()
	{
		return "OutboundBatchRequestPartDTO{" +
				"httpEntity=" + httpEntity +
				", itemType=" + itemType +
				", requestType=" + requestType +
				", changeId=" + changeID +
				'}';
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final OutboundBatchRequestPartDTO that = (OutboundBatchRequestPartDTO) o;

		return new EqualsBuilder()
				.append(httpEntity, that.httpEntity)
				.append(itemType, that.itemType)
				.append(requestType, that.requestType)
				.append(changeID, that.changeID)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder()
				.append(httpEntity)
				.append(itemType)
				.append(requestType)
				.append(changeID)
				.toHashCode();
	}

	public static final class OutboundBatchRequestPartDTOBuilder
	{
		private HttpEntity<Map<String, Object>> httpEntity;
		private String itemType;
		private HttpMethod requestType;
		private String changeId;

		private OutboundBatchRequestPartDTOBuilder()
		{
		}

		public static OutboundBatchRequestPartDTOBuilder outboundBatchRequestPartDTO()
		{
			return new OutboundBatchRequestPartDTOBuilder();
		}

		public OutboundBatchRequestPartDTOBuilder withHttpEntity(final HttpEntity<Map<String, Object>> httpEntity)
		{
			this.httpEntity = httpEntity;
			return this;
		}

		public OutboundBatchRequestPartDTOBuilder withItemType(final String itemType)
		{
			this.itemType = itemType;
			return this;
		}

		public OutboundBatchRequestPartDTOBuilder withRequestType(final HttpMethod requestType)
		{
			this.requestType = requestType;
			return this;
		}

		public OutboundBatchRequestPartDTOBuilder withChangeId(final String changeId)
		{
			this.changeId = changeId;
			return this;
		}

		public OutboundBatchRequestPartDTO build()
		{
			return new OutboundBatchRequestPartDTO(httpEntity, itemType, requestType, changeId);
		}
	}
}

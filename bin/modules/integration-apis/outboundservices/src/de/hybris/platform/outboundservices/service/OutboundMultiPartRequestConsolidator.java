/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.service;

import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO;

import java.util.List;

/**
 * {@link OutboundMultiPartRequestConsolidator} is used to turn a list of individual requests into the payload of a multipart request.
 */
public interface OutboundMultiPartRequestConsolidator
{
	/**
	 * Consolidate a list of individual requests which is represented by {@link OutboundBatchRequestPartDTO} into the payload of a multipart request.
	 * @param requestDTOs represent a list of requests with all the information in {@link OutboundBatchRequestPartDTO}.
	 * @param boundary used to separate individual requests in the consolidated payload.
	 * @return the consolidated payload.
	 */
	String consolidate(List<OutboundBatchRequestPartDTO> requestDTOs, String boundary);
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.service;

import de.hybris.platform.outboundservices.facade.OutboundBatchRequestPartDTO;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;

/**
 * Generates a multipart request.
 */
public interface MultiPartRequestGenerator
{

	/**
	 * Generates a multi-part http request from a list of {@link OutboundBatchRequestPartDTO}.
	 *
	 * @param requestDTOs List of DTOs that represent individual requests and their model type
	 * @return {@link HttpEntity<String>} with consolidated request payload and necessary headers
	 */
	HttpEntity<String> generate(@NotNull List<OutboundBatchRequestPartDTO> requestDTOs);
}

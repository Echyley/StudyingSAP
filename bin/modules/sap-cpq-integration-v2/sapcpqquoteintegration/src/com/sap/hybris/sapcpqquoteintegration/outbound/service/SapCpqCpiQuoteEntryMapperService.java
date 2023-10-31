/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.service;

import com.sap.hybris.sapcpqquoteintegration.model.SAPCPQOutboundQuoteItemModel;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

/**
 * Provides mapping from {@link AbstractOrderEntryModel} of Quote to {@link SAPCPQOutboundQuoteItemModel}.
 *
 * @param <SOURCE> the parameter of the interface
 * @param <TARGET> the parameter of the interface
 */
public interface SapCpqCpiQuoteEntryMapperService<SOURCE extends AbstractOrderEntryModel, TARGET extends SAPCPQOutboundQuoteItemModel> {

	/**
	 * Performs mapping from source to target.
	 *
	 * @param source
	 *           Quote Entry Model
	 * @param target
	 *           SAP CPI Outbound Quote Entry Model
	 */
	void map(SOURCE source, TARGET target);
	
}
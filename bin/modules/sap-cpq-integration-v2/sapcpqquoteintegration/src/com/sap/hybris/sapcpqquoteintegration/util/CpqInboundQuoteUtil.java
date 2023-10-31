/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import com.meterware.httpunit.UnsupportedActionException;
import com.sap.hybris.sapcpqquoteintegration.service.SapCpqQuoteService;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.core.model.order.QuoteModel;

/**
 * The CpqInboundQuoteUtil is an Utility class which is used to createNewQuote
 *
 */
public final class CpqInboundQuoteUtil {

	private CpqInboundQuoteUtil() {
		throw new UnsupportedActionException("This is an utility class and cannot be instantiated");
	}

	/**
	 * The createNewQuote method is called when the given quote does not exist in
	 * commerce. It sets the quoteId same as the CPQ quoteId
	 * 
	 * @param inboundQuote
	 * @param sapCpqQuoteService
	 * @param baseStoreService
	 * @param baseSiteService
	 */
	public static void createNewQuote(QuoteModel inboundQuote, SapCpqQuoteService sapCpqQuoteService,
			BaseStoreService baseStoreService, BaseSiteService baseSiteService) {
		inboundQuote.setCode(inboundQuote.getCpqQuoteNumber());
		inboundQuote.setState(QuoteState.BUYER_OFFER);
		inboundQuote.setDate(convertToDateViaInstant(LocalDateTime.now()));
		String baseStoreUid = sapCpqQuoteService.getSiteAndStoreFromSalesArea(inboundQuote.getCpqSalesOrganization(),
				inboundQuote.getCpqDistributionChannel(), inboundQuote.getCpqDivision());
		inboundQuote.setStore(baseStoreService.getBaseStoreForUid(baseStoreUid));
		inboundQuote.setSite(baseSiteService.getBaseSiteForUID(baseStoreUid));
		inboundQuote.setGuid(UUID.randomUUID().toString());
		inboundQuote.setVersion(1);
	}

	/**
	 * The convertToDateViaInstant returns the Date value which is converted from
	 * the LocalDateTime, as per Java8
	 * 
	 * @param dateToConvert
	 * @return
	 */
	private static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
		return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
	}

}

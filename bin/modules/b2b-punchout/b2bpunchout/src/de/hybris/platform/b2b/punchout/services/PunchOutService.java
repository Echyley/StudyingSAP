/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import org.cxml.CXML;
import org.cxml.OrderRequest;
import org.cxml.ProfileResponse;
import org.cxml.PunchOutOrderMessage;


/**
 * Provides services for Punch Out
 *
 */
public interface PunchOutService
{
	/**
	 * Processes a new PunchOut setup request.
	 *
	 * @param request
	 *           the request
	 * @return the {@link CXML} response
	 */
	CXML processPunchOutSetUpRequest(final CXML request);

	/**
	 * Creates the cmxl message for a {@link PunchOutOrderMessage} to send an Order, using the session cart.
	 *
	 * @return the {@link CXML} object representing an OrderMessage filled with information from the session cart.
	 */
	CXML processPunchOutOrderMessage();


	/**
	 * Processes a purchase order ({@link OrderRequest}) by populating a cart and handles the response in the form of
	 * {@link CXML}.
	 *
	 * @param requestBody
	 *           the request {@link CXML} object
	 * @return the {@link CXML} response
	 */
	CXML processPurchaseOrderRequest(CXML requestBody);

	/**
	 * Retrieve the user id of the originator of the cXML request.<br>
	 * <i>f.y.i.: as there may be multiple credentials declared, the first valid one will be used.</i>
	 *
	 * @param request
	 *           the cXML request.
	 * @return the identity in the "From" tag.
	 */
	String retrieveIdentity(final CXML request);

	/**
	 * Processes a profile request by returned a ProfileResponse with populated supported transactions.
	 *
	 * @param request
	 *           the profile request
	 * @return the resulting {@link CXML} instance with a {@link ProfileResponse} part of it
	 */
	CXML processProfileRequest(CXML request);

	/**
	 * Creates the cancel message for Ariba.
	 *
	 * @return the {@link CXML} with the cancel message.
	 */
	CXML processCancelPunchOutOrderMessage();
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.PunchOutSessionNotFoundException;
import de.hybris.platform.b2b.punchout.model.StoredPunchOutSessionModel;

import org.cxml.CXML;
import org.springframework.security.oauth2.common.OAuth2AccessToken;


/**
 * This service handles the basic operations on {@link PunchOutService} instances.
 */
public interface PunchOutSessionService
{
	/**
	 * Saves current {@link StoredPunchOutSessionModel} in the db.
	 */
	public void saveCurrentPunchoutSession();

	/**
	 * Loads a given {@link StoredPunchOutSessionModel} by sid.
	 *
	 * @param punchOutSessionId The sid to search.
	 * @return The {@link StoredPunchOutSessionModel}, or null if stored session does not exists.
	 */
	StoredPunchOutSessionModel loadStoredPunchOutSessionModel(final String punchOutSessionId);

	/**
	 * Activates a {@link PunchOutSession} for the current user session.
	 *
	 * @param punchOutSession the new punchOut session
	 */
	void activate(PunchOutSession punchOutSession);

	/**
	 * Loads and activates a {@link PunchOutSession} by its ID.
	 *
	 * @param punchOutSessionId the punchOut session ID
	 * @return the newly loaded session
	 * @throws PunchOutSessionNotFoundException when the session is not found
	 */
	PunchOutSession loadPunchOutSession(String punchOutSessionId);

	/**
	 * Check whether punchoutSession is expired and activate;
	 *
	 * @param punchoutSession the punchoutSession to check
	 */
	void checkAndActivatePunchOutSession(PunchOutSession punchoutSession);

	/**
	 * Check whether punchoutSession is expired
	 * @param punchoutSession the punchOut session
	 * @return true punchoutSession is expired, false otherwise
	 */
	boolean isPunchOutSessionExpired(PunchOutSession punchoutSession);

	/**
	 * Set the cart for the current session using the cart saved in a given punch out session. This is necessary as the
	 * punchOut provider may use different sessions for sequential calls (e.g.: edit setup request and edit seamless
	 * login).<br>
	 * <i>Notice that this should only be called after punch out user is authenticated.</i>
	 *
	 * @param punchOutSessionId The punch out session ID.
	 */
	void setCurrentCartFromPunchOutSetup(final String punchOutSessionId);

	/**
	 * Retrieves the currently loaded {@link PunchOutSession}.
	 *
	 * @return the punchOut session or <code>null</code> if none has been loaded yet
	 */
	PunchOutSession getCurrentPunchOutSession();


	/**
	 * Set if PunchOut Session Cart is valid
	 *
	 * @param isValid
	 */
	public void setPunchOutSessionCartIsValid(Boolean isValid);


	/**
	 * Get if PunchOut Session Cart is valid
	 *
	 * @return punchOut session cart is validated ok or not, by default it is null
	 */
	public Boolean isPunchOutSessionCartValid();

	/**
	 * Gets the currently active punchOut session.
	 *
	 * @return the active punchOut session ID
	 */
	String getCurrentPunchOutSessionId();

	/**
	 * initiates and activates a punchout session
	 *
	 * @param request the cXML request
	 */
	void initAndActivatePunchOutSession(CXML request);

	/**
	 * Retrieves the userId from given Punchout Session.
	 *
	 * @param punchoutSession The session of the current punchout user.
	 * @return The userId, if the information contained in the encryptedText passes the security verification (null
	 * @throws PunchOutException If some of the required arguments are missing or empty.
	 */
	String retrieveUserId(final PunchOutSession punchoutSession) throws PunchOutException;

	/**
	 * Retrieves the B2BCustomerModel from given Punchout Session.
	 *
	 * @param punchoutSession The session of the current punchout user.
	 * @return B2BCustomerModel by Punchout Session
	 * @throws PunchOutException If some of the required arguments are missing or empty.
	 */
	B2BCustomerModel loadB2BCustomerModel(final PunchOutSession punchoutSession) throws PunchOutException;

	/**
	 * Get punchOut user token
	 * @param uid the uid of the customer
	 * @param sid the punchOut session ID
	 * @return the oauth token
	 */
	OAuth2AccessToken getPunchOutTokenByUidAndSid(final String uid, final String sid);
}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.login;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaservices.data.GigyaUserObject;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;


/**
 * Service to carry out Gigya login related activity
 */
public interface GigyaLoginService
{

	/**
	 * Method to verify gigya call
	 *
	 * @param gigyaConfigModel
	 *           the gigyaConfigModel
	 * @param uid
	 *           the uid
	 * @param uidSignature
	 *           the uid signature
	 * @param signatureTimeStamp
	 *           the signature timestamp
	 * @return boolean - true if successfully verified
	 */
	boolean verifyGigyaCall(GigyaConfigModel gigyaConfigModel, String uid, String uidSignature, String signatureTimeStamp);

	/**
	 * Find customer using gigya UID
	 *
	 * @param uid
	 *           the uid
	 * @return UserModel the user model
	 */
	UserModel findCustomerByGigyaUid(String uid);

	/**
	 * Notify gigya about logout information
	 *
	 * @param gigyaConfig
	 *           The gigyaConfig model
	 * @param uid
	 *           - unique identifier
	 */
	void notifyGigyaOfLogout(GigyaConfigModel gigyaConfig, String uid);

	/**
	 * Fetch user information from gigya
	 *
	 * @param gigyaConfig
	 *           the gigyaConfig model
	 * @param uid
	 *           the uid
	 * @return GigyaUserObject the gigya user object
	 */
	GigyaUserObject fetchGigyaInfo(GigyaConfigModel gigyaConfig, final String uid);

	/**
	 * Sends gigya user info in commerce to gigya
	 *
	 * @param userModel
	 *           The gigya user
	 * @return boolean - true if successfully sent to gigya
	 */
	boolean sendUserToGigya(UserModel userModel);
	
	/**
	 * Method to verify gigya call with Id Token
	 * 
	 * @param gigyaConfig
	 * 			The gigyaConfig model
	 * @param idToken
	 * 			idToken returned by onLogin event
	 * @return boolean - true if successfully verified
	 * 
	 * @deprecated (use verifyGigyaCallIdTokenExpiryAndSignature instead)
	 */
	@Deprecated(since = "2211", forRemoval = true)
	boolean verifyGigyaCallWithIdToken(GigyaConfigModel gigyaConfig, String idToken);
	
	
	/**
	 * Method to verify gigya call with Id Token
	 * 
	 * @param gigyaConfig
	 * 			The gigyaConfig model
	 * @param idToken
	 * 			idToken returned by onLogin event
	 * @return boolean - true if successfully verified
	 */
	boolean verifyGigyaCallIdTokenExpiryAndSignature(GigyaConfigModel gigyaConfig, String idToken);
	
	/**
	 * Method to validate if the Id token in the gigya call contains the uid parameter
	 * @param idToken
	 * 			JWT Token sent by CDC
	 * @param uid
	 * 			User ID of the CDC user
	 * @return boolean
	 */
	boolean verifyGigyaIdTokenContainsUID(String idToken, String uid);
	
	
	/**
	 * Method to validate if the Id token in the gigya call contains the payload hash
	 * @param idToken
	 * 			JWT Token sent by CDC
	 * @param payloadHash
	 * 			The SHA Hash of the CDC Webhook payload
	 * @return boolean
	 */
	boolean verifyGigyaIdTokenContainsPayloadHash(String idToken, String payloadHash);

}

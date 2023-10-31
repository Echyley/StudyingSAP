/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.keymanagement;

import java.security.PublicKey;

import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;

/**
 * 
 * Service to invoke key management service using gigya's java classes
 */
public interface GigyaKeyService {
	
	/**
	 * Method to retrieve public key of a CMSSite
	 * 
	 * @param gigyaConfig
	 *           The Gigya Config model
	 * 
	 * @param checkKeyId
	 * 			Key ID from ID Token
	 * 
	 * @return PublickKey of Site
	 */
	PublicKey retreivePublicKeyOfASite(GigyaConfigModel gigyaConfig, String checkKeyId);
	
	/**
	 * Method to generate public key from modulus and exponent
	 * 
	 * @param modulus 
	 * 			Modulus of Public Key
	 * 
	 * @param exponent
	 * 			Exponent of Public Key
	 * 
	 * @return PublicKey 
	 */
	PublicKey generatePublicKey(String modulus, String exponent);
	
	/**
	 * Method to validate idToken of Gigya
	 * 
	 * @param gigyaConfig
	 *           The Gigya Config model
	 *           
	 * @param idToken 
	 * 			ID Token sent by Gigya
	 * 
	 * @return Boolean 
	 * 
	 * @deprecated (use validateTokenExpiryAndSignature instead)
	 */
	@Deprecated(since = "2211", forRemoval = true)
	Boolean validateToken(GigyaConfigModel gigyaConfig, String idToken);
	
	/**
	 * Method to validate idToken expiry and idToken signature of Gigya
	 * 
	 * @param gigyaConfig
	 *           The Gigya Config model
	 *           
	 * @param idToken 
	 * 			ID Token sent by Gigya
	 * 
	 * @return Boolean 
	 */
	Boolean validateTokenExpiryAndSignature(GigyaConfigModel gigyaConfig, String idToken);
	
	/**
	 * Method to validate if JWT token contains the UID
	 * 
	 * @param jwtToken
	 * 			JWT Token sent by CDC
	 * @param uid
	 * 			User ID of the CDC user
	 * @return Boolean
	 */
	Boolean validateTokenUID(String jwtToken, String uid);
	
	/**
	 * Method to validate if JWT token contains the Payload Hash
	 * 
	 * @param jwtToken
	 * 			JWT Token sent by CDC
	 * @param payloadHash
	 * 			The SHA Hash of the CDC Webhook payload
	 * @return Boolean
	 */
	Boolean validateTokenPayloadHash(String jwtToken, String payloadHash);
	
}

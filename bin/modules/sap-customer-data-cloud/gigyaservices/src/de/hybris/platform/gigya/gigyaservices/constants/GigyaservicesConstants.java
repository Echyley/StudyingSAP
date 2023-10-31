/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.constants;

import java.util.Set;

import com.google.common.collect.Sets;

import de.hybris.platform.util.Config;

/**
 * Global class for all Gigyaservices constants. You can add global constants
 * for your extension into this class.
 */
public final class GigyaservicesConstants extends GeneratedGigyaservicesConstants
{
	public static final String EXTENSIONNAME = "gigyaservices";

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "gigyaservicesPlatformLogo";

	protected static final Set<String> EXTRA_FIELDS = Sets.newHashSet("languages", "address", "phones", "education", "honors",
	                "publications", "patents", "certifications", "professionalHeadline", "bio", "industry", "specialties", "work", "skills",
	                "religion", "politicalView", "interestedIn", "relationshipStatus", "hometown", "favorites", "followersCount",
	                "followingCount", "username", "locale", "verified", "timezone", "likes", "samlData");

	public static final int MAX_RETRIES = Config.getInt("gigyaservices.max.retries", 2);

	public static final int TRY_NUM = Config.getInt("gigyaservices.initial.retry.num", 1);

	public static final String STATUS_CODE = "statusCode";

	public static final String ERROR_CODE = "errorCode";

	public static final String GIGYA_PROFILE = "profile";

	public static final String UTF8 = "UTF-8";

	public static final String SHA256_RSA = "SHA256withRSA";

	public static final String RSA = "RSA";

	public static final String N = "n";
	public static final String E = "e";
	public static final String KID = "kid";

	private GigyaservicesConstants()
	{
		// empty to avoid instantiating this constant class
	}
}

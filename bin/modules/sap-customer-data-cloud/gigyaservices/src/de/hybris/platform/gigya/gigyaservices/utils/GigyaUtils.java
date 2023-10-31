/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.util.Config;

/**
 * Utility class for CDC integration functionality 
 */
public final class GigyaUtils {
	
	private GigyaUtils() {
		// Suppress creation of objects of utility class
	}
	
	/**
	 * Method to populate environment details 
	 * 
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> getEnvironmentDetails() {
		String buildVersion = Config.getString("build.version", StringUtils.EMPTY);
		Map<String, Object> environment = new HashMap<>();
		environment.put("cms_name", "SAP Commerce");
		environment.put("cms_version", buildVersion);
		environment.put("cms_major_version", "SAP Commerce " + buildVersion);
		environment.put("gigya_version", buildVersion);
		environment.put("lang_version", Config.getString("java.version", StringUtils.EMPTY));
		return environment;
	}

}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

import java.util.Map;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


/**
 * Holds SSC sessions
 */
public interface ConfigurationSessionContainer
{

	/**
	 * @return Session map
	 */
	Map<String, IConfigSession> getSessionMap();

	/**
	 * @param sessionId
	 */
	void releaseSession(String sessionId);

	/**
	 * @param qualifiedId
	 * @return SSC configuration session
	 */
	IConfigSession retrieveConfigSession(String qualifiedId);

	/**
	 * @param qualifiedId
	 * @param configSession
	 */
	void storeConfiguration(String qualifiedId, IConfigSession configSession);

}

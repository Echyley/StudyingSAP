/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices;

/**
 * Allows to access availability of the back end
 */
public interface BackendAvailabilityService
{

	/**
	 * @return Does back end have a downtime?
	 */
	boolean isBackendDown();



}

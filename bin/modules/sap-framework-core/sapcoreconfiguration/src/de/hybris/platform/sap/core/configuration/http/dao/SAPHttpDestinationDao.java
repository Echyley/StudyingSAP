/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.http.dao;

import java.util.List;

import de.hybris.platform.sap.core.configuration.model.SAPHTTPDestinationModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

/**
 * Data access object for SAP HTTP Destination.
 */
public interface SAPHttpDestinationDao extends Dao {

    /**
     * Return a list of Http destination models that are currently persisted. If
     * none are found an empty list is returned.
     * 
     * @return all Http destinations of system
     */
    List<SAPHTTPDestinationModel> findHttpDestinations();

    /**
     * Finds all Http destinatio with given name. If none is found, an empty
     * list will be returned.
     * 
     * @param destinationName the name to search for HTTP destinations
     * @return All HTTP destinations with the given name.
     */
    List<SAPHTTPDestinationModel> findHttpDestinationsByName(String destinationName);

}

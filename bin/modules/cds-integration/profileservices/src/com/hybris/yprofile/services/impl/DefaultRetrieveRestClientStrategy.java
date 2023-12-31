/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.services.impl;

import com.hybris.yprofile.exceptions.ProfileException;
import com.hybris.yprofile.rest.clients.ConsentServiceClient;
import com.hybris.yprofile.rest.clients.ProfileClient;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.log4j.Logger;

public class DefaultRetrieveRestClientStrategy implements RetrieveRestClientStrategy {
    private static final Logger LOG = Logger.getLogger(DefaultRetrieveRestClientStrategy.class);

    private ApiRegistryClientService apiRegistryClientService;

    public ProfileClient getProfileRestClient(){
        try {
            return getApiRegistryClientService().lookupClient(ProfileClient.class);
        } catch (CredentialException | ModelNotFoundException e) {
            LOG.warn("Unable to retrieve Profile Rest Client: " + e.getMessage());
            throw new ProfileException("Unable to retrieve Profile Rest Client ", e);
        }
    }

    public ConsentServiceClient getConsentServiceRestClient(){
        try {
            return getApiRegistryClientService().lookupClient(ConsentServiceClient.class);
        } catch (CredentialException | ModelNotFoundException e) {
            LOG.warn("Unable to retrieve Consent Service Rest Client: " + e.getMessage());
            throw new ProfileException("Unable to retrieve Consent Service Rest Client ", e);
        }
    }

    public ApiRegistryClientService getApiRegistryClientService() {
        return apiRegistryClientService;
    }

    public void setApiRegistryClientService(ApiRegistryClientService apiRegistryClientService) {
        this.apiRegistryClientService = apiRegistryClientService;
    }
}

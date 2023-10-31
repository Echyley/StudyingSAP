/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.yprofile.rest.clients;

import com.hybris.charon.RawResponse;
import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.Http;
import com.hybris.charon.annotations.OAuth;
import rx.Observable;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;

@OAuth
@Http
public interface ConsentServiceClient {

    /**
     * delete consent reference for user
     */
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/${tenant}/consents/{consent-reference}")
    @Control(retries = "${retries:3}", retriesInterval = "${retriesInterval:2000}", timeout = "${timeout:4000}")
    Observable<RawResponse> deleteConsentReference(
            @PathParam("consent-reference") String consentReferenceId,
            @HeaderParam("X-B3-Sampled") String tracingEnabled);
}

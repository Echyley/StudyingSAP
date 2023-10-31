/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.api;

import com.sap.retail.sapppspricing.swagger.businessobject.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")@Component("com.sap.retail.sapppspricing.swagger.businessobject.api.CalculationApi")
public class CalculationApi {
    private ApiClient apiClient;

    public CalculationApi() {
        this(new ApiClient());
    }

    @Autowired
    public CalculationApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Calculates a PriceCalculateTransaction
     * Determines regular prices by applying promotional rules for the provided list of items.
     * <p><b>200</b> - Price calculation was successful.
     * <p><b>400</b> - The price could not be calculated. For more information, see the \&quot;Response\&quot; element of the \&quot;ARTSHeader\&quot; in the returned response. 
     * <p><b>0</b> - This error is not related to the Calculation service.
     * @param tenantName The name of the subdomain in which the service instance/subscription is created. **For the sandbox environment, enter &#x27;oppsapihub&#x27;.** (required)
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void calculateViaRestWithTenant(String tenantName) throws RestClientException {
        calculateViaRestWithTenantWithHttpInfo(tenantName);
    }

    /**
     * Calculates a PriceCalculateTransaction
     * Determines regular prices by applying promotional rules for the provided list of items.
     * <p><b>200</b> - Price calculation was successful.
     * <p><b>400</b> - The price could not be calculated. For more information, see the \&quot;Response\&quot; element of the \&quot;ARTSHeader\&quot; in the returned response. 
     * <p><b>0</b> - This error is not related to the Calculation service.
     * @param tenantName The name of the subdomain in which the service instance/subscription is created. **For the sandbox environment, enter &#x27;oppsapihub&#x27;.** (required)
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> calculateViaRestWithTenantWithHttpInfo(String tenantName) throws RestClientException {
        Object postBody = null;
        // verify the required parameter 'tenantName' is set
        if (tenantName == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'tenantName' when calling calculateViaRestWithTenant");
        }
        // create path and map variables
        final Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("tenantName", tenantName);
        String path = UriComponentsBuilder.fromPath("/restapi/{tenantName}").buildAndExpand(uriVariables).toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();

        final String[] accepts = {  };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = {  };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Void> returnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI(path, HttpMethod.POST, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}

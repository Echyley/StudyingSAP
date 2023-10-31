/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service.impl;

import com.hybris.charon.Charon;
import de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentClientModel;
import de.hybris.platform.cissapdigitalpayment.model.SAPDigitalPaymentConfigurationModel;
import de.hybris.platform.cissapdigitalpayment.service.DigitalPaymentsConfigurationService;
import org.apache.log4j.Logger;

import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.cissapdigitalpayment.client.SapDigitalPaymentClient;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.apiregistryservices.exceptions.CredentialException;

import java.util.HashMap;
import java.util.Map;

import static de.hybris.platform.cissapdigitalpayment.constants.CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_COMPANY_CODE_KEY;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation of {@link DigitalPaymentsConfigurationService}
 */
public class DefaultDigitalPaymentsConfigurationService implements DigitalPaymentsConfigurationService {

    private static final Logger LOG = Logger.getLogger(DefaultDigitalPaymentsConfigurationService.class);
    private ApiRegistryClientService apiRegistryClientService;

    @Override
    public SapDigitalPaymentClient getSapDigitalPaymentsClient() {
        try
        {
            return getApiRegistryClientService().lookupClient(SapDigitalPaymentClient.class);
        }
        catch (final CredentialException e)
        {
            LOG.error("Error occured while fetching SapRevenueCloudSubscriptionClient Configuration");
            throw new SystemException(e);
        }
    }

    @Override
    public SapDigitalPaymentClient getSapDigitalPaymentsClient(SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig) {
        return Charon.from(SapDigitalPaymentClient.class)
                .config(createDigitalPaymentConfigurationMap(sapDigitalPaymentConfig))
                .build();
    }

    private Map<String, String> createDigitalPaymentConfigurationMap(final SAPDigitalPaymentConfigurationModel sapDigitalPaymentConfig)
    {
        final Map<String, String> sapDigitalPaymentConfigMap = new HashMap<>();
        try
        {
            validateParameterNotNull(sapDigitalPaymentConfig, "Sap Digital payment configuration cannot be null");

            final SAPDigitalPaymentClientModel sapDpClientModel = sapDigitalPaymentConfig.getSapDigitalpaymentClient();
            if (null != sapDpClientModel)
            {
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_ID_KEY,
                        sapDpClientModel.getClientId());
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_CLIENT_SECRET_KEY,
                        sapDpClientModel.getClientSecret());
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_GRANT_TYPE_KEY,
                        org.apache.commons.lang3.StringUtils.join(sapDpClientModel.getAuthorizedGrantTypes(), ","));
                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_SCOPE,
                        org.apache.commons.lang3.StringUtils.join(sapDpClientModel.getScope(), ","));

                sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_OAUTH_URL_KEY,
                        sapDpClientModel.getTokenUrl());
            }


            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_URL_KEY,
                    sapDigitalPaymentConfig.getBaseUrl());

            sapDigitalPaymentConfigMap.put(SAP_DIGITAL_PAYMENT_COMPANY_CODE_KEY,
                    sapDigitalPaymentConfig.getCompanyCode());
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_CUSTOMER_COUNTRY_KEY,
                    sapDigitalPaymentConfig.getCustomerCountry());
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_PAYMENT_METHOD_KEY,
                    sapDigitalPaymentConfig.getPaymentMethod());
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_CUSTOM_PARAM_KEY,
                    sapDigitalPaymentConfig.getCustomParam());

            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_RETRIES_KEY,
                    String.valueOf(sapDigitalPaymentConfig.getMaxRetry()));
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_RETRIES_INTERVAL_KEY,
                    String.valueOf(sapDigitalPaymentConfig.getRetryInterval()));
            sapDigitalPaymentConfigMap.put(CisSapDigitalPaymentConstant.SAP_DIGITAL_PAYMENT_TIMEOUT_KEY,
                    String.valueOf(sapDigitalPaymentConfig.getTimeOut()));
        }
        catch (final IllegalArgumentException | SystemException e)
        {
            if (LOG.isDebugEnabled())
            {
                LOG.debug(e);
            }
            LOG.error("Error while reading the SAP Digital payment configurations. Configuration details might be missing"
                    + e.getMessage());
        }
        return sapDigitalPaymentConfigMap;

    }

    public ApiRegistryClientService getApiRegistryClientService() {
        return apiRegistryClientService;
    }

    public void setApiRegistryClientService(ApiRegistryClientService apiRegistryClientService) {
        this.apiRegistryClientService = apiRegistryClientService;
    }
}

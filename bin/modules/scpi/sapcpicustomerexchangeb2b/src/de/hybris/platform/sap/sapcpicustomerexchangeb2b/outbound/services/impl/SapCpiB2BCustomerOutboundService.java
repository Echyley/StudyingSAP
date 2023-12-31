/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.impl;

import com.sap.hybris.sapcustomerb2b.outbound.B2BCustomerExportService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundB2BCustomerModel;
import de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService;
import de.hybris.platform.sap.sapcpicustomerexchangeb2b.outbound.services.SapCpiB2BCustomerConversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static de.hybris.platform.sap.sapcpiadapter.service.SapCpiOutboundService.*;

/**
 * Class to enable sending outbound B2B Customer to SCPI.
 */
public class SapCpiB2BCustomerOutboundService extends B2BCustomerExportService {

  private static final Logger LOG = LoggerFactory.getLogger(SapCpiB2BCustomerOutboundService.class);

  private SapCpiB2BCustomerConversionService sapCpiB2BCustomerConversionService;
  private SapCpiOutboundService sapCpiOutboundService;

  /**
   * Prepare and send outbound B2B Customer to SCPI.
   *
   * @param b2bCustomerModel B2BCustomerModel
   * @param language         String
   */
  @Override
  public void prepareAndSend(B2BCustomerModel b2bCustomerModel, String language) {

    if (Objects.isNull(b2bCustomerModel.getDefaultB2BUnit())) {

      LOG.error("B2B customer [{}] cannot be replicated to SCPI because it is missing the default B2B unit!", b2bCustomerModel.getUid());
      return;

    }

    sendB2BCustomerToSCPI(getSapCpiB2BCustomerConversionService().convertB2BCustomerToSapCpiBb2BCustomer(b2bCustomerModel, language));

  }

  protected void sendB2BCustomerToSCPI(SAPCpiOutboundB2BCustomerModel sapCpiOutboundB2BCustomer) {

    getSapCpiOutboundService().sendB2BCustomer(sapCpiOutboundB2BCustomer).subscribe(

            // onNext
            responseEntityMap -> {

              if (isSentSuccessfully(responseEntityMap)) {

                LOG.info("B2B customer [{}] has been sent to the SAP backend through SCPI! {}",
                        sapCpiOutboundB2BCustomer.getUid(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE));

              } else {

                LOG.error("B2B customer [{}] has not been sent to the SAP backend! {}",
                        sapCpiOutboundB2BCustomer.getUid(), getPropertyValue(responseEntityMap, RESPONSE_MESSAGE));

              }

            }

            // onError
            , error -> LOG.error("B2B customer [{}] has not been sent to the SAP backend through SCPI! {}",
                    sapCpiOutboundB2BCustomer.getUid(), error.getMessage(), error)

    );

  }


  protected SapCpiB2BCustomerConversionService getSapCpiB2BCustomerConversionService() {
    return sapCpiB2BCustomerConversionService;
  }


  public void setSapCpiB2BCustomerConversionService(SapCpiB2BCustomerConversionService sapCpiB2BCustomerConversionService) {
    this.sapCpiB2BCustomerConversionService = sapCpiB2BCustomerConversionService;
  }

  protected SapCpiOutboundService getSapCpiOutboundService() {
    return sapCpiOutboundService;
  }


  public void setSapCpiOutboundService(SapCpiOutboundService sapCpiOutboundService) {
    this.sapCpiOutboundService = sapCpiOutboundService;
  }
}

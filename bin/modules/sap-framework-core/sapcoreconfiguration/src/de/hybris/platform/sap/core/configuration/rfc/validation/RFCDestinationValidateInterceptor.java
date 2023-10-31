/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.rfc.validation;

import de.hybris.platform.sap.core.configuration.model.SAPRFCDestinationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.util.localization.Localization;
import org.apache.commons.lang3.StringUtils;


/**
 * Perform validation of SAPRFCDestination model data.
 * 
 */
public class RFCDestinationValidateInterceptor implements ValidateInterceptor<SAPRFCDestinationModel> {


	@Override
	public void onValidate(final SAPRFCDestinationModel sapRFCDestinationModel, final InterceptorContext ctx)
			throws InterceptorException {
		// If SNC is selected the SNCPartnerName and the quality of protections
		// (QoP) must not be empty
		if (sapRFCDestinationModel.getSncMode()
				&& (StringUtils.isEmpty(sapRFCDestinationModel.getSncPartnerName())
				|| StringUtils.isEmpty(sapRFCDestinationModel.getSncQoP().toString()))) {
			throw new InterceptorException(Localization.getLocalizedString("validation.RFCDestination.IncompleteSncData"));
		}

		checkForServerOrGroupConnection(sapRFCDestinationModel);

		// check required fields if connection pooling is enabled
		if (sapRFCDestinationModel.getPooledConnectionMode() != null && sapRFCDestinationModel.getPooledConnectionMode()) {
			validateConnectionDetails(sapRFCDestinationModel);
		}
	}

	private void validateConnectionDetails(SAPRFCDestinationModel sapRFCDestinationModel) throws InterceptorException {
		if (StringUtils.isEmpty(sapRFCDestinationModel.getPoolSize())
				|| StringUtils.isEmpty(sapRFCDestinationModel.getMaxConnections())
				|| StringUtils.isEmpty(sapRFCDestinationModel.getMaxWaitTime())) {
			throw new InterceptorException(Localization.getLocalizedString("validation.RFCDestination.IncompletePoolingData"));
		}
	}

	private void checkForServerOrGroupConnection(SAPRFCDestinationModel sapRFCDestinationModel) throws InterceptorException {
		// check required fields in case of server connection
		if (sapRFCDestinationModel.getConnectionType() != null && sapRFCDestinationModel.getConnectionType()) {
			// Target host and Instance number are required
			if (StringUtils.isEmpty(sapRFCDestinationModel.getTargetHost())
					|| StringUtils.isEmpty(sapRFCDestinationModel.getInstance())) {
				throw new InterceptorException(
						Localization.getLocalizedString("validation.RFCDestination.IncompleteServerConnectionData"));
			}
		}
		// check required fields for group connection
		else {
			if (StringUtils.isEmpty(sapRFCDestinationModel.getMessageServer())
					|| StringUtils.isEmpty(sapRFCDestinationModel.getGroup())
					|| StringUtils.isEmpty(sapRFCDestinationModel.getSid())) {
				throw new InterceptorException(
						Localization.getLocalizedString("validation.RFCDestination.IncompleteGroupConnectionData"));
			}
		}
	}
}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.interceptor;

import java.util.ArrayList;
import java.util.Arrays;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

import de.hybris.platform.integrationservices.interceptor.IntegrationObjectItemAttributeValidateInterceptor;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * C4cIntegrationObjectItemAttributeValidateInterceptor to skip CPQ and c4c validation
 */
public class C4cIntegrationObjectItemAttributeValidateInterceptor
		extends IntegrationObjectItemAttributeValidateInterceptor {
	private final DescriptorFactory descriptorFactory;

	/**
	 * Instantiate the {@link IntegrationObjectItemAttributeValidateInterceptor}
	 *
	 * @param descriptorFactory the descriptor factory to create type descriptor
	 */
	public C4cIntegrationObjectItemAttributeValidateInterceptor(@NotNull final DescriptorFactory descriptorFactory) {
		super(descriptorFactory);
		Preconditions.checkArgument(descriptorFactory != null, "descriptorFactory cannot be null");
		this.descriptorFactory = descriptorFactory;
	}

	@Override
	public void onValidate(final IntegrationObjectItemAttributeModel attributeModel,
			final InterceptorContext interceptorContext) throws InterceptorException {
		final TypeAttributeDescriptor attributeDescriptor = descriptorFactory
				.createTypeAttributeDescriptor(attributeModel);
		final String typeCode = attributeDescriptor.getTypeDescriptor().getTypeCode();
		
		ArrayList<String> itemType = new ArrayList<>(Arrays.asList("SAPC4CCpiOutboundItem", "SAPC4CCpiOutboundQuote","C4CSalesOrderNotification","SAPC4CComment","SAPCPQOutboundQuote","SAPCPQOutboundQuoteItem","SAPCPQOutboundQuoteCustomer","SAPCPQOutboundQuoteComment","SAPCPQOutboundQuoteStatus"));
		if (itemType.contains(typeCode)) {
			return;
		} else {
			super.onValidate(attributeModel, interceptorContext);
		}
	}
}

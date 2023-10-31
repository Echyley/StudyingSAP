/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;

import com.google.common.base.Preconditions;

/**
 * An interceptor that validates the {@link IntegrationObjectItemAttributeModel}'s attribute cannot be autoCreate or partOf
 * which references an abstract type in the integration object
 * and validates the {@link IntegrationObjectItemAttributeModel}'s attribute cannot be unique which is either Collection or Map
 */
public class IntegrationObjectItemAttributeValidateInterceptor implements ValidateInterceptor<IntegrationObjectItemAttributeModel>
{
	private static final Logger LOG = Log.getLogger(IntegrationObjectItemAttributeValidateInterceptor.class);
	private static final String ABSTRACT_AUTO_CREATE_MSG = "[{}.{}] attribute in {} integration object references an abstract type to be auto-created. This Integration Object cannot be used for inbound requests.";
	private static final String ERROR_MSG_TEMPLATE2 = "Collection, Map or Localized attribute [%s.%s] cannot be unique.";

	private final DescriptorFactory descriptorFactory;

	/**
	 * Instantiate the {@link IntegrationObjectItemAttributeValidateInterceptor}
	 *
	 * @param descriptorFactory the descriptor factory to create type descriptor
	 */
	public IntegrationObjectItemAttributeValidateInterceptor(@NotNull final DescriptorFactory descriptorFactory)
	{
		Preconditions.checkArgument(descriptorFactory != null, "descriptorFactory cannot be null");
		this.descriptorFactory = descriptorFactory;
	}

	@Override
	public void onValidate(final IntegrationObjectItemAttributeModel attributeModel, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		final TypeAttributeDescriptor attributeDescriptor = descriptorFactory.createTypeAttributeDescriptor(attributeModel);
		final String typeCode = attributeDescriptor.getTypeDescriptor().getTypeCode();
		final String attributeName = attributeDescriptor.getAttributeName();

		if (attributeDescriptor.isAutoCreate() && attributeDescriptor.getAttributeType().isAbstract())
		{
			final String ioCode = attributeDescriptor.getTypeDescriptor().getIntegrationObjectCode();
			LOG.warn(ABSTRACT_AUTO_CREATE_MSG, typeCode, attributeName, ioCode);
		}

		if (attributeDescriptor.isKeyAttribute() && (attributeDescriptor.isCollection() || attributeDescriptor.isMap()))
		{
			throw new InterceptorException(String.format(ERROR_MSG_TEMPLATE2, typeCode, attributeName), this);
		}
	}
}

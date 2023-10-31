/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.integrationkey.impl;

import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_PROP_DIV;
import static de.hybris.platform.integrationservices.constants.IntegrationservicesConstants.INTEGRATION_KEY_TYPE_DIV;

import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyMetadataGenerator;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.KeyAttribute;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * An implementation of {@link IntegrationKeyMetadataGenerator} that generates metadata representation of all the key attributes
 * sorted alphabetically.
 */
public class DefaultAlphabeticalIntegrationKeyMetadataGenerator implements IntegrationKeyMetadataGenerator
{
	private static final String EXCEPTION_MESSAGE = "Cannot generate integration key metadata for null";
	private DescriptorFactory descriptorFactory;

	@Override
	public String generateKeyMetadata(final TypeDescriptor descriptor)
	{
		Preconditions.checkArgument(descriptor != null, EXCEPTION_MESSAGE);

		final StringBuilder stringBuilder = descriptor.getKeyDescriptor()
		                                              .getKeyAttributes()
		                                              .stream()
		                                              .map(this::formatAliasPart)
		                                              .distinct()
		                                              .sorted()
		                                              .map(StringBuilder::new)
		                                              .reduce(new StringBuilder(),
				                                              (a, b) -> a.append(b).append(INTEGRATION_KEY_PROP_DIV));
		return StringUtils.chop(stringBuilder.toString());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated use {@link #generateKeyMetadata(TypeDescriptor)} instead.
	 */
	@Override
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public String generateKeyMetadata(final IntegrationObjectItemModel item)
	{
		Preconditions.checkArgument(item != null, EXCEPTION_MESSAGE);

		return generateKeyMetadata(getDescriptorFactory().createItemTypeDescriptor(item));
	}

	protected String formatAliasPart(final KeyAttribute keyAttribute)
	{
		return String.format("%s%s%s",
				keyAttribute.getItemCode(),
				INTEGRATION_KEY_TYPE_DIV,
				keyAttribute.getName());
	}

	/**
	 * @deprecated {@code descriptorFactory} property will be removed in the future.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public void setDescriptorFactory(final DescriptorFactory factory)
	{
		descriptorFactory = factory;
	}

	/**
	 * @deprecated {@code descriptorFactory} property will be removed in the future.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	private DescriptorFactory getDescriptorFactory()
	{
		return descriptorFactory == null ? ApplicationBeans.getBean("integrationServicesDescriptorFactory",
				DescriptorFactory.class) : descriptorFactory;
	}
}

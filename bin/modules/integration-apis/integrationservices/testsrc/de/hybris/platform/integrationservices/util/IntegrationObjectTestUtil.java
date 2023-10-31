/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder;
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder;
import de.hybris.platform.integrationservices.enums.AuthenticationType;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;

import java.util.Collection;
import java.util.Optional;

/**
 * Integration object test utility class.
 */
public class IntegrationObjectTestUtil
{
	public static IntegrationObjectModel createIntegrationObject(final String integrationObjectCode)
	{
		return IntegrationObjectModelBuilder.integrationObject().withCode(integrationObjectCode).build();
	}

	public static IntegrationObjectItemModel createIntegrationObjectRootItem(final IntegrationObjectModel integrationObject,
	                                                                         final String itemCode)
	{
		return createIntegrationObjectItem(integrationObject, itemCode, true);
	}

	public static IntegrationObjectItemModel createIntegrationObjectItem(final IntegrationObjectModel integrationObject,
	                                                                     final String itemCode, final boolean isRoot)
	{
		return IntegrationObjectItemModelBuilder.integrationObjectItem()
		                                        .withIntegrationObject(integrationObject)
		                                        .withCode(itemCode)
		                                        .withRoot(isRoot)
		                                        .build();
	}

	public static IntegrationObjectItemModel createIntegrationObjectItem(final IntegrationObjectModel integrationObject,
	                                                                     final String itemCode)
	{
		return IntegrationObjectTestUtil.createIntegrationObjectItem(integrationObject, itemCode, false);
	}

	public static IntegrationObjectModel findIntegrationObjectByCode(final String code)
	{
		return IntegrationTestUtil.findAny(IntegrationObjectModel.class,
				integrationObject -> integrationObject.getCode().equals(code)).orElse(null);
	}

	/**
	 * Searches for an Integration Object Class for the provided Integration Object with a matching code.
	 *
	 * @param code              code of the integration object class to search descriptor for
	 * @param integrationObject integration object
	 * @return the class model found or {@code null}
	 */
	public static IntegrationObjectClassModel findIntegrationObjectClassByCodeAndIntegrationObject(final String code,
	                                                                                               final IntegrationObjectModel integrationObject)
	{
		return IntegrationTestUtil.findAny(IntegrationObjectClassModel.class,
				integrationObjectClass -> integrationObjectClass.getCode().equals(code) &&
						integrationObjectClass.getIntegrationObject().equals(integrationObject)).orElse(null);
	}

	/**
	 * Searches for an Integration Object Item for the provided Integration Object with a matching code.
	 *
	 * @param code              code of the integration object item to search descriptor for
	 * @param integrationObject integration object
	 * @return a {@link IntegrationObjectItemModel} or null
	 */
	public static IntegrationObjectItemModel findIntegrationObjectItemByCodeAndIntegrationObject(final String code,
	                                                                                             final IntegrationObjectModel integrationObject)
	{
		return IntegrationTestUtil.findAny(IntegrationObjectItemModel.class,
				integrationObjectItem -> integrationObjectItem.getCode().equals(code) &&
						integrationObjectItem.getIntegrationObject().equals(integrationObject)).orElse(null);
	}

	/**
	 * Searches for a descriptor of the specified integration object.
	 *
	 * @param code code of the integration object to search descriptor for
	 * @return descriptor of the specified integration object or {@code null}, if such object does not exist.
	 */
	public static IntegrationObjectDescriptor findIntegrationObjectDescriptorByCode(final String code)
	{
		final DescriptorFactory factory = IntegrationTestUtil.getService("integrationServicesDescriptorFactory",
				DescriptorFactory.class);

		final Optional<IntegrationObjectModel> model = IntegrationTestUtil.findAny(IntegrationObjectModel.class,
				integrationObject -> integrationObject.getCode().equals(code));

		return model.map(factory::createIntegrationObjectDescriptor).orElse(null);
	}

	public static InboundChannelConfigurationModel createInboundChannelConfigurationModel(
			final IntegrationObjectModel integrationObject, final AuthenticationType authenticationType)
	{
		try
		{
			IntegrationTestUtil.importImpEx(
					"INSERT_UPDATE InboundChannelConfiguration; " + "integrationObject(code)[unique = true]; authenticationType(code)",
					"; " + integrationObject.getCode() + "; " + authenticationType.getCode() + ";");
		}
		catch (final ImpExException ex)
		{
			throw new RuntimeException(ex);
		}

		return findInboundChannelConfigurationObject(integrationObject.getCode());
	}

	public static IntegrationObjectModel createNullIntegrationTypeIntegrationObject(final String integrationObjectCode)
			throws ImpExException
	{
		IntegrationTestUtil.importImpEx("INSERT_UPDATE IntegrationObject; code[unique = true]; integrationType(code)",
				"                               ; " + integrationObjectCode + "; ;");
		return findIntegrationObjectByCode(integrationObjectCode);
	}

	public static InboundChannelConfigurationModel findInboundChannelConfigurationObject(final String iccCode)
	{

		return IntegrationTestUtil.findAny(InboundChannelConfigurationModel.class,
				icc -> Optional.ofNullable(icc.getIntegrationObject())
				               .map(integrationObject -> integrationObject.getCode().equals(iccCode))
				               .orElse(false)).orElse(null);

	}

	public static Collection<IntegrationObjectModel> cleanup()
	{
		return IntegrationTestUtil.removeAll(IntegrationObjectModel.class);
	}

}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model;

import de.hybris.platform.integrationservices.model.impl.DefaultAttributeSettableCheckerFactory;
import de.hybris.platform.integrationservices.model.impl.NullAttributeValueGetterFactory;
import de.hybris.platform.integrationservices.model.impl.NullAttributeValueSetterFactory;

import javax.validation.constraints.NotNull;

/**
 * This is an <href="https://en.wikipedia.org/wiki/Abstract_factory_pattern">abstract factory</href> responsible for providing
 * descriptor implementations used for Integration Object metadata representation: {@link IntegrationObjectDescriptor},
 * {@link TypeDescriptor}, and {@link TypeAttributeDescriptor}
 */
public interface DescriptorFactory
{
	/**
	 * Creates a descriptor for an integration object.
	 *
	 * @param model model of the integration object stored in the persistent storage.
	 * @return new instance of the {@code IntegrationObjectDescriptor}.
	 */
	IntegrationObjectDescriptor createIntegrationObjectDescriptor(@NotNull IntegrationObjectModel model);

	/**
	 * Creates a descriptor for an item in an integration object.
	 *
	 * @param model model of the integration object item stored in the persistent storage.
	 * @return new instance of the {@code TypeDescriptor}.
	 */
	TypeDescriptor createItemTypeDescriptor(@NotNull IntegrationObjectItemModel model);

	/**
	 * Creates a descriptor for a class in an integration object.
	 *
	 * @param descriptor an integration object descriptor for the class
	 * @param model model of the integration object item stored in the persistent storage.
	 * @return new instance of the {@code TypeDescriptor}.
	 */
	TypeDescriptor createClassTypeDescriptor(@NotNull IntegrationObjectDescriptor descriptor,
	                                         @NotNull IntegrationObjectClassModel model);

	/**
	 * Creates a descriptor for an attribute in an integration object.
	 *
	 * @param model model of the integration object item attribute stored in the persistent storage
	 * @return new instance of the {@code TypeAttributeDescriptor}
	 */
	TypeAttributeDescriptor createTypeAttributeDescriptor(@NotNull AbstractIntegrationObjectItemAttributeModel model);

	/**
	 * Creates a descriptor for an attribute in an integration object. Default implementation in this interface simply delegates to the
	 * {@link #createTypeAttributeDescriptor(AbstractIntegrationObjectItemAttributeModel)} method. Implementations should override
	 * this method to leverage the availability of the TypeDescriptor in the context of the attribute creation.
	 *
	 * @param type descriptor for the item type containing the attribute.
	 * @param model model of the integration object item attribute stored in the persistent storage
	 * @return new instance of the {@code TypeAttributeDescriptor}
	 */
	default TypeAttributeDescriptor createTypeAttributeDescriptor(@NotNull final TypeDescriptor type,
	                                                              @NotNull final AbstractIntegrationObjectItemAttributeModel model)
	{
		return createTypeAttributeDescriptor(model);
	}

	/**
	 * Creates a descriptor for an attribute in an integration object.
	 *
	 * @param type descriptor for the class type containing the attribute.
	 * @param model model of the integration object class attribute
	 * @return new instance of the {@code TypeAttributeDescriptor}
	 */
	TypeAttributeDescriptor createTypeAttributeDescriptor(@NotNull final TypeDescriptor type,
	                                                      @NotNull final IntegrationObjectClassAttributeModel model);

	/**
	 * Gets the {@link AttributeValueAccessorFactory}
	 *
	 * @return An instance of a factory
	 */
	AttributeValueAccessorFactory getAttributeValueAccessorFactory();

	/**
	 * Gets the {@link AttributeValueGetterFactory}
	 *
	 * @return An instance of a factory
	 */
	default AttributeValueGetterFactory getAttributeValueGetterFactory()
	{
		return new NullAttributeValueGetterFactory();
	}

	/**
	 * Gets the {@link AttributeValueSetterFactory}
	 *
	 * @return An instance of a factory
	 */
	default AttributeValueSetterFactory getAttributeValueSetterFactory()
	{
		return new NullAttributeValueSetterFactory();
	}

	/**
	 * Gets the {@link AttributeSettableCheckerFactory}.
	 *
	 * @return An instance of the factory
	 */
	default AttributeSettableCheckerFactory getAttributeSettableCheckerFactory()
	{
		return new DefaultAttributeSettableCheckerFactory();
	}
}

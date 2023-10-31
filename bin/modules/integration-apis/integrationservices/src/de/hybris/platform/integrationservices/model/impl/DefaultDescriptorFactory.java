/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.model.AbstractIntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.AttributeSettableCheckerFactory;
import de.hybris.platform.integrationservices.model.AttributeValueAccessorFactory;
import de.hybris.platform.integrationservices.model.AttributeValueGetterFactory;
import de.hybris.platform.integrationservices.model.AttributeValueSetterFactory;
import de.hybris.platform.integrationservices.model.DescriptorFactory;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemClassificationAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;
import de.hybris.platform.integrationservices.model.TypeDescriptor;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;

/**
 * Default implementation of the {@link DescriptorFactory}
 */
public class DefaultDescriptorFactory implements DescriptorFactory
{
	private static final AttributeValueAccessorFactory ATTRIBUTE_NULL_VALUE_ACCESSOR_FACTORY = new DefaultAttributeValueAccessorFactory();
	private static final AttributeValueGetterFactory ATTRIBUTE_NULL_VALUE_GETTER_FACTORY = new NullAttributeValueGetterFactory();
	private static final AttributeValueSetterFactory ATTRIBUTE_NULL_VALUE_SETTER_FACTORY = new NullAttributeValueSetterFactory();
	private static final AttributeSettableCheckerFactory NULL_ATTRIBUTE_SETTABLE_CHECKER_FACTORY = new NullAttributeSettableCheckerFactory();
	private AttributeValueAccessorFactory attributeValueAccessorFactory;
	private AttributeValueGetterFactory attributeValueGetterFactory;
	private AttributeValueSetterFactory attributeValueSetterFactory;
	private AttributeSettableCheckerFactory attributeSettableCheckerFactory;
	private ReferencePathFinder referencePathFinder;

	/**
	 * Retrieves instance of the {@code DescriptorFactory} defined in the application context.
	 *
	 * @return {@code DescriptorFactory} configured in the application context.
	 */
	public static DescriptorFactory getContextFactory()
	{
		return ApplicationBeans
				.getBean("integrationServicesDescriptorFactory", DescriptorFactory.class);
	}

	@Override
	public IntegrationObjectDescriptor createIntegrationObjectDescriptor(final IntegrationObjectModel model)
	{
		return new DefaultIntegrationObjectDescriptor(model, this);
	}

	@Override
	public TypeDescriptor createItemTypeDescriptor(@NotNull final IntegrationObjectItemModel model)
	{
		final var descriptor = new ItemTypeDescriptor(model, this);
		descriptor.initialize();
		descriptor.setReferencePathFinder(referencePathFinder);
		return descriptor;
	}

	@Override
	public TypeDescriptor createClassTypeDescriptor(final IntegrationObjectDescriptor descriptor,
	                                                final IntegrationObjectClassModel model)
	{
		final var d = new ClassTypeDescriptor(descriptor, model, this);
		d.setReferencePathFinder(referencePathFinder);
		return d;
	}

	@Override
	public TypeAttributeDescriptor createTypeAttributeDescriptor(@NotNull final AbstractIntegrationObjectItemAttributeModel model)
	{
		Preconditions.checkArgument(model != null, "Attribute model cannot be null");
		final IntegrationObjectItemModel item;
		if (model instanceof IntegrationObjectItemClassificationAttributeModel)
		{
			item = ((IntegrationObjectItemClassificationAttributeModel) model).getIntegrationObjectItem();
		}
		else if (model instanceof IntegrationObjectItemVirtualAttributeModel)
		{
			item = ((IntegrationObjectItemVirtualAttributeModel) model).getIntegrationObjectItem();
		}
		else
		{
			item = ((IntegrationObjectItemAttributeModel) model).getIntegrationObjectItem();
		}
		final var typeDescriptor = new ItemTypeDescriptor(item, this);
		return createTypeAttributeDescriptor(typeDescriptor, model);
	}

	@Override
	public TypeAttributeDescriptor createTypeAttributeDescriptor(@NotNull final TypeDescriptor type,
	                                                             @NotNull final AbstractIntegrationObjectItemAttributeModel model)
	{
		if (model instanceof IntegrationObjectItemClassificationAttributeModel)
		{
			return new ClassificationTypeAttributeDescriptor(
					type, (IntegrationObjectItemClassificationAttributeModel) model, this);
		}
		else if (model instanceof IntegrationObjectItemVirtualAttributeModel)
		{
			return new VirtualTypeAttributeDescriptor(
					type, (IntegrationObjectItemVirtualAttributeModel) model, this);
		}

		return new DefaultTypeAttributeDescriptor(type, (IntegrationObjectItemAttributeModel) model, this);
	}

	@Override
	public TypeAttributeDescriptor createTypeAttributeDescriptor(@NotNull final TypeDescriptor type,
	                                                             @NotNull final IntegrationObjectClassAttributeModel model)
	{
		return new ClassTypeAttributeDescriptor(type, model, this);
	}

	@Override
	public AttributeValueAccessorFactory getAttributeValueAccessorFactory()
	{
		return attributeValueAccessorFactory != null ?
				attributeValueAccessorFactory :
				ATTRIBUTE_NULL_VALUE_ACCESSOR_FACTORY;
	}

	public void setAttributeValueAccessorFactory(final AttributeValueAccessorFactory attributeValueAccessorFactory)
	{
		this.attributeValueAccessorFactory = attributeValueAccessorFactory;
	}

	@Override
	public AttributeValueGetterFactory getAttributeValueGetterFactory()
	{
		return attributeValueGetterFactory != null ?
				attributeValueGetterFactory :
				ATTRIBUTE_NULL_VALUE_GETTER_FACTORY;
	}

	public void setAttributeValueGetterFactory(final AttributeValueGetterFactory attributeValueGetterFactory)
	{
		this.attributeValueGetterFactory = attributeValueGetterFactory;
	}

	@Override
	public AttributeValueSetterFactory getAttributeValueSetterFactory()
	{
		return attributeValueSetterFactory != null ?
				attributeValueSetterFactory :
				ATTRIBUTE_NULL_VALUE_SETTER_FACTORY;
	}

	public void setAttributeValueSetterFactory(final AttributeValueSetterFactory attributeValueSetterFactory)
	{
		this.attributeValueSetterFactory = attributeValueSetterFactory;
	}

	@Override
	public AttributeSettableCheckerFactory getAttributeSettableCheckerFactory()
	{
		return attributeSettableCheckerFactory != null ?
				attributeSettableCheckerFactory :
				NULL_ATTRIBUTE_SETTABLE_CHECKER_FACTORY;
	}

	public void setAttributeSettableCheckerFactory(final AttributeSettableCheckerFactory attributeSettableCheckerFactory)
	{
		this.attributeSettableCheckerFactory = attributeSettableCheckerFactory;
	}

	public void setReferencePathFinder(final ReferencePathFinder finder)
	{
		referencePathFinder = finder;
	}
}

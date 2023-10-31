/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.integrationservices.model.impl.PojoIntrospector;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * Validates the {@link IntegrationObjectClassAttributeModel} attributeName and that the
 * returnIntegrationObjectClass does not conflict with the class attribute's propertyType.
 */
public class IntegrationObjectClassAttributeNameValidateInterceptor
		implements ValidateInterceptor<IntegrationObjectClassAttributeModel>
{
	private static final String ATTRIBUTE_NAME_NOT_FOUND =
			"attributeName [%s] for class [%s] of IntegrationObject [%s] does not exist.";
	private static final String ATTRIBUTE_NAME_CONFLICTS_WITH_RETURN_CLASS =
			"attributeName [%s] for class [%s] of IntegrationObject [%s] does not match the returnIntegrationObjectClass type.";
	private static final String INTROSPECTION_EXCEPTION =
			"An exception occurred when attempting to retrieve attributeName [%s] for class [%s] of IntegrationObject [%s].";

	/**
	 * Validates the context attribute model and throws exceptions when something is incorrectly configured in the attribute.
	 *
	 * @throws InterceptorException when configuration problems found
	 */
	@Override
	public void onValidate(final IntegrationObjectClassAttributeModel attribute, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (StringUtils.isBlank(attribute.getReadMethod()))
		{
			validateAttributeMatchesReturnClassType(attribute, new PojoIntrospector(attribute));
		}
	}

	private void validateAttributeMatchesReturnClassType(final IntegrationObjectClassAttributeModel contextAttribute,
	                                                     final PojoIntrospector introspector)
			throws InterceptorException
	{
		final Class<?> propertyType = introspector.getActualPropertyType(
				findPropertyByAttributeName(contextAttribute, introspector));

		if (returnClassConflictsWithAttributeType(propertyType, contextAttribute.getReturnIntegrationObjectClass()))
		{
			throw interceptorException(ATTRIBUTE_NAME_CONFLICTS_WITH_RETURN_CLASS, contextAttribute);
		}
	}

	private PropertyDescriptor findPropertyByAttributeName(final IntegrationObjectClassAttributeModel contextAttribute,
	                                                       final PojoIntrospector introspector) throws InterceptorException
	{
		try
		{
			return introspector.findProperty()
			                   .orElseThrow(() -> interceptorException(ATTRIBUTE_NAME_NOT_FOUND, contextAttribute));
		}
		catch (final IntrospectionException e)
		{
			throw interceptorException(INTROSPECTION_EXCEPTION, contextAttribute);
		}
	}

	private boolean returnClassConflictsWithAttributeType(final Class<?> attrMethodType,
	                                                      final IntegrationObjectClassModel returnClass)
	{
		return (returnClass == null && isReferenceType(attrMethodType))
				|| (returnClass != null && !attrMethodType.isAssignableFrom(returnClass.getType()));
	}

	private boolean isReferenceType(final Class<?> classType)
	{
		return Optional.ofNullable(classType)
		               .map(clazz -> !(isSimpleValueType(clazz) || isCollection(clazz)))
		               .orElse(false);
	}

	private boolean isCollection(final Class<?> classType)
	{
		return Collection.class.isAssignableFrom(classType) || Map.class.isAssignableFrom(classType);
	}

	private boolean isSimpleValueType(final Class<?> classType)
	{
		return BeanUtils.isSimpleValueType(classType);
	}

	private Class<?> getClassType(final IntegrationObjectClassAttributeModel attribute)
	{
		return attribute.getIntegrationObjectClass().getType();
	}

	private String getClassTypeName(final IntegrationObjectClassAttributeModel contextAttribute)
	{
		return getClassType(contextAttribute).getName();
	}

	private String getIOCode(final IntegrationObjectClassAttributeModel contextAttribute)
	{
		return contextAttribute.getIntegrationObjectClass().getIntegrationObject().getCode();
	}

	private InterceptorException interceptorException(final String messageTemplate,
	                                                  final IntegrationObjectClassAttributeModel contextAttribute)
	{
		return new InterceptorException(messageTemplate.formatted(contextAttribute.getAttributeName(),
				getClassTypeName(contextAttribute),
				getIOCode(contextAttribute)), this);
	}
}

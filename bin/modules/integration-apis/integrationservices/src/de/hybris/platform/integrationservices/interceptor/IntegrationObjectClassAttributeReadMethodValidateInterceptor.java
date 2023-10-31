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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * Validates the {@link IntegrationObjectClassAttributeModel} readMethod and that the
 * returnIntegrationObjectClass does not conflict with the class attribute's propertyType.
 */
public class IntegrationObjectClassAttributeReadMethodValidateInterceptor
		implements ValidateInterceptor<IntegrationObjectClassAttributeModel>
{
	private static final String READ_METHOD_NOT_FOUND =
			"readMethod [%s] for class [%s] of IntegrationObject [%s] does not exist.";
	private static final String VOID_READ_METHOD_NOT_ALLOWED =
			"readMethod [%s] for class [%s] of IntegrationObject [%s] is VOID.";
	private static final String READ_METHOD_WITH_PARAMETERS_NOT_ALLOWED =
			"readMethod [%s] for class [%s] of IntegrationObject [%s] has non-empty parameters.";
	private static final String READ_METHOD_CONFLICTS_WITH_RETURN_CLASS =
			"readMethod [%s] return type for class [%s] of IntegrationObject [%s] does not match the " +
					"returnIntegrationObjectClass type.";

	/**
	 * Validates the context attribute model and throws exceptions when something is incorrectly configured in the attribute.
	 *
	 * @throws InterceptorException when configuration problems found
	 */
	@Override
	public void onValidate(final IntegrationObjectClassAttributeModel attribute, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (StringUtils.isNotBlank(attribute.getReadMethod()))
		{
			validateMethodMatchesReturnClassType(attribute, new PojoIntrospector(attribute));
		}
	}

	private void validateMethodMatchesReturnClassType(final IntegrationObjectClassAttributeModel contextAttribute,
	                                                  final PojoIntrospector introspector) throws InterceptorException
	{
		final Class<?> methodType = introspector.getActualMethodReturnType(findReadMethod(contextAttribute, introspector));
		final IntegrationObjectClassModel returnIOClass = contextAttribute.getReturnIntegrationObjectClass();

		if (returnClassConflictsWithAttributeType(methodType, returnIOClass))
		{
			throw new InterceptorException(
					READ_METHOD_CONFLICTS_WITH_RETURN_CLASS.formatted(contextAttribute.getReadMethod(),
							getClassTypeName(contextAttribute),
							getIOCode(contextAttribute)),
					this);
		}
	}

	private boolean returnClassConflictsWithAttributeType(final Class<?> attrMethodType,
	                                                      final IntegrationObjectClassModel returnClass)
	{
		return (returnClass == null && isReferenceType(attrMethodType))
				|| (returnClass != null && !attrMethodType.isAssignableFrom(returnClass.getType()));
	}

	private Method findReadMethod(final IntegrationObjectClassAttributeModel contextAttribute,
	                              final PojoIntrospector introspector)
			throws InterceptorException
	{
		final String readMethod = contextAttribute.getReadMethod();
		final Class<?> clazz = getClassType(contextAttribute);

		final Method method = introspector.findMethod(readMethod)
		                                  .orElseThrow(() -> new InterceptorException(
				                                  READ_METHOD_NOT_FOUND.formatted(readMethod, clazz.getName(),
						                                  getIOCode(contextAttribute)), this));
		validateReadMethod(contextAttribute, readMethod, clazz, method);
		return method;
	}

	private void validateReadMethod(final IntegrationObjectClassAttributeModel contextAttribute, final String readMethod,
	                                final Class<?> clazz, final Method method) throws InterceptorException
	{
		if (!methodHasValidParams(method))
		{
			throw new InterceptorException(
					String.format(READ_METHOD_WITH_PARAMETERS_NOT_ALLOWED, readMethod, clazz.getName(),
							getIOCode(contextAttribute)), this);
		}

		if (methodHasVoidReturnType(method))
		{
			throw new InterceptorException(
					String.format(VOID_READ_METHOD_NOT_ALLOWED, readMethod, clazz.getName(), getIOCode(contextAttribute)), this);
		}
	}

	private boolean methodHasValidParams(final Method method)
	{
		final Class<?>[] paramTypes = method.getParameterTypes();
		return paramTypes.length == 0 || (paramTypes.length == 1 && Locale.class.equals(paramTypes[0]));
	}

	private boolean methodHasVoidReturnType(final Method method)
	{
		return Void.TYPE.equals(method.getReturnType());
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
}

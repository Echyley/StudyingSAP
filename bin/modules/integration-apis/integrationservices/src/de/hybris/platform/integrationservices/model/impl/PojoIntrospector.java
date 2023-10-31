/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl;

import de.hybris.platform.integrationservices.exception.ClassAttributeExecutionException;
import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

/**
 * A helper class for performing Java Reflections (Java Bean introspections) on POJOs.
 */
public class PojoIntrospector
{
	private static final Object[] LOCALE_PARAMS = { Locale.class };
	private final IntegrationObjectClassAttributeModel attributeModel;
	private final Class<?> contextClass;

	/**
	 * Instantiates this introspector
	 *
	 * @param model attribute model to create the introspector for.
	 */
	public PojoIntrospector(@NotNull final IntegrationObjectClassAttributeModel model)
	{
		attributeModel = model;
		contextClass = model.getIntegrationObjectClass().getType();
	}

	/**
	 * To get all the declared properties in the IO class.
	 *
	 * @return A set of declared properties in the parent class.
	 * @throws IntrospectionException If an exception occurs while getting bean info for the IO class.
	 */
	public Set<PropertyDescriptor> getAllProperties() throws IntrospectionException
	{
		final var beanInfo = Introspector.getBeanInfo(contextClass);
		return Set.of(beanInfo.getPropertyDescriptors());
	}

	/**
	 * Searches for a property descriptor in the context class for this introspector.
	 *
	 * @param name name of the property, which is expected to follow the Java Bean conventions. For example, if a bean has methods
	 *             like {@code getValue} and/or {@code setValue}, then the property name is {@code value}.
	 * @return an Optional containing the property descriptor, if such property was found in the context class,
	 * {@code Optional.empty()} otherwise.
	 * @throws IntrospectionException if Java failed to introspect the context class.
	 * @see Introspector#getBeanInfo(Class)
	 */
	public Optional<PropertyDescriptor> findProperty(final String name) throws IntrospectionException
	{
		return getAllProperties().stream()
		                         .filter(p -> p.getName().equals(name))
		                         .findFirst();
	}

	/**
	 * To get the property descriptor of the attribute model inside the context IO class.
	 *
	 * @return An Optional containing the property descriptor of the attribute model.
	 * @throws IntrospectionException If Java failed to introspect the context class.
	 */
	public Optional<PropertyDescriptor> findProperty() throws IntrospectionException
	{
		return findProperty(attributeModel.getAttributeName());
	}

	/**
	 * Searches for a method in the context class.
	 *
	 * @param paramTypes expected parameter types the method should have.
	 * @return an Optional containing the {@code Method}, if the method matching the search conditions was found, or
	 * {@code Optional.empty()} otherwise.
	 */
	public Optional<Method> findMethod(final Object[] paramTypes)
	{
		final String methodName = getReadMethodName();
		return Arrays.stream(contextClass.getMethods())
		             .filter(m -> m.getName().equals(methodName))
		             .filter(m -> Arrays.equals(m.getParameterTypes(), paramTypes))
		             .findFirst();
	}

	/**
	 * Searches for a method in the context class.
	 *
	 * @param methodName name of the method to find.
	 * @return an Optional containing the {@code Method}, if the method with the matching name was found, or
	 * {@code Optional.empty()} otherwise. This method does not take method parameters into consideration.
	 */
	public Optional<Method> findMethod(final String methodName)
	{
		return Arrays.stream(contextClass.getMethods())
		             .filter(m -> m.getName().equals(methodName))
		             .findFirst();
	}

	/**
	 * gets return type for a method in the context class.
	 *
	 * @param method the {@code Method} to find.
	 * @return actual method type of {@code method}
	 * @throws IllegalArgumentException if {@code method} is null
	 */
	public Class<?> getActualMethodReturnType(final Method method)
	{
		return getGenericType(method, 0);
	}

	/**
	 * gets type for a property descriptor in the context class.
	 *
	 * @param property the {@code PropertyDescriptor} to find
	 * @return actual type of the {@code property}
	 * @throws IllegalArgumentException if {@code property} is null
	 */
	public Class<?> getActualPropertyType(final PropertyDescriptor property)
	{
		Preconditions.checkArgument(property != null, "PropertyDescriptor cannot be null");

		return getActualMethodReturnType(property.getReadMethod());
	}

	/**
	 * Finds the read method of the attribute model in the context class. This method will first try to look for the read method
	 * from the read method name in the attribute model. If there is no read method name provided to the attribute model, this
	 * method will use attribute name to search for the read method.
	 * For example, if attribute model {@code attr} has no read method name assigned, this method will try to look for method name
	 * {@code getAttr()}.
	 * @return Optional of the read method of the attribute model.
	 */
	public Optional<Method> getReadMethod()
	{
		try
		{
			final String readMethodName = attributeModel.getReadMethod();
			return StringUtils.isNotEmpty(readMethodName)
					? findMethod(readMethodName)
					: getReadMethodFromAttributeName();
		}
		catch (final IntrospectionException e)
		{
			throw new ClassAttributeExecutionException(attributeModel, e);
		}
	}

	/**
	 * This method will return the return-type of the read method.
	 * @return optional of return type.
	 */
	public Optional<Class<?>> getReturnType()
	{
		return getReadMethod().map(Method::getReturnType);
	}

	/**
	 * Checks whether given attribute model is localized or not. It is considered to be localized if its read method has
	 * locale parameter type.
	 * @return {@code true} if attribute is localized, else {@code false}.
	 */
	public boolean isLocalized()
	{
		return findMethod(LOCALE_PARAMS).isPresent();
	}

	/**
	 * Gets key type for a method/property with Map return type in the context class.
	 *
	 * @return type of the {@code property} or return null if it is not Map
	 */
	public Class<?> getMapKeyType()
	{
		return isMap() ? getReadMethod().map(method -> getGenericType(method, 0)).orElse(null) : null;
	}

	/**
	 * Gets value type for a method/property with Map return type in the context class.
	 *
	 * @return value type of the {@code property}, or return null if it is not Map
	 */
	public Class<?> getMapValueType()
	{
		if (isMap())
		{
			return getReadMethod().map(method -> getGenericType(method, 1)).orElse(null);
		}

		return null;
	}

	private Optional<Method> getReadMethodFromAttributeName() throws IntrospectionException
	{
		return findProperty().map(PropertyDescriptor::getReadMethod);
	}

	private String getReadMethodName()
	{
		return getReadMethod()
				.map(Method::getName)
				.orElse("");
	}

	private Class<?> getGenericType(final Method method, final int paramIdx)
	{
		Preconditions.checkArgument(method != null, "Method cannot be null");
		Preconditions.checkArgument(paramIdx >=0, "Parameter index cannot be negative");

		final var type = method.getGenericReturnType();
		return type instanceof ParameterizedType ?
				(Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[paramIdx] : (Class<?>) type;
	}

	/**
	 * Returns integration object code for the attribute model in the context of this introspector
	 *
	 * @return an integration object code
	 */
	public String getIntegrationObjectCode()
	{
		return attributeModel.getIntegrationObjectClass().getIntegrationObject().getCode();
	}

	/**
	 * Returns integration object item code for the attribute model in the context of this introspector
	 *
	 * @return an integration object code
	 */
	public String getIntegrationObjectItemCode()
	{
		return attributeModel.getIntegrationObjectClass().getCode();
	}

	/**
	 * Checks whether given return type of {@code Method} is Map or not.
	 *
	 * @return {@code true} if it is Map, else {@code false}.
	 */
	public boolean isMap()
	{
		return getReturnType().map(Map.class::isAssignableFrom)
		                      .orElse(false);
	}

	/**
	 * Checks whether given return type of {@code Method} is Collection or not.
	 *
	 * @return {@code true} if it is Collection, else {@code false}.
	 */
	public boolean isCollection()
	{
		return getReturnType().map(Collection.class::isAssignableFrom)
		                      .orElse(false);
	}
}

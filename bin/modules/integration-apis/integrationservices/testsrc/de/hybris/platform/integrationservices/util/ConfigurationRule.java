/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util;

import de.hybris.platform.core.Registry;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * A test rule that automatically restores configuration changes done by the tests.<br/>
 * IMPORTANT: this rule can only manage beans written in Java. It fails to manage beans, for example, written in Groovy.
 */
public class ConfigurationRule<T> extends ExternalResource
{
	private static final Logger LOG = Log.getLogger(ConfigurationRule.class);
	private static final Object[] NO_ARGS = {};
	private static final Class<?>[] NO_PARAMS = {};

	private final Map<Method, PropertyDescriptor> managedProperties;
	private final Map<Method, Object> initialValues;
	private final T configurationService;
	private T configurationServiceProxy;

	private ConfigurationRule(final T config)
	{
		configurationService = config;
		initialValues = new HashMap<>();
		managedProperties = derivePropertiesFor(config)
				.stream()
				.collect(Collectors.toMap(PropertyDescriptor::getWriteMethod, desc -> desc));
	}

	/**
	 * Creates a configuration rule for managing configuration parameters changes on the specified Spring bean.
	 *
	 * @param beanId ID of the Spring Bean to be managed by this rule.
	 * @param type   type of the Spring Bean to be managed by this rule.
	 * @param <C>    class of the bean managed by this rule.
	 * @return a rule that automatically restores any settings changed on the Spring Bean.
	 */
	public static <C> ConfigurationRule<C> createFor(final String beanId, final Class<C> type)
	{
		final C config = getBean(beanId, type);
		return new ConfigurationRule<>(config);
	}

	/**
	 * Creates a configuration rule for managing changes on the specified Java Bean instance.
	 *
	 * @param bean a Java Bean to manage changes for.
	 * @param <C>    class of the bean managed by this rule.
	 * @return a rule that automatically restores any settings changed on the Java Bean.
	 */
	public static <C> ConfigurationRule<C> createFor(final C bean)
	{
		LOG.info("Creating ConfigurationRule for {}", bean.getClass());
		return new ConfigurationRule<>(bean);
	}

	public T configuration()
	{
		if (configurationServiceProxy == null)
		{
			configurationServiceProxy = createPropertyChangeTrackingProxyFor(configurationService);
		}
		return configurationServiceProxy;
	}

	private T createPropertyChangeTrackingProxyFor(final T config)
	{
		final Class<T> type = (Class<T>) config.getClass();
		try
		{
			return new ByteBuddy()
					.subclass(type)
					.method(ElementMatchers.any())
					.intercept(MethodDelegation.to(this))
					.make()
					.load(type.getClassLoader())
					.getLoaded()
					.getConstructor(NO_PARAMS)
					.newInstance();
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
		{
			throw new RuntimeException("Failed to create a proxy for " + config, e);
		}
	}

	/**
	 * Intercepts invocations to the configuration managed by this rule.
	 *
	 * @param method method being called on the configuration object.
	 * @param values parameters passed into the method.
	 * @return value returned by the configuration method being called.
	 */
	@RuntimeType
	public Object intercept(@Origin final Method method, @AllArguments final Object[] values)
	{
		captureInitialValue(method);
		LOG.debug("Calling {}({})", method.getName(), values);
		return call(method, values);
	}

	private void captureInitialValue(final Method setter)
	{
		if (isInitialValueNotCaptured(setter) && isManagedSetterCalled(setter))
		{
			final Method getter = managedProperties.get(setter).getReadMethod();
			try
			{
				LOG.debug("Capturing current value of {}", getter.getName());
				final Object initialValue = getter.invoke(configurationService, NO_ARGS);
				LOG.info("Saving initial value {} for {}", initialValue, getter.getName());
				initialValues.put(setter, initialValue);
			}
			catch (final IllegalAccessException | InvocationTargetException e)
			{
				LOG.error("Failed to read initial value using: {}", getter);
			}
		}
	}

	private boolean isInitialValueNotCaptured(final Method method)
	{
		return !initialValues.containsKey(method);
	}

	private boolean isManagedSetterCalled(final Method method)
	{
		return managedProperties.containsKey(method);
	}

	private static <C> C getBean(final String beanId, final Class<C> type)
	{
		return Registry.getApplicationContext().getBean(beanId, type);
	}

	private static Set<PropertyDescriptor> derivePropertiesFor(final Object config)
	{
		try
		{
			final var beanInfo = Introspector.getBeanInfo(config.getClass());
			final PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
			return Stream.of(descriptors)
					.filter(desc -> desc.getWriteMethod() != null)
					.filter(desc -> desc.getReadMethod() != null)
					.collect(Collectors.toSet());
		}
		catch (final IntrospectionException e)
		{
			throw new IllegalStateException("Failed to retrieve configuration properties for " + config);
		}
	}

	@Override
	protected void after()
	{
		restoreInitialValues();
		initialValues.clear();
	}

	private void restoreInitialValues()
	{
		LOG.debug("Restoring initial values");
		initialValues.entrySet().forEach(this::callSetterWithInitialValue);
	}

	private void callSetterWithInitialValue(final Map.Entry<Method, Object> e)
	{
		final Method setter = e.getKey();
		final Object value = e.getValue();
		LOG.info("Restoring initial value: {}({})", setter.getName(), value);
		call(setter, value);
	}

	private Object call(final Method method, final Object... values)
	{
		try
		{
			return method.invoke(configurationService, values);
		}
		catch (final IllegalAccessException | InvocationTargetException ex)
		{
			LOG.error("Failed to call {}({})", method, values);
			return null;
		}
	}
}

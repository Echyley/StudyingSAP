package de.hybris.platform.integrationbackoffice;

import de.hybris.platform.core.Registry;

import java.util.HashMap;
import java.util.Map;

import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;

public class BeanRegisteringRule<T> extends ExternalResource
{
	private final Map<String, Class<?>> registeredBeans = new HashMap<>();

	public BeanRegisteringRule register(final Class<T> beanClass, final String beanName)
	{
		registeredBeans.put(beanName, beanClass);
		return this;
	}

	private void registerBean(final String beanName, final Class<?> beanClass)
	{
		final GenericApplicationContext applicationContext = (GenericApplicationContext) Registry.getApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
		final AbstractBeanDefinition validationDefinition = BeanDefinitionBuilder.rootBeanDefinition(beanClass)
		                                                                         .getBeanDefinition();
		beanFactory.registerBeanDefinition(beanName, validationDefinition);
	}

	public T getBean(final Class<T> beanClass, final String beanName)
	{
		return beanClass.cast(Registry.getApplicationContext().getBean(beanName));
	}

	private void deregisterBean(final String beanName)
	{
		final GenericApplicationContext applicationContext = (GenericApplicationContext) Registry.getApplicationContext();
		final DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
		beanFactory.removeBeanDefinition(beanName);
	}

	@Override
	protected void before()
	{
		registeredBeans.forEach(this::registerBean);
	}

	@Override
	protected void after()
	{
		registeredBeans.keySet().forEach(this::deregisterBean);
		registeredBeans.clear();
	}
}

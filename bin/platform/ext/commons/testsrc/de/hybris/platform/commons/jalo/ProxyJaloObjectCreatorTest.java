/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commons.jalo;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloImplementationManager;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.product.GeneratedProduct;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalBaseTest;
import de.hybris.platform.testframework.assertions.assertj.TestLogListenerAssert;
import de.hybris.platform.testframework.log.TestLogListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Empty;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperMethod;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;


@IntegrationTest
public class ProxyJaloObjectCreatorTest extends ServicelayerTransactionalBaseTest
{
	private final TestLogListener testLogListener = new TestLogListener();

	@Before
	public void setUp()
	{
		testLogListener.attach();
	}

	@After
	public void tearDown()
	{
		testLogListener.detach();
		JaloImplementationManager.clearJaloObjectMapping(Product.class);
	}

	@Test
	public void proxyNullTest() throws IllegalArgumentException, SecurityException
	{
		JaloImplementationManager.replaceCoreJaloClass(Product.class,
				new CommonsManager.ProxyJaloObjectCreator(null));

		final String code1 = UUID.randomUUID().toString();

		final ProductManager instance = ProductManager.getInstance();
		Assert.assertThrows(JaloSystemException.class, () -> instance.createProduct(code1));
	}

	@Test
	public void proxyInvalidTest() throws IllegalArgumentException, NoSuchMethodException, SecurityException
	{
		final Class<? extends Item> proxyType = new ByteBuddy().subclass(GeneratedProduct.class)
				.method(ElementMatchers.anyOf(GeneratedProduct.class.getMethod("setDescription", String.class),
						GeneratedProduct.class.getMethod("setName", String.class)))
				.intercept(MethodDelegation.to(TestInterceptor.class)).make().load(getClass().getClassLoader()).getLoaded();

		JaloImplementationManager.replaceCoreJaloClass(Product.class,
				new CommonsManager.ProxyJaloObjectCreator(proxyType));

		final String code1 = UUID.randomUUID().toString();
		final ProductManager instance = ProductManager.getInstance();
		Assert.assertThrows(ClassCastException.class, () -> instance.createProduct(code1));
	}

	@Test
	public void proxyInterceptionTest() throws IllegalArgumentException, NoSuchMethodException, SecurityException
	{
		final String code1 = UUID.randomUUID().toString();
		final Product p1 = ProductManager.getInstance().createProduct(code1);
		p1.setName(code1 + "-name");

		createProxyClassAndreplaceCoreJaloClass();

		final String code2 = UUID.randomUUID().toString();
		final Product p2 = ProductManager.getInstance().createProduct(code2);
		p2.setCode(code2 + "-mod");
		p2.setName(code2 + "-name");

		TestLogListenerAssert.assertThat(testLogListener).hasLog().withMessageContaining("intercept setName " + code2)
				.loggedFrom(TestInterceptor.class).withLogLevel(TestLogListenerAssert.LogLevel.INFO)
				.occurrences(4);

		TestLogListenerAssert.assertThat(testLogListener).hasLog().loggedFrom(TestInterceptor.class)
				.withLogLevel(TestLogListenerAssert.LogLevel.INFO).occurrences(4);
	}

	@Test
	public void setProxyComposedTypeTest() throws IllegalArgumentException, NoSuchMethodException, SecurityException
	{
		final String code1 = UUID.randomUUID().toString();
		final Product p1 = ProductManager.getInstance().createProduct(code1);

		final String code2 = UUID.randomUUID().toString();
		final Product p2 = ProductManager.getInstance().createProduct(code2);

		final Item item1 = p1.setComposedType(p2.getComposedType());
		assertThat(item1).isInstanceOf(Product.class);
		assertThat(item1.getClass().getSimpleName()).doesNotContain("ByteBuddy");

		createProxyClassAndreplaceCoreJaloClass();

		final String code3 = UUID.randomUUID().toString();
		final Product p3 = ProductManager.getInstance().createProduct(code3);

		final Item item2 = p1.setComposedType(p3.getComposedType());
		assertThat(item2).isInstanceOf(Product.class);
		assertThat(item2.getClass().getSimpleName()).contains("ByteBuddy");
	}

	private void createProxyClassAndreplaceCoreJaloClass() throws NoSuchMethodException
	{
		final Class<? extends Item> proxyType = new ByteBuddy().subclass(Product.class)
				.method(ElementMatchers.anyOf(Product.class.getMethod("setDescription", String.class),
						Product.class.getMethod("setName", String.class)))
				.intercept(MethodDelegation.to(TestInterceptor.class)).make().load(getClass().getClassLoader()).getLoaded();

		JaloImplementationManager.replaceCoreJaloClass(Product.class,
				new CommonsManager.ProxyJaloObjectCreator(proxyType));
	}

	protected static final class TestInterceptor
	{
		private TestInterceptor()
		{
		}

		private static final Logger LOG = LoggerFactory.getLogger(TestInterceptor.class);

		@RuntimeType
		public static Object intercept(@This final Object self, @Origin final Method method, @AllArguments final Object[] args,
									   @SuperMethod(nullIfImpossible = true) final Method superMethod,
									   @Empty final Object defaultValue)
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException

		{
			logInfo(method, args, "start");
			if (superMethod == null)
			{
				return defaultValue;
			}

			final Item i = (Item) self;
			final Object[] adjustedArgs = before(i, method, args);
			final Object ret = after(i, method, args, adjustedArgs, superMethod.invoke(i, adjustedArgs));

			logInfo(method, args, "end");

			return ret;
		}

		protected static Object[] before(@SuppressWarnings("unused") final Item p,
										 @SuppressWarnings("unused") final Method method,
										 final Object[] args)
		{
			logInfo(method, args, "before");
			return args;
		}

		protected static Object after(@SuppressWarnings("unused") final Item i, @SuppressWarnings("unused") final Method method,
									  @SuppressWarnings("unused") final Object[] originalArgs,
									  @SuppressWarnings("unused") final Object[] args, final Object returned)
		{
			logInfo(method, args, "after");
			return returned;
		}

		private static String extractArgs(final Object[] args)
		{
			return Stream.of(args).map(Object::toString).collect(Collectors.joining(""));
		}

		private static void logInfo(final Method method, final Object[] args, final String suffix)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("intercept {} {} {}", method.getName(), extractArgs(args), suffix);
			}
		}
	}
}
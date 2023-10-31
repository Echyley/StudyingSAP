/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.property.interceptor;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.property.interceptor.impl.CompositeServerStartupPropertiesInterceptor;
import de.hybris.platform.property.interceptor.impl.DefaultServerStartupPropertiesInterceptionProcessor;
import de.hybris.platform.util.config.ConfigIntf;
import de.hybris.platform.util.config.FastHashMapConfig;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

@UnitTest
public class ServerStartupPropertiesInterceptionProcessorTest
{
	ServerStartupPropertiesInterceptionProcessor processor;

	@Test
	public void shouldInvokeConfigIntfSizeActionDuringIteration()
	{
		final TestInterceptor_1 interceptor = new TestInterceptor_1();
		processor = new DefaultServerStartupPropertiesInterceptionProcessor(configIngWith2Properties(), interceptor);
		assertThat(interceptor.numberActionDuringIteration).isZero();
		processor.startProcessing();
		assertThat(interceptor.numberActionDuringIteration).isEqualTo(2);
	}

	@Test
	public void shouldInvokeConfigIntfSizeActionDuringIterationForEveryInterceptor()
	{
		final TestInterceptor_1 interceptor1 = new TestInterceptor_1();
		final TestInterceptor_2 interceptor2 = new TestInterceptor_2();

		final ServerStartupPropertiesInterceptor compositeInterceptor = CompositeServerStartupPropertiesInterceptor.builder()
		                                                                                                           .register(
				                                                                                                           interceptor1)
		                                                                                                           .register(
				                                                                                                           interceptor2)
		                                                                                                           .build();

		processor = new DefaultServerStartupPropertiesInterceptionProcessor(configIngWith2Properties(), compositeInterceptor);
		assertThat(interceptor1.numberActionDuringIteration).isZero();
		assertThat(interceptor2.numberActionDuringIteration).isZero();
		processor.startProcessing();
		assertThat(interceptor1.numberActionDuringIteration).isEqualTo(2);
		assertThat(interceptor2.numberActionDuringIteration).isEqualTo(2);
	}

	@Test
	public void shouldInvokeOnlyOnceActionAfterIterationForEveryInterceptor()
	{
		final TestInterceptor_1 interceptor1 = new TestInterceptor_1();
		final TestInterceptor_2 interceptor2 = new TestInterceptor_2();

		final ServerStartupPropertiesInterceptor compositeInterceptor = CompositeServerStartupPropertiesInterceptor.builder()
		                                                                                                           .register(
				                                                                                                           interceptor1)
		                                                                                                           .register(
				                                                                                                           interceptor2)
		                                                                                                           .build();

		processor = new DefaultServerStartupPropertiesInterceptionProcessor(configIngWith2Properties(), compositeInterceptor);
		assertThat(interceptor1.numberActionAfterIteration).isZero();
		assertThat(interceptor2.numberActionAfterIteration).isZero();
		processor.startProcessing();
		assertThat(interceptor1.numberActionAfterIteration).isEqualTo(1);
		assertThat(interceptor2.numberActionAfterIteration).isEqualTo(1);
	}

	@Test
	public void shouldInvokeOnlyOnceActionAfterIteration()
	{
		final TestInterceptor_1 interceptor = new TestInterceptor_1();
		processor = new DefaultServerStartupPropertiesInterceptionProcessor(configIngWith2Properties(), interceptor);
		assertThat(interceptor.numberActionAfterIteration).isZero();
		processor.startProcessing();
		assertThat(interceptor.numberActionAfterIteration).isEqualTo(1);
	}

	private ConfigIntf configIngWith2Properties()
	{
		final Map<String, String> cfg = new HashMap<>();
		cfg.put("p1", "v1");
		cfg.put("p2", "v2");
		return new FastHashMapConfig(cfg);
	}

	private static class TestInterceptor_1 implements ServerStartupPropertiesInterceptor
	{
		int numberActionDuringIteration = 0;
		int numberActionAfterIteration = 0;

		@Override
		public void actionDuringIteration(final Map.Entry<String, String> entry)
		{
			numberActionDuringIteration++;
		}

		@Override
		public void actionAfterIteration()
		{
			numberActionAfterIteration++;
		}
	}

	private static class TestInterceptor_2 implements ServerStartupPropertiesInterceptor
	{
		int numberActionDuringIteration = 0;
		int numberActionAfterIteration = 0;

		@Override
		public void actionDuringIteration(final Map.Entry<String, String> entry)
		{
			numberActionDuringIteration++;
		}

		@Override
		public void actionAfterIteration()
		{
			numberActionAfterIteration++;
		}
	}
}

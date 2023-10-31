/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.client.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateCreator;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegrationRestTemplateFactoryUnitTest
{
	@InjectMocks
	private DefaultIntegrationRestTemplateFactory factory;

	@Mock(lenient = true)
	private ConsumedDestinationModel consumedDestination;
	@Mock(lenient = true)
	private IntegrationRestTemplateCreator creator1;
	@Mock(lenient = true)
	private IntegrationRestTemplateCreator creator2;
	@Mock(lenient = true)
	private IntegrationRestTemplateCreator creator3;
	@Mock(lenient = true)
	private RestOperations restTemplate;

	@Before
	public void setup()
	{
		factory.setRestTemplateCreators(Lists.newArrayList(creator1, creator2, creator3));
	}

	@Test
	public void setRestTemplateCreatorsDoNotLeakReferences()
	{
		final List<IntegrationRestTemplateCreator> creators = new ArrayList<>();
		creators.add(creator1);

		factory.setRestTemplateCreators(creators);
		creators.clear();

		assertThat(factory.getRestTemplateCreators()).isNotEmpty();
	}

	@Test
	public void setRestTemplateCreatorsToNullResultsInEmptyList()
	{
		factory.setRestTemplateCreators(null);

		assertThat(factory.getRestTemplateCreators()).isEmpty();
	}

	@Test
	public void getRestTemplateCreatorsDoesNotLeakReferences()
	{
		final List<IntegrationRestTemplateCreator> creators = new ArrayList<>();
		creators.add(creator1);
		factory.setRestTemplateCreators(creators);

		final List<IntegrationRestTemplateCreator> copy = factory.getRestTemplateCreators();
		copy.clear();

		assertThat(factory.getRestTemplateCreators()).isEqualTo(creators);
	}

	@Test
	public void shouldReturnRestTemplate()
	{
		lenient().when(creator1.isApplicable(consumedDestination)).thenReturn(true);
		lenient().when(creator2.isApplicable(consumedDestination)).thenReturn(false);
		lenient().when(creator3.isApplicable(consumedDestination)).thenReturn(true);

		lenient().when(creator1.create(consumedDestination)).thenReturn(restTemplate);

		final RestOperations restOperation = factory.create(consumedDestination);

		assertThat(restOperation).isEqualTo(restTemplate);
		verify(creator2, never()).create(consumedDestination);
		verify(creator3, never()).create(consumedDestination);
	}

	@Test
	public void shouldThrowUnsupportedException()
	{
		lenient().when(creator1.isApplicable(consumedDestination)).thenReturn(false);
		lenient().when(creator2.isApplicable(consumedDestination)).thenReturn(false);
		assertThatThrownBy(() -> factory.create(consumedDestination))
				.isInstanceOf(UnsupportedRestTemplateException.class);
	}

	@Test
	public void shouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> factory.create(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Consumed destination model cannot be null.");
	}
}

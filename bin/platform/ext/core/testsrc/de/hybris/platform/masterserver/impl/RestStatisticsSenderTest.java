/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.masterserver.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class RestStatisticsSenderTest
{

	public static final String SOME_DATA = "someData";
	public static final String PASSWORD = "nimda";
	private static final String ED = "ed";
	private static final String LID = "lid";

	@Mock
	RestTemplate restTemplate;

	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenRestTemplateIsNotProvided()
	{
		new StatisticsPublisher.RestStatisticsSender(
				null);
	}

	@Test
	public void shouldReturnTruePayloadIsEmpty()
	{
		final StatisticsPublisher.RestStatisticsSender statisticsSender = new StatisticsPublisher.RestStatisticsSender(
				restTemplate);

		final boolean sent = statisticsSender.sendStatistics(null);
		assertThat(sent).isTrue();
	}

	@Test
	public void shouldReturnTruePayloadIsValid()
	{
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(
				new ResponseEntity<>("msg", HttpStatus.OK));
		final StatisticsPublisher.RestStatisticsSender statisticsSender = new StatisticsPublisher.RestStatisticsSender(
				restTemplate);

		final boolean sent = statisticsSender.sendStatistics(statisticsPayload());

		assertThat(sent).isTrue();
	}

	@Test
	public void shouldReturnFalsePayloadIsValidPostResponseIsNot2xx()
	{
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(
				new ResponseEntity<>("msg", HttpStatus.NOT_FOUND));
		final StatisticsPublisher.RestStatisticsSender statisticsSender = new StatisticsPublisher.RestStatisticsSender(
				restTemplate);

		final boolean sent = statisticsSender.sendStatistics(statisticsPayload());

		assertThat(sent).isFalse();
	}


	@Test
	public void shouldReturnFalsePayloadIsValidPostThrownException()
	{
		when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenThrow(RestClientException.class);
		final StatisticsPublisher.RestStatisticsSender statisticsSender = new StatisticsPublisher.RestStatisticsSender(
				restTemplate);

		final boolean sent = statisticsSender.sendStatistics(statisticsPayload());

		assertThat(sent).isFalse();
	}

	@Test
	public void shouldFieldsOfHttpRequestEntityBeProperlySet()
	{
		final StatisticsPublisher.RestStatisticsSender statisticsSender = new StatisticsPublisher.RestStatisticsSender(
				restTemplate);

		final Optional<HttpEntity<MultiValueMap<String, String>>> httpEntity = statisticsSender.prepareHttpRequest(
				statisticsPayload());

		assertThat(httpEntity).isPresent();
		final MultiValueMap<String, String> body = httpEntity.get().getBody();
		assertThat(body).isNotNull();
		assertThat(body.get(ED)).isEqualTo(Collections.singletonList(SOME_DATA));
		assertThat(body.get(LID)).isEqualTo(Collections.singletonList(PASSWORD));
	}

	StatisticsPayload statisticsPayload()
	{
		return new StatisticsPayload(PASSWORD, "hybris.com", SOME_DATA);
	}

}

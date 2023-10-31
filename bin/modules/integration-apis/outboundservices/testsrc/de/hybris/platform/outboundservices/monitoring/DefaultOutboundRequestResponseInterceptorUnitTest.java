/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundservices.monitoring;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.enums.IntegrationRequestStatus;
import de.hybris.platform.integrationservices.model.MonitoredRequestErrorModel;
import de.hybris.platform.integrationservices.monitoring.MonitoredRequestErrorParser;
import de.hybris.platform.integrationservices.service.MediaPersistenceService;
import de.hybris.platform.outboundservices.config.OutboundServicesConfiguration;
import de.hybris.platform.outboundservices.model.OutboundRequestMediaModel;
import de.hybris.platform.outboundservices.model.OutboundRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import com.google.common.collect.Lists;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOutboundRequestResponseInterceptorUnitTest
{
	private static final String OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME = "X-OutboundMonitoring-MessageId";
	private static final String MY_MESSAGE_ID = "MY_MESSAGE_ID";
	private static final String RESPONSE_PAYLOAD = "{ error = 'error' }";
	private static final String REQUEST_PAYLOAD = "my_payload";
	private static final byte[] REQUEST_PAYLOAD_BYTES = REQUEST_PAYLOAD.getBytes(StandardCharsets.UTF_8);
	private static final String ERROR_MESSAGE = "My error message";
	private final List<MonitoredRequestErrorParser<MonitoredRequestErrorModel>> errorParsers = Lists.newArrayList();
	private final HttpHeaders httpHeadersRequest = new HttpHeaders();
	private final HttpHeaders httpHeadersResponse = new HttpHeaders();
	private static final long TIMEOUT = 10;

	@Mock(lenient = true)
	private ModelService modelService;
	@Mock(lenient = true)
	private FlexibleSearchService flexibleSearchService;
	@Mock(lenient = true)
	private MediaPersistenceService mediaPersistenceService;
	@Mock(lenient = true)
	private OutboundServicesConfiguration outboundServicesConfiguration;
	@InjectMocks
	private DefaultOutboundRequestResponseInterceptor interceptor;
	@Mock(lenient = true)
	private ClientHttpRequestExecution requestExecution;
	@Mock(lenient = true)
	private HttpRequest httpRequest;
	@Mock(lenient = true)
	private OutboundRequestModel outboundRequestModel;
	@Mock(lenient = true)
	private ClientHttpResponse httpResponse;
	@Mock(lenient = true)
	private OutboundRequestMediaModel requestMediaModel;
	@Mock(lenient = true)
	private MonitoredRequestErrorParser<MonitoredRequestErrorModel> errorParser;
	@Mock(lenient = true)
	private MonitoredRequestErrorParser<MonitoredRequestErrorModel> fallbackErrorParser;
	@Mock(lenient = true)
	private MonitoredRequestErrorParser<MonitoredRequestErrorModel> exceptionErrorParser;
	@Mock(lenient = true)
	private MonitoredRequestErrorModel errorModel;
	
	@Before
	public void setUp() throws IOException
	{
		lenient().when(httpRequest.getHeaders()).thenReturn(httpHeadersRequest);
		lenient().when(modelService.create(OutboundRequestModel.class)).thenReturn(outboundRequestModel);

		final SearchResult<OutboundRequestModel> result = searchResult(outboundRequestModel);
		doReturn(result).when(flexibleSearchService).search(anyString(), anyMap());
		lenient().when(requestExecution.execute(any(), any())).thenReturn(httpResponse);
		lenient().when(outboundServicesConfiguration.getRequestExecutionTimeout()).thenReturn(TIMEOUT);
		lenient().when(mediaPersistenceService.persistMedias(any(), any())).thenReturn(Collections.singletonList(requestMediaModel));
		lenient().when(httpResponse.getHeaders()).thenReturn(httpHeadersResponse);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(200);
		lenient().when(httpResponse.getBody()).thenReturn(new ByteArrayInputStream("{ error = 'error' }".getBytes()));

		lenient().when(errorModel.getMessage()).thenReturn(ERROR_MESSAGE);
		lenient().when(errorParser.isApplicable(any(), anyInt())).thenReturn(true);
		lenient().when(errorParser.parseErrorFrom(any(), anyInt(), any())).thenReturn(errorModel);

		lenient().when(fallbackErrorParser.isApplicable(any(), anyInt())).thenReturn(true);
		lenient().when(fallbackErrorParser.parseErrorFrom(any(), anyInt(), any())).thenReturn(errorModel);

		lenient().when(exceptionErrorParser.isApplicable(any(), anyInt())).thenReturn(true);
		lenient().when(exceptionErrorParser.parseErrorFrom(any(), anyInt(), any())).thenReturn(errorModel);

		errorParsers.add(errorParser);
		interceptor.setErrorParsers(errorParsers);

		lenient().when(outboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).thenReturn(true);
		lenient().when(outboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()).thenReturn(true);
		lenient().when(outboundServicesConfiguration.isMonitoringEnabled()).thenReturn(true);
		lenient().when(outboundServicesConfiguration.getMaximumResponsePayloadSize()).thenReturn(1024);
	}

	@Test
	public void testIntercept_success() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(200);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.SUCCESS);
		verify(outboundRequestModel).setPayload(requestMediaModel);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());

		assertThat(response).isSameAs(httpResponse);
	}

	@Test
	public void testIntercept_success_successRetentionFalse() throws IOException
	{
		lenient().when(outboundServicesConfiguration.isPayloadRetentionForSuccessEnabled()).thenReturn(false);

		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(200);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.SUCCESS);
		verify(outboundRequestModel, never()).setPayload(any());
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService, never()).persistMedias(any(), any());

		assertThat(response).isSameAs(httpResponse);
	}

	@Test
	public void testIntercept_error_fallbackErrorParser() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel).setPayload(requestMediaModel);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(fallbackErrorParser).parseErrorFrom(MonitoredRequestErrorModel.class, 400, RESPONSE_PAYLOAD);
		assertThat(response).isNotSameAs(httpResponse); // response was wrapped into another class.
	}

	@Test
	public void testIntercept_error_errorParser() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel).setPayload(requestMediaModel);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(errorParser).parseErrorFrom(MonitoredRequestErrorModel.class, 400, RESPONSE_PAYLOAD);
		assertThat(response).isNotSameAs(httpResponse); // response was wrapped into another class.
	}

	@Test
	public void testIntercept_error_errorParsersNotApplicable() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);
		lenient().when(errorParser.isApplicable(any(), anyInt())).thenReturn(false);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel).setPayload(requestMediaModel);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(fallbackErrorParser).parseErrorFrom(MonitoredRequestErrorModel.class, 400, RESPONSE_PAYLOAD);
		assertThat(response).isNotSameAs(httpResponse); // response was wrapped into another class.
	}

	@Test
	public void testSettingErrorParsersToNullResultsInEmptyList()
	{
		interceptor.setErrorParsers(null);

		assertThat(interceptor.getErrorParsers()).isEmpty();
	}

	@Test
	public void testSettingErrorParsersDoesNotLeakReferences()
	{
		final List<MonitoredRequestErrorParser<MonitoredRequestErrorModel>> errorParsers = new ArrayList<>();
		errorParsers.add(errorParser);

		interceptor.setErrorParsers(errorParsers);
		errorParsers.clear();

		assertThat(interceptor.getErrorParsers()).isNotEmpty();
	}

	@Test
	public void testGettingErrorParsersDoesNotLeakReferences()
	{
		final List<MonitoredRequestErrorParser<MonitoredRequestErrorModel>> errorParsers = new ArrayList<>();
		errorParsers.add(errorParser);
		interceptor.setErrorParsers(errorParsers);

		final List<MonitoredRequestErrorParser<MonitoredRequestErrorModel>> copy = interceptor.getErrorParsers();
		copy.clear();

		assertThat(interceptor.getErrorParsers()).isNotEmpty();
	}

	@Test
	public void testIntercept_error_errorRetentionFalse() throws IOException
	{
		lenient().when(outboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).thenReturn(false);

		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel, never()).setPayload(any());
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService, never()).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(errorParser).parseErrorFrom(MonitoredRequestErrorModel.class, 400, RESPONSE_PAYLOAD);
		assertThat(response).isNotSameAs(httpResponse); // response was wrapped into another class.
	}

	@Test
	public void testInterceptIntegrationTimeoutExceptionWhenIssuingRequest() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		lenient().when(requestExecution.execute(any(), any())).thenThrow(new SocketTimeoutException("readTimeout has occurred"));

		assertThatThrownBy(() -> interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution))
				.isInstanceOf(OutboundRequestTimeoutException.class)
				.hasMessage("Request timed out after 10 ms.");

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel).setPayload(requestMediaModel);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(exceptionErrorParser).parseErrorFrom(MonitoredRequestErrorModel.class, -1, "Request timed out after 10 ms.");
	}

	@Test
	public void testInterceptIntegrationTimeoutExceptionWhenIssuingRequestErrorRetentionFalse() throws IOException
	{
		lenient().when(outboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).thenReturn(false);

		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		lenient().when(requestExecution.execute(any(), any())).thenThrow(new SocketTimeoutException("readTimeout has occurred"));

		assertThatThrownBy(() -> interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution))
				.isInstanceOf(OutboundRequestTimeoutException.class)
				.hasMessage("Request timed out after 10 ms.");

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel, never()).setPayload(any());  // No payload generated
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService, never()).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(exceptionErrorParser).parseErrorFrom(MonitoredRequestErrorModel.class, -1, "Request timed out after 10 ms.");
	}
	
	@Test
	public void testIOExceptionWhenIssuingRequest() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		lenient().when(requestExecution.execute(any(), any())).thenThrow(new IOException("IOException msg"));

		assertThatThrownBy(() -> interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution))
				.isInstanceOf(OutboundRequestExecutionException.class)
				.hasMessage("Request encountered an exception.");

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel).setPayload(requestMediaModel);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(exceptionErrorParser).parseErrorFrom(MonitoredRequestErrorModel.class, -1, "Request encountered an exception.");
	}

	@Test
	public void testIOExceptionWhenIssuingRequestErrorRetentionFalse() throws IOException
	{
		lenient().when(outboundServicesConfiguration.isPayloadRetentionForErrorEnabled()).thenReturn(false);

		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		lenient().when(requestExecution.execute(any(), any())).thenThrow(new IOException("IOException msg"));

		assertThatThrownBy(() -> interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution))
				.isInstanceOf(OutboundRequestExecutionException.class)
				.hasMessage("Request encountered an exception.");

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.ERROR);
		verify(outboundRequestModel, never()).setPayload(any());  // No payload generated
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService, never()).persistMedias(any(), any());
		verify(outboundRequestModel).setError(ERROR_MESSAGE);
		verify(exceptionErrorParser).parseErrorFrom(MonitoredRequestErrorModel.class, -1, "Request encountered an exception.");
	}

	@Test
	public void testIntercept_noMessageIdPresent() throws IOException
	{
		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		assertThat(response.getRawStatusCode()).isEqualTo(200);
		verify(outboundRequestModel, never()).setStatus(any());
		verify(outboundRequestModel, never()).setPayload(any());
	}

	@Test
	public void testIntercept_noMessageIdPresent_inCaseOfEmptyValue() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, "");
		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		assertThat(response.getRawStatusCode()).isEqualTo(200);
		verify(outboundRequestModel, never()).setStatus(any());
		verify(outboundRequestModel, never()).setPayload(any());
	}

	@Test
	public void testIntercept_noPayloadGenerated_emptyList() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(mediaPersistenceService.persistMedias(any(), any())).thenReturn(Collections.emptyList());

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.SUCCESS);
		verify(outboundRequestModel).setPayload(null);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());

		assertThat(response).isSameAs(httpResponse);
	}

	@Test
	public void testIntercept_noPayloadGenerated_null() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(mediaPersistenceService.persistMedias(any(), any())).thenReturn(null);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setStatus(IntegrationRequestStatus.SUCCESS);
		verify(outboundRequestModel).setPayload(null);
		verify(modelService).save(outboundRequestModel);
		verify(mediaPersistenceService).persistMedias(any(), any());

		assertThat(response).isSameAs(httpResponse);
	}

	@Test
	public void testIntercept_noOutboundRequestModelPresent() throws IOException
	{
		final SearchResult<OutboundRequestModel> result = searchResult();
		doReturn(result).when(flexibleSearchService).search(anyString(), anyMap());
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verifyNoMoreInteractions(modelService);
		verifyNoMoreInteractions(mediaPersistenceService);

		assertThat(response).isSameAs(httpResponse);
	}

	@Test
	public void testIntercept_multipleOutboundRequestModelsPresent() throws IOException
	{
		final SearchResult<OutboundRequestModel> result = searchResult(outboundRequestModel,
				Mockito.mock(OutboundRequestModel.class));
		doReturn(result).when(flexibleSearchService).search(anyString(), anyMap());
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);

		final ClientHttpResponse response = interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verifyNoMoreInteractions(modelService);
		verifyNoMoreInteractions(mediaPersistenceService);

		assertThat(response).isSameAs(httpResponse);
	}

	@Test
	public void test_returnPayloadExceedsMaxSize_NoContentLengthHeader() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);
		responsePayloadBiggerThanMaxAllowedSize();

		interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setError("The error response message exceeded the allowed size");
	}

	@Test
	public void test_returnPayloadExceedsMaxSize_ContentLengthHeaderPresent() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(outboundServicesConfiguration.getMaximumResponsePayloadSize()).thenReturn(2);
		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.CONTENT_LENGTH,"3");
		lenient().when(httpResponse.getHeaders()).thenReturn(responseHeaders);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);

		interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setError("The error response message exceeded the allowed size");
	}

	@Test
	public void test_returnPayloadExceedsMaxSize_ContentLengthAndTransferEncodingHeaderPresent() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		final HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.CONTENT_LENGTH,"50");
		responseHeaders.add(HttpHeaders.TRANSFER_ENCODING,"anyValue");
		lenient().when(httpResponse.getHeaders()).thenReturn(responseHeaders);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);
		responsePayloadBiggerThanMaxAllowedSize();

		interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setError("The error response message exceeded the allowed size");
	}

	@Test
	public void test_returnPayloadEqualsMaxSize_NoContentLenghHeader() throws IOException
	{
		httpHeadersRequest.add(OUTBOUND_MONITORING_MESSAGE_ID_HEADER_NAME, MY_MESSAGE_ID);
		lenient().when(httpResponse.getRawStatusCode()).thenReturn(400);
		responsePayloadEqualsThanMaxAllowedSize();

		interceptor.intercept(httpRequest, REQUEST_PAYLOAD_BYTES, requestExecution);

		verify(outboundRequestModel).setError(ERROR_MESSAGE);
	}

	private void responsePayloadBiggerThanMaxAllowedSize() throws IOException
	{
		lenient().when(outboundServicesConfiguration.getMaximumResponsePayloadSize()).thenReturn(2);
		lenient().when(httpResponse.getBody()).thenReturn(new ByteArrayInputStream("abc".getBytes()));
	}

	private void responsePayloadEqualsThanMaxAllowedSize() throws IOException
	{
		lenient().when(outboundServicesConfiguration.getMaximumResponsePayloadSize()).thenReturn(2);
		lenient().when(httpResponse.getBody()).thenReturn(new ByteArrayInputStream("ab".getBytes()));
	}

	private SearchResult<OutboundRequestModel> searchResult(final OutboundRequestModel... models)
	{
		final SearchResult<OutboundRequestModel> result = Mockito.mock(SearchResult.class);
		lenient().when(result.getResult()).thenReturn(new ArrayList<>(Arrays.asList(models)));
		return result;
	}
}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customerticketingc4cintegration.facade;

import static io.netty.util.internal.StringUtil.EMPTY_STRING;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerticketingc4cintegration.SitePropsHolder;
import de.hybris.platform.customerticketingc4cintegration.constants.Customerticketingc4cintegrationConstants;
import de.hybris.platform.customerticketingc4cintegration.data.Contact;
import de.hybris.platform.customerticketingc4cintegration.data.IndividualCustomer;
import de.hybris.platform.customerticketingc4cintegration.data.MemoActivity;
import de.hybris.platform.customerticketingc4cintegration.data.Note;
import de.hybris.platform.customerticketingc4cintegration.data.RelatedTransaction;
import de.hybris.platform.customerticketingc4cintegration.data.ServiceRequestData;
import de.hybris.platform.customerticketingc4cintegration.exception.C4CServiceException;
import de.hybris.platform.customerticketingc4cintegration.facade.utils.HttpHeaderUtil;
import de.hybris.platform.customerticketingc4cintegration.service.C4CCustomerService;
import de.hybris.platform.customerticketingc4cintegration.service.C4CServiceRequestService;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.paginated.util.PaginatedSearchUtils;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;


/**
 * Test cases for {@link C4CTicketFacadeImpl}
 */
@UnitTest
public class C4CTicketFacadeImplTest extends ServicelayerTest
{
	@InjectMocks
	@Spy
	private C4CTicketFacadeImpl c4CTicketFacade;

	@Mock
	private C4CBaseFacade c4cBaseFacade;

	@Mock
	private C4CCustomerService c4CCustomerService;
	@Mock
	private C4CServiceRequestService c4CServiceRequestService;
	@Mock
	private CustomerService customerService;
	@Mock
	private ModelService modelService;

	@Mock
	private RestTemplate restTemplate;
	@Mock
	private SitePropsHolder sitePropsHolder;
	@Mock
	private CustomerFacade customerFacade;
	@Mock
	private StatusData completedStatus;
	@Mock
	private ObjectMapper jacksonObjectMapper;

	@InjectMocks
	private HttpHeaderUtil httpHeaderUtilMock;

	@Mock
	private HttpHeaderUtil httpHeaderUtil;

	@Mock
	private Converter<ServiceRequestData, TicketData> ticketConverter;
	@Mock
	private Converter<TicketData, ServiceRequestData> defaultC4CTicketConverter;
	@Mock
	private Converter<TicketData, Note> updateMessageConverter;
	@Mock
	private Converter<TicketData, MemoActivity> memoActivityConverter;
	@Mock
	private Converter<MemoActivity, ServiceRequestData> relatedTransactionConverter;
	@Mock
	private SessionService sessionService;
	@Mock
	private Session session;

	private static final String COMPLETED = "COMPLETED";
	private static final String OPEN = "OPEN";
	private static final String SUBJECT = "subject";
	private static final String MESSAGE = "message";
	private static final String RESPONSE_BODY = "response body";
	private static final String CUSTOMER_ID = "customerId";
	private static final String C4C_CUSTOMER_ID = "c4cCustomerId";
	private static final String C4C_CONTACT_ID = "c4cContactId";
	private static final String JSON_STRING = "jsonString";
	private static final String TICKET_ID = "ticketId";
	private static final String SITE_ID = "siteId";
	private static final String EXTERNAL_ID = "externalId";
	private static final String C4C_BUYER_ID = "buyerId";
	private static final String C4C_BUYER_MAIN_CONTACT_ID = "buyerMainContactId";

	/**
	 * Test setup.
	 */
	@Before
	public void setup() throws C4CServiceException {
		Config.setParameter("customerticketingc4cintegration.c4c-url", "http://127.0.0.1"); // NOSONAR
		Config.setParameter("customerticketingc4cintegration.c4c-username", "username");
		Config.setParameter("customerticketingc4cintegration.c4c-password", "password");

		MockitoAnnotations.initMocks(this);

		final IndividualCustomer individualCustomer = new IndividualCustomer();
		individualCustomer.setCustomerID(C4C_CUSTOMER_ID);
		individualCustomer.setExternalID(CUSTOMER_ID);
		when(c4CCustomerService.getIndividualCustomerByExternalId(eq(CUSTOMER_ID))).thenReturn(individualCustomer);

		final Contact contact = new Contact();
		contact.setContactID(C4C_CONTACT_ID);
		contact.setExternalID(CUSTOMER_ID);
		when(c4CCustomerService.getContactByExternalId(eq(CUSTOMER_ID))).thenReturn(contact);

		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		when(customerService.getCustomerByCustomerId(anyString())).thenReturn(customerModel);
		when(customerModel.getC4cBuyerId()).thenReturn(C4C_CUSTOMER_ID);

		doNothing().when(modelService).save(any());

		final CustomerData customer = Mockito.mock(CustomerData.class);
		when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);
		when(customerFacade.getCurrentCustomer()).thenReturn(customer);
	}

	/**
	 * Test {@link C4CTicketFacadeImpl#createTicket(TicketData)} Should create a ticket.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldCreateATicket() throws C4CServiceException {
		// Prepare input
		final TicketData ticketData = new TicketData();
		ticketData.setSubject(SUBJECT);
		ticketData.setMessage(MESSAGE);

		// Mock C4C party ID
		CustomerModel customerModel = mock(CustomerModel.class);
		when(customerModel.getC4cBuyerId()).thenReturn(C4C_CUSTOMER_ID);
		when(customerService.getCustomerByCustomerId(anyString())).thenReturn(customerModel);

		// Mock Service Request converter
		ServiceRequestData serviceRequest = mock(ServiceRequestData.class);
		when(defaultC4CTicketConverter.convert(ticketData)).thenReturn(serviceRequest);

		MemoActivity memoRequest = mock(MemoActivity.class);
		when(memoActivityConverter.convert(ticketData)).thenReturn(memoRequest);

		when(c4CServiceRequestService.createMemoActivity(memoRequest)).thenReturn(memoRequest);

		when(relatedTransactionConverter.convert(memoRequest, serviceRequest)).thenReturn(serviceRequest);

		// Mock API call
		when(c4CServiceRequestService.createServiceRequest(serviceRequest)).thenReturn(serviceRequest);

		doNothing().when(serviceRequest).setMemoActivities(any());

		// Mock response converter
		when(ticketConverter.convert(serviceRequest)).thenReturn(ticketData);

		// Actual call
		TicketData response = c4CTicketFacade.createTicket(ticketData);

		// Verify
		Assert.assertEquals(ticketData, response);


	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#createTicket(TicketData)} throws runtime exp. when RestClientException happens
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnRuntimeExceptionWhenThrowRestClientException() throws C4CServiceException {
		// Prepare input
		final TicketData ticketData = new TicketData();
		ticketData.setSubject(SUBJECT);
		ticketData.setMessage(MESSAGE);

		// Mock C4C party ID
		CustomerModel customerModel = mock(CustomerModel.class);
		when(customerModel.getC4cBuyerId()).thenReturn(C4C_CUSTOMER_ID);
		when(customerService.getCustomerByCustomerId(anyString())).thenReturn(customerModel);

		// Mock Service Request converter
		ServiceRequestData serviceRequest = mock(ServiceRequestData.class);
		when(defaultC4CTicketConverter.convert(ticketData)).thenReturn(serviceRequest);

		MemoActivity memoRequest = mock(MemoActivity.class);
		when(memoActivityConverter.convert(ticketData)).thenReturn(memoRequest);

		when(c4CServiceRequestService.createMemoActivity(memoRequest)).thenReturn(memoRequest);

		when(relatedTransactionConverter.convert(memoRequest, serviceRequest)).thenReturn(serviceRequest);

		doNothing().when(serviceRequest).setMemoActivities(any());

		doThrow(C4CServiceException.class).when(c4CServiceRequestService).createServiceRequest(serviceRequest);

		try {
			// Actual call
			c4CTicketFacade.createTicket(ticketData);
			Assert.fail("RuntimeException is expected before this point!");
		}
		catch (final RuntimeException expectedException) {
			// Verify
			Assert.assertEquals("check error message", "Can't send request", expectedException.getMessage());
		}
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicket(String)} should get a ticket DTO by given ticket id.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldGetTicket() throws C4CServiceException {

	        String ticketObjectId = "ticketObjectId";
		ServiceRequestData serviceRequest = new ServiceRequestData();
		serviceRequest.setObjectID(TICKET_ID);
		serviceRequest.setStatusCode(OPEN);
		serviceRequest.setBuyerPartyID(C4C_CUSTOMER_ID);

		List<MemoActivity> memos = new ArrayList<MemoActivity>();
		when(c4CServiceRequestService.getServiceRequest(anyString())).thenReturn(serviceRequest);
		when(c4CServiceRequestService.getMemoActivities(anyString())).thenReturn(memos);

		when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		TicketData ticketData = mock(TicketData.class);
		when(ticketData.getId()).thenReturn(TICKET_ID);
		when(ticketData.getId()).thenReturn(OPEN);
		when(ticketData.getCustomerId()).thenReturn(C4C_CUSTOMER_ID);

		Map<String, String> ticketIdObjectIdMap = new HashMap<>();
		ticketIdObjectIdMap.put(TICKET_ID, ticketObjectId);
		when(sessionService.getCurrentSession()).thenReturn(session);
		when(session.getAttribute(any())).thenReturn(ticketIdObjectIdMap);
		doNothing().when(session).setAttribute(any(), any());

		when(ticketConverter.convert(any())).thenReturn(ticketData);

		// Actual Call
		TicketData result = c4CTicketFacade.getTicket(TICKET_ID);

		Assert.assertEquals("check response", ticketData, result);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicket(String)} Should return runtime exception when something goes wrong
	 *
	 * @throws IOException
	 */
	@Test(expected = RuntimeException.class)
	public void shouldReturnRuntimeExceptionWhenGetTicketThrowIOException() throws C4CServiceException {
		doThrow(C4CServiceException.class).when(c4CServiceRequestService.getServiceRequest(anyString()));

		// Actual Call
		c4CTicketFacade.getTicket(TICKET_ID);
		Assert.fail("RuntimeException is expected before this point!");
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicket(String)} Should return runtime exception when something goes wrong
	 *
	 * @throws IOException
	 * @throws RuntimeException
	 */
	@Test(expected = RuntimeException.class)
	public void shouldReturnRuntimeExceptionWhenGetTicketThrowRestClientException() throws C4CServiceException {

		doThrow(C4CServiceException.class).when(c4CServiceRequestService.getServiceRequest(anyString()));

		c4CTicketFacade.getTicket(TICKET_ID);

	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTickets(PageableData)} should return a SearchPageData<TicketData> result.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldGetTickets() throws C4CServiceException {

		final int pageSize = 1;
		final int currentPage = 1;
		final String sort = EMPTY_STRING;
		String ticketId = "ticketId";
		String ticketObjectId = "00163EA739191EDBB1EFD2E8E13A03AB";

		when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		ServiceRequestData serviceRequestData = Mockito.mock(ServiceRequestData.class);
		when(serviceRequestData.getBuyerMainContactPartyID()).thenReturn(C4C_BUYER_MAIN_CONTACT_ID);
		when(serviceRequestData.getBuyerPartyID()).thenReturn(C4C_BUYER_ID);
		when(serviceRequestData.getID()).thenReturn(ticketId);
		when(serviceRequestData.getObjectID()).thenReturn(ticketObjectId);
		when(sessionService.getCurrentSession()).thenReturn(session);
		doNothing().when(session).setAttribute(any(), any());

		de.hybris.platform.core.servicelayer.data.SearchPageData<ServiceRequestData> searchPageData = PaginatedSearchUtils.createSearchPageDataWithPagination(pageSize, currentPage, true);
		List<ServiceRequestData> results = Lists.newArrayList(serviceRequestData);
		searchPageData.setResults(results);
		searchPageData.getPagination().setTotalNumberOfResults(1);
		when(c4CServiceRequestService.getServiceRequestsByBuyerPartyID(eq(C4C_CUSTOMER_ID), eq(pageSize), eq(currentPage), anyString()))
				.thenReturn(searchPageData);

		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(pageSize);
		pageableData.setCurrentPage(currentPage);
		pageableData.setSort(sort);

		final TicketData convertedTicketData = new TicketData();
		when(ticketConverter.convert(any())).thenReturn(convertedTicketData);

		final SearchPageData mockedResults = Mockito.mock(SearchPageData.class);
		when(c4cBaseFacade.convertPageData(results, ticketConverter, pageableData, 1))
				.thenReturn(mockedResults);
		final SearchPageData<TicketData> tickets = c4CTicketFacade.getTickets(pageableData);


		Assert.assertEquals(mockedResults, tickets);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTickets(PageableData)} should return an empty result list if throw
	 * IOException.
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnEmptySearchResultWhenGetTicketsThrowRestClientException() throws C4CServiceException {

		final CustomerData customer = Mockito.mock(CustomerData.class);
		when(customerFacade.getCurrentCustomer()).thenReturn(customer);
		when(customer.getCustomerId()).thenReturn(CUSTOMER_ID);

		final CustomerModel customerModel = mock(CustomerModel.class);
		when(customerModel.getC4cBuyerId()).thenReturn(C4C_CUSTOMER_ID);
		when(customerService.getCustomerByCustomerId(anyString())).thenReturn(customerModel);

		when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);

		doThrow(C4CServiceException.class).when(c4CServiceRequestService).getServiceRequestsByBuyerPartyID(anyString(), anyInt(), anyInt(), anyString());

		final SearchPageData mockedEmptyResults = mock(SearchPageData.class);
		final PageableData pageableData = new PageableData();
		pageableData.setPageSize(1);
		pageableData.setCurrentPage(1);
		pageableData.setSort(StringUtils.EMPTY);
		when(c4cBaseFacade.convertPageData(Collections.emptyList(), ticketConverter, pageableData, 0))
				.thenReturn(mockedEmptyResults);

		c4CTicketFacade.getTickets(pageableData);
		Mockito.verify(c4CTicketFacade).getTickets(pageableData);

	}

	/**
	 * Test of {@link HttpHeaderUtil#createBasicAuthHeader(String, String)} Should create a correct basic auth
	 * header.
	 */
	@Test
	public void shouldCreateBasicAuthHeader()
	{
		httpHeaderUtil = new HttpHeaderUtil();
		final String basicAuthHeader = httpHeaderUtil.createBasicAuthHeader("username", "password");
		Assert.assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", basicAuthHeader);
	}

	/**
	 * Test of {@link HttpHeaderUtil#getDefaultHeaders(String)} should get a correct default header by given site
	 * id.
	 */
	@Test
	public void shouldGetDefaultHeaders()
	{
		httpHeaderUtil = new HttpHeaderUtil();
		c4CTicketFacade = new C4CTicketFacadeImpl();
		final HttpHeaders headers = httpHeaderUtil.getDefaultHeaders(SITE_ID);

		Assert.assertEquals(Arrays.asList(SITE_ID), headers.get(Customerticketingc4cintegrationConstants.SITE_HEADER));
		Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
		Assert.assertEquals(Arrays.asList(Customerticketingc4cintegrationConstants.ACCEPT), headers.get(HttpHeaders.ACCEPT));
		Assert.assertEquals(Arrays.asList("Basic dXNlcm5hbWU6cGFzc3dvcmQ="), headers.get(HttpHeaders.AUTHORIZATION));
		Assert.assertEquals(Arrays.asList(Customerticketingc4cintegrationConstants.TOKEN_EMPTY),
				headers.get(Customerticketingc4cintegrationConstants.TOKEN_NAMING));
	}

	/**
	 * Test of {@link HttpHeaderUtil#getEnrichedHeaders()} should return an enriched header.
	 */
	@Test
	public void shouldGetEnrichedHeaders()
	{

		final HttpHeaders mockHeaders = Mockito.mock(HttpHeaders.class);
		final HttpHeaders mockHttpHeaders = Mockito.mock(HttpHeaders.class);
		httpHeaderUtilMock = new HttpHeaderUtil()
		{
			@Override
			public HttpHeaders enrichHeaders(final HttpHeaders headers, final String siteId)
			{
				return mockHeaders;
			}

			@Override
			public HttpHeaders getDefaultHeaders(final String siteId)
			{
				return mockHttpHeaders;
			}
		};

		MockitoAnnotations.initMocks(this);

		when(sitePropsHolder.getSiteId()).thenReturn(SITE_ID);
		final HttpHeaders enrichedHeaders = httpHeaderUtilMock.getEnrichedHeaders();

		Mockito.verify(sitePropsHolder).getSiteId();
		Assert.assertEquals(mockHeaders, enrichedHeaders);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getAssociatedToObjects()} Should throw UnsupportedOperationException.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void shouldGetAssociatedToObjectsThrowUnsupportedOperationException()
	{
		c4CTicketFacade = new C4CTicketFacadeImpl();
		c4CTicketFacade.getAssociatedToObjects();
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#getTicketCategories()} Should throw UnsupportedOperationException.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void shouldGetTicketCategoriesThrowUnsupportedOperationException()
	{
		c4CTicketFacade = new C4CTicketFacadeImpl();
		c4CTicketFacade.getTicketCategories();
	}

	/**
	 * Test o f{@link HttpHeaderUtil#addBatchHeaders(String)} should return http headers which populated correctly
	 * data.
	 */
	@Test
	public void shouldAddBatchHeaders()
	{
		c4CTicketFacade = new C4CTicketFacadeImpl();
		httpHeaderUtil = new HttpHeaderUtil();
		final HttpHeaders headers = httpHeaderUtil.addBatchHeaders("test-url");

		Assert.assertEquals(Arrays.asList("test-url"), headers.get(""));
		Assert.assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
		Assert.assertTrue(CollectionUtils.isNotEmpty(headers.get(Customerticketingc4cintegrationConstants.CONTENT_ID)));
		Assert.assertTrue(headers.get(Customerticketingc4cintegrationConstants.CONTENT_ID).get(0)
				.startsWith(Customerticketingc4cintegrationConstants.CONTENT_ID_VALUE_PREFIX));
	}

	/**
	 * Test of {@link HttpHeaderUtil#enrichHeaders(HttpHeaders, String)} should return the enriched http headers
	 * with required data.
	 */
	@Test
	public void shouldEnrichHeaders()
	{
		final HttpHeaders mockHttpHeaders = new HttpHeaders();
		httpHeaderUtilMock = new HttpHeaderUtil()
		{
			@Override
			public HttpHeaders getDefaultHeaders(final String siteId)
			{
				return mockHttpHeaders;
			}
		};
		MockitoAnnotations.initMocks(this);

		final ResponseEntity responseEntity = Mockito.mock(ResponseEntity.class);
		final HttpEntity<String> entity = new HttpEntity<>(mockHttpHeaders);
		when(
				restTemplate.exchange(Customerticketingc4cintegrationConstants.URL
						+ Customerticketingc4cintegrationConstants.TICKETING_SUFFIX
						+ Customerticketingc4cintegrationConstants.TOKEN_URL_SUFFIX, HttpMethod.GET, entity, String.class)).thenReturn(
				responseEntity);

		final HttpHeaders returnedHttpHeader = new HttpHeaders();
		when(responseEntity.getHeaders()).thenReturn(returnedHttpHeader);
		returnedHttpHeader.put(Customerticketingc4cintegrationConstants.RESPONSE_COOKIE_NAME, Arrays.asList("response-cookie-name"));
		returnedHttpHeader.put(Customerticketingc4cintegrationConstants.TOKEN_NAMING, Arrays.asList("token-naming"));

		final HttpHeaders headers = new HttpHeaders();
		final HttpHeaders enrichedHeaders = httpHeaderUtilMock.enrichHeaders(headers, SITE_ID);

		Mockito.verify(restTemplate).exchange(
				Customerticketingc4cintegrationConstants.URL + Customerticketingc4cintegrationConstants.TICKETING_SUFFIX
						+ Customerticketingc4cintegrationConstants.TOKEN_URL_SUFFIX, HttpMethod.GET, entity, String.class);

		Assert.assertEquals("response-cookie-name", enrichedHeaders.get(HttpHeaders.COOKIE).get(0));
		Assert.assertEquals("token-naming", enrichedHeaders.get(Customerticketingc4cintegrationConstants.TOKEN_NAMING).get(0));
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#updateTicket(TicketData)} should throw IllegalArgumentException when ticket is
	 * completed status.
	 *
	 */
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrownIllegalArgumentExceptionWhenUpdateTicketWithCompletedStatus() throws C4CServiceException {
		final StatusData completedStatus = new StatusData();
		completedStatus.setId(COMPLETED);

		// Mock C4C party ID
		CustomerModel customerModel = mock(CustomerModel.class);
		when(customerModel.getC4cBuyerId()).thenReturn(C4C_CUSTOMER_ID);
		when(customerService.getCustomerByCustomerId(anyString())).thenReturn(customerModel);

		// Mock Get Ticket
		TicketData ticket = Mockito.mock(TicketData.class);
		when(ticket.getId()).thenReturn(TICKET_ID);
		when(ticket.getStatus()).thenReturn(completedStatus);

		ServiceRequestData serviceRequest = new ServiceRequestData();
		serviceRequest.setObjectID(TICKET_ID);
		serviceRequest.setStatusCode(COMPLETED);
		serviceRequest.setBuyerPartyID(C4C_CUSTOMER_ID);

		when(defaultC4CTicketConverter.convert(ticket)).thenReturn(serviceRequest);
		doNothing().when(c4CServiceRequestService).updateTicket(serviceRequest);
		when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);
		when(c4CServiceRequestService.getServiceRequest(anyString())).thenReturn(serviceRequest);
		when(ticketConverter.convert(serviceRequest)).thenReturn(ticket);

		// Actual Call
		c4CTicketFacade.updateTicket(ticket);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#updateTicket(TicketData)} should update the ticket when the status is open.
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void shouldUpdateTicketWithOpenStatus() throws C4CServiceException {

		final String message = "Some Text";
		final String ticketId = "ticketId";
		final String ticketObjectId = "ticketObjectId";
		final StatusData openStatus = new StatusData();
		openStatus.setId(OPEN);
		final StatusData completedStatus = new StatusData();
		completedStatus.setId(COMPLETED);

		final TicketData remoteTicket = new TicketData();
		remoteTicket.setStatus(completedStatus);
		remoteTicket.setId(ticketId);
		remoteTicket.setMessage(message);

		//when(c4CTicketFacade.getTicket(anyString())).thenReturn(remoteTicket);

		Note note = new Note();
		note.setText(remoteTicket.getMessage());
		when(updateMessageConverter.convert(remoteTicket)).thenReturn(note);
		doNothing().when(c4CServiceRequestService).createNote(note);

		// Prepare Input
		TicketData ticket = Mockito.mock(TicketData.class);
		when(ticket.getMessage()).thenReturn(message);
		when(ticket.getId()).thenReturn(ticketId);
		when(ticket.getStatus()).thenReturn(openStatus);

		ServiceRequestData serviceRequest = new ServiceRequestData();
		serviceRequest.setObjectID(ticketId);
		serviceRequest.setStatusCode(OPEN);
		serviceRequest.setBuyerPartyID(C4C_CUSTOMER_ID);
		RelatedTransaction rt = new RelatedTransaction();
		serviceRequest.setRelatedTransactions(Collections.singletonList(rt));
		when(defaultC4CTicketConverter.convert(ticket)).thenReturn(serviceRequest);
		doNothing().when(c4CServiceRequestService).updateTicket(serviceRequest);

		MemoActivity memo = new MemoActivity();
		when(memoActivityConverter.convert(ticket)).thenReturn(memo);
		when(c4CServiceRequestService.createMemoActivity(memo)).thenReturn(memo);
		when(relatedTransactionConverter.convert(memo)).thenReturn(serviceRequest);
		doNothing().when(c4CServiceRequestService).updateTicketWithMemoActivity(rt);

		// Mock Get Ticket
		when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);
		when(c4CServiceRequestService.getServiceRequest(anyString())).thenReturn(serviceRequest);
		when(ticketConverter.convert(serviceRequest)).thenReturn(ticket);
		
		Map<String,String> ticketIdObjectIdMap = new HashMap<>();
		ticketIdObjectIdMap.put(ticketId, ticketObjectId);
		when(sessionService.getCurrentSession()).thenReturn(session);
		when(session.getAttribute(any())).thenReturn(ticketIdObjectIdMap);
		doNothing().when(session).setAttribute(any(), any());

		// Actual Call
		TicketData responseTicketData = c4CTicketFacade.updateTicket(ticket);

		// Verify
		System.out.println("Sagar Remote Status: "+", "+remoteTicket.getStatus().getId());
		Assert.assertEquals(responseTicketData.getStatus(), openStatus);
	}

	/**
	 * Test of {@link C4CTicketFacadeImpl#updateTicket(TicketData)} should return null if update ticket with open status
	 * bu the response contains errors.
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void shouldReturnNullIfUpdateTicketWithOpenStatusResponseContainsError() throws JsonProcessingException, C4CServiceException {
		final String message = "Some Message";
		final String ticketObjectId = "ticketObjectId";
		final StatusData openStatus = new StatusData();
		openStatus.setId(OPEN);

		// Mock C4C party ID
		CustomerModel customerModel = mock(CustomerModel.class);
		when(customerModel.getC4cBuyerId()).thenReturn(C4C_CUSTOMER_ID);
		when(customerService.getCustomerByCustomerId(anyString())).thenReturn(customerModel);

		// Mock Get Ticket
		TicketData ticket = Mockito.mock(TicketData.class);
		when(ticket.getMessage()).thenReturn(message);
		when(ticket.getId()).thenReturn(TICKET_ID);
		when(ticket.getStatus()).thenReturn(openStatus);

		Map<String, String> ticketIdObjectIdMap = new HashMap<>();
		ticketIdObjectIdMap.put(TICKET_ID, ticketObjectId);
		when(sessionService.getCurrentSession()).thenReturn(session);
		when(session.getAttribute(any())).thenReturn(ticketIdObjectIdMap);
		doNothing().when(session).setAttribute(any(), any());

		ServiceRequestData serviceRequest = new ServiceRequestData();
		serviceRequest.setObjectID(TICKET_ID);
		serviceRequest.setStatusCode(OPEN);
		serviceRequest.setBuyerPartyID(C4C_CUSTOMER_ID);

		when(defaultC4CTicketConverter.convert(ticket)).thenReturn(serviceRequest);
		doNothing().when(c4CServiceRequestService).updateTicket(serviceRequest);
		when(Boolean.valueOf(sitePropsHolder.isB2C())).thenReturn(Boolean.TRUE);
		when(c4CServiceRequestService.getServiceRequest(anyString())).thenReturn(serviceRequest);
		when(ticketConverter.convert(serviceRequest)).thenReturn(ticket);


		MemoActivity memo = new MemoActivity();
		when(memoActivityConverter.convert(ticket)).thenReturn(memo);
		doThrow(C4CServiceException.class).when(c4CServiceRequestService).createMemoActivity(memo);

		// Actual Call
		TicketData responseTicketData = c4CTicketFacade.updateTicket(ticket);

		// Verify
		Assert.assertNull(responseTicketData);

	}

}

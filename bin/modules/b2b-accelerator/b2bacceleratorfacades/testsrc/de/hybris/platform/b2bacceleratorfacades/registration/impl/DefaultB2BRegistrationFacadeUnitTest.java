/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.registration.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BRegistrationModel;
import de.hybris.platform.b2bacceleratorfacades.exception.CustomerAlreadyExistsException;
import de.hybris.platform.b2bacceleratorfacades.exception.RegistrationNotEnabledException;
import de.hybris.platform.b2bacceleratorfacades.registration.B2BRegistrationWorkflowFacade;
import de.hybris.platform.b2bcommercefacades.data.B2BRegistrationData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.test.TransactionTest;
import de.hybris.platform.workflow.WorkflowTemplateService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@IntegrationTest
public class DefaultB2BRegistrationFacadeUnitTest extends TransactionTest
{

	private BaseSiteService baseSiteService;

	private CommonI18NService commonI18NService;

	private ModelService modelService;

	private BaseStoreService baseStoreService;

	private UserService userService;

	private B2BRegistrationWorkflowFacade b2bRegistrationWorkflowFacade;

	private WorkflowTemplateService workflowTemplateService;

	private DefaultB2BRegistrationFacade b2bRegistrationFacade;
	private TitleModel titleModel;

	@Before
	public void setUp()
	{

		b2bRegistrationFacade = new DefaultB2BRegistrationFacade();

		baseSiteService = mock(BaseSiteService.class);
		b2bRegistrationFacade.setBaseSiteService(baseSiteService);

		baseStoreService = mock(BaseStoreService.class);
		b2bRegistrationFacade.setBaseStoreService(baseStoreService);


		commonI18NService = mock(CommonI18NService.class);
		b2bRegistrationFacade.setCommonI18NService(commonI18NService);

		modelService = mock(ModelService.class);
		b2bRegistrationFacade.setModelService(modelService);


		userService = mock(UserService.class);
		b2bRegistrationFacade.setUserService(userService);

		b2bRegistrationWorkflowFacade = mock(B2BRegistrationWorkflowFacade.class);
		b2bRegistrationFacade.setB2bRegistrationWorkflowFacade(b2bRegistrationWorkflowFacade);


		workflowTemplateService = mock(WorkflowTemplateService.class);
		b2bRegistrationFacade.setWorkflowTemplateService(workflowTemplateService);
	}

	@Test(expected = CustomerAlreadyExistsException.class)
	public void registerTestExpectedException() throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{

		final B2BRegistrationData data = createB2BRegistrationData();
		final BaseSiteModel site = createBaseSiteModel(true);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(true);

		b2bRegistrationFacade.register(data);
	}

	@Test(expected = RegistrationNotEnabledException.class)
	public void registerTestRegistrationNotEnabledException()
			throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{

		final B2BRegistrationData data = createB2BRegistrationData();
		final BaseSiteModel site = createBaseSiteModel(false);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);

		b2bRegistrationFacade.register(data);
	}

	@Test
	public void registerTest() throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{

		final B2BRegistrationData data = createB2BRegistrationData();
		final B2BRegistrationModel registration = createB2BRegistrationModel();
		final CustomerModel customer = createCustomerModel();
		final BaseSiteModel site = createBaseSiteModel(true);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(false);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		b2bRegistrationFacade.register(data);

		final ArgumentCaptor<CustomerModel> customerModelCaptor = ArgumentCaptor.forClass(CustomerModel.class);
		final ArgumentCaptor<B2BRegistrationModel> b2BRegistrationModelCaptor = ArgumentCaptor.forClass(B2BRegistrationModel.class);
		verify(b2bRegistrationWorkflowFacade).launchWorkflow(workflowTemplate, registration);
		verify(modelService, times(1)).saveAll(customerModelCaptor.capture(), b2BRegistrationModelCaptor.capture());
		Assert.assertNotNull(customerModelCaptor.getValue().getCustomerID());

	}

	@Test
	public void registerTestNoTitle() throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{

		final B2BRegistrationData data = createB2BRegistrationDataNoTitle();
		final B2BRegistrationModel registration = createB2BRegistrationModelNoTitle();
		final CustomerModel customer = createCustomerModelNoTitle();
		final BaseSiteModel site = createBaseSiteModel(true);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(false);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		b2bRegistrationFacade.register(data);

		verify(userService, times(0)).getTitleForCode(anyString());
		verify(modelService).saveAll(customer, registration);
		verify(b2bRegistrationWorkflowFacade).launchWorkflow(workflowTemplate, registration);

	}

	@Test(expected = UnknownIdentifierException.class)
	public void registerTestInvalidTitle() throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{

		final B2BRegistrationData data = createB2BRegistrationDataInvalidTitle();
		final B2BRegistrationModel registration = createB2BRegistrationModelInvalidTitle();
		final BaseSiteModel site = createBaseSiteModel(true);
		final CustomerModel customer = createCustomerModel();

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(false);
		when(userService.getTitleForCode(data.getTitleCode())).thenThrow(new UnknownIdentifierException("Title code not found"));
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);

		b2bRegistrationFacade.register(data);
	}

	@Test
	public void registerTestNoCountry() throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{
		final B2BRegistrationData data = createB2BRegistrationDataNoCountry();
		final B2BRegistrationModel registration = createB2BRegistrationModelNoCountry();
		final CustomerModel customer = createCustomerModel();
		final BaseSiteModel site = createBaseSiteModel(true);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(false);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		b2bRegistrationFacade.register(data);

		verify(commonI18NService, times(0)).getCountry(anyString());
		verify(commonI18NService, times(0)).getRegion(any(), anyString());
		verify(modelService).saveAll(customer, registration);
		verify(b2bRegistrationWorkflowFacade).launchWorkflow(workflowTemplate, registration);
	}

	@Test
	public void registerTestNoRegion() throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{
		final B2BRegistrationData data = createB2BRegistrationDataNoRegion();
		final B2BRegistrationModel registration = createB2BRegistrationModelNoRegion();
		final CustomerModel customer = createCustomerModel();
		final BaseSiteModel site = createBaseSiteModel(true);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(false);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		b2bRegistrationFacade.register(data);

		verify(commonI18NService, times(1)).getCountry(anyString());
		verify(commonI18NService, times(0)).getRegion(any(), anyString());
		verify(modelService).saveAll(customer, registration);
		verify(b2bRegistrationWorkflowFacade).launchWorkflow(workflowTemplate, registration);
	}

	@Test
	public void registerTestEmailWithUpperCase() throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{

		final B2BRegistrationData data = createB2BRegistrationData();
		final B2BRegistrationModel registration = createB2BRegistrationModel();
		final CustomerModel customer = createCustomerModel();
		final BaseSiteModel site = createBaseSiteModel(true);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(false);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		data.setEmail(data.getEmail().toUpperCase());
		b2bRegistrationFacade.register(data);

		final ArgumentCaptor<CustomerModel> customerModelCaptor = ArgumentCaptor.forClass(CustomerModel.class);
		final ArgumentCaptor<B2BRegistrationModel> b2BRegistrationModelCaptor = ArgumentCaptor.forClass(B2BRegistrationModel.class);
		verify(b2bRegistrationWorkflowFacade).launchWorkflow(workflowTemplate, registration);
		verify(modelService, times(1)).saveAll(customerModelCaptor.capture(), b2BRegistrationModelCaptor.capture());
		Assert.assertNotNull(customerModelCaptor.getValue().getCustomerID());
	}


	@Test
	public void registerTestCustomerIsAddedToB2BRegistration()
			throws CustomerAlreadyExistsException, RegistrationNotEnabledException
	{

		final B2BRegistrationData data = createB2BRegistrationData();
		final B2BRegistrationModel registration = createB2BRegistrationModel();
		final CustomerModel customer = createCustomerModel();
		final BaseSiteModel site = createBaseSiteModel(true);

		when(baseSiteService.getCurrentBaseSite()).thenReturn(site);
		when(userService.isUserExisting(data.getEmail())).thenReturn(false);

		final WorkflowTemplateModel workflowTemplate = mock(WorkflowTemplateModel.class);
		when(workflowTemplateService.getWorkflowTemplateForCode(Mockito.anyString())).thenReturn(workflowTemplate);

		when(modelService.create(B2BRegistrationModel.class)).thenReturn(registration);
		when(modelService.create(CustomerModel.class)).thenReturn(customer);

		b2bRegistrationFacade.register(data);

		Assert.assertEquals(customer, registration.getCustomer());
	}

	private B2BRegistrationData createB2BRegistrationDataNoTitle()
	{
		final B2BRegistrationData data = createB2BRegistrationData();
		data.setTitleCode(null);
		return data;
	}

	private B2BRegistrationData createB2BRegistrationDataInvalidTitle()
	{
		final B2BRegistrationData data = createB2BRegistrationData();
		data.setTitleCode("invalidTitle");
		return data;
	}

	private B2BRegistrationData createB2BRegistrationData()
	{
		final B2BRegistrationData data = new B2BRegistrationData();

		data.setPosition("Programmer");
		data.setTitleCode("01");
		data.setCompanyAddressCity("Montreal");
		data.setCompanyName("companyName");
		data.setMessage("Test");
		data.setTelephoneExtension("1234");
		data.setCompanyAddressPostalCode("J7V0J6");
		data.setEmail("test@gmail.com");
		data.setCompanyAddressStreet("Main St.");
		data.setCompanyAddressStreetLine2("");
		data.setName("Test Name");
		data.setCompanyAddressRegion("New York");
		data.setCompanyAddressCountryIso("US");
		data.setTelephone("122-232-2222");

		return data;
	}

	private B2BRegistrationModel createB2BRegistrationModelNoTitle()
	{
		final B2BRegistrationModel model = createB2BRegistrationModel();
		model.setTitle(null);
		return model;
	}

	private B2BRegistrationModel createB2BRegistrationModelInvalidTitle()
	{
		final B2BRegistrationModel model = createB2BRegistrationModel();
		final TitleModel titleModel = new TitleModel();
		titleModel.setCode("invalidTitle");
		model.setTitle(titleModel);
		return model;
	}

	private B2BRegistrationModel createB2BRegistrationModel()
	{

		final B2BRegistrationModel b2BRegistration = new B2BRegistrationModel();

		b2BRegistration.setTitle(titleModel);

		final CountryModel country = new CountryModel();
		country.setIsocode("US");
		country.setActive(Boolean.TRUE);

		final BaseStoreModel baseStore = new BaseStoreModel();
		final Locale locale = Locale.getDefault();
		baseStore.setName("cms site", locale);

		final BaseSiteModel baseSite = new BaseSiteModel();

		final LanguageModel language = new LanguageModel();
		language.setIsocode("US");

		final CurrencyModel currency = new CurrencyModel();
		currency.setIsocode("US");

		b2BRegistration.setCompanyAddressCountry(country);
		b2BRegistration.setBaseStore(baseStore);
		b2BRegistration.setBaseSite(baseSite);
		b2BRegistration.setLanguage(language);
		b2BRegistration.setCurrency(currency);

		return b2BRegistration;
	}

	private CustomerModel createCustomerModel()
	{
		final CustomerModel customer = createCustomerModelNoTitle();

		final TitleModel titleModel = new TitleModel();
		titleModel.setCode("01");
		customer.setTitle(titleModel);

		return customer;
	}

	private CustomerModel createCustomerModelNoTitle()
	{
		final CustomerModel customer = new CustomerModel();
		customer.setName("Test Name");
		customer.setUid("test@gmail.com");

		return customer;
	}

	private B2BRegistrationData createB2BRegistrationDataNoCountry()
	{
		final B2BRegistrationData data = createB2BRegistrationData();
		data.setCompanyAddressCountryIso(null);
		return data;
	}

	private B2BRegistrationModel createB2BRegistrationModelNoCountry()
	{
		final B2BRegistrationModel model = createB2BRegistrationModel();
		model.setCompanyAddressCountry(null);
		return model;
	}

	private B2BRegistrationData createB2BRegistrationDataNoRegion()
	{
		final B2BRegistrationData data = createB2BRegistrationData();
		data.setCompanyAddressRegion(null);
		return data;
	}

	private B2BRegistrationModel createB2BRegistrationModelNoRegion()
	{
		final B2BRegistrationModel model = createB2BRegistrationModel();
		model.setCompanyAddressRegion(null);
		return model;
	}

	private BaseSiteModel createBaseSiteModel(final boolean isRegistrationEnable)
	{
		final BaseSiteModel site = new BaseSiteModel();
		site.setEnableRegistration(isRegistrationEnable);
		return site;
	}
}

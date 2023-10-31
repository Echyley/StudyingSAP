/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.impex.jalo.imp;

import static de.hybris.platform.impex.util.CompanyAddressesTestImpexFileGenerator.AddressProcessMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CompanyModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.util.CompanyAddressesTestImpexFileGenerator;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.interceptor.impl.DefaultInterceptorRegistry;
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorMapping;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.assertions.assertj.TestLogListenerAssert;
import de.hybris.platform.testframework.log.TestLogListener;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for CXEC-20645
 */
@IntegrationTest
public class ClearingModelContextWhenImportingImpexTest extends ServicelayerTransactionalTest
{
	private static final int NUMBER_OF_ADDRESSES_TO_IMPORT = 5;
	private static final String ASSIGNED_ADDRESSES_TO_COMPANY = "Assigned addresses to Company:%s";
	private static final String TEST_COMPANY_ADDRESSES_FILE_TO_IMPORT = "/impex/testfiles/testCompanyAddresses.csv";
	private static final String TEST_COMPANY_ADDRESSES_FILE_TO_CREATE = "/ext/impex/resources/impex/testfiles/testCompanyAddresses.csv";
	private static final Logger LOG = LoggerFactory.getLogger(ClearingModelContextWhenImportingImpexTest.class);
	private final PropertyConfigSwitcher clearingModelContextEnabled = new PropertyConfigSwitcher(
			DefaultImportProcessor.CLEARING_MODEL_CONTEXT_AFTER_PROCESSING_EACH_ITEM_ENABLED);
	private final TestLogListener testLogListener = new TestLogListener();

	@Resource
	private ModelService modelService;

	private DefaultImportProcessor defaultImportProcessor;

	private DefaultInterceptorRegistry registry;

	private InterceptorMapping addressInterceptor;

	@Before
	public void setUp()
	{
		defaultImportProcessor = spy(new DefaultImportProcessor());
		registry = (DefaultInterceptorRegistry) ((DefaultModelService) modelService).getInterceptorRegistry();
		addressInterceptor = createInterceptorMapping(new AddressValidateInterceptor());
		testLogListener.attach();
	}

	@After
	public void tearDown()
	{
		clearingModelContextEnabled.switchBackToDefault();
		unRegister(addressInterceptor);
		testLogListener.detach();
	}

	@Test
	public void shouldClearModelContextAfterProcessingItemIfClearingIsEnabled() throws ImpExException
	{
		//given
		clearingModelContextEnabled.switchToValue("true");

		//when
		defaultImportProcessor.processItemData_Impl(createValueLine());

		//then
		verify(defaultImportProcessor, times(1)).clearModelContextAfterProcessingItem();
	}

	@Test
	public void shouldNotClearModelContextAfterProcessingItemIfClearingIsDisabled() throws ImpExException
	{
		//given
		clearingModelContextEnabled.switchToValue("false");

		//when
		defaultImportProcessor.processItemData_Impl(createValueLine());

		//then
		verify(defaultImportProcessor, times(0)).clearModelContextAfterProcessingItem();
	}

	@Test
	public void shouldLogNotValidDataInValidatorIfThereIsNoClearingModelContextAfterPerformingInsertAddresses()
	{
		//given
		clearingModelContextEnabled.switchToValue("false");
		createCompanyAddressesTestImpexFile(AddressProcessMode.INSERT);

		//when, then
		shouldLogNotValidDataInValidatorIfThereIsNoClearingModelAfterContextAfterProcessingItem();
	}

	@Test
	public void shouldLogNotValidDataInValidatorIfThereIsNoClearingModelContextAfterPerformingInsertUpdateAddresses()
	{
		//given
		clearingModelContextEnabled.switchToValue("false");
		createCompanyAddressesTestImpexFile(AddressProcessMode.INSERT_UPDATE);

		//when, then
		shouldLogNotValidDataInValidatorIfThereIsNoClearingModelAfterContextAfterProcessingItem();
	}

	@Test
	public void shouldNotLogValidDataInValidatorIfThereIsNoClearingModelContextAfterPerformingInsertAndRemoveAddresses()
	{
		//given
		clearingModelContextEnabled.switchToValue("false");
		createCompanyAddressesTestImpexFileWithRemovingFirstTwoAddresses(AddressProcessMode.INSERT);

		//when, then
		shouldLogNotValidDataInValidatorIfThereIsNoClearingModelAfterContextAfterProcessingItem();
	}

	@Test
	public void shouldLogValidDataInValidatorIfThereIsClearingModelContextAfterPerformingInsertAddresses()
	{
		//given
		clearingModelContextEnabled.switchToValue("true");
		createCompanyAddressesTestImpexFile(AddressProcessMode.INSERT);

		//when, then
		shouldLogValidDataInValidatorIfThereIsClearingModelAfterContextAfterProcessingItem();
	}

	@Test
	public void shouldLogValidDataInValidatorIfThereIsClearingModelContextAfterPerformingInsertUpdateAddresses()
	{
		//given
		clearingModelContextEnabled.switchToValue("true");
		createCompanyAddressesTestImpexFile(AddressProcessMode.INSERT_UPDATE);

		//when, then
		shouldLogValidDataInValidatorIfThereIsClearingModelAfterContextAfterProcessingItem();
	}

	@Test
	public void shouldLogValidDataInValidatorIfThereIsClearingModelContextAfterPerformingInsertAndRemoveAddresses()
	{
		//given
		clearingModelContextEnabled.switchToValue("true");
		createCompanyAddressesTestImpexFileWithRemovingFirstTwoAddresses(AddressProcessMode.INSERT);

		//when
		importCsvFile(TEST_COMPANY_ADDRESSES_FILE_TO_IMPORT);

		//then
		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(ClearingModelContextWhenImportingImpexTest.class)
		                     .withMessageContaining(String.format(ASSIGNED_ADDRESSES_TO_COMPANY, 0))
		                     .occurrences(1);

		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(ClearingModelContextWhenImportingImpexTest.class)
		                     .withMessageContaining(String.format(ASSIGNED_ADDRESSES_TO_COMPANY, 1))
		                     .occurrences(1);

		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(ClearingModelContextWhenImportingImpexTest.class)
		                     .withMessageContaining(String.format(ASSIGNED_ADDRESSES_TO_COMPANY, 2))
		                     .occurrences(2);

		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(ClearingModelContextWhenImportingImpexTest.class)
		                     .withMessageContaining(String.format(ASSIGNED_ADDRESSES_TO_COMPANY, 3))
		                     .occurrences(1);
	}

	private void shouldLogNotValidDataInValidatorIfThereIsNoClearingModelAfterContextAfterProcessingItem()
	{
		//when
		boolean assertionError = false;
		importCsvFile(TEST_COMPANY_ADDRESSES_FILE_TO_IMPORT);

		//then
		try
		{
			//it will throw AssertionError if logs in Validator are different from expected valid logs
			assertValidLogsForImportedAddresses();
		}
		catch (final AssertionError ex)
		{
			assertionError = true;
		}

		assertThat(assertionError).isTrue();
	}

	private void shouldLogValidDataInValidatorIfThereIsClearingModelAfterContextAfterProcessingItem()
	{
		//when
		importCsvFile(TEST_COMPANY_ADDRESSES_FILE_TO_IMPORT);

		//then
		assertValidLogsForImportedAddresses();
	}

	private void assertValidLogsForImportedAddresses()
	{
		for (int i = 0; i < NUMBER_OF_ADDRESSES_TO_IMPORT; i++)
		{
			TestLogListenerAssert.assertThat(testLogListener)
			                     .hasLog()
			                     .loggedFrom(ClearingModelContextWhenImportingImpexTest.class)
			                     .withMessageContaining(String.format(ASSIGNED_ADDRESSES_TO_COMPANY, i))
			                     .occurrences(1);
		}
	}

	private ValueLine createValueLine()
	{
		return mock(ValueLine.class);
	}

	private InterceptorMapping createInterceptorMapping(final Interceptor interceptor)
	{
		final InterceptorMapping mapping = new InterceptorMapping();
		mapping.setTypeCode(AddressModel._TYPECODE);
		mapping.setInterceptor(interceptor);
		registry.registerInterceptor(mapping);
		return mapping;
	}

	private void unRegister(final InterceptorMapping interceptor)
	{
		if (interceptor != null)
		{
			registry.unregisterInterceptor(interceptor);
		}
	}

	private void createCompanyAddressesTestImpexFile(final AddressProcessMode addressProcessMode)
	{
		CompanyAddressesTestImpexFileGenerator.createCompanyAddressesTestImpexFile(NUMBER_OF_ADDRESSES_TO_IMPORT,
				TEST_COMPANY_ADDRESSES_FILE_TO_CREATE, addressProcessMode);
	}

	private void createCompanyAddressesTestImpexFileWithRemovingFirstTwoAddresses(final AddressProcessMode addressProcessMode)
	{
		CompanyAddressesTestImpexFileGenerator.createCompanyAddressesTestImpexFileWithRemovingFirstTwoAddresses(NUMBER_OF_ADDRESSES_TO_IMPORT,
				TEST_COMPANY_ADDRESSES_FILE_TO_CREATE, addressProcessMode);
	}

	private void importCsvFile(final String csvFile)
	{
		try
		{
			importCsv(csvFile, "UTF-8");
		}
		catch (final ImpExException ex)
		{
			LOG.info(ex.getMessage());
		}
	}

	private static class AddressValidateInterceptor implements ValidateInterceptor<AddressModel>
	{
		@Override
		public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
		{
			final ItemModel owner = addressModel.getOwner();
			if (owner instanceof CompanyModel)
			{
				final int addressesSize = ((CompanyModel) owner).getAddresses().size();
				LOG.info(String.format(ASSIGNED_ADDRESSES_TO_COMPANY, addressesSize));
			}
		}
	}
}



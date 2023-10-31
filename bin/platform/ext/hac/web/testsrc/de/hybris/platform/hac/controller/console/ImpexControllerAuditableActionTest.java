/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.controller.console;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.AuditableActionsUtil;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.hac.data.dto.ImportDataResult;
import de.hybris.platform.hac.data.form.ImpexContentFormData;
import de.hybris.platform.hac.data.form.ImpexFileFormData;
import de.hybris.platform.hac.facade.HacImpexFacade;
import de.hybris.platform.hac.util.HacAuditUtil;
import de.hybris.platform.impex.enums.ImpExValidationModeEnum;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@IntegrationTest
public class ImpexControllerAuditableActionTest extends ServicelayerBaseTest
{
	private static final String ACTION_NAME = "ImpEx Import started";
	private static final String IMPORT_MODE = "importMode";
	private static final String ENABLE_CODE_EXECUTION = "enableCodeExecution";
	private static final String DISTRIBUTED_MODE = "distributedMode";
	private static final String MAX_THREADS = "maxThreads";
	private static final String CONTENT_SIGNATURE = "contentSignature";
	private static final String FILE_NAME = "fileName";
	private static final String CONTENT_VALUE = "INSERT_UPDATE Title; code[unique=true];\r\n;test_title";

	@Resource
	private HacImpexFacade hacImpexFacade;
	@Resource
	private EnumerationService enumerationService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	private ImpexController impexController;
	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;
	private BindingResult bindingResult;
	private Model model;

	@Before
	public void setUp()
	{
		testAuditableActionHandler = mock(AuditableActionHandler.class);
		originalActionHandler = AuditableActionsUtil.getAuditableActionHandlerFactory();
		AuditableActionsUtil.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);
		impexController = new ImpexController(hacImpexFacade, enumerationService, flexibleSearchService);
		bindingResult = mock(BindingResult.class);
		when(bindingResult.hasErrors()).thenReturn(false);
		model = mock(Model.class);
		when(model.addAttribute(eq("importResult"), any(ImportDataResult.class))).thenReturn(null);
	}

	@After
	public void tearDown()
	{
		AuditableActionsUtil.setAuditableActionHandlerFactory(originalActionHandler);
	}

	@Test
	public void shouldAuditContentImport()
	{
		//given
		final ImpexContentFormData impexContentFormData = createImpexContentFormData();

		//when
		impexController.handleImpexImportContent(model, impexContentFormData, bindingResult, null, null);

		//then
		final Optional<String> contentSignatureToCompare = HacAuditUtil.generateSHA256Signature(
				HacAuditUtil.normalizeEOL(CONTENT_VALUE));

		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(ACTION_NAME);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry(IMPORT_MODE, "content"),
				entry(ENABLE_CODE_EXECUTION, false), entry(DISTRIBUTED_MODE, false), entry(MAX_THREADS, 1),
				entry(CONTENT_SIGNATURE, contentSignatureToCompare.get()));
	}

	@Test
	public void shouldAuditScriptImport() throws IOException
	{
		//given
		final String fileName = "test.impex";
		final ImpexFileFormData impexFileFormData = createImpexFileFormData(fileName);
		
		//when
		impexController.handleImpexImportUpload(model, impexFileFormData, bindingResult, null, null);

		//then
		final Optional<String> contentSignatureToCompare = HacAuditUtil.generateSHA256Signature(
				HacAuditUtil.normalizeEOL(CONTENT_VALUE));

		final Optional<AuditableActions.Action> optionalAction = getAuditableAction(ACTION_NAME);
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry(IMPORT_MODE, "script"),
				entry(ENABLE_CODE_EXECUTION, true), entry(DISTRIBUTED_MODE, true), entry(MAX_THREADS, 1),
				entry(FILE_NAME, fileName), entry(CONTENT_SIGNATURE, contentSignatureToCompare.get()));
	}

	private ImpexContentFormData createImpexContentFormData()
	{
		final ImpexContentFormData impexContentFormData = new ImpexContentFormData();
		configureImpexContentFormData(impexContentFormData);
		impexContentFormData.setScriptContent(CONTENT_VALUE);
		impexContentFormData.setEnableCodeExecution(false);
		impexContentFormData.setDistributedMode(false);
		return impexContentFormData;
	}

	private ImpexFileFormData createImpexFileFormData(final String fileName)
	{
		final ImpexFileFormData impexFileFormData = new ImpexFileFormData();
		configureImpexContentFormData(impexFileFormData);
		impexFileFormData.setFile(createTestMultipartFile(fileName));
		impexFileFormData.setEnableCodeExecution(true);
		impexFileFormData.setDistributedMode(true);
		return impexFileFormData;
	}

	private void configureImpexContentFormData(final ImpexContentFormData formData)
	{
		formData.setMaxThreads(1);
		formData.setValidationEnum(ImpExValidationModeEnum.IMPORT_STRICT);
		formData.setEncoding("UTF-8");
	}

	private MockMultipartFile createTestMultipartFile(final String fileName)
	{
		return new MockMultipartFile(FILE_NAME, fileName, MediaType.APPLICATION_OCTET_STREAM_VALUE,
				CONTENT_VALUE.getBytes(StandardCharsets.UTF_8));
	}

	private Optional<AuditableActions.Action> getAuditableAction(final String actionName)
	{
		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		return argCaptor.getAllValues()
		                .stream()
		                .filter(action -> actionName.equals(action.getActionName()))
		                .findFirst();
	}
}

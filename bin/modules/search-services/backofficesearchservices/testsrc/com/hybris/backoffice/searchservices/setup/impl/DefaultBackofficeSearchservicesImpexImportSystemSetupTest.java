/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.setup.impl;

import com.hybris.backoffice.search.setup.BackofficeSearchSystemSetupConfig;
import com.hybris.backoffice.search.setup.impl.FileBasedImpExResourceFactory;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;
import de.hybris.platform.servicelayer.impex.impl.FileBasedImpExResource;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mockito;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBackofficeSearchservicesImpexImportSystemSetupTest
{

	private static final String DE_CODE = "de";
	private static final String EN_CODE = "en";
	private static final String COMMA = ",";
	private static final String UNDERSCORE = "_";
	private static final String UTF_8 = "UTF-8";
	private static final String LOCALIZED_ROOT = "/test/test";
	private static final String NON_LOCALIZED_ROOT = "test.impex";
	private static final String TEST_TEST_EN_IMPEX = "test_en.impex";
	private static final String TEST_TEST_DE_IMPEX = "test_de.impex";

	@Mock
	private ImportService importService;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private ModelService modelService;
	@Mock
	private CronJobService cronJobService;
	@Mock
	private Configuration configuration;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private FileBasedImpExResourceFactory fileBasedImpExResourceFactory;
	@Mock
	private BackofficeSearchSystemSetupConfig backofficeSearchSystemSetupConfig;

	@InjectMocks
	@Spy
	private DefaultBackofficeSearchservicesImpexImportSystemSetup systemSetup;


	@Before
	public void setUp()
	{
		mockDefaultConfig();
		mockDefaultBehaviour();
	}

	@Test
	public void shouldImportConfiguredImpexFiles()
	{
		mockNonLocalizedFilesConfig();
		//given
		final ArgumentCaptor<File> filePathsCaptor = ArgumentCaptor.forClass(File.class);
		final ArgumentCaptor<String> encodingCaptor = ArgumentCaptor.forClass(String.class);
		final int twice = 2;
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configurationService.getConfiguration().getString(anyString(), anyString())).thenReturn(StringUtils.EMPTY);

		//when
		systemSetup.importImpex();

		//then
		verify(fileBasedImpExResourceFactory, times(twice)).createFileBasedImpExResource(filePathsCaptor.capture(),
				encodingCaptor.capture());
		assertThat(filePathsCaptor.getAllValues().stream().map(file -> file.getName()).collect(Collectors.toList()))
				.containsExactly(TEST_TEST_EN_IMPEX, TEST_TEST_DE_IMPEX);
	}

	@Test
	public void shouldAdjustCronjob()
	{
		//given
		final String nodeGroup = "TEST";
		final CronJobModel fullCronJob = spy(new CronJobModel());
		final CronJobModel updateCronJob = spy(new CronJobModel());
		when(cronJobService.getCronJob("indexer-backoffice-product-full")).thenReturn(fullCronJob);
		when(cronJobService.getCronJob("indexer-backoffice-product-update")).thenReturn(updateCronJob);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getString("backoffice.search.services.cronjob.nodegroup", StringUtils.EMPTY)).thenReturn(nodeGroup);

		//when
		systemSetup.adjustIndexUpdatingCronjob();

		//then
		verify(fullCronJob).setNodeGroup(nodeGroup);
		verify(modelService).save(fullCronJob);
		assertThat(fullCronJob.getNodeGroup()).isEqualTo(nodeGroup);
		verify(updateCronJob).setNodeGroup(nodeGroup);
		verify(modelService).save(updateCronJob);
		assertThat(updateCronJob.getNodeGroup()).isEqualTo(nodeGroup);
	}

	@Test
	public void shouldNotSetNodeGroupInCronJoBWhenNodeGroupIsEmpty()
	{
		//given
		final String nodeGroup = StringUtils.EMPTY;
		final CronJobModel fullCronJob = spy(new CronJobModel());
		final CronJobModel updateCronJob = spy(new CronJobModel());
		when(cronJobService.getCronJob("indexer-backoffice-product-full")).thenReturn(fullCronJob);
		when(cronJobService.getCronJob("indexer-backoffice-product-update")).thenReturn(updateCronJob);
		when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.lenient().when(configurationService.getConfiguration().getString("backoffice.search.services.cronjob.nodegroup")).thenReturn(nodeGroup);

		//when
		systemSetup.adjustIndexUpdatingCronjob();

		//then
		verify(fullCronJob, times(0)).setNodeGroup(anyString());
		verify(modelService, times(0)).save(fullCronJob);
		assertThat(fullCronJob.getNodeGroup()).isNull();
		verify(updateCronJob, times(0)).setNodeGroup(anyString());
		verify(modelService, times(0)).save(updateCronJob);
		assertThat(updateCronJob.getNodeGroup()).isNull();
	}

	private void mockDefaultConfig()
	{
		when(backofficeSearchSystemSetupConfig.getFileEncoding()).thenReturn(UTF_8);
		when(backofficeSearchSystemSetupConfig.getRootNameLanguageSeparator()).thenReturn(UNDERSCORE);
		Mockito.lenient().when(backofficeSearchSystemSetupConfig.getLocalizedRootNames()).thenReturn(Arrays.asList(LOCALIZED_ROOT));
	}

	private void mockNonLocalizedFilesConfig()
	{
		when(backofficeSearchSystemSetupConfig.getNonLocalizedRootNames()).thenReturn(Arrays.asList(NON_LOCALIZED_ROOT));
	}

	private void mockDefaultBehaviour()
	{
		final ImportConfig importConfig = mock(ImportConfig.class);
		final FileBasedImpExResource fileBasedImpExResource = mock(FileBasedImpExResource.class);
		Mockito.lenient().when(fileBasedImpExResourceFactory.createFileBasedImpExResource(any(), any())).thenReturn(fileBasedImpExResource);
		when(importService.importData(any(ImportConfig.class))).thenReturn(mock(ImportResult.class));

		final LanguageModel languageModelEn = mock(LanguageModel.class);
		when(languageModelEn.getIsocode()).thenReturn(EN_CODE);
		final LanguageModel languageModelDe = mock(LanguageModel.class);
		when(languageModelDe.getIsocode()).thenReturn(DE_CODE);

		when(commonI18NService.getAllLanguages()).thenReturn(Arrays.asList(languageModelEn, languageModelDe));
	}

}

/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.impex.distributed.process;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.distributed.batch.ImportDataDumpStrategy;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.processing.distributed.DistributedProcessService;
import de.hybris.platform.processing.enums.DistributedProcessState;
import de.hybris.platform.processing.model.DistributedProcessModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.TestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Test;


@IntegrationTest
public class DefaultDistributedImportProcessHandlerIntegrationTest extends ServicelayerBaseTest
{

	private final PropertyConfigSwitcher removeOnSuccessImportBatchContentSwitcher = new PropertyConfigSwitcher(
			"impex.distributed.importbatchcontent.removeonsuccess");

	@Resource
	private UserService userService;
	@Resource
	private DistributedProcessService distributedProcessService;
	@Resource
	private ImportDataDumpStrategy importDataDumpStrategy;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@After
	public void cleanup()
	{
		removeOnSuccessImportBatchContentSwitcher.switchBackToDefault();
	}

	@Test
	public void shouldImportMultipleBatches() throws InterruptedException
	{
		final DistributedProcessModel process = prepareDistributedProcess();

		distributedProcessService.start(process.getCode());
		distributedProcessService.wait(process.getCode(), 10L);

		assertThat(process.getState()).isEqualTo(DistributedProcessState.SUCCEEDED);

		final TitleModel a1 = userService.getTitleForCode("ALABAMA1");
		assertThat(a1).isNotNull();
		assertThat(a1.getName()).isNull();

		final TitleModel a2 = userService.getTitleForCode("ALABAMA2");
		assertThat(a2).isNotNull();
		assertThat(a2.getName()).isNull();

		final TitleModel k1 = userService.getTitleForCode("KENTUCKY1");
		assertThat(k1).isNotNull();
		assertThat(k1.getName()).isEqualTo("Kentucky1");

		final TitleModel k2 = userService.getTitleForCode("KENTUCKY2");
		assertThat(k2).isNotNull();
		assertThat(k2.getName()).isEqualTo("Kentucky2");
	}

	@Test
	public void shouldImportPossibleBatchesWithImportRelaxedMode() throws InterruptedException
	{
		TestUtils.disableFileAnalyzer("Expected errors from invaid impex", 1000);

		final String input = "INSERT_UPDATE Title;code[unique=true]\n" //
				+ ";ALABAMA1\n;ALABAMA2\n" //
				+ "REMOVE NonExistent;code[unique=true]\n" //
				+ ";TEXAS1\n;TEXAS2\n" //
				+ "INSERT_UPDATE Title;code[unique=true];name[lang=en]\n" //
				+ ";KENTUCKY1;Kentucky1\n" //
				+ ";KENTUCKY2;Kentucky2\n";
		final ImportProcessCreationData.ImportProcessContext ctx = new ImportProcessCreationData.ImportProcessContext();
		ctx.setValidationMode(ImpExConstants.Enumerations.ImpExValidationModeEnum.IMPORT_RELAXED);
		final ImportProcessCreationData data = new ImportProcessCreationData("TEST_PROCESS", getAsStream(input),
				importDataDumpStrategy, ctx);

		final DistributedProcessModel process = distributedProcessService.create(data);

		distributedProcessService.start(process.getCode());
		distributedProcessService.wait(process.getCode(), 10);

		assertThat(process.getState()).isEqualTo(DistributedProcessState.SUCCEEDED);

		final TitleModel a1 = userService.getTitleForCode("ALABAMA1");
		assertThat(a1).isNotNull();
		assertThat(a1.getName()).isNull();

		final TitleModel a2 = userService.getTitleForCode("ALABAMA2");
		assertThat(a2).isNotNull();
		assertThat(a2.getName()).isNull();

		final TitleModel k1 = userService.getTitleForCode("KENTUCKY1");
		assertThat(k1).isNotNull();
		assertThat(k1.getName()).isEqualTo("Kentucky1");

		final TitleModel k2 = userService.getTitleForCode("KENTUCKY2");
		assertThat(k2).isNotNull();
		assertThat(k2.getName()).isEqualTo("Kentucky2");
	}

	private void checkTitleModelsAreValidAfterDistributedImpex(){
		final TitleModel a1 = userService.getTitleForCode("ALABAMA1");
		assertThat(a1).isNotNull();
		assertThat(a1.getName()).isNull();

		final TitleModel a2 = userService.getTitleForCode("ALABAMA2");
		assertThat(a2).isNotNull();
		assertThat(a2.getName()).isNull();

		final TitleModel k1 = userService.getTitleForCode("KENTUCKY1");
		assertThat(k1).isNotNull();
		assertThat(k1.getName()).isEqualTo("Kentucky1");

		final TitleModel k2 = userService.getTitleForCode("KENTUCKY2");
		assertThat(k2).isNotNull();
		assertThat(k2.getName()).isEqualTo("Kentucky2");
	}

	@Test
	public void shouldNotRemoveImportBatchContentWhenPropertyForRemoveOnSuccessIsDisabled() throws InterruptedException{
		//given
		removeOnSuccessImportBatchContentSwitcher.switchToValue("false");
		final int importBatchContentCountBefore = countImportBatchContentItems();
		final DistributedProcessModel process = prepareDistributedProcess();

		//when
		distributedProcessService.start(process.getCode());
		distributedProcessService.wait(process.getCode(), 10L);

		//then
		assertThat(process.getState()).isEqualTo(DistributedProcessState.SUCCEEDED);
		final int importBatchContentCountAfter = countImportBatchContentItems();
		assertThat(importBatchContentCountAfter - importBatchContentCountBefore).isEqualTo(2);
		checkTitleModelsAreValidAfterDistributedImpex();
	}

	private DistributedProcessModel prepareDistributedProcess()
	{
		final String input = "INSERT_UPDATE Title;code[unique=true]\n" //
				+ ";ALABAMA1\n" //
				+ ";ALABAMA2\n" //
				+ "INSERT_UPDATE Title;code[unique=true];name[lang=en]\n" //
				+ ";KENTUCKY1;Kentucky1\n" //
				+ ";KENTUCKY2;Kentucky2\n";

		final ImportProcessCreationData data = new ImportProcessCreationData("TEST_PROCESS", getAsStream(input),
				importDataDumpStrategy);

		return distributedProcessService.create(data);
	}

	@Test
	public void shouldRemoveImportBatchContentWhenPropertyForRemoveOnSuccessIsEnabled() throws InterruptedException
	{
		//given
		removeOnSuccessImportBatchContentSwitcher.switchToValue("true");
		final int importBatchContentCountBefore = countImportBatchContentItems();
		final DistributedProcessModel process = prepareDistributedProcess();

		//when
		distributedProcessService.start(process.getCode());
		distributedProcessService.wait(process.getCode(), 10L);

		//then
		assertThat(process.getState()).isEqualTo(DistributedProcessState.SUCCEEDED);
		final int importBatchContentCountAfter = countImportBatchContentItems();
		assertThat(importBatchContentCountAfter - importBatchContentCountBefore).isZero();
		checkTitleModelsAreValidAfterDistributedImpex();
	}

	private int countImportBatchContentItems()
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery("SELECT COUNT( {" + Item.PK + "})  FROM {ImportBatchContent}");
		fQuery.setResultClassList(Arrays.asList(Integer.class));
		return (Integer) flexibleSearchService.search(fQuery).getResult().get(0);
	}

	private InputStream getAsStream(final String input)
	{
		return new ByteArrayInputStream(input.getBytes());
	}
}

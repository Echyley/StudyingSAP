/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.cockpitng.modules;

import static com.hybris.backoffice.cockpitng.modules.BackofficeWidgetLibUtils.CONSTANT_DATA_HOME;
import static com.hybris.cockpitng.core.persistence.packaging.WidgetLibConstants.CONSTANT_TEMP_DIR;
import static com.hybris.cockpitng.core.persistence.packaging.WidgetLibConstants.CONSTANT_USER_HOME;
import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.util.Utilities;

import java.io.File;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.constants.BackofficeConstants;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeWidgetLibUtilsTest
{
	@Spy
	private BackofficeWidgetLibUtils utils;

	@Test
	public void shouldProcessSupportedVariablesWhenGetRootDir()
	{
		final String dir = "/1/" + CONSTANT_DATA_HOME + "/2/" + CONSTANT_USER_HOME + "/3/" + CONSTANT_TEMP_DIR
				+ "/${unsupported}/4";

		final String actual = Stream.of(BackofficeWidgetLibUtils.getBackofficeDirProcessors()).reduce(dir,
				(before, processor) -> processor.apply(before), (before, after) -> after);
		final String expect = "/1/" + Utilities.getPlatformConfig().getSystemConfig().getDataDir() + File.separator
				+ BackofficeConstants.EXTENSIONNAME + "/2/" + FileUtils.getUserDirectoryPath() + "/3/"
				+ FileUtils.getTempDirectoryPath() + "/${unsupported}/4";

		assertThat(actual).isEqualTo(expect);
	}
}

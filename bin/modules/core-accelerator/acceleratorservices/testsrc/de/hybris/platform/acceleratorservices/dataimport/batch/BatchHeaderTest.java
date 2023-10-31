/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.acceleratorservices.dataimport.batch;

import java.io.File;
import java.util.Collections;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BatchHeaderTest
{

	@Test
	public void setTransformedFilesNull()
	{
		BatchHeader header = new BatchHeader();
		header.setTransformedFiles(null);

		assertThat(header.getTransformedFiles())
				.isNotNull()
				.isEmpty();
	}

	@Test
	public void setTransformedFilesEmpty()
	{
		BatchHeader header = new BatchHeader();
		header.setTransformedFiles(Collections.emptyList());

		assertThat(header.getTransformedFiles())
				.isNotNull()
				.isEmpty();
	}

	@Test
	public void setTransformedFiles()
	{
		BatchHeader header = new BatchHeader();
		header.setTransformedFiles(Collections.singletonList(mock(File.class)));

		assertThat(header.getTransformedFiles())
				.isNotNull()
				.hasSize(1);
	}

}

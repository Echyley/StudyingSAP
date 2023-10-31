/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataexport.generic.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.file.remote.session.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultUploadTransportServiceTest
{
	@InjectMocks
	DefaultUploadTransportService defaultUploadTransportService;

	@Mock
	Session session;

	@Test
	public void testSendFileToRemoteDirectorySuccess() throws IOException
	{

		final File file = new ClassPathResource(
				"de/hybris/platform/acceleratorservices/dataexport/generic/impl/DefaultUploadTransportServiceTest.class").getFile();
		defaultUploadTransportService.sendFileToRemoteDirectory(file, "test", "FileName", session);
		verify(session, times(1)).write(any(), any());
		verify(session, times(1)).rename(any(), any());
	}

	@Test
	public void testSendFileToRemoteDirectoryFailed() throws IOException
	{

		final File file = new File("notExist");
		defaultUploadTransportService.sendFileToRemoteDirectory(file, "test", "FileName", session);
		verify(session, times(0)).write(any(), any());
		verify(session, times(0)).rename(any(), any());
	}
}

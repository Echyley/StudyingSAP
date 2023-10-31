/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicebol.backend.impl;



import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.bol.test.SapcorebolSpringJUnitTest;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecording;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;



@SuppressWarnings("javadoc")
@UnitTest
@JCoRecording(mode = JCoRecMode.PLAYBACK, recordingExtensionName = "sapinvoicebol")
@ContextConfiguration(locations =
{ "classpath:test/sapinvoicebol-test-spring.xml", "classpath:test/sapcorejco-test-spring.xml" })
public class SapInvoiceBackendImplTest extends SapcorebolSpringJUnitTest
{
	@Resource(name = "sapInvoiceBackendTypeERP")
	protected SapInvoiceBackendImpl classUndertest;

	@Test
	public void getInvoiceInByteTest()
	{
		try
		{
			//classUndertest.getInvoiceInByte("90012528");
			Assert.assertNotNull(classUndertest.getInvoiceInByte("90012528"));
		}
		catch (final BackendException e)
		{
			Assert.fail("BackendRuntimeException expected");
		}

	}
}

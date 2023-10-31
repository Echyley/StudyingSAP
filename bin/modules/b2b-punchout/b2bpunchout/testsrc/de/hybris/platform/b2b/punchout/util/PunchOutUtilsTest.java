/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PunchOutUtilsTest
{
	@Test
	public void testMarshallFromBeanTree() throws FileNotFoundException
	{
		final CXML requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");

		assertThat(requestXML).isNotNull();
		assertThat(PunchOutUtils.marshallFromBeanTree(requestXML)).isNotNull();
	}

	@Test
	public void testTransformCXMLToBase64() throws FileNotFoundException
	{
		final CXML requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
		assertThat(requestXML).isNotNull();
		assertThat(PunchOutUtils.transformCXMLToBase64(requestXML)).isNotNull();
	}

	@Test
	public void testFileNotFound()
	{
		assertThatThrownBy(() -> PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/notExisting.xml"))
				.isInstanceOf(FileNotFoundException.class);
	}
}

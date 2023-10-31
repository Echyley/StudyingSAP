/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsfacades.utils;

import static org.junit.Assert.*;
import static org.spockframework.util.Assert.notNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.xyformsfacades.data.YFormDefinitionData;
import de.hybris.platform.xyformsservices.exception.YFormServiceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;


@UnitTest
public class FormDefinitionUtilsTest
{
	@Test
	public void getFormDefinitionContentShouldReturnCorrectResultTest() throws YFormServiceException, IOException
	{
		String correctContent = getContentAsString("/test/data/correct-input.xhtml");

		YFormDefinitionData yFormDefinitionData = new YFormDefinitionData();
		yFormDefinitionData.setApplicationId("ApplicationId");
		yFormDefinitionData.setFormId("FormId");

		String formDefinition = FormDefinitionUtils.getFormDefinitionContent(correctContent, yFormDefinitionData);
		notNull(formDefinition);
	}

	@Test(expected = YFormServiceException.class)
	public void getFormDefinitionContentShouldThrowExceptionTest() throws YFormServiceException, IOException
	{
		YFormDefinitionData yFormDefinitionData = new YFormDefinitionData();
		yFormDefinitionData.setApplicationId("ApplicationId");
		yFormDefinitionData.setFormId("FormId");

		String incorrectContent = getContentAsString("/test/data/vulnerable-input.xhtml");
		FormDefinitionUtils.getFormDefinitionContent(incorrectContent, yFormDefinitionData);
	}

	@Test
	public void getFormDefinitionMetadataShouldReturnCorrectResultTest() throws IOException, YFormServiceException
	{
		final String content = getContentAsString("/test/data/correct-input.xhtml");
		final Map<String, String> metaMap = FormDefinitionUtils.getFormDefinitionMetadata(content);
		assertEquals("XXE Test", metaMap.get("title"));
		assertEquals("This form is correct", metaMap.get("description"));
	}

	@Test(expected = YFormServiceException.class)
	public void getFormDefinitionMetadataShouldThrowException() throws IOException, YFormServiceException
	{
		final String content = getContentAsString("/test/data/invalid-input.xhtml");
		FormDefinitionUtils.getFormDefinitionMetadata(content);
	}

	@Test
	public void normalizeShouldReturnCorrectResultTest() throws IOException, YFormServiceException
	{
		final String result = FormDefinitionUtils.normalize("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><a></a>");
		assertEquals(result, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><a/>");
	}

	@Test
	public void getFormDefinitionShouldReturnCorrectResultTest() throws IOException, YFormServiceException
	{
		final String content = getContentAsString("/test/data/correct-input.xhtml");
		final String result = FormDefinitionUtils.getFormDefinition(content);
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><form>\n" + "                    <section-1>\n"
				+ "                        <grid-1>\n" + "                            <control-1/>\n"
				+ "                            <control-2/>\n" + "                        </grid-1>\n"
				+ "                    </section-1>\n" + "                </form>", result);
	}

	@Test(expected = YFormServiceException.class)
	public void getFormDefinitionShouldThrowExceptionTest() throws IOException, YFormServiceException
	{
		final String content = getContentAsString("/test/data/invalid-input.xhtml");
		FormDefinitionUtils.getFormDefinition(content);
	}

	@Test
	public void getIdFromCreatingEmptyDataTest() throws IOException, YFormServiceException
	{
		String response = getContentAsString("/test/data/response-with-data-id.xml");
		String createdId = FormDefinitionUtils.getFormDataIdFromResponse(response);
		assertEquals("id-created-for-test", createdId);
	}

	@Test(expected = YFormServiceException.class)
	public void getFormDataIdFromResponseShoulfReturnExceptionTest() throws YFormServiceException, IOException
	{
		String response = getContentAsString("/test/data/invalid-input.xhtml");
		FormDefinitionUtils.getFormDataIdFromResponse(response);
	}

	private String getContentAsString(String filename) throws IOException
	{
		String resource = this.getClass().getResource(filename).getPath();
		return Files.readString(Paths.get(resource));
	}
}

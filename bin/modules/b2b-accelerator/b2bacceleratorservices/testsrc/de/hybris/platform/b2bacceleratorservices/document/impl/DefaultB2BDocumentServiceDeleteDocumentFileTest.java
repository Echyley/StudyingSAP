/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.hybris.platform.b2bacceleratorservices.document.B2BIntegrationTest;
import de.hybris.platform.b2bacceleratorservices.company.B2BDocumentService;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@IntegrationTest
public class DefaultB2BDocumentServiceDeleteDocumentFileTest extends
		B2BIntegrationTest {
	
	@Resource
	private B2BDocumentService b2bDocumentService;
	@Resource
	private MediaService mediaService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
	
	@Before
	public void setUp() throws Exception {
		createCoreData();
		importCsv("/b2bacceleratorservices/test/testOrganizations.csv", "utf-8");
		importCsv("/b2bacceleratorservices/test/testB2bdocument.csv", "utf-8");
	}
	
	@Test
	public void shouldDeleteB2BDocumentFile()
	{
		MediaModel mediaModel = mediaService.getMedia("documentMedia2");
		TestCase.assertNotNull(mediaModel);
		
		int numberOfDay = -1;

		B2BDocumentTypeModel documentType1 = new B2BDocumentTypeModel();
		documentType1.setCode( "Invoice");
		List<B2BDocumentTypeModel> documentTypes = Arrays.asList(flexibleSearchService .getModelByExample(documentType1));    
		List<DocumentStatus> documentStatuses = Arrays.asList(DocumentStatus.OPEN);
		
		b2bDocumentService.deleteB2BDocumentFiles(numberOfDay, documentTypes, documentStatuses);
	 
		//document media should be deleted
		try{
				mediaService.getMedia("documentMedia2");
				TestCase.fail();
		}catch(UnknownIdentifierException e){
			TestCase.assertEquals("No media with code documentMedia2 can be found.", e.getMessage());
		}
		
		//the link between document and document media should be deleted as well
		B2BDocumentModel document = new B2BDocumentModel();
		document.setDocumentNumber("INV-002");
		document = flexibleSearchService .getModelByExample(document);
		TestCase.assertNotNull(document);
		TestCase.assertNull(document.getDocumentMedia());
		
	}
	
	@Test
	public void shouldNotDeleteB2BDocumentFile()
	{
		MediaModel mediaModel = mediaService.getMedia("documentMedia2");
		TestCase.assertNotNull(mediaModel);
		
		int numberOfDay = -1;

		List<DocumentStatus> documentStatuses = Arrays.asList(DocumentStatus.CLOSED);
		b2bDocumentService.deleteB2BDocumentFiles(numberOfDay, null, documentStatuses);
		
		//document media should NOT be deleted
		mediaModel = mediaService.getMedia("documentMedia2");
		TestCase.assertNotNull(mediaModel);
		
		//the link between document and document media should NOT be deleted either
		B2BDocumentModel document = new B2BDocumentModel();
		document.setDocumentNumber("INV-002");
		document = flexibleSearchService.getModelByExample(document);
		TestCase.assertNotNull(document);
		TestCase.assertEquals(document.getDocumentMedia().getCode(), "documentMedia2");
	}
	
}


/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceaddon.document.service.impl;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapinvoiceaddon.document.dao.B2BInvoiceDao;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class B2BInvoiceServiceImplTest
{
	B2BInvoiceServiceImpl classUnderTest;

	@Mock
	B2BInvoiceDao b2bInvoiceDao;

	final String invoiceDocumentNumber = "0090012503";

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new B2BInvoiceServiceImpl();
		classUnderTest.setB2bInvoiceDao(b2bInvoiceDao);
	}

	@Test
	public void test()
	{

		Mockito.when(b2bInvoiceDao.findInvoiceByDocumentNumber(invoiceDocumentNumber)).thenReturn(
				createSapB2BDocumentModel(invoiceDocumentNumber));
		Assert.assertEquals(invoiceDocumentNumber, classUnderTest.getInvoiceForDocumentNumber(invoiceDocumentNumber)
				.getDocumentNumber());
	}

	@Test
	public void testB2bInvoiceDao()
	{
		assertNotNull(classUnderTest.getB2bInvoiceDao());
	}

	private SapB2BDocumentModel createSapB2BDocumentModel(final String invoiceDocumentNumber)
	{
		final SapB2BDocumentModel b2bDocumentModel = new SapB2BDocumentModel();
		b2bDocumentModel.setDocumentNumber(invoiceDocumentNumber);
		return b2bDocumentModel;
	}

}

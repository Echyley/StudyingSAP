/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

import org.junit.Test;


@UnitTest
@SuppressWarnings("javadoc")
public class DocumentTypeMappingTest
{

	@Test
	public void testGetDocumentTypeByTransactionGroupForQuotation()
	{
		assertEquals(DocumentType.QUOTATION,
				DocumentTypeMapping.getDocumentTypeByTransactionGroup(RFCConstants.TRANSACTION_GROUP_QUOTATION));
	}

	@Test
	public void testGetDocumentTypeByProcessType()
	{
		assertEquals(DocumentType.QUOTATION, DocumentTypeMapping.getDocumentTypeByProcess(RFCConstants.TRANSACTION_TYPE_QUOTATION));
		assertEquals(DocumentType.RFQ, DocumentTypeMapping.getDocumentTypeByProcess(RFCConstants.TRANSACTION_TYPE_INQUIRY));
	}

	@Test
	public void testGetDocumentTypeByTransactionGroupForRFQ()
	{
		assertEquals(DocumentType.RFQ,
				DocumentTypeMapping.getDocumentTypeByTransactionGroup(RFCConstants.TRANSACTION_GROUP_INQUIRY));
	}


}

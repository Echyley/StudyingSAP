/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.hook;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;


/**
 * Hook interface for SalesDocumentERP
 */
public interface SalesDocumentERPHook
{

	/**
	 * @param salesDocument
	 * @param aJCoCon
	 */
	void afterWriteDocument(SalesDocument salesDocument, JCoConnection aJCoCon);

	/**
	 * @param salesDocument
	 * @param aJCoCon
	 */
	void afterReadFromBackend(SalesDocument salesDocument, JCoConnection aJCoCon);

}

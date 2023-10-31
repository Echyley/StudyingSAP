/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;


/**
 * The SalesDocumentConfiguration interface handles all kind of application settings relevant for the Sales Document.
 *
 */
public interface SalesDocumentConfiguration extends BusinessObject
{

	/**
	 * @deprecated Returns the property processType, which is provided by the sales module
	 * @return processType
	 */
	@Deprecated(since = "ages", forRemoval= true )
	String getProcessType();

}

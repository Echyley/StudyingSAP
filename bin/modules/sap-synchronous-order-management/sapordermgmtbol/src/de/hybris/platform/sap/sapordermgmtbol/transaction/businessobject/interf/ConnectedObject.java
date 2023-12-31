/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;


/**
 * Represents the ConnectedObject object. <br>
 *
 */
public interface ConnectedObject extends BusinessObject, Cloneable
{

	/**
	 * Transfer update type which is used for a predecessor document in the back end.
	 */
	String IL_TRANSFER_AND_UPDATE = "A";

	/**
	 * Transfer update type which filled for a the predecessor document if no information from back end is found.
	 */
	String IL_NO_TRANSFER_AND_UPDATE = "B";

	/**
	 * Transfer update type undefined.
	 */
	String UNDEFINED = "";

	/**
	 * Document Origin CRM<br>
	 * If the document Origin has this value, then the was the document initially created in the CRM Back end.
	 */
	String DOC_ORIGIN_CRM = "CRM";

	/**
	 * Returns the document number which is used to identify the document in the backend. <br>
	 *
	 * @return Document number
	 */
	String getDocNumber();

	/**
	 * Sets the reference GUID.<br>
	 *
	 * @param refGuid
	 *           Reference GUID
	 */
	void setRefGuid(String refGuid);

	/**
	 * Returns the reference GUID which is used to identify the document-header or item in the backend. <br>
	 *
	 * @return Reference GUID
	 */
	String getRefGuid();

	/**
	 * Returns the document item number which is used to identify the document item in the backend.<br>
	 *
	 * @return Document item number
	 */
	String getDocItemNumber();

	/**
	 * Sets the document number which is used to identify the document in the backend. <br>
	 *
	 * @param docNumber
	 *           Document number
	 */
	void setDocNumber(String docNumber);

	/**
	 * Sets the document item number which is used to identify the document in the backend.<br>
	 *
	 * @param docItemNumber
	 *           Document item number
	 */
	void setDocItemNumber(String docItemNumber);

	/**
	 * Returns the document type which characterises the document in the backend (e.g. order, quotation, order template).
	 * <br>
	 *
	 * @return Document type
	 */
	DocumentType getDocType();

	/**
	 * Sets the document type.<br>
	 *
	 * @param docType
	 *           the document type
	 */
	void setDocType(DocumentType docType);

	/**
	 * Returns the binary transfer update type.<br>
	 *
	 * @return Binary transfer update type
	 */
	String getTransferUpdateType();

	/**
	 * Sets the binary transfer update type.<br>
	 *
	 * @param transferUpdateType
	 *           Binary transfer update type
	 */
	void setTransferUpdateType(String transferUpdateType);

	/**
	 * Returns the displayable property. <br>
	 *
	 * @return true, if the document may be displayed in the order status
	 */
	boolean isDisplayable();

	/**
	 * Sets the displayable property. <br>
	 *
	 * @param displayable
	 *           controls whether the connected object should be displayed or not
	 */
	void setDisplayable(boolean displayable);

	/**
	 * Sets the document origin (e.g RFC destination) of a document.<br>
	 *
	 * @param docOrigin
	 *           Documents origin
	 */
	void setDocumentOrigin(String docOrigin);

	/**
	 * Returns the origin of the document (e.g RFC destination).<br>
	 *
	 * @return Documents origin
	 */
	String getDocumentOrigin();

	/**
	 * Sets the business object type of the document.<br>
	 *
	 * @param busObjectType
	 *           Business object type
	 */
	void setBusObjectType(String busObjectType);

	/**
	 * Returns the business object type of the document.<br>
	 *
	 * @return Business object type
	 */
	String getBusObjectType();

	/**
	 * Performs a deep-copy of the object rather than a shallow copy.<br>
	 *
	 * @return returns a deep copy
	 */
	@SuppressWarnings(
	{ "squid:S1161", "squid:S2975" })
	Object clone();
}
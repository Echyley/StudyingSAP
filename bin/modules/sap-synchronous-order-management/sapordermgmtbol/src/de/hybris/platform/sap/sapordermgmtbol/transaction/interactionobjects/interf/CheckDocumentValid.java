/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.interactionobjects.interf;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;


/**
 * In general this class deals with checks that influence if a document is considered as valid or not. In particular
 * this class provides checks for the following.<br>
 * <ul>
 * <li>Authority Checks on header level</li>
 * <li>Checks for the basket, controlling if it can be checked out</li>
 * <li>Checks to find out if an attached message belongs to checkout or basket</li>
 * <li>Checks if a document can be handled in WEC. Certain documents, e.g. value contracts not referring to materials,
 * cannot be handled.</li>
 * </ul>
 * 
 */
public interface CheckDocumentValid
{
	/**
	 * Collects header messages of the sales document.<br>
	 * 
	 * @param salesDoc
	 *           where the messages are collected
	 * @return list of all header messages
	 */
	MessageList collectHeaderMessages(SalesDocument salesDoc);


	/**
	 * Check if the specified soldTo is allowed to see an order
	 * 
	 * @param order
	 *           BOL order
	 * @param soldToId
	 *           ID of a soldto-party
	 * @return Has permissions?
	 */
	boolean hasPermissions(Order order, String soldToId);

	/**
	 * Checks whether we can handle the transaction in WebChannel. Certain documents are excluded from MyAccount and
	 * search, but as navigation is also possible with the document ID, we need to cover this in the UI.
	 * 
	 * @param salesDoc
	 *           salesdocument to check
	 * @param expectedDocType
	 *           the expected document type
	 * @return is relevant for WebChannel
	 * @throws CommunicationException
	 *            Is raised when the communication with the back end failed
	 */
	boolean isDocumentSupported(SalesDocument salesDoc, DocumentType expectedDocType) throws CommunicationException;
}

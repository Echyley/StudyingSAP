/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import de.hybris.platform.sap.core.bol.businessobject.BusinessObject;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;

import com.sap.conn.jco.JCoRecord;
import com.sap.conn.jco.JCoTable;


/**
 * Maps backend messages according to messages.xml
 */
public interface BackendMessageMapper
{

	/**
	 * Maps messages contained in the provided table and/or structure to a BO. Mapping is according to the rules defined
	 * in messages.xml
	 * 
	 * @param rootBo
	 *           BO which gets the messages
	 * @param singleMsg
	 *           Error structure
	 * @param msgTable
	 *           Error table, typically of structure BAPIRET2
	 * @throws BackendException
	 */
	public void map(BusinessObject rootBo, //
			JCoRecord singleMsg, JCoTable msgTable) throws BackendException;

	/**
	 * Maps messages contained in the provided table structure to a BO. Mapping is according to the rules defined in
	 * messages.xml
	 * 
	 * @param rootBo
	 *           BO which gets the messages
	 * @param struct
	 * @return A BOL message
	 * @throws BackendException
	 */
	public Message map(BusinessObject rootBo, JCoRecord struct) throws BackendException;

}

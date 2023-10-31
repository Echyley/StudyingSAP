/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.TransactionConfiguration;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.LoadOperation;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy.BaseStrategyERP.ReturnValue;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.AdditionalPricing;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.BackendState;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util.BackendCallResult;


/**
 * Standard actions provided by the LORD API; such as save, load, copy, etc. <br>
 *
 */
public interface LrdActionsStrategy
{

	/**
	 * Object type constant to delete partner function.
	 */
	String SHIPTO_PARTNER = "SHIPTO_PARTNER";

	/**
	 * Object type constant to delete sales document items.
	 */
	String ITEMS = "ITEMS";

	/**
	 * Strategy for ERP_LORD_DO_ACTIONS.
	 *
	 * @param shop
	 *           The shop object
	 * @param salesDoc
	 *           The sales document (e.g. basket, order, ...)
	 * @param cn
	 *           Connection to use
	 * @param objectName
	 *           Object type to delete, one of <code>DoActionsStrategy.CREDIT_CARD</code>,
	 *           <code>DoActionsStrategy.ITEMS</code> or <code>DoActionsStrategy.SHIP_TO_PARTNER</code>.
	 * @param itemsToDelete
	 *           Array of <code>TechKeys</code> of the items to delete. Use <code>DoActionsStrategy.NO_ITEMS</code> if no
	 *           items are to be deleted.
	 * @return Object containing messages of call and (if present) the return code generated by the function module.
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	ReturnValue executeLrdDoActionsDelete(TransactionConfiguration shop, SalesDocument salesDoc, JCoConnection cn,
			String objectName, TechKey[] itemsToDelete) throws BackendException;

	/**
	 * Strategy for ERP_LORD_SAVE.
	 *
	 * @param posd
	 *           The sales document (e.g. basket, order, ...)
	 * @param commit
	 *           Specifies if the save should be committed also
	 * @param cn
	 *           Connection to use
	 * @return Object containing messages of call and (if present) the return code generated by the function module.
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	BackendCallResult executeLrdSave(SalesDocument posd, boolean commit, JCoConnection cn) throws BackendException;

	/**
	 * Strategy for ERP_LORD_LOAD.
	 *
	 * @param posd
	 *           The sales document (e.g. basket, order, ...)
	 * @param erpDocument
	 *           back-end layer representation of ERP sales document
	 * @param cn
	 *           Connection to use
	 * @param loadState
	 *           operation with should be performed, create, display or
	 * @return Object containing messages of call and (if present) the return code generated by the function module.
	 * @throws BackendException
	 *            in case of a back-end error
	 */
	BackendCallResult executeLrdLoad(SalesDocument posd, BackendState erpDocument, JCoConnection cn, LoadOperation loadState)
			throws BackendException;


	/**
	 * Strategy for ERP_LORD_SET_ACTIVE_FIELDS. Allows to instruct LRD API to perform only checks on fields which are
	 * actually in use (performance).<br>
	 * The "ActiveFieldsList" should be filled as follows: <br>
	 * <br>
	 * <code>
	 *     ArrayList activFieldList = <b>new</b> ArrayList();<br>
	 *     SetActiveFieldsListEntry aflE = <b>new</b> SetActiveFieldsListEntry("HEAD", "BSTKD");<br>
	 *     activeFieldList.add(aflE);<br>
	 *   </code>
	 *
	 * @param cn
	 *           Connection to use
	 * @return Object containing messages of call and (if present) the return code generated by the function module.
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	ReturnValue executeSetActiveFields(JCoConnection cn) throws BackendException;

	/**
	 * Executes ERP_LORD_DO_ACTIONS with PRICING action on header level. Updates the freights, i.e. runs with pricing
	 * type 'H'<br>
	 *
	 * @param salesDoc
	 *           Sales document
	 * @param cn
	 *           JCO connection to ERP
	 * @param transConf
	 *           Sales transaction specific customizing
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void executeLrdDoActionsDocumentPricing(SalesDocument salesDoc, JCoConnection cn, TransactionConfiguration transConf)
			throws BackendException;

	/**
	 * Executes ERP_LORD_DO_ACTIONS with PRICING action on header level. <br>
	 *
	 * @param salesDoc
	 *           Sales document
	 * @param pricingType
	 *           Pricing type. See {@link AdditionalPricing#getPriceType()}
	 * @param cn
	 *           JCO connection to ERP
	 * @param transConf
	 *           Sales transaction specific customising
	 * @throws BackendException
	 *            in case of a back-end or communication error
	 */
	void executeLrdDoActionsDocumentPricing(SalesDocument salesDoc, String pricingType, JCoConnection cn,
			TransactionConfiguration transConf) throws BackendException;

}

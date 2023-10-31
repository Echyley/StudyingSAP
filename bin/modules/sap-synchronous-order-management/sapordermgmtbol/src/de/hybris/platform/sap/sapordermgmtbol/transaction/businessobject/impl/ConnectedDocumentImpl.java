/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;


/**
 * The ConnectedDocument represents an entry in the document flow.
 *
 * @see de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument
 */
@SuppressWarnings("squid:S2160")
public class ConnectedDocumentImpl extends ConnectedObjectImpl implements ConnectedDocument
{

	/**
	 * Reference to related document
	 */
	protected String refGuid = "";
	/**
	 * Application type
	 */
	protected String appTyp = "";
	/**
	 * Document origen
	 */
	protected String docOrigin = "";

	/**
	 * Returns the document number
	 *
	 * @return document number is used to identify the document in the backend
	 */
	@Override
	public String getRefGuid()
	{
		return refGuid;
	}

	/**
	 * Sets the document number
	 */
	@Override
	public void setRefGuid(final String refGuid)
	{
		this.refGuid = refGuid;
	}

	/**
	 * Returns the document number
	 *
	 * @return document type is used to identify the backend of the document (one order document (1) or from CRM billing (B)
	 */
	@Override
	public String getAppTyp()
	{
		return appTyp;
	}

	/**
	 * Sets the document type used to identify the back end of the document (one order document or from CRM billing)
	 */
	@Override
	public void setAppTyp(final String appTyp)
	{
		this.appTyp = appTyp;
	}

	/**
	 * Set the origin (e.g RFC destination) of a document
	 *
	 * @param docOrigin
	 *           containing a documents origin
	 */
	@Override
	public void setDocumentOrigin(final String docOrigin)
	{
		this.docOrigin = docOrigin;
	}

	/**
	 * Returns the origin (e.g RFC destination) of a document
	 *
	 * @return String containing a documents origin
	 */
	@Override
	public String getDocumentOrigin()
	{
		return this.docOrigin;
	}

	@Override
	public String getBusObjectType()
	{

		return busObjectType;
	}

	@Override
	public void setBusObjectType(final String busObjectType)
	{

		this.busObjectType = busObjectType;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @seecom.sap.wec.app.common.module.transaction.businessobject.impl. ConnectedObjectImpl#clone()
	 */
	@Override
	@SuppressWarnings("squid:S2975")
	public Object clone()
	{
		// we only contain immutable fields so super clone is fine.
		return super.clone();
	}

}

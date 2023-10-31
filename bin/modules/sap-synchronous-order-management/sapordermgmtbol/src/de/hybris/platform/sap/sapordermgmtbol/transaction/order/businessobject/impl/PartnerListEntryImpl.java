/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerListEntry;


/**
 * An entry in the partner list, attached to a sales document
 */
public class PartnerListEntryImpl implements PartnerListEntry, Cloneable
{

	private static final long serialVersionUID = 1L;

	/**
	 * Partner guid (in ERP case: like ID)
	 */
	protected TechKey partnerGUID;

	/**
	 * Partner ID
	 */
	protected String partnerId;

	/**
	 * Handle
	 */
	protected String handle = "";

	/**
	 * Creates a new <code>PartnerListEntry</code> object.
	 */
	public PartnerListEntryImpl()
	{
		// Default constructor
	}

	/**
	 * Creates a new <code>PartnerListEntry</code> object.
	 *
	 * @param partnerGUID
	 *           techkey of the business partner
	 * @param partnerId
	 *           id of the business partner
	 * @deprecated This constructor is deprecated.
	 */
	@Deprecated(since = "ages", forRemoval = true)
	public PartnerListEntryImpl(final TechKey partnerGUID, final String partnerId)
	{
		this.partnerGUID = partnerGUID;
		this.partnerId = partnerId;
	}

	/**
	 * Get the TechKey of the PartnerListEntry
	 *
	 * @return TechKey of entry
	 */
	@Override
	public TechKey getPartnerTechKey()
	{
		return partnerGUID;
	}

	/**
	 * Set the TechKey of the PartnerListEntry
	 *
	 * @param partnerGUID
	 *           techkey of the business partner
	 */
	@Override
	public void setPartnerTechKey(final TechKey partnerGUID)
	{
		this.partnerGUID = partnerGUID;
	}

	/**
	 * get the Id of the PartnerListEntry
	 *
	 * @return id of entry
	 */
	@Override
	public String getPartnerId()
	{
		return partnerId;
	}

	/**
	 * Set the TechKey of the PartnerListEntry
	 *
	 * @param partnerId
	 *           bp-id of the business partner
	 */
	@Override
	public void setPartnerId(final String partnerId)
	{
		this.partnerId = partnerId;
	}


	/**
	 * Performs a copy of the object.
	 *
	 * @return copy of this object
	 */
	@Override
	@SuppressWarnings("squid:S2975")
	public Object clone()
	{
		PartnerListEntry myClone;
		try
		{
			myClone = (PartnerListEntry) super.clone();
		}
		catch (final CloneNotSupportedException e)
		{

			throw new ApplicationBaseRuntimeException("Could not clone PartnerListEntry", e);

		}
		myClone.setPartnerTechKey(partnerGUID);
		myClone.setPartnerId(partnerId);

		return myClone;

	}

	/**
	 * This method returns the handle, as an alternative key for the business object, because at the creation point no
	 * techkey for the object exists. Therefore maybe the handle is needed to identify the object in back end return the
	 * handle of business object which is needed to identify the object in the back end, if the techkey still not exists
	 */
	@Override
	public String getHandle()
	{
		return handle;
	}

	/**
	 * This method sets the handle, as an alternative key for the business object, because at the creation point no techkey
	 * for the object exists. Therefore maybe the handle is needed to identify the object in back end
	 *
	 * @param handle
	 *           the handle of business object which identifies the object in the back end, if the techkey still not exists
	 */
	@Override
	public void setHandle(final String handle)
	{
		this.handle = handle;
	}

}

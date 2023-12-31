/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;

import java.util.Date;


/**
 * <b>Immutable Object.</b> Default implementation of {@link KBKey}.
 */
public class KBKeyImpl implements KBKey
{

	private final String productCode;
	private final String kbName;
	private final String kbLogsys;
	private final String kbVersion;
	private final Date date;
	private static final int PRIME = 31;


	/**
	 * Minimal constructor, will use the actual Date for the KB key. Other fields will be null.
	 *
	 * @param productCode
	 *           code of the product this KBKey represents
	 */
	public KBKeyImpl(final String productCode)
	{
		this(productCode, null, null, null, new Date());
	}

	/**
	 * Convenience constructor, will use the actual Date for the KB key.
	 *
	 * @param productCode
	 *           code of the product this KBKey represents
	 * @param kbName
	 *           name of KB object
	 * @param kbLogsys
	 *           name of the KN logical system
	 * @param kbVersion
	 *           version of the KB object
	 */
	public KBKeyImpl(final String productCode, final String kbName, final String kbLogsys, final String kbVersion)
	{
		this(productCode, kbName, kbLogsys, kbVersion, new Date());
	}

	/**
	 * Constructor that allows to set all fields.
	 *
	 * @param productCode
	 *           code of the product this KBKey represents
	 * @param kbName
	 *           name of KB object
	 * @param kbLogsys
	 *           name of the KN logical system
	 * @param kbVersion
	 *           version of the KB object
	 * @param date
	 *           KB needs to be valid at this date to match this KBKey
	 */
	public KBKeyImpl(final String productCode, final String kbName, final String kbLogsys, final String kbVersion, final Date date)
	{
		this.productCode = productCode;
		this.kbName = kbName;
		this.kbLogsys = kbLogsys;
		this.kbVersion = kbVersion;
		if (date != null)
		{
			this.date = (Date) date.clone();
		}
		else
		{
			this.date = new Date();
		}
	}


	@Override
	public String getProductCode()
	{
		return productCode;
	}

	@Override
	public String getKbName()
	{
		return kbName;
	}

	@Override
	public String getKbLogsys()
	{
		return kbLogsys;
	}

	@Override
	public String getKbVersion()
	{
		return kbVersion;
	}

	@Override
	public Date getDate()
	{
		return (Date) date.clone();
	}


	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("KBKeyImpl [productCode=");
		builder.append(productCode);
		builder.append(", kbName=");
		builder.append(kbName);
		builder.append(", kbLogsys=");
		builder.append(kbLogsys);
		builder.append(", kbVersion=");
		builder.append(kbVersion);
		builder.append(", date=");
		builder.append(date);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		int result = 1;
		result = PRIME * result + date.hashCode();
		result = PRIME * result + ((kbName == null) ? 0 : kbName.hashCode());
		result = PRIME * result + ((kbLogsys == null) ? 0 : kbLogsys.hashCode());
		result = PRIME * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = PRIME * result + ((kbVersion == null) ? 0 : kbVersion.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final KBKeyImpl other = (KBKeyImpl) obj;
		if (!date.equals(other.date))
		{
			return false;
		}
		if (kbName == null)
		{
			if (other.kbName != null)
			{
				return false;
			}
		}
		else if (!kbName.equals(other.kbName))
		{
			return false;
		}
		if (kbLogsys == null)
		{
			if (other.kbLogsys != null)
			{
				return false;
			}
		}
		else if (!kbLogsys.equals(other.kbLogsys))
		{
			return false;
		}
		if (productCode == null)
		{
			if (other.productCode != null)
			{
				return false;
			}
		}
		else if (!productCode.equals(other.productCode))
		{
			return false;
		}
		if (kbVersion == null)
		{
			if (other.kbVersion != null)
			{
				return false;
			}
		}
		else if (!kbVersion.equals(other.kbVersion))
		{
			return false;
		}
		return true;
	}

}

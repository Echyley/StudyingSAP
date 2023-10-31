/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.adapters;

import de.hybris.platform.assistedservicewebservices.dto.Customer360DataWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import javax.annotation.Resource;


/**
 * Customer360 fragments data adapter
 */
public abstract class C360FragmentDataAdapter<T>
{
	@Resource(name = "dataMapper")
	protected DataMapper dataMapper;

	/**
	 * Adapt data to Customer360DataWsDTO
	 *
	 * @param data FragmentData that need to do adapt
	 * @return Customer360DataWsDTO
	 */
	public abstract Customer360DataWsDTO adapt(T data);


	public void setDataMapper(DataMapper dataMapper)
	{
		this.dataMapper = dataMapper;
	}

	public DataMapper getDataMapper()
	{
		return dataMapper;
	}
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch;

public class HeaderTaskConfig
{

	private boolean propagateError = false;

	/**
	 * Defines whether to enable or disable propagating error when impex cronjob fails with system error. If enabled and an
	 * error occurs during the import, processed file will be moved to error, subsequent files will not be imported.
	 *
	 * @param propagateError
	 *           false if not propagate error
	 */
	public void setPropagateError(final boolean propagateError)
	{
		this.propagateError = propagateError;
	}

	/**
	 * Whether to propagate error or not when impex cronjob fails with system error, the value is false by default.
	 *
	 * @return false if not propagate error
	 */
	public boolean isPropagateError()
	{
		return propagateError;
	}
}

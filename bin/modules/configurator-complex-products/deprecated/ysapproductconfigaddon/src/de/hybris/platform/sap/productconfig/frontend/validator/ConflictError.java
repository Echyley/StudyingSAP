/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.CsticData;


/**
 * A conflict error is a specific validation error indicating that the value of the cstic is in conflict with another
 * cstic value.<br>
 * Technical a conflict is not limited to be caused by exactly two cstics. It is also possible that only one or more
 * than 2 cstics are participating in the conflict. However 2 cstics participating is the typical case.<br>
 * In contrast to other validation errors, it is still safe to send user input causing a conflict error to the
 * configuration engine, hence it is treated within CPQ more like a 'warning'.<br>
 * Object is immutable.
 */
public class ConflictError extends CSticRelatedFieldError
{

	/**
	 * Default constructor.
	 *
	 * @param cstic
	 *           cstic causing the error
	 * @param path
	 *           path to the UI field causing the error
	 * @param rejectedValue
	 *           user input causing the error
	 * @param errorCodes
	 *           error codes
	 * @param defaultMessage
	 *           message to be displayed on the UI
	 */
	public ConflictError(final CsticData cstic, final String path, final String rejectedValue, final String[] errorCodes,
			final String defaultMessage)
	{
		super(cstic, path, rejectedValue, errorCodes, defaultMessage);
	}

}

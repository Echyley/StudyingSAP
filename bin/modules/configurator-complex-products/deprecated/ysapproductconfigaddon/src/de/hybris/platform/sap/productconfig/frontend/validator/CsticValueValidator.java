/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.CsticData;

import org.springframework.validation.Errors;


/**
 * Interface for validating a characteristic value
 */
public interface CsticValueValidator
{

	/**
	 * Validates the value of the cstic.
	 *
	 * @param cstic
	 *           to access any meta data for validation
	 * @param errorObj
	 *           to add errors if found
	 * @param value
	 *           actual value to check
	 * @return modified value, or <code>null</code> if no value modification is required
	 */
	String validate(final CsticData cstic, final Errors errorObj, String value);


	/**
	 * @param cstic
	 * @return <code>true</code> only if this validator is apllicable to this cstic
	 */
	boolean appliesTo(CsticData cstic);

	/**
	 * @return <code>true</code> only if this validator is apllicable to this cstic values
	 */
	boolean appliesToValues();

	/**
	 * @return <code>true</code> only if this validator is apllicable to formatted cstic values
	 */
	boolean appliesToFormattedValues();


	/**
	 * @return <code>true</code> only if this validator is apllicable to additional cstic values
	 */
	boolean appliesToAdditionalValues();
}

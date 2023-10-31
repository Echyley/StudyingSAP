/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service;



/**
 * A switchable entry number hook can globally activate / deactivate order entry number spacing. This is generally
 * useful in the junit tenant, to avoid side effects. Other tests might assume default entry numbering schema 0,1,2,...,
 * so entry number spacing can only be activated for tests, that are aware of it.
 */
public interface SwitchableEntryNumberHook
{

	/**
	 * globally activates entry number spacing
	 */
	void activateEntryNumberSpacing();

	/**
	 * globally deactivates entry number spacing
	 */
	void deActivateEntryNumberSpacing();

}

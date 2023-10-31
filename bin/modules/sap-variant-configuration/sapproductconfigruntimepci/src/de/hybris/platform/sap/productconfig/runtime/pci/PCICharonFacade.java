/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.pci;

import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;


/**
 * Facilitates charon calls to Product Configuration Intelligence (PCI) analytics REST service
 */
public interface PCICharonFacade
{
	/**
	 * Creates analytics document containing e.g. popularity information for possible values
	 *
	 * @param analyticsDocumentInput
	 *           input document
	 * @return analytics document
	 */
	AnalyticsDocument createAnalyticsDocument(AnalyticsDocument analyticsDocumentInput);

}

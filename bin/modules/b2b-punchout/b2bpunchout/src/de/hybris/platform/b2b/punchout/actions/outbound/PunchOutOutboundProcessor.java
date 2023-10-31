/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import org.cxml.CXML;


/**
 * A generic outbound processor that depending on its purpose will generate a CXML
 *
 */
public interface PunchOutOutboundProcessor
{

	/**
	 * Generates the cXML for the command from session .
	 */
	CXML generatecXML();

}

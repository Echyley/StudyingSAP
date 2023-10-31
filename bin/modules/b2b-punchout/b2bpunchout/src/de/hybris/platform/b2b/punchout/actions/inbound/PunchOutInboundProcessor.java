/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import org.cxml.CXML;


/**
 * An inbound processor that populate a CXML response for a cXML Profile Request
 *
 */
public interface PunchOutInboundProcessor
{

	/**
	 * Generates the cXML for the result.
	 */
	CXML generatecXML(final CXML request);

}

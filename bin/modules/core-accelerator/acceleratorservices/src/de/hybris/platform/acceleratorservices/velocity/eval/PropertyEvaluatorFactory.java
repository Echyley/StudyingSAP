/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;


public interface PropertyEvaluatorFactory
{
	PropertyEvaluator create(ScriptUrlContext urlContext);
}

/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.resource.store;

import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import java.io.Reader;
import java.io.Writer;

public interface ScriptReadWriteHandler
{
	Reader prepareTemplateReader(final ScriptUrlContext urlContext);

	void confirmTarget(final ScriptUrlContext urlContext);

	Writer prepareTemporaryTargetWriter(final ScriptUrlContext urlContext);
}

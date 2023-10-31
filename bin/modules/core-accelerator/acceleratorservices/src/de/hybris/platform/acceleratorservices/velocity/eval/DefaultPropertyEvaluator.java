/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.eval;

import de.hybris.platform.acceleratorservices.velocity.eval.key.InvalidPropertyKeyFormatException;
import de.hybris.platform.acceleratorservices.velocity.resource.PropertyFileMissingHandler;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;
import de.hybris.platform.acceleratorservices.velocity.util.LocalizedPropertyUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;


/**
 *
 * Object available in the velocity context.
 *
 */
public class DefaultPropertyEvaluator implements PropertyEvaluator
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPropertyEvaluator.class);

	private final ScriptUrlContext ctx;
	private final PropertyFileMissingHandler missingHandler;

	private RowsEvaluator<? extends EvaluatedRow<?, ?>, ?> rowEvaluator;

	public DefaultPropertyEvaluator(final PropertyFileMissingHandler handler, final ScriptUrlContext ctx)
	{
		this.ctx = ctx;
		this.missingHandler = handler;
	}

	@Override
	public Collection<? extends EvaluatedRow<?, ?>> load(final String resourceId)
	{
		return load(resourceId, null);
	}

	@Override
	public Collection<? extends EvaluatedRow<?, ?>> load(final String resourceId, final String key)
	{
		final DefaultResourceLoader loader = getDefaultResourceLoader();

		final Resource localizedPropertyResource = loader.getResource("file:" + ctx.getPropertyEffectivePath(resourceId));
		//handle not existing resource
		if (localizedPropertyResource.exists() && localizedPropertyResource.isReadable())
		{
			final Map properties = loadProperties(localizedPropertyResource);
			try
			{
				final RowsEvaluator<? extends EvaluatedRow<?, ?>, ?> rowFactory = getRowEvaluator(properties);
				Set<? extends EvaluatedRow<?, ?>> result = null;
				if (key == null)
				{
					result = rowFactory.evaluateRows(properties);
				}
				else
				{
					result = rowFactory.evaluateRows(key, properties);
				}
				if (LOG.isDebugEnabled())
				{
					for (final EvaluatedRow<?, ?> eachRow : result)
					{
						LOG.debug(eachRow.getKey().toString());
					}
				}
				return result;
			}
			catch (final InvalidPropertyKeyFormatException e)
			{
				LOG.error("During processing property [{}] occurred.", localizedPropertyResource.getFilename(), e);
				throw e;
			}
		}
		else
		{
			missingHandler.handle(ctx, localizedPropertyResource);
		}

		return Collections.emptySet();
	}

	protected Map loadProperties(final Resource localizedPropertyResource)
	{
		try
		{
			return LocalizedPropertyUtil.loadProperties(localizedPropertyResource);
		}
		catch (final IOException e)
		{
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setRowEvaluator(final RowsEvaluator<? extends EvaluatedRow<?, ?>, ?> rowEvaluator)
	{
		this.rowEvaluator = rowEvaluator;
	}

	/**
	 * Factory method to create a specific {@link RowsEvaluator}
	 */
	protected RowsEvaluator<? extends EvaluatedRow<?, ?>, ?> getRowEvaluator(final Map properties)
	{
		return rowEvaluator;
	}

	protected DefaultResourceLoader getDefaultResourceLoader()
	{
		return new FileSystemResourceLoader();
	}
}

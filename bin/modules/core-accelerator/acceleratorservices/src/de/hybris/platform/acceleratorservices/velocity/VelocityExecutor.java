/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity;

import de.hybris.platform.acceleratorservices.velocity.core.CoreVelocityConfigurer;
import de.hybris.platform.acceleratorservices.velocity.eval.key.InvalidPropertyKeyFormatException;
import de.hybris.platform.acceleratorservices.velocity.resource.PropertyResourceNotFoundException;
import de.hybris.platform.acceleratorservices.velocity.resource.store.ScriptReadWriteHandler;
import de.hybris.platform.acceleratorservices.velocity.resource.url.ScriptUrlContext;

import java.io.Reader;
import java.io.Writer;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityExecutor
{
	private static final Logger LOG = LoggerFactory.getLogger(VelocityExecutor.class);

	private CoreVelocityConfigurer<VelocityEngine, Void> velocityEngineConfigurer;
	private ScriptReadWriteHandler readWriteHandler;

	public void execute(final ScriptUrlContext urlContext, final VelocityContext velocityContext)
	{
		execute(urlContext, velocityContext, new VelocityExecutionCallback()
		{
			@Override
			public void onSuccess(final ScriptReadWriteHandler handler)
			{
				handler.confirmTarget(urlContext);
			}

			@Override
			public void onError(final ScriptReadWriteHandler handler, final Throwable exception)
			{
				if (exception instanceof PropertyResourceNotFoundException)
				{
					LOG.warn(exception.getMessage());
				}
				else if (exception instanceof RuntimeException)
				{
					throw (RuntimeException) exception;
				}
				else
				{
					throw new RuntimeException(exception);
				}
			}
		});
	}

	private void execute(final ScriptUrlContext urlContext, final VelocityContext velocityContext,
			final VelocityExecutionCallback callback)
	{
		final ScriptReadWriteHandler handler = getReadWriteHandler();
		try
		{
			if (executeInternal(handler, urlContext, velocityContext))
			{
				callback.onSuccess(handler);//any action can be performed only after closing temporary streams
			}
		}
		catch (final MethodInvocationException e)
		{
			callback.onError(handler, e.getCause());
		}

	}

	private boolean executeInternal(final ScriptReadWriteHandler handler, final ScriptUrlContext urlContext,
			final VelocityContext velocityContext)
	{
		Writer writer = null;
		Reader reader = null;
		final long start = System.currentTimeMillis();

		try
		{
			reader = handler.prepareTemplateReader(urlContext);
			writer = handler.prepareTemporaryTargetWriter(urlContext);

			final VelocityEngine engine = getVelocityEngineConfigurer().configure();
			return engine.evaluate(velocityContext, writer, "[" + urlContext.getScriptFileName() + "]", reader);
		}
		catch (final InvalidPropertyKeyFormatException e)
		{
			LOG.error("Error processing property file for locale [{}] in  template [{}] for target script [{}}]",
					urlContext.getLocale(), buildScriptFilePath(urlContext), urlContext.getTargetScriptEffectivePath());
			throw e;
		}
		catch (final ParseErrorException e)
		{
			LOG.error("Error parsing template [{}] for target script [{}]",
					buildScriptFilePath(urlContext), urlContext.getTargetScriptEffectivePath());
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(reader);
			if (LOG.isDebugEnabled())
			{
				LOG.debug(" template [{}] evaluated in {} [ms] at :{} using [{}] ...",
						urlContext.getScriptFileName(), (System.currentTimeMillis() - start), urlContext.getScriptRootFolder(), urlContext.getLocale());
			}
		}
	}

	@Resource
	public void setReadWriteHandler(final ScriptReadWriteHandler readWritehandler)
	{
		this.readWriteHandler = readWritehandler;
	}

	protected ScriptReadWriteHandler getReadWriteHandler()
	{
		return this.readWriteHandler;
	}

	protected CoreVelocityConfigurer<VelocityEngine, Void> getVelocityEngineConfigurer()
	{
		return velocityEngineConfigurer;
	}

	protected String buildScriptFilePath(final ScriptUrlContext urlContext)
	{
		return urlContext.getTargetScriptEffectivePath();
	}

	@Resource
	public void setVelocityEngineConfigurer(final CoreVelocityConfigurer<VelocityEngine, Void> velocityEngineConfigurer)
	{
		this.velocityEngineConfigurer = velocityEngineConfigurer;
	}

	interface VelocityExecutionCallback
	{
		void onSuccess(ScriptReadWriteHandler handler);

		void onError(ScriptReadWriteHandler handler, Throwable exception);
	}
}

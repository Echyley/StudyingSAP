/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.setup;

import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;
import de.hybris.platform.commerceservices.setup.impl.DefaultSetupImpexService;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


/**
 * Extends the {@link DefaultSetupImpexService} with functionality for addons
 * 
 * @deprecated since 22.11 release due to using of the standard essentialdata impex logic
 */
@Deprecated(since = "2211", forRemoval = true)
public class ProductConfigRulesSetupImpexService extends DefaultSetupImpexService
{
	private static final Logger LOG = Logger.getLogger(ProductConfigRulesSetupImpexService.class);

	@Override
	public boolean importImpexFile(final String file, final Map<String, Object> macroParameters, final boolean errorIfMissing)
	{
		try (final InputStream resourceAsStream = getClass().getResourceAsStream(file))
		{
			if (resourceAsStream == null)
			{
				if (errorIfMissing)
				{
					LOG.error("Importing [" + file + "]... ERROR (MISSING FILE)", null);
				}
				else
				{
					LOG.info("Importing [" + file + "]... SKIPPED (Optional File Not Found)");
				}
				return false;
			}

			try (final var mergedInputStream = getMergedInputStream(macroParameters, resourceAsStream))
			{
				importImpexFile(file, mergedInputStream, false);
				return true;
			}
		}
		catch (final IOException e)
		{
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean importImpexFile(final String file, final ImpexMacroParameterData macroParameters, final boolean errorIfMissing)
	{
		final Map<String, Object> params = MapUtils.isNotEmpty(macroParameters.getAdditionalParameterMap())
				? new HashMap<>(macroParameters.getAdditionalParameterMap())
				: Collections.emptyMap();
		return importImpexFile(file, params, errorIfMissing);
	}

	protected String buildMacroHeader(final Map<String, Object> macroParameters)
	{
		// no pun intended with this method name
		final var builder = new StringBuilder();
		for (final Entry<String, Object> entry : macroParameters.entrySet())
		{
			final String macroName;
			if (entry.getKey().charAt(0) == '$')
			{
				macroName = entry.getKey();
			}
			else
			{
				macroName = '$' + entry.getKey();
			}

			final Object val = entry.getValue();
			builder.append(macroName).append("=").append(val == null ? "" : String.valueOf(val)).append("\n");
		}
		return builder.toString();
	}

	protected InputStream getMergedInputStream(final Map<String, Object> macroParameters, final InputStream fileStream)
	{

		if (macroParameters != null && !macroParameters.isEmpty())
		{
			final String header = buildMacroHeader(macroParameters);
			return new SequenceInputStream(IOUtils.toInputStream(header, StandardCharsets.UTF_8), fileStream);
		}
		else
		{
			return fileStream;
		}
	}

}

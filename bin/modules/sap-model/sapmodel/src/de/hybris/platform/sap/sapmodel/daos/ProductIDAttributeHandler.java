/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.daos;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.model.SAPProductIDDataConversionModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;


public class ProductIDAttributeHandler implements DynamicAttributeHandler<String, ProductModel>, Serializable
{
	private static final Logger LOGGER = Logger.getLogger(ProductIDAttributeHandler.class.getName());
	private static final long serialVersionUID = 1L;

	private static final int MAX_ID_LENGTH = 18;

	@Autowired
	protected transient FlexibleSearchService flexibleSearchService;

	protected String convertID(final String productID)
	{
		SAPProductIDDataConversionModel customizing = null;

		final SAPProductIDDataConversionModel data = new SAPProductIDDataConversionModel();
		data.setConversionID("MATCONV");
		try
		{
			customizing = flexibleSearchService.getModelByExample(data);
		}
		catch (final ModelNotFoundException e)
		{
			LOGGER.logp(Level.WARNING, ProductIDAttributeHandler.class.getName(), "convertID",
					"Missing SAPProductIDDataConversion customizing, using default value", e);
		}

		if (customizing == null)
		{
			data.setMatnrLength(MAX_ID_LENGTH);
			data.setDisplayLeadingZeros(false);
			data.setDisplayLexicographic(false);
			data.setMask("");
			customizing = data;
		}

		if (StringUtils.isEmpty(productID) || customizing.getDisplayLexicographic())
		{
			return productID;
		}

		final int maxLength = customizing.getMatnrLength();
		final String mask = Objects.toString(customizing.getMask(), "");

		final Set<Character> symbols = new HashSet<Character>();
		mask.codePoints()
		    .filter(c -> c != '_')
		    .mapToObj(c -> (char) c)
		    .forEach(c -> symbols.add(c));
		int leadZeroCount = getLeadingZeroCount(productID);

		//check if inputString is numeric
		final int size = productID.length();
		boolean isNumeric = isNumeric(productID, symbols, size);

		// Mask processing
		if (!StringUtils.isEmpty(mask))
		{
			return processMask(mask, productID, customizing, leadZeroCount, isNumeric);
		}

		if (!isNumeric)
		{
			return productID;
		}

		if (customizing.getDisplayLeadingZeros())
		{
			return size + maxLength < (MAX_ID_LENGTH + 1) ? "" : productID.substring(MAX_ID_LENGTH - maxLength,
					Math.min(size, MAX_ID_LENGTH));
		}

		//remove leading zeros
		return productID.substring(leadZeroCount);
	}



	public String get(final ProductModel model)
	{
		return this.convertID(model.getCode());
	}

	public void set(final ProductModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}

	private boolean isNumeric(final String productID, final Set<Character> symbols, final int size)
	{
		for (int i = 0; i < size; i++)
		{
			final char ch = productID.charAt(i);

			if ((ch > '9' || ch < '0') && !symbols.contains(ch))
			{
				return false;
			}
		}

		return true;
	}

	private String constructWorkProductID(final String productID, final SAPProductIDDataConversionModel customizing,
	                                      final int leadZeroCount, final boolean isNumeric, final int nonMarkCount)
	{
		String workProductID = productID;
		workProductID = workProductID.substring(0, Math.min(workProductID.length(), MAX_ID_LENGTH));
		workProductID = isNumeric && !customizing.getDisplayLeadingZeros()
				? workProductID.substring(Math.max(leadZeroCount, nonMarkCount))
				: workProductID;
		return workProductID;
	}
	private String processMask(final String mask, final String productID, final SAPProductIDDataConversionModel customizing,
			final int leadZeroCount, boolean isNumeric)
	{
		final int nonMarkCount = (int) mask.codePoints().filter(c -> c != '_').count();

		final String workProductID = constructWorkProductID(productID, customizing, leadZeroCount, isNumeric, nonMarkCount);

		if (leadZeroCount < nonMarkCount)
		{
			isNumeric = false;
		}

		final int workSize = workProductID.length();
		final int maskSize = mask.length();
		final StringBuilder sb = new StringBuilder(mask);
		int builtLength = 0;
		int j = 1;
		for (int i = 1; i <= maskSize && j <= workSize; i++)
		{
			final int maskIndex = isNumeric ? (maskSize - i) : (i - 1);
			if (sb.charAt(maskIndex) == '_')
			{
				sb.setCharAt(maskIndex, workProductID.charAt(isNumeric ? (workSize - j) : (j - 1)));
				j++;
			}
			builtLength = i;
		}
		if (builtLength == 0)
		{
			return mask.replace("_", " ").trim();
		}
		return isNumeric ? sb.substring(maskSize - builtLength, maskSize) : sb.substring(0, builtLength);
	}

	private int getLeadingZeroCount(final String productID)
	{
		// Count leading zeros
		int i = 0;
		while (i < productID.length() - 1 && productID.charAt(i) == '0')
		{
			i++;
		}
		return i;
	}
}

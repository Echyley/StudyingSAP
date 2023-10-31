/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.spring;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.sap.core.common.exceptions.CoreBaseRuntimeException;
import de.hybris.platform.sap.core.constants.SapcoreConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Generic populator configurer which allows to add additional populators to an existing converter.
 */
public class GenericPopulatorConfigurer
{

	private AbstractPopulatingConverter converter = null;
	private final Collection<Populator> populatorCollection = new ArrayList<Populator>();

	/**
	 * Inject setter for {@link AbstractPopulatingConverter}.
	 * 
	 * @param converter
	 *           {@link AbstractPopulatingConverter} to set
	 */
	public void setConverter(final AbstractPopulatingConverter converter)
	{
		this.converter = converter;
	}

	/**
	 * Inject setter for populator.
	 * 
	 * @param populator
	 *           {@link Populator} to set
	 */
	public void setPopulator(final Populator populator)
	{
		this.populatorCollection.add(populator);
	}

	/**
	 * Inject setter for populator collection.
	 * 
	 * @param populatorCollection
	 *           list {@link Populator} to set
	 */
	public void setPopulators(final Collection<Populator> populatorCollection)
	{
		this.populatorCollection.addAll(populatorCollection);
	}


	/**
	 * Adds the populator to the converter's population list.
	 */
	
	public void addPopulatorsToConverter()
	{
		// Check if Abstract Converter is available
		if (converter == null)
		{
			throw new CoreBaseRuntimeException("Converter exception: Converter not injected!");
		}
		List<Populator> populators = converter.getPopulators();
		if (populators == null)
		{
			populators = new ArrayList<Populator>();
		}

		populators.addAll(populatorCollection);
		converter.setPopulators(populators);
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcoreConstants.CRLF + "- Converter: " + this.converter.toString() + SapcoreConstants.CRLF
				+ "- Populators: " + this.populatorCollection.toString();
	}

}

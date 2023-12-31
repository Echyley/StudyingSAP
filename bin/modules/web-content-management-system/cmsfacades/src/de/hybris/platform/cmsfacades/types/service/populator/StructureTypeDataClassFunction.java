/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.types.service.populator;

import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Function that returns the Class that corresponds to the {@link ComposedTypeModel} type passed as an argument.
 */
public class StructureTypeDataClassFunction implements Function<ComposedTypeModel, Class>
{

	private static final Logger LOGGER = LoggerFactory.getLogger(StructureTypeDataClassFunction.class);

	private String typeClassPackage;
	private String typeClassSuffix;

	@Override
	public Class apply(final ComposedTypeModel composedType)
	{
		final String classFullPathName = getTypeClassPackage() + "." + composedType.getCode() + getTypeClassSuffix();
		try
		{
			return Class.forName(classFullPathName);
		}
		catch (final ClassNotFoundException e)
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Could not load type class for type {}.{}  does not exist.", composedType, classFullPathName);
			}
			return null;
		}
	}


	protected String getTypeClassPackage()
	{
		return typeClassPackage;
	}

	@Required
	public void setTypeClassPackage(final String typeClassPackage)
	{
		this.typeClassPackage = typeClassPackage;
	}

	protected String getTypeClassSuffix()
	{
		return typeClassSuffix;
	}

	@Required
	public void setTypeClassSuffix(final String typeClassSuffix)
	{
		this.typeClassSuffix = typeClassSuffix;
	}
}

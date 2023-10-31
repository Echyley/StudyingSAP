/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.Objects;

import com.google.common.base.Preconditions;

/**
 * Serves as the key for the modeling view's definition map. Allows different instances of a {@link ComposedTypeModel} to be defined. Code is the unique identifier for an instance.
 */
public class IntegrationMapKeyDTO
{
	private ComposedTypeModel type;
	private String code;

	/**
	 * Instantiates this DTO for the given composed type. The integration object item code is assumed to be the type code.
	 * @param type The type of the key
	 */
	public IntegrationMapKeyDTO(final ComposedTypeModel type)
	{
		Preconditions.checkArgument(type != null, "type must be provided");
		this.type = type;
		this.code = type.getCode();
	}

	/**
	 * @param type The type of the key, represented by a {@link ComposedTypeModel}
	 * @param code The code of the {@link de.hybris.platform.integrationservices.model.IntegrationObjectItemModel} representing the instance of the given type.
	 */
	public IntegrationMapKeyDTO(final ComposedTypeModel type, final String code)
	{
		Preconditions.checkArgument(code != null, "code must be provided");
		Preconditions.checkArgument(type != null, "type must be provided");
		this.type = type;
		this.code = code;
	}

	public ComposedTypeModel getType()
	{
		return type;
	}

	public String getCode()
	{
		return code;
	}

	public void setType(final ComposedTypeModel type)
	{
		this.type = type;
	}

	public void setCode(final String code)
	{
		this.code = code;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		final IntegrationMapKeyDTO that = (IntegrationMapKeyDTO) o;
		return code.equals(that.code);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(code);
	}

	public boolean hasAlias()
	{
		return !code.equals(type.getCode());
	}
}

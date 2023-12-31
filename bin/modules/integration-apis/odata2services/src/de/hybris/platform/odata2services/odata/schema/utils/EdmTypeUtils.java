/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.odata2.api.edm.EdmSimpleTypeKind;

import com.google.common.base.Preconditions;

/**
 * A utility class for dealing with EDM types.
 */
public final class EdmTypeUtils
{
	private static final Map<String, EdmSimpleTypeKind> JAVA_TO_EDM_TYPE_MAP = initialize();

	private EdmTypeUtils()
	{
		// non-instantiable
	}

	private static Map<String, EdmSimpleTypeKind> initialize()
	{
		final Map<String, EdmSimpleTypeKind> typeMap = new LinkedHashMap<>();
		typeMap.put(BigDecimal.class.getName(), EdmSimpleTypeKind.Decimal);
		typeMap.put(BigInteger.class.getName(), EdmSimpleTypeKind.String);
		typeMap.put(Boolean.class.getName(), EdmSimpleTypeKind.Boolean);
		typeMap.put(Boolean.TYPE.getName(), EdmSimpleTypeKind.Boolean);
		typeMap.put(Byte.class.getName(), EdmSimpleTypeKind.SByte);
		typeMap.put(Byte.TYPE.getName(), EdmSimpleTypeKind.SByte);
		typeMap.put(Character.class.getName(), EdmSimpleTypeKind.String);
		typeMap.put(Character.TYPE.getName(), EdmSimpleTypeKind.String);
		typeMap.put(Date.class.getName(), EdmSimpleTypeKind.DateTime);
		typeMap.put(Double.class.getName(), EdmSimpleTypeKind.Double);
		typeMap.put(Double.TYPE.getName(), EdmSimpleTypeKind.Double);
		typeMap.put(Float.class.getName(), EdmSimpleTypeKind.Single);
		typeMap.put(Float.TYPE.getName(), EdmSimpleTypeKind.Single);
		typeMap.put(Integer.class.getName(), EdmSimpleTypeKind.Int32);
		typeMap.put(Integer.TYPE.getName(), EdmSimpleTypeKind.Int32);
		typeMap.put(Long.class.getName(), EdmSimpleTypeKind.Int64);
		typeMap.put(Long.TYPE.getName(), EdmSimpleTypeKind.Int64);
		typeMap.put(Short.class.getName(), EdmSimpleTypeKind.Int16);
		typeMap.put(Short.TYPE.getName(), EdmSimpleTypeKind.Int16);
		typeMap.put(String.class.getName(), EdmSimpleTypeKind.String);
		typeMap.put(Object.class.getName(), EdmSimpleTypeKind.String);
		typeMap.put(Class.class.getName(), EdmSimpleTypeKind.String);
		return typeMap;
	}

	/**
	 * Converts the given {@code platformType} into the {@link EdmSimpleTypeKind}.
	 * If the {@code platformType} is a primitive (e.g. java.lang.String), having
	 * any prefix before the type (e.g. localized:java.lang.String) will fail to convert.
	 *
	 * @param platformType Platform type to convert
	 * @return EdmSimpleTypeKind
	 */
	public static EdmSimpleTypeKind convert(final String platformType)
	{
		Preconditions.checkArgument(platformType != null, "Platform Type should never be null.");
		
		return JAVA_TO_EDM_TYPE_MAP.entrySet().stream()
				.filter(entry -> StringUtils.equals(platformType, entry.getKey()))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Can't map given platform type '" + platformType + "' to a primitive. Please check your schema modeling and try again."));
	}
}

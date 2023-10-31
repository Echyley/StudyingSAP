/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorderfacades.util;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;

public class SapServiceOrderFacadesUtil
{
	private SapServiceOrderFacadesUtil()
	{
		
	}
	
	public static boolean isServiceProduct(ProductData product)
	{
		return (product.getServiceCode()!= null && !product.getServiceCode().isEmpty());
	}
	
	public static boolean isServiceEntry(OrderEntryData entry)
	{
		return isServiceProduct(entry.getProduct());
	}
	
}	
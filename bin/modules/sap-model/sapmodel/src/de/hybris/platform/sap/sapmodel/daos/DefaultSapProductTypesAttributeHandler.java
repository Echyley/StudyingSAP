/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.daos;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

/* Returns the set of SAPProductTypes that describe a product,
 * as determined by the services added to sapProductTypesHooks,
 * followed by postProcessSapProductTypes.
 */
public class DefaultSapProductTypesAttributeHandler extends AbstractDynamicAttributeHandler<Collection<SAPProductType>, ProductModel>
{
	protected List<SapProductTypesAttributeHelper> sapProductTypesAttributeHelpers;
	

	@Override
	public Collection<SAPProductType> get(ProductModel product)
	{
		Collection<SAPProductType> results = new HashSet<>();
		
		for(SapProductTypesAttributeHelper helper: getSapProductTypesAttributeHelpers())
		{
			results.addAll(helper.getSapProductTypes(product));
		}
		
		postProcessSapProductTypes(results);
		
		return results;
	}


	protected void postProcessSapProductTypes(Collection<SAPProductType> results) {
		if(!(results.contains(SAPProductType.SERVICE) || results.contains(SAPProductType.SUBSCRIPTION))) {
			results.add(SAPProductType.PHYSICAL);
		}
	}

	public List<SapProductTypesAttributeHelper> getSapProductTypesAttributeHelpers()
	{
		return sapProductTypesAttributeHelpers;
	}

	public void setSapProductTypesAttributeHelpers(List<SapProductTypesAttributeHelper> sapProductTypesAttributeHelpers)
	{
		this.sapProductTypesAttributeHelpers = sapProductTypesAttributeHelpers;
	}
}

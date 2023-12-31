/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.search.provider.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collection;
import java.util.Optional;




/**
 * Publishes base product to index server in case product is of type {@link VariantProductModel}
 */
public class BaseProductValueResolver extends AbstractValueResolver<ProductModel, Optional<String>, Optional<String>>
{


	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final ProductModel model,
			final de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver.ValueResolverContext<Optional<String>, Optional<String>> resolverContext)
					throws FieldValueProviderException
	{
		final Optional<String> baseProduct = resolverContext.getData();
		if (baseProduct.isPresent())
		{
			document.addField(indexedProperty, baseProduct.get(), resolverContext.getFieldQualifier());
		}

	}



	@Override
	protected Optional<String> loadData(final IndexerBatchContext batchContext,
			final Collection<IndexedProperty> indexedProperties, final ProductModel product) throws FieldValueProviderException
	{
		if (product instanceof VariantProductModel)
		{
			final ProductModel baseProduct = ((VariantProductModel) product).getBaseProduct();
			checkNotNull(baseProduct, "A variant must carry a base product");
			return Optional.of(baseProduct.getCode());
		}
		else
		{
			return Optional.empty();
		}
	}


}

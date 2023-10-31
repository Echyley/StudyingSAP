/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.admin.model.interceptor;

import de.hybris.platform.searchservices.core.model.interceptor.AbstractSnInterceptor;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.model.SnFieldModel;
import de.hybris.platform.searchservices.model.SnIndexerItemSourceOperationModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Interceptor for {@link SnIndexerItemSourceOperationModel}.
 */
public class SnIndexerItemSourceOperationInterceptor extends AbstractSnInterceptor<SnIndexerItemSourceOperationModel>
{

	protected static final String RESOURCE_ITEMSOURCEOPERATION_FIELDS_EMPTY = "searchservices.core.itemsourceop.fields.empty";

	@Override
	public void onValidate(final SnIndexerItemSourceOperationModel indexerItemSourceOperation,
			final InterceptorContext interceptorContext) throws InterceptorException
	{
		final SnDocumentOperationType documentOperationType = indexerItemSourceOperation.getDocumentOperationType();
		final Collection<SnFieldModel> fields = CollectionUtils.emptyIfNull(indexerItemSourceOperation.getFields());

		if (documentOperationType == SnDocumentOperationType.PARTIAL_UPDATE && fields.isEmpty())
		{
			throw new InterceptorException(
					getLocalizedMessage(RESOURCE_ITEMSOURCEOPERATION_FIELDS_EMPTY, SnIndexerItemSourceOperationModel.FIELDS,
							indexerItemSourceOperation, indexerItemSourceOperation.getItemtype(), SnFieldModel._TYPECODE),
					this);
		}
	}
}

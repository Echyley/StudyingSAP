/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.model;

import de.hybris.platform.searchservices.document.data.SnDocument;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;


/**
 * Class which encapsulates the informations for indexed product details which are used to construct the {@ Product}.
 */
public class MerchSnDocumentContainer
{
	private SnDocument inputDocument;
	private MerchSnConfigModel merchConfig;
	private SnIndexerContext indexerContext;
	private MerchSnSynchContext merchContext;

	public static MerchSnDocumentContainer from(final SnIndexerContext indexerContext, final MerchSnSynchContext merchContext, final MerchSnConfigModel merchConfig)
	{
		final MerchSnDocumentContainer container = new MerchSnDocumentContainer();
		container.indexerContext = indexerContext;
		container.merchContext = merchContext;
		container.merchConfig = merchConfig;
		return container;
	}

	public SnDocument getInputDocument()
	{
		return inputDocument;
	}

	public void setInputDocument(final SnDocument inputDocument)
	{
		this.inputDocument = inputDocument;
	}

	public MerchSnSynchContext getMerchContext()
	{
		return merchContext;
	}

	public void setMerchContext(final MerchSnSynchContext merchContext)
	{
		this.merchContext = merchContext;
	}

	public MerchSnConfigModel getMerchConfig()
	{
		return merchConfig;
	}

	public void setMerchConfig(final MerchSnConfigModel merchConfig)
	{
		this.merchConfig = merchConfig;
	}

	public SnIndexerContext getIndexerContext()
	{
		return indexerContext;
	}

	public void setIndexerContext(final SnIndexerContext indexerContext)
	{
		this.indexerContext = indexerContext;
	}

}

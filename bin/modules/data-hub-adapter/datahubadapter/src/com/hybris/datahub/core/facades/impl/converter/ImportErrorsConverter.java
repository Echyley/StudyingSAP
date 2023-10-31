/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.datahub.core.facades.impl.converter;

import com.hybris.datahub.core.facades.ImportError;
import com.hybris.datahub.core.facades.ItemImportResult;
import de.hybris.platform.dataimportcommons.facades.impl.converter.DefaultImportErrorsConverter;
import de.hybris.platform.servicelayer.impex.ImpExError;


public class ImportErrorsConverter<T extends ItemImportResult> extends DefaultImportErrorsConverter
{
	@Override
	protected T createDataItemImportResult()
	{
		return (T) new ItemImportResult();
	}

	@Override
	protected T createDataItemImportResult(final String msg)
	{
		return (T) new ItemImportResult(msg);
	}

	@Override
	protected ImportError toError(final ImpExError impExError)
	{
		return ImportError.create(impExError);
	}
}

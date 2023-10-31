/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp.strategy;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.ConstantsR3Lrd;


/**
 * Abstract class which provides generic functionality to exchange texts between ERP (via LO-API) and the BOL layer
 */
public abstract class TextMapper extends BaseMapper
{

	/**
	 * ID of LO-API segment dealing with texts
	 */
	public static final String OBJECT_ID_TEXT = "TEXT";

	protected String configTextId = null;
	protected String configLangIso = null;

	/**
	 * Injected (eventually) text ID
	 *
	 * @param configTextId
	 */
	public void setConfigTextId(final String configTextId)
	{
		this.configTextId = configTextId;
	}

	/**
	 * Injected (eventually) Language Iso code.
	 *
	 * @param configLangIso
	 */
	public void setConfigLangIso(final String configLangIso)
	{
		this.configLangIso = configLangIso;
	}


	protected void traceReadTexts(final Text text, final String description, final Log4JWrapper sapLogger)
	{
		final StringBuilder debugOutput = new StringBuilder("Text read from backend: ");
		debugOutput.append(ConstantsR3Lrd.SEPARATOR).append("Text found  ").append(description);
		debugOutput.append(ConstantsR3Lrd.SEPARATOR).append("ID:  ").append(text.getId());
		debugOutput.append(ConstantsR3Lrd.SEPARATOR).append("Text:       ").append(text.getText());
		debugOutput.append(ConstantsR3Lrd.SEPARATOR).append("Handle:     ").append(text.getHandle());
		sapLogger.debug(debugOutput);
	}

	protected void traceText(final String heading, final String id, final String langu, final Text text, final String handle,
			final String parentHandle, final Log4JWrapper sapLogger)
	{

		final StringBuilder debugOutput = new StringBuilder(heading);
		debugOutput.append(ConstantsR3Lrd.LF).append("Handle         : ").append(handle);
		debugOutput.append(ConstantsR3Lrd.LF).append("Parent handle  : ").append(parentHandle);
		debugOutput.append(ConstantsR3Lrd.LF).append("ID             : ").append(id);
		debugOutput.append(ConstantsR3Lrd.LF).append("Langu          : ").append(langu);

		if (text != null)
		{
			debugOutput.append(ConstantsR3Lrd.LF).append("Text            : ").append(text.getText());
		}
		else
		{
			debugOutput.append(ConstantsR3Lrd.LF).append("No text");
		}
		sapLogger.debug(debugOutput);
	}

}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoiceaddon.aspect;


import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;


/**
 *
 * This aspect is used to apply the UI changes when switching the View of Account Summary order management AOM.
 *
 */
public class SapInvoiceViewSwitchUIComponentsAspect
{

	public static final Logger LOG = Logger.getLogger(SapInvoiceViewSwitchUIComponentsAspect.class);
	public static final String INVOICE_ADDON_PREFIX = "addon:/sapinvoiceaddon/";


	/**
	 * Switch accountsummary page
	 *
	 * @param pjp
	 * @return UI Component
	 * @throws Throwable
	 */
	public Object switchInvoicePage(final ProceedingJoinPoint pjp) throws Throwable
	{
		final String uiComponent = applyInvoiceUIChanges(pjp).toString();
		return uiComponent.replace("accountLayoutPage", "accountInvoiceLayoutPage");
	}


	//add addon prefix
	public Object applyInvoiceUIChanges(final ProceedingJoinPoint pjp) throws Throwable
	{

		String uiComponent = pjp.proceed().toString();

			final StringBuilder prefix = new StringBuilder(INVOICE_ADDON_PREFIX);
			prefix.append(uiComponent);
			uiComponent = prefix.toString();
			if (LOG.isInfoEnabled()) {
				LOG.info("For document view, switching from AccountSummaryAddon to SapInvoiceAddon");
			}
			return uiComponent;

	}

}

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.cronjob;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderMessageListener;
import com.sap.sxe.loader.download.Message;


/**
 * Default implementation for listening to data loader messages
 */
public class DefaultDataloaderMessageListenerImpl implements DataloaderMessageListener
{

	private static final Logger LOG = Logger.getLogger(DefaultDataloaderMessageListenerImpl.class);


	@Override
	public void messageReported(final Message message)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(message.getMessage());
		}
	}



}

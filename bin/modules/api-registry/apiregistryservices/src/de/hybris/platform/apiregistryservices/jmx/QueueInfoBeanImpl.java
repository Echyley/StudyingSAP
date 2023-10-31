/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.apiregistryservices.jmx;

import de.hybris.platform.jmx.mbeans.impl.AbstractJMXMBean;

import org.springframework.integration.channel.QueueChannel;

/**
 * @see QueueInfoBean
 */
public class QueueInfoBeanImpl extends AbstractJMXMBean implements QueueInfoBean
{
	private String beanName;
	private QueueChannel channel;

	@Override
	public String getBeanName()
	{
		return beanName;
	}

	public void setBeanName(final String beanName)
	{
		this.beanName = beanName;
	}

	public QueueChannel getChannel()
	{
		return channel;
	}

	public void setChannel(final QueueChannel channel)
	{
		this.channel = channel;
	}

	@Override
	public int getQueueSize()
	{
		return channel.getQueueSize();
	}

	@Override
	public int getRemainingCapacity()
	{
		return channel.getRemainingCapacity();
	}

	@Override
	public String toString()
	{
		return "QueueInfoBeanImpl{" +
				"beanName='" + beanName + '\'' +
				", channel=" + channel +
				'}';
	}
}

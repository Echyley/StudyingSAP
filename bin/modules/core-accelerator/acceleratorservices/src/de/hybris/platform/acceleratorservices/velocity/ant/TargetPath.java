/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.ant;

/**
 *
 * Attribute object for ant task representing templatePath abstraction
 *
 */
public class TargetPath
{
	private boolean relative = true;

	private String targetFilePath = ".";

	public TargetPath()
	{
		//
	}

	public boolean isRelative()
	{
		return relative;
	}

	public void setRelative(final boolean relative)
	{
		this.relative = relative;
	}

	public String getTargetFilePath()
	{
		return targetFilePath;
	}

	public void setTargetFilePath(final String targetFilePath)
	{
		this.targetFilePath = targetFilePath;
	}
}

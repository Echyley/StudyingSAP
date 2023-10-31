/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.ant;

import java.util.List;

import org.apache.tools.ant.types.FileSet;


/**
 *
 * Attribute object for ant task representing templatePath abstraction
 *
 */
public class SourcePath
{
	private static final String CURRENT_DIR = ".";

	private String propertyFilePath = CURRENT_DIR;
	private final List<FileSet> templatesFileSet;

	private boolean relative = true;

	public SourcePath(final List<FileSet> templatesFileSet)
	{
		this.templatesFileSet = templatesFileSet;
	}

	public List<FileSet> getTemplateFileSet()
	{
		return templatesFileSet;
	}

	public void addFileset(final FileSet templateFileSet)
	{
		this.templatesFileSet.add(templateFileSet);
	}

	public void setRelative(final boolean relative)
	{
		this.relative = relative;
	}

	public boolean isRelative()
	{
		return relative;
	}

	public String getPropertyFilePath()
	{
		return propertyFilePath;
	}

	public void setPropertyFilePath(final String propertyFilePath)
	{
		this.propertyFilePath = propertyFilePath;
	}
}

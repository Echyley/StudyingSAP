/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.property;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;



/**
 * Property definition based on a file. <br>
 */
class PropertiesWithSource extends LinkedProperties
{

	private static final long serialVersionUID = -1624316810350885190L;
	private static final Logger LOG = Logger.getLogger(PropertiesWithSource.class.getName());


	/**
	 * Path to the property file.
	 */
	private final String path;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *           parent parent linked properties
	 * @param path
	 *           path to the property file
	 */
	public PropertiesWithSource(final LinkedProperties parent, final String path)
	{
		super(parent);
		this.path = path;
	}


	/**
	 * Returns the path.
	 * 
	 * @return path
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Returns the canonical path.
	 * 
	 * @return canonical path
	 * @throws IOException
	 *            if file does not exists
	 */
	public String getCanonicalPath() throws IOException
	{
		final File propFile = new File(path);
		return propFile.getCanonicalPath();
	}

	/**
	 * Loads the properties from the file.
	 * 
	 * @throws FileNotFoundException
	 *            {@link FileNotFoundException}
	 * @throws IOException
	 *            {@link IOException}
	 */
	public void loadFromFile() throws FileNotFoundException, IOException
	{
		try(final FileInputStream fileInputStream = new FileInputStream(new File(path)))
		{
			super.load(fileInputStream);
		}
	}


	@Override
	public String getInfo()
	{

		String fullQualifiedFileName;
		try
		{
			fullQualifiedFileName = this.getCanonicalPath();
		}
		catch (final IOException ex)
		{
			LOG.error(ex);
			fullQualifiedFileName = "[ERROR] : " + path + " is not a vaild filename. " + ex.getMessage();
		}
		return fullQualifiedFileName;
	}


	protected void safeClose(final InputStream resource)
	{
		if (resource != null)
		{
			try
			{
				resource.close();
			}
			catch (IOException ex)
			{
				LOG.error("Error when closing the file that is used to load the properties! " + ex.getMessage(), ex);
			}
		}
	}
}

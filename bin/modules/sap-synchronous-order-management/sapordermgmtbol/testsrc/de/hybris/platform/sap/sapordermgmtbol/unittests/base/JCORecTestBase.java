/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.unittests.base;


import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepositoryFactory;
import de.hybris.platform.sap.core.jco.rec.JCoRecRuntimeException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.annotation.Resource;

import org.apache.log4j.Logger;




@SuppressWarnings("javadoc")
public class JCORecTestBase extends SapordermanagmentBolSpringJunitTest
{

	/**
	 * JCo mock repository factory. .
	 */
	@Resource(name = "sapCoreJCoMockRepositoryFactory")
	protected JCoMockRepositoryFactory mockReposiotryFactory;

	public final static String JCO_DATA_PATH_PREFIX = "test//";
	private static final Logger LOG = Logger.getLogger(JCORecTestBase.class.getName());

	protected JCoMockRepository getJCORepository(final String name)
	{

		try
		{
			final URL repositoryResource = this.getClass().getClassLoader().getResource(JCO_DATA_PATH_PREFIX + name + ".xml");
			File file = null;
			try
			{
				file = new File(repositoryResource.toURI());
			}
			catch (final URISyntaxException e)
			{

				LOG.info(e);
			}

			return mockReposiotryFactory.getMockRepository(file);

		}
		catch (final JCoRecRuntimeException e1)
		{
			throw new RuntimeException("Repository not found", e1);
		}
	}

}

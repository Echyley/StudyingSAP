/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.mock.impl;

import de.hybris.platform.sap.core.jco.mock.JCoMockRepository;
import de.hybris.platform.sap.core.jco.mock.JCoMockRepositoryFactory;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.impl.RepositoryPlaybackFactoryImpl;

import java.io.File;


/**
 * Default factory providing parsing of JCo mock data files.
 */
public class DefaultJCoMockRepositoryFactory implements JCoMockRepositoryFactory
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.jco.rec.JCoMockRepositoryFactory#getMockRepository(java.lang.String)
	 */
	@Override
	public JCoMockRepository getMockRepository(final File file)
	{
		final RepositoryPlayback repositoryPlayback = new RepositoryPlaybackFactoryImpl(file).createRepositoryPlayback();
		return new JCoMockRepositoryDelegator(repositoryPlayback);
	}

}

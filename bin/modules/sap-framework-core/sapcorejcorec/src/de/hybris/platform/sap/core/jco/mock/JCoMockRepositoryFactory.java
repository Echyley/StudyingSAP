/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.mock;

import java.io.File;


/**
 * Factory providing parsing of JCo mock data files.
 */
public interface JCoMockRepositoryFactory
{
	/**
	 * Returns a mocked JCo Repository filled with data from a xml file.
	 * 
	 * @param file
	 *           file with mocking data
	 * @return mocked JCo Repository.
	 */
	public JCoMockRepository getMockRepository(File file);
}

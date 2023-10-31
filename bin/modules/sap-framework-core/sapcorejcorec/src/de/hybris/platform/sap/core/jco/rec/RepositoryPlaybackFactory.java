/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;


/**
 * This factory creates new instances of {@link RepositoryPlayback} implementations depending on the RepositoryVersion
 * in the repository-file.
 */
public interface RepositoryPlaybackFactory
{

	/**
	 * The actual factory method.
	 * 
	 * @return Returns a new instance of the {@link RepositoryPlayback} implementation.
	 */
	public RepositoryPlayback createRepositoryPlayback();
}

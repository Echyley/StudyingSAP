/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;



/**
 * This factory creates new instances of the {@link RepositoryRecording} implementations.
 */
public interface RepositoryRecordingFactory
{


	/**
	 * The actual factory method.
	 * 
	 * @return Returns a new instance of the {@link RepositoryRecording} implementation.
	 */
	public RepositoryRecording createRepositoryRecording();
}

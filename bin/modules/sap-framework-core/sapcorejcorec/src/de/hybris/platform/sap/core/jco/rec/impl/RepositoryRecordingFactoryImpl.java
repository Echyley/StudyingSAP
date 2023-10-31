/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.rec.RepositoryRecording;
import de.hybris.platform.sap.core.jco.rec.RepositoryRecordingFactory;
import de.hybris.platform.sap.core.jco.rec.version100.RepositoryRecording100;

import java.io.File;


/**
 * Implementation of {@link RepositoryRecordingFactory} interface.
 */
public class RepositoryRecordingFactoryImpl implements RepositoryRecordingFactory
{

	private final File repoFile;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *           this file is the location the recorded data will be saved to.
	 */
	public RepositoryRecordingFactoryImpl(final File file)
	{
		this.repoFile = file;
	}

	@Override
	public RepositoryRecording createRepositoryRecording()
	{
		return new RepositoryRecording100(repoFile);
	}



}

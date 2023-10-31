/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;

/**
 * This enum represents the possible states for the JCoRecorder mode.
 */
public enum JCoRecMode
{
	/**
	 * The JCoRecorder redirects the calls to the backend.
	 */
	OFF,

	/**
	 * The JcoRecorder redirects the calls to the backend and writes the data of the backend-calls to a repository file.
	 */
	RECORDING,

	/**
	 * The JCoRecorder intersects the calls to the backend and will rather look up the data from the parsed repository
	 * file.
	 */
	PLAYBACK
}

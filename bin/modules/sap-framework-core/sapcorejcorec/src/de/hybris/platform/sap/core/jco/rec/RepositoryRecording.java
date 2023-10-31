/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;

import com.sap.conn.jco.JCoFunction;


/**
 * Interface for the RECORDING mode of the JCoRecorder.<br/>
 * There should be an implementation for every version of the repository-file.<br/>
 * For instance:
 * <ul>
 * <li>JCoRecRepository for originally version of the repository-file.</li>
 * <li>RepositoryRecording100 for the first revised repository-file version.</li>
 * </ul>
 * 
 */
public interface RepositoryRecording
{

	/**
	 * While recording, this method helps to add a (new) function to the Repository.
	 * 
	 * @param function
	 *           the JCoFunction that should be added.
	 * @return Returns a number that indicates the position in the requesting/execution-order.
	 */
	public int putFunction(JCoFunction function);

	/**
	 * Saves the collected data in a file on disk.
	 * 
	 * @throws JCoRecException
	 *            if an error (e.g. I/O exception) occurs during saving.
	 */
	public void writeRepositoryToFile() throws JCoRecException;
}

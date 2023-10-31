/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.intf;

/**
 * Spring managed parameter container for dataload jobs
 */
public interface DataLoaderCronjobParameters
{

	/**
	 * @return Data Loader Start Job Bead Id
	 */
	String getDataloadStartJobBeanId();

	/**
	 * @param dataloadStartJobBeanId
	 *           Data Loader Start Job Bead Id
	 */
	void setDataloadStartJobBeanId(String dataloadStartJobBeanId);

	/**
	 * @return Data Loader Stop Job Bead Id
	 */
	String getDataloadStopJobBeanId();

	/**
	 * @param dataloadStopJobBeanId
	 *           Data Loader Stop Job Bead Id
	 */
	void setDataloadStopJobBeanId(String dataloadStopJobBeanId);

	/**
	 *
	 * @return Node Id for Start Job
	 */
	Integer retrieveNodeIdForStartJob();

	/**
	 * @return Node Id for Stop Job
	 */
	Integer retrieveNodeIdForStopJob();

}

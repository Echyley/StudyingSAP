/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package com.hybris.datahub.core.services;

import com.hybris.datahub.core.dto.ResultData;
import com.hybris.datahub.core.rest.DataHubCommunicationException;
import com.hybris.datahub.core.rest.DataHubOutboundException;

import java.util.List;
import java.util.Map;


/**
 * A service for sending CSV formatted Raw Item data to a DataHub instance.
 * <p/>
 * <p>
 * The Data Hub must be running the CSV Data Hub extension.
 * </p>
 * <p/>
 * <p>
 * The URL location of the Data Hub must be configured in the hybris Core platform using the
 * datahubadapter.datahuboutbound.url property in config/local.properties. ie.
 * datahubadapter.datahuboutbound.url=http://{host}:{port}/datahub-webapp/v1
 * </p>
 * <p/>
 * <p>
 * Dates are converted to UTC time zone and pattern formatted before sending to the Data Hub. The date format pattern
 * can be specified by setting the datahubadapter.datahuboutbound.date.pattern property in config/local.properties. ie.
 * datahubadapter.datahuboutbound.date.pattern=yyyy-MM-dd HH:mm:ss.S
 * </p>
 * <p/>
 * <p>
 * <code>Collection</code> is not supported as a property of the <code>Object</code> or the value of a
 * <code>Map.Entry</code> in a <code>Map</code>.
 * </p>
 */
public interface DataHubOutboundService
{
	/**
	 * Sends the specified <code>Object</code> to the DataHub as a Raw Item in CSV format.
	 * <p/>
	 * <p>>
	 * <code>Collection</code> are not supported as object properties and will be ignored. Properties with null values
	 * will be ignored.
	 * </p>
	 * <p/>
	 * <p>
	 * The object's property names are appended to the CSV header. The string value of the object's properties will be
	 * appended to the CSV body.
	 * </p>
	 *
	 * @param feedName the name of the Data Feed
	 * @param rawType the Raw Type to create in the DataHub
	 * @param obj the object to send to Data Hub
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData sendToDataHub(String feedName, String rawType, Object obj) throws DataHubOutboundException;

	/**
	 * Sends the specified <code>Object</code> to the DataHub as a Raw Item in CSV format.
	 * <p/>
	 * <p>
	 * <code>Collection</code> are not supported as object properties and will be ignored. Properties with null values
	 * will be ignored.
	 * </p>
	 * <p/>
	 * <p>
	 * The object's property names are appended to the CSV header. The string value of the object's properties will be
	 * appended to the CSV body.
	 * </p>
	 * <p/>
	 * <p>
	 * The value of the feedName is defaulted to DEFAULT_FEED.
	 * </p>
	 *
	 * @param rawType the Raw Type to create in the DataHub
	 * @param obj the object to send to Data Hub
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData sendToDataHub(String rawType, Object obj) throws DataHubOutboundException;

	/**
	 * Sends the specified <code>Map</code> to the DataHub as a Raw Item in CSV format.
	 * <p/>
	 * <p>
	 * <code>Collection</code> are not supported as <code>Map.Entry</code> values and will be ignored.
	 * </p>
	 * <p/>
	 * <p>
	 * The <code>Map</code> keys are used as values in the CSV header. The string value of <code>Map.Entry</code> values
	 * will be used in the CSV body.
	 * </p>
	 *
	 * @param feedName the name of the Data Feed
	 * @param rawType the Raw Type to create in the DataHub
	 * @param objectMap the Map of key/value pairs to send to Data Hub. The key represents the field name
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData sendToDataHub(String feedName, String rawType, Map<String, Object> objectMap)
			throws DataHubOutboundException;

	/**
	 * Sends the specified Map to the DataHub as a Raw Item in CSV format.
	 * <p/>
	 * <p>
	 * <code>Collection</code> are not supported as <code>Map.Entry</code> values and will be ignored.
	 * </p>
	 * <p/>
	 * <p>
	 * The value of the feedName is defaulted to DEFAULT_FEED.
	 * </p>
	 *
	 * @param rawType the Raw Type to create in the DataHub
	 * @param objectMap the Map of key/value pairs to send to Data Hub. The key represents the field name
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData sendToDataHub(String rawType, Map<String, Object> objectMap) throws DataHubOutboundException;

	/**
	 * Sends the specified List of Maps to the DataHub as Raw Items in CSV format.
	 * <p/>
	 * <p>
	 * <code>Collection</code> are not supported as <code>Map.Entry</code> values and will be ignored.
	 * </p>
	 * <p/>
	 * <p>
	 * CSV header is populated using the keys from the first Map in the List. Each Map in the List should contain the
	 * same keys.
	 * </p>
	 *
	 * @param feedName the name of the Data Feed
	 * @param rawType the Raw Type to create in the DataHub
	 * @param objList a List of Maps. Each Map represents one row of CSV data to send to Data Hub.
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData sendToDataHub(String feedName, String rawType, List<Map<String, Object>> objList)
			throws DataHubOutboundException;

	/**
	 * Sends the specified List of Maps to the DataHub as Raw Items in CSV format.
	 * <p/>
	 * <p>
	 * <code>Collection</code> are not supported as <code>Map.Entry</code> values and will be ignored.
	 * </p>
	 * <p/>
	 * <p>
	 * CSV header is populated using the keys from the first Map in the List. Each Map in the List should contain the
	 * same keys.
	 * </p>
	 * <p/>
	 * <p>
	 * The value of the feedName is defaulted to DEFAULT_FEED.
	 * </p>
	 *
	 * @param rawType the Raw Type to create in the DataHub
	 * @param objList a List of Maps. Each Map represents one row of CSV data to send to Data Hub.
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData sendToDataHub(String rawType, List<Map<String, Object>> objList)
			throws DataHubOutboundException;

	/**
	 * Deletes item with an integration key comprised of the specified primary keys, of the type specified, in the pool
	 * specified on the Data Hub server
	 *
	 * @param poolName name of the data pool the item to be deleted resides in
	 * @param canonicalItemType type code for the item to delete
	 * @param keyFields map of primary key attribute names and values to uniquely identify the item to be deleted
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData deleteItem(String poolName, String canonicalItemType, Map<String, String> keyFields)
			throws DataHubOutboundException;

	/**
	 * Deletes item with an integration key comprised of the specified primary keys, of the type specified, in the GLOBAL
	 * pool on the Data Hub server
	 *
	 * @param canonicalItemType type code for the item to delete
	 * @param keyFields map of primary key attribute names and values to uniquely identify the item to be deleted
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData deleteItem(String canonicalItemType, Map<String, String> keyFields)
			throws DataHubOutboundException;

	/**
	 * Deletes all items originating from the data feed and raw type specified
	 *
	 * @param feedName Name of the data feed the items originated from
	 * @param rawItemType Name of the raw type the items originated from
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData deleteByFeed(String feedName, String rawItemType) throws DataHubOutboundException;

	/**
	 * Deletes all items originating from the DEFAULT_FEED on the Data Hub server and raw type specified and the
	 *
	 * @param rawItemType Name of the raw type the items originated from
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData deleteByFeed(String rawItemType) throws DataHubOutboundException;

	/**
	 * Deletes all canonical items composed from the specified raw item.
	 *
	 * @param feedName Name of the data feed item originated from
	 * @param rawItemType Name of the raw type the item originated from
	 * @param objectMap map attribute values for the object to delete. The key in the map is the attribute name, the value is
	 * the attribute value. The map must contain at least all primary key attribute values.
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData deleteByFeed(String feedName, String rawItemType, Map<String, Object> objectMap)
			throws DataHubOutboundException;

	/**
	 * Deletes all canonical items in the default data pool composed from the specified raw item.
	 *
	 * @param rawItemType Name of the raw type the item originated from
	 * @param objectMap map attribute values for the object to delete. The key in the map is the attribute name, the value is
	 * the attribute value. The map must contain at least all primary key attribute values.
	 * @return the result of the deletion
	 * @throws DataHubCommunicationException if communication with the Data Hub server failed
	 * @throws DataHubOutboundException if the Data Hub server was unable to successfully complete the export operation
	 */
	ResultData deleteByFeed(String rawItemType, Map<String, Object> objectMap) throws DataHubOutboundException;
}

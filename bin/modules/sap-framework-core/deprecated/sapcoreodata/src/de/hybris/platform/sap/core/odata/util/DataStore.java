/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.odata.util;

import java.util.List;
import java.util.Map;


/**
 * This class is used for callback purpose. To file to inline data such as scenarios, basketObjects and leadingObjects
 * @deprecated Since 6.4, replace with extension sapymktcommon
 */
@Deprecated(since= "6.4", forRemoval = true)
public class DataStore
{
	Map<String, Object> data;

	/**
	 * Default constuctor
	 */
	public DataStore()
	{
	};

	/**
	 * @param data
	 *           (RecommendationScenario map)
	 */
	public DataStore(final Map<String, Object> data)
	{
		this.data = data; // TODO : NULL CHECKS
	}

	/**
	 * @return list of maps of Scenarios
	 */
	
	public List<Map<String, Object>> getEntities(String entityName)//getScenarios()
	{
		return (List<Map<String, Object>>) data.get(entityName);//"Scenarios");
	}

	/**
	 * 
	 * @param parentEntityName
	 * @param parentEntityIdAttribute
	 * @param parentEntityId
	 * @param subEntityName
	 * @return
	 */
	
	public List<Map<String, Object>> getSubEntityList(final String parentEntityName, final String parentEntityIdAttribute,
			final String parentEntityId, final String subEntityName)//getBasketObjects(final String scenarioId)
	{

		for (final Map<String, Object> parentEntity : (List<Map<String, Object>>) data.get(parentEntityName))//("Scenarios"))
		{
			if (parentEntity.get(parentEntityIdAttribute).equals(parentEntityId))//(scenarioId))
			{
				return (List<Map<String, Object>>) parentEntity.get(subEntityName);//("BasketObjects");
			}
		}
		return null;
	}

	//
	/**
	 * @param scenarioId
	 * @return list of maps of LeadingObjects
	 */
	/*
	 *  public List<Map<String, Object>> getLeadingObjects(final String scenarioId) { for
	 * (final Map<String, Object> scenario : (List<Map<String, Object>>) data.get("Scenarios")) { if
	 * (scenario.get("ScenarioId").equals(scenarioId)) { return (List<Map<String, Object>>)
	 * scenario.get("LeadingObjects"); } } return null; }
	 */

	/**
	 * @return data (RecommendationScenario map)
	 */
	public Map<String, Object> getData()
	{
		return data; // TODO : NULL CHECKS
	}

	/**
	 * @param data
	 *           (RecommendationScenario map)
	 */
	public void setData(final Map<String, Object> data)
	{
		this.data = data;
	}

}

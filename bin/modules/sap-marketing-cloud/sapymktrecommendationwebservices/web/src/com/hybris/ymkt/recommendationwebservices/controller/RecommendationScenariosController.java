/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.recommendationwebservices.controller;

import com.hybris.ymkt.recommendationwebservices.facades.RecommendationPopulatorFacade;

import de.hybris.platform.cmsfacades.data.OptionData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;


@Controller
@Tag(name = "RecommendationScenarios")
public class RecommendationScenariosController {

	@Resource(name = "recommendationPopulatorFacade")
	protected RecommendationPopulatorFacade recommendationPopulatorFacade;

	/**
	 * Retrieve dropdown values via oData and return content
	 *
	 * @param sourceType: Dropdown to fill
	 * @return JSON containing option data list
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/data/product/{sourceField}", method = RequestMethod.GET, produces = "application/json")
	@Operation(operationId = "productRecoPopulator", summary = "Returns values to populate given dropdown field")
	public String populateDropdown(
			@Parameter(description = "Dropdown field to fill. Determines the data to be returned", required = true)
			@PathVariable String sourceField) throws IOException
	{
		final Map<String, List<OptionData>> map = new HashMap<>();
		final List<OptionData> optionDataList = recommendationPopulatorFacade.populateDropDown(sourceField);

		//construct JSON for dropdowns
		map.put("options", optionDataList);
		return new ObjectMapper().writeValueAsString(map);
	}

	public void setRecommendationPopulatorFacade(RecommendationPopulatorFacade recommendationPopulatorFacade) {
		this.recommendationPopulatorFacade = recommendationPopulatorFacade;
	}

}

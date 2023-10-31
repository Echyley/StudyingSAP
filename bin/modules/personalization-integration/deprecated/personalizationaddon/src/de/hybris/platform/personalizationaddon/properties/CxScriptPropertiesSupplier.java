/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationaddon.properties;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.cmsitems.properties.CMSItemPropertiesSupplier;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationaddon.data.CxViewActionResult;
import de.hybris.platform.personalizationaddon.data.CxViewValueCoder;
import de.hybris.platform.personalizationaddon.data.ScriptComponentData;
import de.hybris.platform.personalizationaddon.model.PersonalizationScriptComponentModel;
import de.hybris.platform.personalizationfacades.customersegmentation.CustomerSegmentationFacade;
import de.hybris.platform.personalizationfacades.data.SegmentData;
import de.hybris.platform.personalizationservices.data.CxAbstractActionResult;
import de.hybris.platform.personalizationservices.service.CxService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CxScriptPropertiesSupplier implements CMSItemPropertiesSupplier
{
	private static final Logger LOG = LoggerFactory.getLogger(CxScriptPropertiesSupplier.class);

	private CxService cxService;
	private CustomerSegmentationFacade customerSegmentationFacade;
	private UserService userService;
	private CxViewValueCoder cxViewValueCoder;

	@Override
	public Predicate<CMSItemModel> getConstrainedBy()
	{
		return PersonalizationScriptComponentModel.class::isInstance;
	}

	@Override
	public Map<String, Object> getProperties(final CMSItemModel itemModel)
	{
		final Map<String, Object> map = new HashMap<>();

		final ScriptComponentData data = new ScriptComponentData();
		data.setActions(getFormatedActionResults());
		data.setSegments(getFormatedSegmentData());

		final ObjectMapper mapper = new ObjectMapper();
		try
		{
			final String jsonData = mapper.writeValueAsString(data);
			final String encodedData = cxViewValueCoder.encode(jsonData);
			map.put("data", encodedData);
		}
		catch (final JsonProcessingException e)
		{
			LOG.debug("Adding personalization data to the page has failed", e);
		}

		return map;
	}

	protected List<Object> getFormatedActionResults()
	{
		return getEncodedActionResults().stream().map(a -> {
			final Map<String, String> result = new HashMap<>();
			result.put("action_code", a.getActionCode());
			result.put("action_type", a.getType());
			result.put("variation_code", a.getVariationCode());
			result.put("variation_name", a.getVariationName());
			result.put("customization_code", a.getCustomizationCode());
			result.put("customization_name", a.getCustomizationName());
			return result;
		}).collect(Collectors.toList());
	}

	public List<CxViewActionResult> getEncodedActionResults()
	{
		final UserModel currentUser = userService.getCurrentUser();
		return cxService.getActionResultsFromSession(currentUser).stream().map(this::encodeActionResult)
				.collect(Collectors.toList());
	}

	protected List<Object> getFormatedSegmentData()
	{
		return getEncodedSegmentData().stream().map(SegmentData::getCode).collect(Collectors.toList());
	}

	public List<SegmentData> getEncodedSegmentData()
	{
		final List<SegmentData> segmentList = customerSegmentationFacade.getSegmentsForCurrentUser();
		segmentList.forEach(this::encodeSegmentData);
		return segmentList;
	}

	protected CxViewActionResult encodeActionResult(final CxAbstractActionResult action)
	{
		final CxViewActionResult result = new CxViewActionResult();
		final String actionCode = cxViewValueCoder.encode(action.getActionCode());
		result.setActionCode(actionCode);

		final String variationCode = cxViewValueCoder.encode(action.getVariationCode());
		result.setVariationCode(variationCode);
		final String variationName = cxViewValueCoder.encode(action.getVariationName());
		result.setVariationName(variationName);

		final String customizationCode = cxViewValueCoder.encode(action.getCustomizationCode());
		result.setCustomizationCode(customizationCode);
		final String customizationName = cxViewValueCoder.encode(action.getCustomizationName());
		result.setCustomizationName(customizationName);

		final String type = cxViewValueCoder.encode(action.getClass().getSimpleName());
		result.setType(type);

		return result;
	}

	protected SegmentData encodeSegmentData(final SegmentData segment)
	{
		final String code = cxViewValueCoder.encode(segment.getCode());
		segment.setCode(code);
		return segment;
	}

	@Override
	public String groupName()
	{
		return "script";
	}

	public void setCxService(final CxService cxService)
	{
		this.cxService = cxService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public void setCustomerSegmentationFacade(final CustomerSegmentationFacade customerSegmentationFacade)
	{
		this.customerSegmentationFacade = customerSegmentationFacade;
	}

	public void setCxViewValueCoder(final CxViewValueCoder cxViewValueCoder)
	{
		this.cxViewValueCoder = cxViewValueCoder;
	}
}

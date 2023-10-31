/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.workflow.populator;

import de.hybris.platform.cms2.workflow.service.CMSWorkflowParticipantService;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.common.service.TimeDiffService;
import de.hybris.platform.cmsfacades.data.CMSWorkflowActionData;
import de.hybris.platform.cmsfacades.data.CMSWorkflowDecisionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@link CMSWorkflowActionData} instance from the {@link WorkflowActionModel} source data model.
 */
public class CMSWorkflowActionDataPopulator implements Populator<WorkflowActionModel, CMSWorkflowActionData>
{

	private CMSWorkflowParticipantService cmsWorkflowParticipantService;
	private TimeDiffService timeDiffService;
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final WorkflowActionModel workflowActionModel, final CMSWorkflowActionData workflowActionData)
	{
		workflowActionData.setActionType(workflowActionModel.getActionType().name());
		workflowActionData.setCode(workflowActionModel.getCode());
		workflowActionData.setStatus(workflowActionModel.getStatus().name());
		workflowActionData.setModifiedtime(workflowActionModel.getModifiedtime());
		workflowActionData.setDecisions(
				workflowActionModel.getDecisions().stream().map(this::getWorkflowDecisionData).collect(Collectors.toList()));
		workflowActionData
				.setIsCurrentUserParticipant(getCmsWorkflowParticipantService().isWorkflowActionParticipant(workflowActionModel));
		final Map<String, String> workflowActionDataNameMap = Optional.ofNullable(workflowActionData.getName())
				.orElseGet(() -> getNewWorkflowActionDataNameMap(workflowActionData));
		getLocalizedPopulator().populate(
				(locale, value) -> workflowActionDataNameMap.put(getLocalizedPopulator().getLanguage(locale), value),
				locale -> Optional.ofNullable(workflowActionModel.getName(locale)).orElse(null));
		final Map<String, String> workflowActionDataDescriptionMap = Optional.ofNullable(workflowActionData.getDescription())
				.orElseGet(() -> getNewWorkflowActionDataDescriptionMap(workflowActionData));
		getLocalizedPopulator().populate(
				(locale, value) -> workflowActionDataDescriptionMap.put(getLocalizedPopulator().getLanguage(locale), value),
				locale -> Optional.ofNullable(workflowActionModel.getDescription(locale)).orElse(null));

		if (workflowActionModel.getStatus().equals(WorkflowActionStatus.IN_PROGRESS))
		{
			workflowActionData.setStartedAgoInMillis(getTimeDiffService().difference(workflowActionModel.getActivated()));
		}
	}

	/**
	 * Method that converts the {@link WorkflowDecisionModel workflowDecisionModel} to {@link CMSWorkflowDecisionData
	 * cmsWorkflowDecisionData}.
	 *
	 * @param workflowDecisionModel
	 *           The {@link WorkflowDecisionModel workflowDecisionModel}.
	 * @return The {@link CMSWorkflowDecisionData cmsWorkflowDecisionData}.
	 */
	protected CMSWorkflowDecisionData getWorkflowDecisionData(final WorkflowDecisionModel workflowDecisionModel)
	{
		final var workflowDecisionData = new CMSWorkflowDecisionData();
		workflowDecisionData.setCode(workflowDecisionModel.getCode());
		final Map<String, String> workflowDecisionDataNameMap = Optional.ofNullable(workflowDecisionData.getName())
				.orElseGet(() -> getNewCMSWorkflowDecisionDataNameMap(workflowDecisionData));
		getLocalizedPopulator().populate(
				(locale, value) -> workflowDecisionDataNameMap.put(getLocalizedPopulator().getLanguage(locale), value),
				locale -> Optional.ofNullable(workflowDecisionModel.getName(locale)).orElse(null));
		final Map<String, String> workflowDecisionDataDescriptionMap = Optional.ofNullable(workflowDecisionData.getDescription())
				.orElseGet(() -> getNewCMSWorkflowDecisionDataDescriptionMap(workflowDecisionData));
		getLocalizedPopulator().populate(
				(locale, value) -> workflowDecisionDataDescriptionMap.put(getLocalizedPopulator().getLanguage(locale), value),
				locale -> Optional.ofNullable(workflowDecisionModel.getDescription(locale)).orElse(null));
		return workflowDecisionData;
	}


	protected CMSWorkflowParticipantService getCmsWorkflowParticipantService()
	{
		return cmsWorkflowParticipantService;
	}

	@Required
	public void setCmsWorkflowParticipantService(final CMSWorkflowParticipantService cmsWorkflowParticipantService)
	{
		this.cmsWorkflowParticipantService = cmsWorkflowParticipantService;
	}

	protected TimeDiffService getTimeDiffService()
	{
		return timeDiffService;
	}

	@Required
	public void setTimeDiffService(final TimeDiffService timeDiffService)
	{
		this.timeDiffService = timeDiffService;
	}

	protected Map<String, String> getNewWorkflowActionDataNameMap(final CMSWorkflowActionData workflowActionData)
	{
		workflowActionData.setName(new LinkedHashMap<>());
		return workflowActionData.getName();
	}

	protected Map<String, String> getNewWorkflowActionDataDescriptionMap(final CMSWorkflowActionData workflowActionData)
	{
		workflowActionData.setDescription(new LinkedHashMap<>());
		return workflowActionData.getDescription();
	}

	protected Map<String, String> getNewCMSWorkflowDecisionDataNameMap(final CMSWorkflowDecisionData workflowDecisionData)
	{
		workflowDecisionData.setName(new LinkedHashMap<>());
		return workflowDecisionData.getName();
	}

	protected Map<String, String> getNewCMSWorkflowDecisionDataDescriptionMap(final CMSWorkflowDecisionData workflowDecisionData)
	{
		workflowDecisionData.setDescription(new LinkedHashMap<>());
		return workflowDecisionData.getDescription();
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}
}

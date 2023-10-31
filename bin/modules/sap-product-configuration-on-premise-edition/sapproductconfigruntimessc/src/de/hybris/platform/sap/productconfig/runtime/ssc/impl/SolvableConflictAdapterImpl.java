/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.sap.productconfig.runtime.ssc.SolvableConflictAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IAssumptions;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConflictingAssumptionsContainer;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;


/**
 * Default implementation of {@link SolvableConflictAdapter}
 */
public class SolvableConflictAdapterImpl implements SolvableConflictAdapter
{
	private static final Logger LOG = Logger.getLogger(SolvableConflictAdapterImpl.class);
	private TextConverterImpl textConverter;
	private final SSCTimer timer = new SSCTimer();


	/**
	 * @param textConverter
	 */
	@Required
	public void setTextConverter(final TextConverterImpl textConverter)
	{
		this.textConverter = textConverter;
	}


	@Override
	public void transferSolvableConflicts(final IConfigSession configSession, final String configId, final ConfigModel configModel)
	{
		try
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Transferring conflicts for config: " + configId);
			}
			timer.start("getConflicts");
			final IConflictingAssumptionsContainer[] solvableConflicts = configSession.getConflictingAssumptions(configId);
			timer.stop();
			if (solvableConflicts != null)
			{
				final List<SolvableConflictModel> solvableConflictModelList = Arrays.asList(solvableConflicts) //
						.stream() //
						.map(this::createSolvableConflictModel) //
						.collect(Collectors.toCollection(ArrayList::new));
				configModel.setSolvableConflicts(solvableConflictModelList);
			}
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Cannot get conflicts", e);
		}

	}

	protected SolvableConflictModel createSolvableConflictModel(final IConflictingAssumptionsContainer solvableConflict)
	{
		final String conflictLongName = textConverter.convertDependencyText(solvableConflict.getConflictLongText());

		if (LOG.isDebugEnabled())
		{
			LOG.debug("create SolvableConflictModel" + conflictLongName);
		}

		final SolvableConflictModel solvableConflictModel = new SolvableConflictModelImpl();
		solvableConflictModel.setDescription(conflictLongName);


		final IAssumptions[] assumptions = solvableConflict.getAssumptions();
		if (assumptions == null)
		{
			return solvableConflictModel;
		}
		final List<ConflictingAssumptionModel> conflictingAssumptionsList = Arrays.asList(assumptions) //
				.stream() //
				.map(SolvableConflictAdapterImpl::createConflictingAssumptionsModel) //
				.collect(Collectors.toCollection(ArrayList::new));
		solvableConflictModel.setConflictingAssumptions(conflictingAssumptionsList);


		if (!conflictingAssumptionsList.isEmpty())
		{
			final String groupId = conflictingAssumptionsList.get(0).getId();
			solvableConflictModel.setId(groupId);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Set groupId to: " + groupId);
			}
		}

		return solvableConflictModel;
	}

	protected static ConflictingAssumptionModel createConflictingAssumptionsModel(final IAssumptions assumption)
	{
		final String observableName = assumption.getObservableName();
		final String observableValueName = assumption.getObservableValueName();
		final String asumptionId = assumption.getAsumptionId();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("create ConflictingAssumptionModel for: " + observableName + ", " + observableValueName + ", " + asumptionId);
		}
		final ConflictingAssumptionModel assumptionModel = new ConflictingAssumptionModelImpl();
		assumptionModel.setCsticName(observableName);
		assumptionModel.setValueName(observableValueName);
		assumptionModel.setInstanceId(assumption.getInstanceId());
		assumptionModel.setId(asumptionId);
		return assumptionModel;

	}


	protected void performDebugOutputAssumption(final String id)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Assumption ID found" + id);
		}
	}

	@Override
	public String getAssumptionId(final CsticModel csticModel, final ConfigModel configModel)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Searching assumption ID for: " + configModel);
		}

		final List<SolvableConflictModel> solvableConflicts = configModel.getSolvableConflicts();
		if (CollectionUtils.isEmpty(solvableConflicts))
		{
			return null;
		}

		final String assumptionId = getAssumptionIdForCsticOrValue(csticModel, solvableConflicts);

		if (assumptionId != null)
		{
			performDebugOutputAssumption(assumptionId);
		}
		return assumptionId;
	}


	protected String getAssumptionIdForCsticOrValue(final CsticModel csticModel,
			final List<SolvableConflictModel> solvableConflicts)
	{
		final List<ConflictingAssumptionModel> assumptions = solvableConflicts.stream()
				.map(solvableConflict -> solvableConflict.getConflictingAssumptions()).flatMap(Collection::stream)
				.filter(conflictingAssumption -> conflictingAssumption.getInstanceId().equals(csticModel.getInstanceId())
						&& conflictingAssumption.getCsticName().equals(csticModel.getName()))
				.collect(Collectors.toCollection(ArrayList::new));

		//Check first if we have an assumption model for value
		final Optional<ConflictingAssumptionModel> assumptionForValue = assumptions.stream()
				.filter(conflictingAssumption -> isAssumptionForAssignedValue(conflictingAssumption.getValueName(), csticModel))
				.findFirst();

		if (assumptionForValue.isPresent())
		{
			return assumptionForValue.get().getId();
		}

		//Check if we have an assumption model for cstic
		final Optional<ConflictingAssumptionModel> assumptionForCstic = assumptions.stream().findFirst();

		if (assumptionForCstic.isPresent())
		{
			return assumptionForCstic.get().getId();
		}

		return null;
	}


	protected boolean isAssumptionForAssignedValue(final String valueName, final CsticModel csticModel)
	{
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();
		if (CollectionUtils.isEmpty(assignedValues))
		{
			return false;
		}

		for (final CsticValueModel assignedValue : assignedValues)
		{
			if (valueName.equals(assignedValue.getName()))
			{
				return true;
			}
		}

		return false;
	}

}

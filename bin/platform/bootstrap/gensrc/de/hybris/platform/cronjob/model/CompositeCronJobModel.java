/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN! ---
 * --- Generated at 31 de out. de 2023 09:42:36                 ---
 * ----------------------------------------------------------------
 *  
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cronjob.model;

import de.hybris.bootstrap.annotations.Accessor;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cronjob.model.CompositeEntryModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;
import java.util.List;

/**
 * Generated model class for type CompositeCronJob first defined at extension processing.
 */
@SuppressWarnings("all")
public class CompositeCronJobModel extends CronJobModel
{
	/**<i>Generated model type code constant.</i>*/
	public static final String _TYPECODE = "CompositeCronJob";
	
	/** <i>Generated constant</i> - Attribute key of <code>CompositeCronJob.compositeEntries</code> attribute defined at extension <code>processing</code>. */
	public static final String COMPOSITEENTRIES = "compositeEntries";
	
	
	/**
	 * <i>Generated constructor</i> - Default constructor for generic creation.
	 */
	public CompositeCronJobModel()
	{
		super();
	}
	
	/**
	 * <i>Generated constructor</i> - Default constructor for creation with existing context
	 * @param ctx the model context to be injected, must not be null
	 */
	public CompositeCronJobModel(final ItemModelContext ctx)
	{
		super(ctx);
	}
	
	/**
	 * <i>Generated constructor</i> - Constructor with all mandatory attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _job initial attribute declared by type <code>CronJob</code> at extension <code>processing</code>
	 */
	@Deprecated(since = "4.1.1", forRemoval = true)
	public CompositeCronJobModel(final JobModel _job)
	{
		super();
		setJob(_job);
	}
	
	/**
	 * <i>Generated constructor</i> - for all mandatory and initial attributes.
	 * @deprecated since 4.1.1 Please use the default constructor without parameters
	 * @param _job initial attribute declared by type <code>CronJob</code> at extension <code>processing</code>
	 * @param _owner initial attribute declared by type <code>Item</code> at extension <code>core</code>
	 */
	@Deprecated(since = "4.1.1", forRemoval = true)
	public CompositeCronJobModel(final JobModel _job, final ItemModel _owner)
	{
		super();
		setJob(_job);
		setOwner(_owner);
	}
	
	
	/**
	 * <i>Generated method</i> - Getter of the <code>CompositeCronJob.compositeEntries</code> attribute defined at extension <code>processing</code>. 
	 * Consider using FlexibleSearchService::searchRelation for pagination support of large result sets.
	 * @return the compositeEntries - list of cronjobs/jobs which will be executed
	 */
	@Accessor(qualifier = "compositeEntries", type = Accessor.Type.GETTER)
	public List<CompositeEntryModel> getCompositeEntries()
	{
		return getPersistenceContext().getPropertyValue(COMPOSITEENTRIES);
	}
	
	/**
	 * <i>Generated method</i> - Setter of <code>CompositeCronJob.compositeEntries</code> attribute defined at extension <code>processing</code>. 
	 *  
	 * @param value the compositeEntries - list of cronjobs/jobs which will be executed
	 */
	@Accessor(qualifier = "compositeEntries", type = Accessor.Type.SETTER)
	public void setCompositeEntries(final List<CompositeEntryModel> value)
	{
		getPersistenceContext().setPropertyValue(COMPOSITEENTRIES, value);
	}
	
}
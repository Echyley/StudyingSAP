/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryGroupData;
import de.hybris.platform.commercefacades.order.data.PickupOrderEntryGroupData;
import de.hybris.platform.commercefacades.storelocator.data.PointOfServiceData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Required;


/**
 * Updates {@link AbstractOrderData} with order group data basing on delivery POS. Order entry data must be already
 * populated in {@link AbstractOrderData}.
 * 
 */
public class PickupOrderEntryGroupPopulator extends OrderEntryGroupPopulator
{
	private Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		super.populate(source, target);

		if (target.getPickupOrderGroups() == null)
		{
			target.setPickupOrderGroups(new ArrayList<PickupOrderEntryGroupData>());
		}

		for (final AbstractOrderEntryModel entryModel : source.getEntries())
		{
			createUpdatePickupGroupData(entryModel, target);
		}

		for (final OrderEntryGroupData orderEntryGroup : target.getPickupOrderGroups())
		{
			orderEntryGroup.setQuantity(Long.valueOf(sumOrderGroupQuantity(orderEntryGroup)));
		}

		target.setPickupItemsQuantity(Long.valueOf(sumPickupItemsQuantity(source)));
	}


	protected void createUpdatePickupGroupData(final AbstractOrderEntryModel entryModel, final AbstractOrderData target)
	{
		final PointOfServiceModel pointOfServiceModel = entryModel.getDeliveryPointOfService();
		if (pointOfServiceModel != null)
		{
			PickupOrderEntryGroupData groupData = null;
			for (final PickupOrderEntryGroupData listGroupData : target.getPickupOrderGroups())
			{
				if (pointOfServiceModel.getName().equals(listGroupData.getDeliveryPointOfService().getName()))
				{
					groupData = listGroupData;
					break;
				}
			}
			if (groupData == null)
			{
				groupData = new PickupOrderEntryGroupData();
				final PointOfServiceData pointOfServiceData = getPointOfServiceConverter().convert(pointOfServiceModel);
				if (pointOfServiceData != null)
				{
					groupData.setDeliveryPointOfService(pointOfServiceData);
					groupData.setDistance(pointOfServiceData.getDistanceKm());
				}
				groupData.setEntries(new ArrayList<>());
				target.getPickupOrderGroups().add(groupData);
			}

			updateGroupTotalPriceWithTax(entryModel, groupData);
			groupData.getEntries().add(getOrderEntryData(target, entryModel.getEntryNumber()));
		}
	}

	protected Converter<PointOfServiceModel, PointOfServiceData> getPointOfServiceConverter()
	{
		return pointOfServiceConverter;
	}

	@Required
	public void setPointOfServiceConverter(final Converter<PointOfServiceModel, PointOfServiceData> pointOfServiceConverter)
	{
		this.pointOfServiceConverter = pointOfServiceConverter;
	}

}

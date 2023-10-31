/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.saps4omservices.dto.SAPS4OMItemRequestData;
import de.hybris.platform.saps4omservices.dto.SAPS4OMRequestData;

/**
 * Request payload conversion service
 */
public interface SAPS4OMRequestPayloadConversionService {
	
	/**
	 * Prepare request header
	 *
	 * @param itemModel
	 *           the itemModel
	 * @param salesOrg
	 *           sales organization
	 * @param distributionChannel
	 *           distribution channel
	 * @param division
	 *           division
	 * @param orderType
	 * 			order type 
	 * @param userModel
	 * 			user model 
	 * @param isCreditCheckRequired
	 * 			check credit check required 
	 * @return header request data
	 */
	
	public SAPS4OMRequestData getSAPS4OMRequestDataHeaderLevel(ItemModel itemModel, String salesOrg,
			String distributionChannel, String division, String orderType,UserModel userModel, boolean isCreditCheckRequired);
	
	/**
	 * Prepare item data
	 *
	 * @param salesOrder
	 *           sales order
	 * @param itemNumber
	 *           item number
	 * @param material
	 *           material
	 * @param qty
	 *           quantity
	 * @param plant
	 * 			plant
	 * @return  item request data
	 */
	public SAPS4OMItemRequestData getSAPS4OMtemRequestData(String salesOrder, String itemNumber,
			String material, String qty, String plant) ;
	

}

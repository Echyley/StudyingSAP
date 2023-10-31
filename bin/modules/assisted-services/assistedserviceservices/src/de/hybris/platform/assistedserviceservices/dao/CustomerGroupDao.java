/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.dao;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.List;
import java.util.Map;


/**
 * Customer Group DAO
 */
public interface CustomerGroupDao
{

	/**
	 * Get paginated customers for specific customer list
	 *
	 * @param groupsUid
	 *           groups that we want to get customers for
	 * @param pageableData
	 *           paging information
	 * @return customer model search page data
	 */
	<T extends CustomerModel> SearchPageData<T> findAllCustomersByGroups(List<UserGroupModel> groupsUid,
			PageableData pageableData);

	/**
	 * Get paginated customers for specific customer list
	 *
	 * @param groupsUid
	 *           groups that we want to get customers for
	 * @param pageableData
	 *           paging information	 *
	 * @param parameterMap
	 *           extra parameters
	 * @return customer model search page data
	 */
	default <T extends CustomerModel> SearchPageData<T> findAllCustomersByGroups(List<UserGroupModel> groupsUid,
	        PageableData pageableData, final Map<String, Object> parameterMap)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Get paginated customers for specific pos-list where customer have a consignment.
	 *
	 * @param pointOfServiceModels
	 *           POS-es with employee belongs to
	 * @param pageableData
	 *           paging information
	 * @return customer model search page data
	 */
	<T extends CustomerModel> SearchPageData<T> findAllCustomersByConsignmentsInPointOfServices(List<PointOfServiceModel> pointOfServiceModels,
																								PageableData pageableData);
	/**
	 * Get paginated customers for specific pos-list where customer have a consignment.
	 *
	 * @param pointOfServiceModels
	 *           POS-es with employee belongs to
	 * @param pageableData
	 *           paging information
	 * @param parameterMap
	 *           extra parameters
	 * @return customer model search page data
	 */
	default <T extends CustomerModel> SearchPageData<T> findAllCustomersByConsignmentsInPointOfServices(List<PointOfServiceModel> pointOfServiceModels, PageableData pageableData, final Map<String, Object> parameterMap) {
		throw new UnsupportedOperationException();
	}
}

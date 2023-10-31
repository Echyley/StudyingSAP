/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound;

import de.hybris.platform.servicelayer.model.AbstractItemModel;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Converter from a hybris item model to a CSV string. Depends on {@link RawItemContributor} instances defining the
 * columns and extracting the values. The CsvBuilder creates the header rows and data rows and takes care for escaping.
 * 
 * @param <T>
 * 
 */
public interface RawItemBuilder<T extends AbstractItemModel>
{

	/**
	 * Inject the list of contributors to the CSV string to be built. To be called before method @see
	 * {@link #rowsAsNameValuePairs(AbstractItemModel)} is called
	 * 
	 * @param contributors
	 */
	void setContributors(List<RawItemContributor<T>> contributors);

	/**
	 * @return Contributors to the CSV creation
	 */
	List<RawItemContributor<T>> getContributors();

	/**
	 * @param model
	 *           item model for which the raw item shall be assembled
	 * @return Columns of the CSV according to the registered contributors
	 */
	List<Map<String, Object>> rowsAsNameValuePairs(T model);

	/**
	 * 
	 * @return the union of all column names used the the registered contributors
	 */
	Set<String> getColumns();
	
	
	/**
	 * @param c
	 *           contributor to add
	 *
	 */
	public void addContributor(final RawItemContributor<T> c);
	

}

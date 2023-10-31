/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.sqlserver.jdbc.StringUtils;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.saps4omservices.constants.Saps4omservicesConstants;
import de.hybris.platform.sap.saps4omservices.filter.dto.FilterData;
import de.hybris.platform.sap.saps4omservices.order.services.SapS4OMOrderFilterBuilder;
import de.hybris.platform.sap.saps4omservices.order.services.SapS4OMOrderFilterBuilderHook;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultSapS4OMOrderFilterBuilder implements SapS4OMOrderFilterBuilder {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMOrderFilterBuilder.class);
	
	private static final String SINGLE_QUOTE = "'";
	private static final String EQUAL_OPERATOR = "eq";
	private static final String AND_OPERATOR = "and ";
	private static final String SINGLE_SPACE = " ";
	private static final String FILETR_SUFFIX = "$filter=";
	private static final String PAGING_TOP_SUFFIX = "$top=";
	private static final String PAGING_SKIP_SUFFIX = "$skip=";
	private static final String ORDER_BY_SUFFIX = "$orderby=";
	private static final String PAGING_COUNT_SUFFIX="$inlinecount=";
	private static final String PAGING_COUNT="allpages";
	private static final String EXPAND_PARAM_ITEM = "to_Item";
	private static final String EXPAND_PARAM_PRICING_ELEMENT = "to_PricingElement";
	private static final String EXPAND_PARAM_PARTNER = "to_Partner";
	private static final String EXPAND_PARAM_SCHEDULE_LINES = "to_ScheduleLine";
	private static final String EXPAND_SUFFIX = "$expand=";
	private static final String EXPAND_PARAM_BILLING_DOCUMENT = "to_BillingDocument";
	
	private SapS4OrderUtil sapS4OrderUtil;
	private List<SapS4OMOrderFilterBuilderHook> sapS4OMOrderFilterBuilderHooks;
	private ConfigurationService configurationService;
	private BaseStoreService baseStoreService;


	@Override
	public Map<String,List<FilterData>> getOrderHistoryFilters(CustomerModel customerModel,OrderStatus[] status,
			PageableData paginationData, String sortType) {
		
		Map<String,List<FilterData>> filters = new HashMap<>();
		List<FilterData> orderHistoryFilters = new ArrayList<>();
		
		final String orderTransactionType = getSapS4OrderUtil().getCommonTransactionType();
		if(!StringUtils.isEmpty(orderTransactionType)) {
			LOG.debug("Order transaction type is {}", orderTransactionType);
			final FilterData orderTypeFilterData = new FilterData.FilterDataBuilder(getConfigurationService().getConfiguration().getString(Saps4omservicesConstants.ORDER_TYPE))
					.valueWithSpacePrefix(SINGLE_QUOTE+orderTransactionType+SINGLE_QUOTE)
					.operatorWithSpacePrefix(EQUAL_OPERATOR)
					.separatorWithSpacePrefix(AND_OPERATOR)
					.build();
			orderHistoryFilters.add(orderTypeFilterData);
		}
		
		final String soldToParty = getSapS4OrderUtil().getSoldToParty(customerModel);
		if(!StringUtils.isEmpty(soldToParty)) {
			LOG.debug("Sold to party is {}", soldToParty);
			final FilterData soldToFilterData = new FilterData.FilterDataBuilder(getConfigurationService().getConfiguration().getString(Saps4omservicesConstants.SOLD_TO))
					.valueWithSpacePrefix(SINGLE_QUOTE+soldToParty+SINGLE_QUOTE)
					.operatorWithSpacePrefix(EQUAL_OPERATOR)
					.build();
			orderHistoryFilters.add(soldToFilterData);
		}
		
		populateOrgDivisonDistributionFilter(orderHistoryFilters);
		
		if(!orderHistoryFilters.isEmpty()) {
			filters.put(FILETR_SUFFIX, orderHistoryFilters);
		}
		
		final int currentPage = paginationData.getCurrentPage();
		final int pageSize = paginationData.getPageSize();
		final int skip = pageSize * currentPage;
		
		FilterData paginTopFiler = new FilterData.FilterDataBuilder(String.valueOf(pageSize))
				.build();
		filters.put(PAGING_TOP_SUFFIX,Arrays.asList(paginTopFiler));
		
		FilterData paginSkipFiler = new FilterData.FilterDataBuilder(String.valueOf(skip))
				.build();
		filters.put(PAGING_SKIP_SUFFIX,Arrays.asList(paginSkipFiler));
		
		FilterData orderByFiler = new FilterData.FilterDataBuilder(sortType)
				.valueWithSpacePrefix(getConfigurationService().getConfiguration().getString(Saps4omservicesConstants.SORT_ORDER))
				.build();
		filters.put(ORDER_BY_SUFFIX,Arrays.asList(orderByFiler));
		
		FilterData pageCountFiler = new FilterData.FilterDataBuilder(PAGING_COUNT)
				.build();
		filters.put(PAGING_COUNT_SUFFIX,Arrays.asList(pageCountFiler));
		
		LOG.debug("Before populate - Order history filters: {}", filters.toString());
		afterPoulateOrderHistoryFilters(filters,customerModel,status,paginationData,sortType);
		LOG.debug("After populate - Order history filters: {}", filters.toString());
		
		return filters;
	}
	
	@Override
	public Map<String,List<FilterData>> getOrderDetailFilters(){
		Map<String,List<FilterData>> orderDetailsFilters = new HashMap<>();
		List<FilterData> orderDetailsFilter = new ArrayList<>();
		List<FilterData> orderDetailsExpandFilters = new ArrayList<>();
		
		final String soldToParty = getSapS4OrderUtil().getSoldToParty(null);
		if(!StringUtils.isEmpty(soldToParty)) {
			LOG.debug("Sold to party is {}", soldToParty);
			final FilterData soldToFilterData = new FilterData.FilterDataBuilder(getConfigurationService().getConfiguration().getString(Saps4omservicesConstants.SOLD_TO))
					.valueWithSpacePrefix(SINGLE_QUOTE+soldToParty+SINGLE_QUOTE)
					.operatorWithSpacePrefix(EQUAL_OPERATOR)
					.build();
			orderDetailsFilter.add(soldToFilterData);
		}
		
		populateOrgDivisonDistributionFilter(orderDetailsFilter);
		if(!orderDetailsFilter.isEmpty()) {
			orderDetailsFilters.put(FILETR_SUFFIX,orderDetailsFilter);
		}
		
		FilterData expandOrderItemPrice = new FilterData.FilterDataBuilder(EXPAND_PARAM_ITEM + "/" + EXPAND_PARAM_PRICING_ELEMENT)
				.filterDataOperator(",")
				.build();
		orderDetailsExpandFilters.add(expandOrderItemPrice);
		FilterData expandOrderItemScheduleLines = new FilterData.FilterDataBuilder(EXPAND_PARAM_ITEM + "/" + EXPAND_PARAM_SCHEDULE_LINES)
				.filterDataOperator(",")
				.build();
		orderDetailsExpandFilters.add(expandOrderItemScheduleLines);
		FilterData expandOrderPrice = new FilterData.FilterDataBuilder(EXPAND_PARAM_PRICING_ELEMENT)
				.filterDataOperator(",")
				.build();
		orderDetailsExpandFilters.add(expandOrderPrice);
		FilterData expandOrderPartner = new FilterData.FilterDataBuilder(EXPAND_PARAM_PARTNER)
				.build();
		orderDetailsExpandFilters.add(expandOrderPartner);
		
		orderDetailsFilters.put(EXPAND_SUFFIX, orderDetailsExpandFilters);
		
		LOG.debug("Before populate - Expand filters: {}", orderDetailsFilters.toString());
		afterPoulateOrderDetailFilters(orderDetailsFilters);
		LOG.debug("After populate - Expand filters: {}", orderDetailsFilters.toString());
		
		return orderDetailsFilters;
	}
	
	protected void populateOrgDivisonDistributionFilter(List<FilterData> orderHistoryFilters) {
		SAPConfigurationModel sapConfigModel = getBaseStoreService().getCurrentBaseStore().getSAPConfiguration();
		if(sapConfigModel!=null) {
			String salesOrg = sapConfigModel.getSapcommon_salesOrganization();
			if(!StringUtils.isEmpty(salesOrg)) {
				LOG.debug("Sales Organization is {}", salesOrg);
				final FilterData salesOrgFilterData = new FilterData.FilterDataBuilder(SINGLE_SPACE+AND_OPERATOR+getConfigurationService().getConfiguration().getString(Saps4omservicesConstants.SALES_ORG))
						.valueWithSpacePrefix(SINGLE_QUOTE+salesOrg+SINGLE_QUOTE)
						.operatorWithSpacePrefix(EQUAL_OPERATOR)
						.build();
				orderHistoryFilters.add(salesOrgFilterData);
			}
			
			String distChannel = sapConfigModel.getSapcommon_distributionChannel();
			if(!StringUtils.isEmpty(distChannel)) {
				LOG.debug("Distribution channel is {}", distChannel);
				final FilterData distChannelFilterData = new FilterData.FilterDataBuilder(SINGLE_SPACE+AND_OPERATOR+getConfigurationService().getConfiguration().getString(Saps4omservicesConstants.DISTRIBUTION_CHANNEL))
						.valueWithSpacePrefix(SINGLE_QUOTE+distChannel+SINGLE_QUOTE)
						.operatorWithSpacePrefix(EQUAL_OPERATOR)
						.build();
				orderHistoryFilters.add(distChannelFilterData);
			}
			
			String division = sapConfigModel.getSapcommon_division();
			if(!StringUtils.isEmpty(division)) {
				LOG.debug("Division is {}", division);
				final FilterData divisionFilterData = new FilterData.FilterDataBuilder(SINGLE_SPACE+AND_OPERATOR+getConfigurationService().getConfiguration().getString(Saps4omservicesConstants.DIVISION))
						.valueWithSpacePrefix(SINGLE_QUOTE+division+SINGLE_QUOTE)
						.operatorWithSpacePrefix(EQUAL_OPERATOR)
						.build();
				orderHistoryFilters.add(divisionFilterData);
			}
		}
	}
	@Override
	public Map<String,List<FilterData>> getBillingDetailFilters(String orderCode){
		Map<String,List<FilterData>> filters = new HashMap<>();
		List<FilterData> billingDetailsFilters = new ArrayList<>();
		final FilterData orderCodeFilterData = new FilterData.FilterDataBuilder("SalesDocument")
				.operatorWithSpacePrefix(EQUAL_OPERATOR)
				.valueWithSpacePrefix(SINGLE_QUOTE+orderCode+SINGLE_QUOTE+"&$expand="+EXPAND_PARAM_BILLING_DOCUMENT)
				.build();
		
		billingDetailsFilters.add(orderCodeFilterData);
		filters.put(FILETR_SUFFIX, billingDetailsFilters);
		return filters;
	}
	@Override
	public Map<String, List<FilterData>> getBillingPDFFilters(String billingDocumentID) {
		Map<String, List<FilterData>> filters = new HashMap<>();
		List<FilterData> billingDetailsFilters = new ArrayList<>();
		final FilterData orderCodeFilterData = new FilterData.FilterDataBuilder(
				"BillingDocument=" +SINGLE_QUOTE+billingDocumentID+SINGLE_QUOTE).build();

		billingDetailsFilters.add(orderCodeFilterData);
		filters.put("", billingDetailsFilters);
		return filters;
	}
	
	protected void afterPoulateOrderHistoryFilters(Map<String, List<FilterData>> filters,
			CustomerModel customerModel, OrderStatus[] status,PageableData paginationData, String sortType) {
		if (getSapS4OMOrderFilterBuilderHooks() != null)
		{
			for (final SapS4OMOrderFilterBuilderHook orderFilterBuilderHook : getSapS4OMOrderFilterBuilderHooks())
			{
				orderFilterBuilderHook.afterOrderHistoryFilter(filters, customerModel, status, paginationData, sortType);
			}
		}
	}
	
	protected void afterPoulateOrderDetailFilters(Map<String, List<FilterData>> filters) {
		if (getSapS4OMOrderFilterBuilderHooks() != null)
		{
			for (final SapS4OMOrderFilterBuilderHook orderFilterBuilderHook : getSapS4OMOrderFilterBuilderHooks())
			{
				orderFilterBuilderHook.afterOrderDetailsFilter(filters);
			}
		}
	}

	public SapS4OrderUtil getSapS4OrderUtil() {
		return sapS4OrderUtil;
	}

	public void setSapS4OrderUtil(SapS4OrderUtil sapS4OrderUtil) {
		this.sapS4OrderUtil = sapS4OrderUtil;
	}

	public List<SapS4OMOrderFilterBuilderHook> getSapS4OMOrderFilterBuilderHooks() {
		return sapS4OMOrderFilterBuilderHooks;
	}

	public void setSapS4OMOrderFilterBuilderHooks(
			List<SapS4OMOrderFilterBuilderHook> sapS4OMOrderFilterBuilderHooks) {
		this.sapS4OMOrderFilterBuilderHooks = sapS4OMOrderFilterBuilderHooks;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}
	

}

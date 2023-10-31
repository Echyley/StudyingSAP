/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.jalo.BusinessProcess;
import de.hybris.platform.sap.orderexchange.outbound.OrderExchangeRepair;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.List;


/**
 * This class is to repair order information (creation oder or cancelling) from hybris to ERP by a cron job. It provides
 * service implementations that can be used to realize this.
 */
public class DefaultOrderExchangeRepair implements OrderExchangeRepair {

  private FlexibleSearchService flexibleSearchService;
  private ModelService modelService;

  public List<OrderProcessModel> findAllProcessModelsToRepair(final String processDefinitionName, final String endMessage) {

    StringBuilder queryBuilder = new StringBuilder("SELECT {PK} FROM {OrderProcess} WHERE {");

    queryBuilder.append(BusinessProcess.PROCESSDEFINITIONNAME);
    queryBuilder.append("} = ?processDefinitionName AND {");
    queryBuilder.append(BusinessProcess.STATE);
    queryBuilder.append("} = ?state AND {");
    queryBuilder.append(BusinessProcess.ENDMESSAGE);
    queryBuilder.append("} LIKE ?endMessage");


    final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryBuilder);

    searchQuery.addQueryParameter("processDefinitionName", processDefinitionName);
    searchQuery.addQueryParameter("state", ProcessState.ERROR);
    searchQuery.addQueryParameter("endMessage", endMessage);
    
    return flexibleSearchService.<OrderProcessModel>search(searchQuery).getResult();

  }

  @Override
  public List<OrderProcessModel> findProcessesByActionIds(final String processName, final String processCurrentActions[]) {

    StringBuilder queryBuilder = new StringBuilder("SELECT {bp.PK} ");

    queryBuilder.append("FROM {OrderProcess AS bp JOIN ProcessTask AS pt ON {bp.PK} = {pt.process}} ");
    queryBuilder.append("WHERE {bp.processDefinitionName} = ?processDefinitionName AND {pt.action} IN (?processCurrentActions)");

    final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryBuilder);
    searchQuery.addQueryParameter("processDefinitionName", processName);
    searchQuery.addQueryParameter("processCurrentActions", Arrays.asList(processCurrentActions));

    return flexibleSearchService.<OrderProcessModel>search(searchQuery).getResult();

  }


  @Override
  public List<OrderModel> findAllOrdersInStatus(final OrderStatus orderStatus) {

    final String query = "SELECT {PK} FROM {Order} WHERE  {status} = ?status AND {versionID} IS NULL";

    final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
    searchQuery.addQueryParameter("status", orderStatus);

    return flexibleSearchService.<OrderModel>search(searchQuery).getResult();

  }


  
  public FlexibleSearchService getFlexibleSearchService() {
    return flexibleSearchService;
  }

  
  @Required
  public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
    this.flexibleSearchService = flexibleSearchService;
  }

  
  public ModelService getModelService() {
    return modelService;
  }

  
  @Required
  public void setModelService(final ModelService modelService) {
    this.modelService = modelService;
  }

}

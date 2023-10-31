/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.dao.impl;

import com.sap.hybris.saprevenuecloudproduct.dao.impl.DefaultSapSubscriptionProductDao;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import com.sap.hybris.saprevenuecloudproduct.dao.SapSubscriptionPricePlanWithEffectiveDateDao;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

public class DefaultSapSubscriptionPricePlanWithEffectiveDateDao extends DefaultSapSubscriptionProductDao implements SapSubscriptionPricePlanWithEffectiveDateDao {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSapSubscriptionPricePlanWithEffectiveDateDao.class);


    private static final String GET_SUBSCRIPTION_PRICE_PLAN_FOR_ID = "SELECT {" + SubscriptionPricePlanModel.PK
            + "} FROM { " + SubscriptionPricePlanModel._TYPECODE + " AS sp} WHERE {sp."
            + SubscriptionPricePlanModel.PRICEPLANID + "} = ?" + SubscriptionPricePlanModel.PRICEPLANID + " AND {"
            + SubscriptionPricePlanModel.CATALOGVERSION + "} = ?" + SubscriptionPricePlanModel.CATALOGVERSION + " AND {"+
            SubscriptionPricePlanModel.STARTTIME +"} <= ?" + SubscriptionPricePlanModel.STARTTIME + " AND {"+  SubscriptionPricePlanModel.ENDTIME +"} >=?" + SubscriptionPricePlanModel.ENDTIME ;

    private static final String CATALOG_VERSIONS = "catalogVersions";
    private static final String QUERY_SUBSCRIPTION_PRICE_PLAN_BY_ID_AND_CATALOGS = "SELECT {" + SubscriptionPricePlanModel.PK
            + "} FROM { " + SubscriptionPricePlanModel._TYPECODE + " AS sp} WHERE {sp."
            + SubscriptionPricePlanModel.PRICEPLANID + "} = ?" + SubscriptionPricePlanModel.PRICEPLANID + " AND {"
            + SubscriptionPricePlanModel.STARTTIME +"} <= ?" + SubscriptionPricePlanModel.STARTTIME + " AND {"+  SubscriptionPricePlanModel.ENDTIME +"} >=?" + SubscriptionPricePlanModel.ENDTIME
            + " AND {" + SubscriptionPricePlanModel.CATALOGVERSION + "} IN (?" + CATALOG_VERSIONS+")";
    @Override
    public Optional<SubscriptionPricePlanModel> getSubscriptionPricesWithEffectiveDate(final String pricePlanId,
                                                                                       final CatalogVersionModel catalogVersion, final Date effectiveAt) {

        validateParameterNotNullStandardMessage("pricePlanId", pricePlanId);
        validateParameterNotNullStandardMessage("catalogVersion", catalogVersion);
        validateParameterNotNullStandardMessage("effectiveAt",effectiveAt);



        final FlexibleSearchQuery query = new FlexibleSearchQuery(GET_SUBSCRIPTION_PRICE_PLAN_FOR_ID);
        query.addQueryParameter(SubscriptionPricePlanModel.PRICEPLANID, pricePlanId);
        query.addQueryParameter(SubscriptionPricePlanModel.CATALOGVERSION, catalogVersion);
        query.addQueryParameter(SubscriptionPricePlanModel.STARTTIME, effectiveAt);
        query.addQueryParameter(SubscriptionPricePlanModel.ENDTIME, effectiveAt);
        query.setCount(1);

        try {
            final SubscriptionPricePlanModel pricePlan = getFlexibleSearchService().searchUnique(query);
            return Optional.of(pricePlan);
        } catch (ModelNotFoundException | AmbiguousIdentifierException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Error while fetching the SubscriptionPricePlan for price plan ID:" + pricePlanId + ".Error :"
                        + e);
            }
            LOG.error(String.format(
                    "Error while fetching the SubscriptionPricePlan for price plan ID [%s] and Catalog Version [%s:%s] ",
                    pricePlanId, catalogVersion.getCatalog().getId(), catalogVersion.getVersion()));
            return Optional.empty();
        }
    }



    @Override
    public Optional<SubscriptionPricePlanModel> getSubscriptionPricesWithEffectiveDate(final String pricePlanId, final List<CatalogVersionModel> catalogVersions, final Date effectiveAt){

        validateParameterNotNullStandardMessage("pricePlanId", pricePlanId);
        validateParameterNotNullStandardMessage("catalogVersions", catalogVersions);
        validateParameterNotNullStandardMessage("effectiveAt",effectiveAt);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_SUBSCRIPTION_PRICE_PLAN_BY_ID_AND_CATALOGS);
        query.addQueryParameter(SubscriptionPricePlanModel.PRICEPLANID, pricePlanId);
        query.addQueryParameter(CATALOG_VERSIONS, catalogVersions);
        query.addQueryParameter(SubscriptionPricePlanModel.STARTTIME, effectiveAt);
        query.addQueryParameter(SubscriptionPricePlanModel.ENDTIME, effectiveAt);
        query.setCount(1);

        try {
            SubscriptionPricePlanModel pricePlan = getFlexibleSearchService().searchUnique(query);
            return Optional.of(pricePlan);

        } catch (ModelNotFoundException | AmbiguousIdentifierException e) {

            List<String> catalogsNames = catalogVersions.stream().map( catalogVersionModel -> {
                CatalogModel catalog = catalogVersionModel.getCatalog();
                return String.format("%s : %s", catalog.getId(), catalog.getVersion());
            }).collect(Collectors.toList());

            LOG.error(String.format(
                    "Error while fetching the SubscriptionPricePlan for price plan ID [%s] and Catalog Version [%s] ",
                    pricePlanId, catalogsNames));
        }
        return  Optional.empty();
    }

}
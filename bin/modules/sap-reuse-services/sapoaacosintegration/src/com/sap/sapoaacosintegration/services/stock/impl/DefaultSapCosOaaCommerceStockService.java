/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.stock.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import de.hybris.platform.basecommerce.enums.PointOfServiceTypeEnum;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.storelocator.PointOfServiceDao;


import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.atp.strategy.ATPAggregationStrategy;
import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.stock.impl.DefaultSapOaaCommerceStockService;


/**
 * Provides stock level for COS use-cases
 */
public class DefaultSapCosOaaCommerceStockService extends DefaultSapOaaCommerceStockService
{

	private static final Logger LOG = Logger.getLogger(DefaultSapCosOaaCommerceStockService.class);

	private ATPService atpService;
	private PointOfServiceDao pointOfServiceDao;
	private ATPAggregationStrategy atpAggregationStrategy;
	private CommonUtils commonUtils;


	/**
	 * fetches {@link StockLevelStatus} for a {@link ProductModel} and {@link BaseStoreModel}
	 *
	 * @param product
	 * @param baseStore
	 * @return {@link Long}
	 */
	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		final StockLevelStatus stockLevelStatus = this.getStockLevelStatusForProductAndBaseStore(product, baseStore);

		return getStockLevel(stockLevelStatus, product, baseStore);
		}
		else
		{
			return super.getStockLevelForProductAndBaseStore(product, baseStore);
		}
	}


	/**
	 * @param product
	 * @param baseStore
	 * @return {@link StockLevelStatus}
	 */
	@Override
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		return checkStockLevelStatus(getAvailableStockLevel(null, null, product, null));
		}
		else
		{
			return super.getStockLevelStatusForProductAndBaseStore(product, baseStore);
		}

	}

	@Override
	public StockLevelStatus getStockLevelStatusForProductAndPointOfService(final ProductModel product,
			final PointOfServiceModel pointOfService)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		if (this.getStockLevelForProductAndPointOfService(product, pointOfService).longValue() > 0)
		{
			return StockLevelStatus.INSTOCK;
		}
		else
		{
			return StockLevelStatus.OUTOFSTOCK;
		}
		}
		else
		{
			return super.getStockLevelStatusForProductAndPointOfService(product, pointOfService);
		}
	}

	@Override
	public Long getStockLevelForProductAndPointOfService(final ProductModel product, final PointOfServiceModel pointOfService)
	{
		if (getCommonUtils().isCOSEnabled())
		{
			return super.retrieveStockLevelForProduct(product, pointOfService);
		}
		else
		{
			return super.getStockLevelForProductAndPointOfService(product, pointOfService);
		}
	}

	@Override
	public Long getStockLevel(final StockLevelStatus stockLevelStatus, final ProductModel product,
			final BaseStoreModel baseStore)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		if (stockLevelStatus.equals(StockLevelStatus.INSTOCK))
		{
			return null;
		}

		if (stockLevelStatus.equals(StockLevelStatus.OUTOFSTOCK))
		{
			return Long.valueOf(0);
		}

		if (stockLevelStatus.equals(StockLevelStatus.LOWSTOCK))
		{
			return getAvailableStockLevel(null, null, product, null);

		}
		return Long.valueOf(0);
		}
		else
		{
			return super.getStockLevel(stockLevelStatus, product, baseStore);
		}
	}

	/**
	 * @param stockLevel
	 * @return {@link StockLevelStatus}
	 */

	private StockLevelStatus checkStockLevelStatus(final Long stockLevel)
	{
		if (stockLevel > 0)
		{
			return StockLevelStatus.INSTOCK;
		}
		else
		{
			return StockLevelStatus.OUTOFSTOCK;
		}

	}


	@Override
	public List<ATPAvailability> getAvailabilityForProduct(final String cartGuid, final String itemId, final ProductModel product)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		return atpService.callRestAvailabilityServiceForProduct(cartGuid, itemId, product);
		}
		else
		{
			return super.getAvailabilityForProduct(cartGuid, itemId, product);
		}
	}


	@Override
	public List<ATPAvailability> getAvailabilityForProductAndSource(final ProductModel product, final String source)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		return atpService.callRestAvailabilityServiceForProductAndSource(product, source);
		}
		else
		{
			return super.getAvailabilityForProductAndSource(product, source);
		}
	}


	@Override
	public List<ATPAvailability> getAvailabilityForProductAndSource(final String cartGuid, final String itemId,
			final ProductModel product, final String source)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		return atpService.callRestAvailabilityServiceForProductAndSource(cartGuid, itemId, product, source);
		}
		else
		{
			return super.getAvailabilityForProductAndSource(cartGuid, itemId, product, source);
		}
	}


	@Override
	public List<ATPProductAvailability> getAvailabilityForProducts(final String cartGuid, final String itemId,
			final String productUnit, final List<ProductModel> productList)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		//As hybris does not support batch mode, we pass only single item id and single product unit
		final List<String> itemIdList = new ArrayList<>();
		itemIdList.add(itemId);

		return atpService.callRestAvailabilityServiceForProducts(cartGuid, itemIdList, productUnit, productList);
		}
		else
		{
			return super.getAvailabilityForProducts(cartGuid, itemId, productUnit, productList);
		}
	}


	@Override
	public List<ATPProductAvailability> getAvailabilityForProductAndSources(final String cartGuid, final String itemId,
			final ProductModel product, final List<String> sourcesList)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		return atpService.callRestAvailabilityServiceForProductAndSources(cartGuid, itemId, product, sourcesList);
		}
		else
		{
			return super.getAvailabilityForProductAndSources(cartGuid, itemId, product, sourcesList);
		}
	}

	@Override
	public Map<PointOfServiceModel, StockLevelStatus> getPosAndStockLevelStatusForProduct(final ProductModel product,
			final BaseStoreModel baseStore)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		final List<String> sourcesList = new ArrayList<>();
		for (final PointOfServiceModel pointOfService : baseStore.getPointsOfService())
		{
			if (pointOfService.getType().equals(PointOfServiceTypeEnum.STORE))
			{
				sourcesList.add(pointOfService.getName());
			}
		}

		final Map<PointOfServiceModel, StockLevelStatus> stockLevelStatusMap = new HashedMap();

		try
		{
			final List<ATPProductAvailability> availabilities = atpService.callRestAvailabilityServiceForProductAndSources(null,
					null, product, sourcesList);

			for (final ATPProductAvailability availability : availabilities)
			{
				final PointOfServiceModel pointOfService = determinePointOfService(availability.getSourceId());


				if (getAtpAggregationStrategy()
						.aggregateAvailability(null, product, pointOfService, availability.getAvailabilityList()).longValue() > 0
						&& pointOfService != null)
				{
					stockLevelStatusMap.put(pointOfService, StockLevelStatus.INSTOCK);
				}
				else
				{
					stockLevelStatusMap.put(pointOfService, StockLevelStatus.OUTOFSTOCK);
				}
			}
		}
		catch (final ATPException e)
		{
			LOG.error("Error when fetching stock level information form COS", e);
		}
		catch (final BackendDownException e)
		{
			LOG.error(e.getMessage(), e);
		}


		return stockLevelStatusMap;
		}
		else
		{
			return super.getPosAndStockLevelStatusForProduct(product, baseStore);
		}
	}

	@Override
	public Long getAvailableStockLevelForPos(final String cartGuid, final ProductModel productModel,
			final PointOfServiceModel pointOfService)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		long availableStock = 0;

		final List<ATPAvailability> productAvailabilityList = getAvailabilityForProductAndSource(productModel,
				pointOfService.getName());

		if (productAvailabilityList != null && !productAvailabilityList.isEmpty())
		{
			availableStock = atpAggregationStrategy
					.aggregateAvailability(null, productModel, pointOfService, productAvailabilityList).longValue();
		}

		return Long.valueOf(availableStock);
		}
		else
		{
			return super.getAvailableStockLevelForPos(cartGuid, productModel, pointOfService);
		}
	}


	@Override
	public StockLevelModel getStockLevelForRSI(final ProductModel product, final BaseStoreModel baseStore)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		final Collection<StockLevelModel> stockLevels = getStockService().getStockLevels(product,
				getWarehouseSelectionStrategy().getWarehousesForBaseStore(baseStore));

		if (stockLevels.isEmpty())
		{
			return null;
		}

		return stockLevels.iterator().next();
		}
		else
		{
			return super.getStockLevelForRSI(product, baseStore);
		}

	}


	@Override
	public Long getAvailableStockLevel(final String cartGuid, final String itemId, final ProductModel productModel,
			final PointOfServiceModel pointOfServiceModel)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		long availableStock = 0;

		List<ATPAvailability> productAvailabilityList;

		if (pointOfServiceModel == null)
		{
			productAvailabilityList = atpService.callRestAvailabilityServiceForProduct(cartGuid, itemId, productModel);
		}
		else
		{
			productAvailabilityList = atpService.callRestAvailabilityServiceForProductAndSource(cartGuid, itemId, productModel,
					pointOfServiceModel.getName());
		}

		if (productAvailabilityList != null && !productAvailabilityList.isEmpty())
		{
			availableStock = atpAggregationStrategy
					.aggregateAvailability(cartGuid, productModel, pointOfServiceModel, productAvailabilityList).longValue();
		}
		return Long.valueOf(availableStock);
		}
		else
		{
			return super.getAvailableStockLevel(cartGuid, itemId, productModel, pointOfServiceModel);
		}
	}


	/**
	 * @return the atpService
	 */
	@Override
	public ATPService getAtpService()
	{
		return atpService;
	}


	/**
	 * @param atpService
	 *           the atpService to set
	 */
	@Override
	public void setAtpService(final ATPService atpService)
	{
		this.atpService = atpService;
	}


	/**
	 * @return the pointOfServiceDao
	 */
	@Override
	public PointOfServiceDao getPointOfServiceDao()
	{
		return pointOfServiceDao;
	}


	/**
	 * @param pointOfServiceDao
	 *           the pointOfServiceDao to set
	 */
	@Override
	public void setPointOfServiceDao(final PointOfServiceDao pointOfServiceDao)
	{
		this.pointOfServiceDao = pointOfServiceDao;
	}


	/**
	 * @return the atpAggregationStrategy
	 */
	@Override
	public ATPAggregationStrategy getAtpAggregationStrategy()
	{
		return atpAggregationStrategy;
	}


	/**
	 * @param atpAggregationStrategy
	 *           the atpAggregationStrategy to set
	 */
	@Override
	public void setAtpAggregationStrategy(final ATPAggregationStrategy atpAggregationStrategy)
	{
		this.atpAggregationStrategy = atpAggregationStrategy;
	}

	/**
	 * @param sourceId
	 * @return pointOfServiceModel
	 */
	@Override
	public PointOfServiceModel determinePointOfService(final String sourceId)
	{
		return pointOfServiceDao.getPosByName(sourceId);
	}

	@Override
	public CommonUtils getCommonUtils()
	{
		return commonUtils;
	}


	@Override
	public void setCommonUtils(final CommonUtils commonUtils)
	{
		this.commonUtils = commonUtils;
	}

}

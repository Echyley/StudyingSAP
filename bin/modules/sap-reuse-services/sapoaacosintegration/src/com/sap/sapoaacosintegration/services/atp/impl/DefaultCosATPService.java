/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.atp.impl;



import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.outboundservices.client.IntegrationRestTemplateFactory;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;
import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.common.util.ServiceUtils;
import com.sap.retail.oaa.commerce.services.rest.util.exception.RestInitializationException;
import com.sap.sapoaacosintegration.constants.SapoaacosintegrationConstants;
import com.sap.sapoaacosintegration.exception.COSDownException;
import com.sap.sapoaacosintegration.services.atp.CosATPResourcePathBuilder;
import com.sap.sapoaacosintegration.services.atp.CosATPResultHandler;
import com.sap.sapoaacosintegration.services.atp.request.ArticleSources;
import com.sap.sapoaacosintegration.services.atp.request.CosSource;
import com.sap.sapoaacosintegration.services.atp.response.ArticleBySource;
import com.sap.sapoaacosintegration.services.atp.response.ArticleQuantityBySource;
import com.sap.sapoaacosintegration.services.atp.response.ArticleResponse;
import com.sap.sapoaacosintegration.services.atp.response.ArticleSourceResponse;
import com.sap.sapoaacosintegration.services.common.util.CosServiceUtils;
import com.sap.sapoaacosintegration.services.rest.impl.DefaultAbstractCosRestService;




/**
 * Default COS ATP service
 */
public class DefaultCosATPService extends DefaultAbstractCosRestService implements ATPService
{
	private static final Logger LOG = Logger.getLogger(DefaultCosATPService.class);

	private CosATPResultHandler cosAtpResultHandler;
	private CosATPResourcePathBuilder cosAtpResourcePathBuilder;
	private IntegrationRestTemplateFactory integrationRestTemplateFactory;
	private CosServiceUtils cosServiceUtils;
	private ServiceUtils serviceUtils;
	private CommonUtils commonUtils;





	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProduct(java.lang.String,
	 * de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProduct(final String cartGuid, final String itemId,
			final ProductModel product)
	{
		final List<ATPAvailability> atpavailabilities = new ArrayList<>();
		if (getCommonUtils().isCOSEnabled())
		{
			HttpEntity entity = null;

			try
			{
				validateProduct(product);
				entity = getCosAtpResourcePathBuilder().prepareRestCallForProduct(product);
				return exchangeRestTemplateAndExtractATPResult(entity);
			}
			catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
			{
				LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
				throw new ATPException(e);
			}
		}
		return atpavailabilities;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProductAndSource(
	 * de.hybris.platform.core.model.product.ProductModel, java.lang.String)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final String cartGuid, final String itemId,
			final ProductModel product, final String source)
	{
		final List<ATPAvailability> atpavailabilities = new ArrayList<>();
		if (getCommonUtils().isCOSEnabled())
		{
			HttpEntity entity = null;

			try
			{
				validateProductAndSource(product, source);
				entity = getCosAtpResourcePathBuilder().prepareRestCallForProductAndSource(product, source);
				return exchangeRestTemplateAndExtractATPResult(entity);
			}
			catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
			{
				LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
				throw new ATPException(e);
			}
		}
		return atpavailabilities;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProductAndSource(de.hybris.
	 * platform .core.model.product.ProductModel, java.lang.String)
	 */
	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(final ProductModel product, final String source)
	{
		return this.callRestAvailabilityServiceForProductAndSource(null, null, product, source);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProducts(java.util.List)
	 */
	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProducts(final String cartGuid,
			final List<String> itemIdList, final String productUnit, final List<ProductModel> productList)
	{

		final List<ATPProductAvailability> atpProductAvailabilities = new ArrayList<>();
		if (getCommonUtils().isCOSEnabled())
		{
			HttpEntity entity = null;

			try
			{
				validateProducts(productList);
				entity = getCosAtpResourcePathBuilder().prepareRestCallForProducts(productList);
				return exchangeRestTemplateAndExtractATPProductResult(entity);


			}
			catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
			{
				LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
				throw new ATPException(e);
			}
		}
		return atpProductAvailabilities;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sap.retail.oaa.commerce.services.atp.ATPService#callRestAvailabilityServiceForProductAndSources(de.hybris.
	 * platform.core.model.product.ProductModel, java.util.List)
	 */
	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProductAndSources(final String cartGuid, final String itemId,
			final ProductModel product, final List<String> sourcesList)
	{
		final List<ATPProductAvailability> atpProductAvailabilities = new ArrayList<>();
		if (getCommonUtils().isCOSEnabled())
		{

			try
			{
				validateProductAndSources(product, sourcesList);
				return exchangeRestTemplateAndExtractATPBatchResult(sourcesList, product);
			}
			catch (final IllegalArgumentException | RestClientException | RestInitializationException e)
			{
				LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
				throw new ATPException(e);
			}
		}
		return atpProductAvailabilities;

	}



	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param product
	 * @param sourcesList
	 *
	 * @return list of ATPProductAvailability
	 *
	 * @throws ATPException
	 * @throws COSDownException
	 */
	private List<ATPProductAvailability> exchangeRestTemplateAndExtractATPBatchResult(final List<String> sourcesList,
			final ProductModel product)
	{
		ResponseEntity<ArticleSourceResponse> response = null;
		HttpEntity entity;
		List<ATPProductAvailability> batchResult = new ArrayList<>();
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);

			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.AVAILABILITY_SOURCE_RESOURCE_PATH);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			entity = prepareRestCallForProductAndSources(product, sourcesList);
			response = restOperations.exchange(finalUri.toString(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<ArticleSourceResponse>()
					{
					});

			final ArticleSourceResponse articleSourceResponse = response.getBody();
			if (articleSourceResponse != null && !articleSourceResponse.getItems().isEmpty())
			{
				batchResult = getProductAvailabilitiesForBatch(articleSourceResponse);
			}
			else
			{
				for (final String source : sourcesList)
				{
					final ATPProductAvailability productAvailability = new ATPProductAvailability();
					productAvailability.setArticleId(product.getCode());
					productAvailability.setSourceId(source);
					final List<ATPAvailability> availabilities = new ArrayList<>();
					final ATPAvailability noStockAvailable = new ATPAvailability();
					noStockAvailable.setQuantity(Double.valueOf("0"));
					noStockAvailable.setAtpDate(new Date());
					availabilities.add(noStockAvailable);
					productAvailability.setAvailabilityList(availabilities);
					batchResult.add(productAvailability);
				}

			}

		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE + "   " + e.getResponseBodyAsString());
			throw new ATPException(e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}


		return batchResult;
	}

	private HttpEntity prepareRestCallForProductAndSources(final ProductModel product, final List<String> sources)
	{
		HttpEntity entity;
		final List<CosSource> sourceList = new ArrayList<>();
		final HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.ACCEPT, MediaType.ALL_VALUE);
		header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		final ArticleSources articleSource = new ArticleSources();
		articleSource.setArticleId(product.getCode());
		articleSource.setUnit(StringUtils.left(product.getUnit().getCode(), SapoaacosintegrationConstants.UNIT_MAX_LENGTH));
		for (final String source : sources)
		{
			final CosSource cosSource = new CosSource();
			cosSource.setSourceId(source);
			cosSource.setSourceType(SapoaacosintegrationConstants.SOURCE_TYPE_STORE);
			sourceList.add(cosSource);

		}
		articleSource.setSources(sourceList);
		entity = new HttpEntity<>(articleSource, header);
		return entity;
	}


	/**
	 *
	 */
	private List<ATPProductAvailability> getProductAvailabilitiesForBatch(final ArticleSourceResponse articleSourceResponse)
	{
		final List<ATPProductAvailability> productAvailabilityList = new ArrayList<>();
		final ArticleBySource productQuantityBySource = articleSourceResponse.getItems().get(0);
		for (final ArticleQuantityBySource quantitybySource : productQuantityBySource.getQuantityBySource())
		{
			final ATPProductAvailability productAvailability = new ATPProductAvailability();
			productAvailability.setArticleId(serviceUtils.removeLeadingZeros(productQuantityBySource.getProductId()));
			productAvailability.setSourceId(quantitybySource.getSource().getSourceId());
			productAvailability.setAvailabilityList(getAvailabilitiesForBatch(quantitybySource));
			productAvailabilityList.add(productAvailability);
		}
		return productAvailabilityList;
	}

	/**
	 *
	 */
	private List<ATPAvailability> getAvailabilitiesForBatch(final ArticleQuantityBySource quantitybySource)
	{
		final List<ATPAvailability> availabilities = new ArrayList<>();
		final ATPAvailability availabilityModel = new ATPAvailability();
		availabilityModel.setQuantity(quantitybySource.getQuantity());
		availabilityModel.setAtpDate(new Date());
		availabilities.add(availabilityModel);
		return availabilities;

	}



	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param entity
	 * @return list of ATPAvailability
	 * @throws ATPException
	 * @throws COSDownException
	 */
	private List<ATPAvailability> exchangeRestTemplateAndExtractATPResult(final HttpEntity entity)
	{
		ResponseEntity<List<ArticleResponse>> response = null;
		final List<ATPAvailability> atpResult = new ArrayList<>();
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);
			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.AVAILABILITY_RESOURCE_PATH);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			response = restOperations.exchange(finalUri.toString(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<List<ArticleResponse>>()
					{
					});


			return response.getBody() != null
					? getCosAtpResultHandler().extractATPAvailabilityFromArticleResponse(response.getBody())
					: atpResult;

		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}

	}

	/**
	 * Encapsulated of Rest template exchange call. Extracts and converts response to result POJO
	 *
	 * @param entity
	 *           Entity Value
	 * @return list of ATPAvailability
	 * @throws ATPException
	 * @throws COSDownException
	 */
	public List<ATPProductAvailability> exchangeRestTemplateAndExtractATPProductResult(final HttpEntity entity)
	{
		ResponseEntity<List<ArticleResponse>> response = null;
		final List<ATPProductAvailability> atpResult = new ArrayList<>();
		try
		{
			final ConsumedDestinationModel destinationModel = getCosServiceUtils()
					.getConsumedDestinationModelById(SapoaacosintegrationConstants.SCP_COS_DESTINATIONID);
			final StringBuilder finalUri = new StringBuilder(destinationModel.getUrl()).append(SapoaacosintegrationConstants.SLASH)
					.append(SapoaacosintegrationConstants.AVAILABILITY_RESOURCE_PATH);
			final RestOperations restOperations = getIntegrationRestTemplateFactory().create(destinationModel);
			response = restOperations.exchange(finalUri.toString(), HttpMethod.POST, entity,
					new ParameterizedTypeReference<List<ArticleResponse>>()
					{
					});

			return (response != null && response.getBody() != null)
					? getCosAtpResultHandler().extractATPProductAvailabilityFromArticleResponse(response.getBody())
					: atpResult;

		}
		catch (final HttpClientErrorException e)
		{
			LOG.error(SapoaacosintegrationConstants.COS_ATP_ERROR_MESSAGE);
			throw new ATPException(e);
		}
		catch (final ModelNotFoundException e)
		{
			throw new ATPException(e);
		}
		catch (final ResourceAccessException e)
		{
			setBackendDown(true);
			throw new COSDownException(SapoaacosintegrationConstants.COS_DOWN_MESSAGE, e);
		}

	}

	/**
	 * Validates if a product is set
	 *
	 * @param productModel
	 */
	private void validateProduct(final ProductModel productModel)
	{
		if (productModel == null || productModel.getCode() == null)
		{
			throw new IllegalArgumentException("Product cannot be null");
		}
	}

	/**
	 * Validates if an OAA profile, product and source are set
	 *
	 * @param productModel
	 * @throws IllegalArgumentException
	 */
	private void validateProductAndSource(final ProductModel productModel, final String source)
	{
		this.validateProduct(productModel);
		if (source == null || source.isEmpty())
		{
			throw new IllegalArgumentException("Source is not maintained");
		}
	}

	/**
	 * Validates if an OAA profile and product list are set
	 *
	 * @param productModelList
	 * @throws IllegalArgumentException
	 */
	private void validateProducts(final List<ProductModel> productModelList)
	{
		if (productModelList == null || productModelList.isEmpty())
		{
			throw new IllegalArgumentException("Product list cannot be null or empty");
		}
		else
		{
			for (final ProductModel productModel : productModelList)
			{
				this.validateProduct(productModel);
			}
		}
	}

	/**
	 * Validates if product and list of sources are set
	 *
	 * @param productModel
	 * @param sourcesList
	 * @throws IllegalArgumentException
	 */
	private void validateProductAndSources(final ProductModel productModel, final List<String> sourcesList)
	{
		this.validateProduct(productModel);
		if (sourcesList == null || sourcesList.isEmpty())
		{
			throw new IllegalArgumentException("Source is not maintained");
		}
	}



	public IntegrationRestTemplateFactory getIntegrationRestTemplateFactory()
	{
		return integrationRestTemplateFactory;
	}

	public void setIntegrationRestTemplateFactory(final IntegrationRestTemplateFactory integrationRestTemplateFactory)
	{
		this.integrationRestTemplateFactory = integrationRestTemplateFactory;
	}

	public CosATPResultHandler getCosAtpResultHandler()
	{
		return cosAtpResultHandler;
	}


	public void setCosAtpResultHandler(final CosATPResultHandler cosAtpResultHandler)
	{
		this.cosAtpResultHandler = cosAtpResultHandler;
	}


	public CosATPResourcePathBuilder getCosAtpResourcePathBuilder()
	{
		return cosAtpResourcePathBuilder;
	}


	public void setCosAtpResourcePathBuilder(final CosATPResourcePathBuilder cosAtpResourcePathBuilder)
	{
		this.cosAtpResourcePathBuilder = cosAtpResourcePathBuilder;
	}

	/**
	 * @return the cosServiceUtils
	 */
	public CosServiceUtils getCosServiceUtils()
	{
		return cosServiceUtils;
	}

	/**
	 * @param cosServiceUtils
	 *           the cosServiceUtils to set
	 */
	public void setCosServiceUtils(final CosServiceUtils cosServiceUtils)
	{
		this.cosServiceUtils = cosServiceUtils;
	}

	/**
	 * @param serviceUtils
	 *           the serviceUtils to set
	 */
	public void setServiceUtils(final ServiceUtils serviceUtils)
	{
		this.serviceUtils = serviceUtils;
	}

	/**
	 * @return the serviceUtils
	 */
	protected ServiceUtils getServiceUtils()
	{
		return serviceUtils;
	}

	public CommonUtils getCommonUtils()
	{
		return commonUtils;
	}

	public void setCommonUtils(final CommonUtils commonUtils)
	{
		this.commonUtils = commonUtils;
	}

}

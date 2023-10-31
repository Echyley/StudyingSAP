/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.configurablebundlefacades.order.impl;

import com.sap.security.core.server.csi.XSSEncoder;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.ProductSearchFacade;
import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.DisableProductBundleRuleModel;
import de.hybris.platform.core.enums.GroupType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.util.Config;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link BundleCartFacade}
 *
 * @since 6.4
 */
public class DefaultBundleCommerceCartFacade implements BundleCartFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultBundleCommerceCartFacade.class);

	protected static final String FACET_SEPARATOR = ":";
	protected static final String NOT_FOUND_IN_CART = " was not found in cart ";
	private static final String AUTO_PICK_ENABLED = "configurablebundleservices.autopick.enabled";
	private static final int BUNDLE_PRODUCT_QUANTITY = 1;

	private BundleTemplateService bundleTemplateService;
	private CartService cartService;
	private ProductService productService;
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	private EntryGroupService entryGroupService;
	private CommerceCartService commerceCartService;
	private ProductSearchFacade<ProductData> productSearchFacade;
	private BundleRuleService bundleRuleService;
	private Boolean isAutoPickEnabled;

	@Override
	public CartModificationData startBundle(@Nonnull final String bundleTemplateId, @Nonnull final String productCode,
			final long quantity) throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(getCartService().getSessionCart());
		parameter.setEntryGroupNumbers(Collections.emptySet());
		parameter.setBundleTemplate(getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId));
		parameter.setProduct(getProductService().getProductForCode(productCode));
		parameter.setQuantity(quantity);

		CartModificationData modificationData = getCartModificationConverter().convert(getCommerceCartService().addToCart(parameter));

		if (isAutoPickEnabled())
		{
			addProductsIfAutoPickSelectionCriteria(parameter, productCode, bundleTemplateId);
		}
		return modificationData;
	}

	@Override
	public CartModificationData addToCart(@Nonnull final String productCode, final long quantity, final int groupNumber)
			throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(getCartService().getSessionCart());
		parameter.setBundleTemplate(null);
		parameter.setProduct(getProductService().getProductForCode(productCode));
		parameter.setQuantity(quantity);

		final CartModel cartModel = getCartService().getSessionCart();
		try{
			getEntryGroupService().getGroup(cartModel, groupNumber);
		}
		catch (IllegalArgumentException e){
			throw e;
		}
		parameter.setEntryGroupNumbers(new HashSet<>(Collections.singletonList(Integer.valueOf(groupNumber))));
		return getCartModificationConverter().convert(getCommerceCartService().addToCart(parameter));
	}

	@Override
	@Nonnull
	public ProductSearchPageData<SearchStateData, ProductData> getAllowedProducts(
			@Nonnull Integer groupNumber, String searchQuery, @Nonnull PageableData pageableData)
	{
		final EntryGroup entryGroup = getEntryGroupService().getGroupOfType(getCartService().getSessionCart(),
				Collections.singletonList(groupNumber), GroupType.CONFIGURABLEBUNDLE);

		final SearchStateData searchState = createSearchStateWithBundleComponentFilter(searchQuery, entryGroup.getExternalReferenceId());

		final ProductSearchPageData<SearchStateData, ProductData> searchResult = encodeSearchPageData(
				"/entrygroups/CONFIGURABLEBUNDLE/" + groupNumber + "/?q=",
				getProductSearchFacade().textSearch(searchState, pageableData));
		if (CollectionUtils.isNotEmpty(searchResult.getResults()))
		{
			enrichProductData(searchResult.getResults(), entryGroup);
		}
		return searchResult;
	}

	protected void addProductsIfAutoPickSelectionCriteria(@Nonnull CommerceCartParameter parameter,  final String addedProductCode,
			final String bundleTemplateId) throws CommerceCartModificationException
	{
		final EntryGroup rootGroup = getEntryGroupService().getRoot(getCartService().getSessionCart(), getBundleEntryGroupNumber(parameter));

		addAutoPickProductsForEntryGroup(parameter, rootGroup, addedProductCode, bundleTemplateId);
	}

	private void addAutoPickProductsForEntryGroup(CommerceCartParameter parameter, EntryGroup rootGroup, final String addedProductCode,
			final String bundleTemplateId) throws CommerceCartModificationException
	{
		for (EntryGroup entryGroup : rootGroup.getChildren())
		{
			final BundleTemplateModel component = getBundleTemplateService().getBundleTemplateForCode(entryGroup.getExternalReferenceId());
			if (getBundleTemplateService().isAutoPickComponent(component))
			{
				createCommerceCartParametersForAutoPickComponent(parameter, component, entryGroup, addedProductCode, bundleTemplateId);
			}
			addAutoPickProductsForEntryGroup(parameter, entryGroup, addedProductCode, bundleTemplateId);
		}
	}

	protected Integer getBundleEntryGroupNumber(final CommerceCartParameter parameter)
	{
		if (CollectionUtils.isEmpty(parameter.getEntryGroupNumbers()))
		{
			return null;
		}

		return getBundleTemplateService().getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers())
				.getGroupNumber();
	}

	protected void createCommerceCartParametersForAutoPickComponent(
			final CommerceCartParameter commerceCartParameter,
			final BundleTemplateModel component,
			final EntryGroup entryGroup,
			final String addedProductCode,
			final String bundleTemplateId) throws CommerceCartModificationException
	{
		for (ProductModel productModel : component.getProducts())
		{
			if (bundleTemplateId.equalsIgnoreCase(component.getId()) && addedProductCode.equalsIgnoreCase(productModel.getCode()))
			{
				continue;
			}
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(commerceCartParameter.isEnableHooks());
			parameter.setCart(commerceCartParameter.getCart());
			parameter.setEntryGroupNumbers(Set.of(entryGroup.getGroupNumber()));
			parameter.setBundleTemplate(component);
			parameter.setProduct(productModel);
			parameter.setQuantity(BUNDLE_PRODUCT_QUANTITY);

			getCommerceCartService().addToCart(parameter);
		}
	}

	/**
	 * Populate some extra fields of product DTO restored from solr documents.
	 *
	 * @param searchResult products that could be added to a component
	 * @param currentEntryGroup bundle component the products are being selected for
	 */
	protected void enrichProductData(final List<ProductData> searchResult, final EntryGroup currentEntryGroup)
	{
		searchResult.forEach(product -> applyDisableRules(product, currentEntryGroup));
	}

	protected void applyDisableRules(final ProductData productData, final EntryGroup currentEntryGroup)
	{
		final ProductModel product = getProductService().getProductForCode(productData.getCode());

		final List<DisableProductBundleRuleModel> disableProductRules = getBundleRuleService().getDisableProductBundleRules(
				product, currentEntryGroup, getCartService().getSessionCart());
		if (CollectionUtils.isNotEmpty(disableProductRules))
		{
			productData.setAddToCartDisabled(Boolean.TRUE);
			productData.setAddToCartDisabledMessage(getBundleRuleService().createMessageForDisableRule(disableProductRules.get(0),
					product));
		}
	}

	protected void removeEntriesByGroupNumber(@Nonnull final CartModel cart, @Nonnull final Integer entryGroupNumber)
			throws CommerceCartModificationException
	{
		final AbstractOrderEntryModel entry = cart.getEntries().stream().filter(e -> e.getEntryGroupNumbers() != null).filter(e -> !e.getEntryGroupNumbers().isEmpty())
				.filter(e -> e.getEntryGroupNumbers().contains(entryGroupNumber)).findAny()
				.orElseThrow(() -> new IllegalArgumentException(
						"Entry group #" + entryGroupNumber + NOT_FOUND_IN_CART + cart.getCode()));
		final CommerceCartParameter updateParameter = new CommerceCartParameter();
		updateParameter.setCart(cart);
		updateParameter.setEnableHooks(true);
		updateParameter.setQuantity(0L);
		if (entry.getEntryNumber() == null)
		{
			throw new IllegalStateException("Entry has null entryNumber");
		}
		updateParameter.setEntryNumber(entry.getEntryNumber().longValue());
		getCommerceCartService().updateQuantityForCartEntry(updateParameter);
	}

	protected SearchStateData createSearchStateWithBundleComponentFilter(final String searchQuery, final String componentId)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("componentId", componentId);
		final SearchQueryData searchQueryData = new SearchQueryData();
		searchQueryData.setValue(searchQuery);

		final SearchFilterQueryData searchFilterQueryData = new SearchFilterQueryData();
		searchFilterQueryData.setKey("bundleTemplates");
		searchFilterQueryData.setValues(new HashSet<>(Collections.singletonList(componentId)));

		searchQueryData.setFilterQueries(Collections.singletonList(searchFilterQueryData));

		final SearchStateData searchStateData = new SearchStateData();
		searchStateData.setQuery(searchQueryData);

		return searchStateData;
	}

	protected ProductSearchPageData<SearchStateData, ProductData> encodeSearchPageData(final String urlPrefix,
			final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		patchURLs(urlPrefix, searchPageData);
		final SearchStateData currentQuery = searchPageData.getCurrentQuery();

		if (currentQuery != null)
		{
			try
			{
				final SearchQueryData query = currentQuery.getQuery();
				final String encodedQueryValue = XSSEncoder.encodeHTML(query.getValue());
				query.setValue(encodedQueryValue);
				currentQuery.setQuery(query);
				searchPageData.setCurrentQuery(currentQuery);
				searchPageData.setFreeTextSearch(XSSEncoder.encodeHTML(searchPageData.getFreeTextSearch()));

				final List<FacetData<SearchStateData>> facets = searchPageData.getFacets();
				if (CollectionUtils.isNotEmpty(facets))
				{
					processFacetData(facets);
				}
			}
			catch (final UnsupportedEncodingException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Error occured during Encoding the Search Page data values", e);
				}
			}
		}
		return searchPageData;
	}

	protected void patchURLs(final String urlPrefix, final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		if (searchPageData.getBreadcrumbs() != null)
		{
			searchPageData.getBreadcrumbs().forEach(breadcrumbData -> breadcrumbData.getRemoveQuery()
					.setUrl(urlPrefix + encodeURL(breadcrumbData.getRemoveQuery().getQuery().getValue())));
		}
		if (searchPageData.getFacets() != null)
		{
			searchPageData.getFacets().forEach(facetData -> {
				if (facetData.getTopValues() != null)
				{
					facetData.getTopValues().forEach(facetValueData -> facetValueData.getQuery()
							.setUrl(urlPrefix + encodeURL(facetValueData.getQuery().getQuery().getValue())));
				}
				if (facetData.getValues() != null)
				{
					facetData.getValues().forEach(facetValueData -> facetValueData.getQuery()
							.setUrl(urlPrefix + encodeURL(facetValueData.getQuery().getQuery().getValue())));
				}
			});
		}
		searchPageData.getCurrentQuery().setUrl(urlPrefix + encodeURL(searchPageData.getCurrentQuery().getQuery().getValue()));
	}

	protected String encodeURL(final String url)
	{
		try
		{
			return XSSEncoder.encodeURL(url);
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.info(e);
			return url;
		}
	}

	protected void processFacetData(final List<FacetData<SearchStateData>> facets) throws UnsupportedEncodingException
	{
		for (final FacetData<SearchStateData> facetData : facets)
		{
			final List<FacetValueData<SearchStateData>> topFacetValueDatas = facetData.getTopValues();
			if (CollectionUtils.isNotEmpty(topFacetValueDatas))
			{
				processFacetDatas(topFacetValueDatas);
			}
			final List<FacetValueData<SearchStateData>> facetValueDatas = facetData.getValues();
			if (CollectionUtils.isNotEmpty(facetValueDatas))
			{
				processFacetDatas(facetValueDatas);
			}
		}
	}

	protected void processFacetDatas(final List<FacetValueData<SearchStateData>> facetValueDatas)
			throws UnsupportedEncodingException
	{
		for (final FacetValueData<SearchStateData> facetValueData : facetValueDatas)
		{
			final SearchStateData facetQuery = facetValueData.getQuery();
			final SearchQueryData queryData = facetQuery.getQuery();
			final String queryValue = queryData.getValue();
			if (StringUtils.isNotBlank(queryValue))
			{
				final String[] queryValues = queryValue.split(FACET_SEPARATOR);
				final StringBuilder queryValueBuilder = new StringBuilder();
				queryValueBuilder.append(XSSEncoder.encodeHTML(queryValues[0]));
				for (int i = 1; i < queryValues.length; i++)
				{
					queryValueBuilder.append(FACET_SEPARATOR).append(queryValues[i]);
				}
				queryData.setValue(queryValueBuilder.toString());
			}
		}
	}

	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}

	@Required
	public void setCartModificationConverter(
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartModificationConverter = cartModificationConverter;
	}

	protected EntryGroupService getEntryGroupService()
	{
		return entryGroupService;
	}

	@Required
	public void setEntryGroupService(final EntryGroupService entryGroupService)
	{
		this.entryGroupService = entryGroupService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected ProductSearchFacade<ProductData> getProductSearchFacade()
	{
		return productSearchFacade;
	}

	@Required
	public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade)
	{
		this.productSearchFacade = productSearchFacade;
	}

	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	@Required
	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	protected boolean isAutoPickEnabled()
	{
		if (isAutoPickEnabled == null)
		{
			return Config.getBoolean(AUTO_PICK_ENABLED, false);
		}

		return isAutoPickEnabled;
	}

	protected void setAutoPickEnabled(final boolean autoPickEnabled)
	{
		isAutoPickEnabled = autoPickEnabled;
	}
}

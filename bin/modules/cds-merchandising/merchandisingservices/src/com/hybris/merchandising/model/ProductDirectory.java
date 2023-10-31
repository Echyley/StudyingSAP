/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.model;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * POJO to represent ProductDirectory which will be stored at Conversion persistence layer
 */
public class ProductDirectory
{
	public static final String SEARCH_SERVICE_TYPE = "searchService";
	private String id;
	private String name;
	private String rollupStrategy;
	private String defaultLanguage;
	private String defaultCurrency;
	private String type;
	private List<String> sites;

	/**
	 * Method which takes {@link MerchProductDirectoryConfigModel} and returns {@link ProductDirectory} which we store in CDS
	 *
	 * @param merchProductDirectoryConfigModel
	 * @return ProductDirectory
	 */
	public static ProductDirectory fromMerchProductDirectoryConfigModel(
			final MerchProductDirectoryConfigModel merchProductDirectoryConfigModel)
	{
		final ProductDirectoryBuilder builder = new ProductDirectoryBuilder(merchProductDirectoryConfigModel.getDisplayName(),
				merchProductDirectoryConfigModel.getRollUpStrategy(),
				merchProductDirectoryConfigModel.getBaseSites().stream().map(BaseSiteModel::getUid).collect(Collectors.toList()),
				merchProductDirectoryConfigModel.getDefaultLanguage().getIsocode());
		builder.withDefaultCurrency(merchProductDirectoryConfigModel.getCurrency().getIsocode());
		Optional.ofNullable(merchProductDirectoryConfigModel.getCdsIdentifier())
				.ifPresent(builder::withId);
		return builder.build();
	}

	/**
	 * Method which takes {@link MerchSnConfigModel} and returns {@link ProductDirectory} which we store in CDS
	 *
	 * @param config Merchandising configuration
	 * @return ProductDirectory
	 */
	public static ProductDirectory fromMerchSnConfigModel(final MerchSnConfigModel config)
	{
		final ProductDirectoryBuilder builder = new ProductDirectoryBuilder(config.getDisplayName(), config.getRollUpStrategy(),
				List.of(config.getBaseSite().getUid()), config.getDefaultLanguage().getIsocode());
		builder.withDefaultCurrency(config.getCurrency().getIsocode())
		       .withType(SEARCH_SERVICE_TYPE);
		Optional.ofNullable(config.getCdsIdentifier())
		        .ifPresent(builder::withId);
		return builder.build();
	}

	/**
	 * Builder class
	 */
	public static class ProductDirectoryBuilder
	{
		private String id;
		private final String productDirectoryName;
		private final String rollupStrategy;
		private final List<String> baseSites;
		private final String defaultLanguage;
		private String defaultCurrency;
		private String type;

		public ProductDirectoryBuilder(final String productDirectoryName, final String rollUpStrategy,
				final List<String> baseSites,
				final String defaultLanguage)
		{
			this.productDirectoryName = productDirectoryName;
			this.rollupStrategy = rollUpStrategy;
			this.baseSites = baseSites;
			this.defaultLanguage = defaultLanguage;
		}

		public ProductDirectoryBuilder withId(final String id)
		{
			this.id = id;
			return this;
		}

		public ProductDirectoryBuilder withDefaultCurrency(final String currency)
		{
			this.defaultCurrency = currency;
			return this;
		}

		public ProductDirectoryBuilder withType(final String type)
		{
			this.type = type;
			return this;
		}

		public ProductDirectory build()
		{
			final ProductDirectory productDirectory = new ProductDirectory();
			productDirectory.setId(id);
			productDirectory.setSites(baseSites);
			productDirectory.setName(productDirectoryName);
			productDirectory.setRollupStrategy(rollupStrategy);
			productDirectory.setDefaultLanguage(defaultLanguage);
			productDirectory.setDefaultCurrency(defaultCurrency);
			productDirectory.setType(type);
			return productDirectory;
		}
	}

	/**
	 *
	 * @return unique identifier
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Set unique identifier
	 *
	 * @param id
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 *
	 * @return returns product directory name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets name
	 *
	 * @param name
	 */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 *
	 * @return rollup strategy for variant roll up
	 */
	public String getRollupStrategy()
	{
		return rollupStrategy;
	}

	/**
	 * Sets roll up strategy
	 *
	 * @param rollUpStrategy
	 */
	public void setRollupStrategy(final String rollUpStrategy)
	{
		this.rollupStrategy = rollUpStrategy;
	}

	/**
	 *
	 * @return List of base sites
	 */
	public List<String> getSites()
	{
		return sites;
	}

	/**
	 * Sets list of base sites
	 *
	 * @param sites
	 */
	public void setSites(final List<String> sites)
	{
		this.sites = sites;
	}

	/**
	 * Returns default Language
	 *
	 * @return defaultLanguage
	 */
	public String getDefaultLanguage()
	{
		return defaultLanguage;
	}

	/**
	 * Sets default Language
	 *
	 * @param defaultLanguage
	 */
	public void setDefaultLanguage(final String defaultLanguage)
	{
		this.defaultLanguage = defaultLanguage;
	}

	public String getDefaultCurrency()
	{
		return defaultCurrency;
	}

	public void setDefaultCurrency(final String defaultCurrency)
	{
		this.defaultCurrency = defaultCurrency;
	}

	public String getType()
	{
		return type;
	}

	public void setType(final String type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return "ProductDirectory{" +
				"id=" + id +
				", type " + type +
				", name='" + name + '\'' +
				", rollUpStrategy='" + rollupStrategy + '\'' +
				", baseSites=" + sites +
				'}';
	}
}

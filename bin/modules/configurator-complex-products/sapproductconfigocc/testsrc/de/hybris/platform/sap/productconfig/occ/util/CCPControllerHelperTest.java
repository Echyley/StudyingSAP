/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.occ.util;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.occ.ConfigurationSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.ConfigurationWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticSupplementsWsDTO;
import de.hybris.platform.sap.productconfig.occ.CsticValueSupplementsWsDTO;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


@UnitTest
public class CCPControllerHelperTest
{
	private final CCPControllerHelper classUnderTest = new CCPControllerHelper()
	{

		@Override
		public ConfigurationData readDefaultConfiguration(final String productCode)
		{
			return null;
		}

		@Override
		public ConfigurationWsDTO mapDTOData(final ConfigurationData configData)
		{
			return null;
		}

		@Override
		public boolean isNotRequestedGroup(final UiGroupData group, final String requestedGroupId)
		{
			return false;
		}

		@Override
		public boolean hasSubGroups(final UiGroupData group)
		{
			return false;
		}

		@Override
		public UniqueUIKeyGenerator getUniqueUIKeyGenerator()
		{
			return null;
		}

		@Override
		public UiGroupData getUiGroup(final List<UiGroupData> groupList, final String groupId)
		{
			return null;
		}

		@Override
		public UiGroupData getUiGroup(final String configId, final String groupId)
		{
			return null;
		}

		@Override
		public ImageHandler getImageHandler()
		{
			return null;
		}

		@Override
		public Stream<UiGroupData> getFlattened(final UiGroupData uiGroup)
		{
			return null;
		}

		@Override
		public void filterGroups(final List<UiGroupData> groups, final String requestedGroupId)
		{
			//nothing happens
		}

		@Override
		public void filterGroups(final ConfigurationData configData, final String requestedGroupId)
		{
			//nothing happens
		}

		@Override
		public String determineFirstGroupId(final List<UiGroupData> uiGroups)
		{
			return null;
		}

		@Override
		public void deleteCstics(final UiGroupData group)
		{
			//nothing happens
		}

		@Override
		public List<CsticValueSupplementsWsDTO> createPriceSupplements(final Map<String, PriceDataPair> prices)
		{
			return null;
		}

		@Override
		public CsticSupplementsWsDTO createAttributeSupplementDTO(final PriceValueUpdateData valuePrices)
		{
			return null;
		}

		@Override
		public CsticValueSupplementsWsDTO convertEntrytoWsDTO(final Entry<String, PriceDataPair> entry)
		{
			return null;
		}

		@Override
		public List<String> compileValuePriceInput(final UiGroupData uiGroup)
		{
			return null;
		}

		@Override
		public ConfigurationSupplementsWsDTO compilePricingResult(final String configId, final PricingData priceSummary,
				final List<PriceValueUpdateData> valuePrices)
		{
			return null;
		}

		@Override
		public String compileCsticKey(final CsticData cstic, final UiGroupData uiGroup)
		{
			return null;
		}
	};

	@Test(expected = IllegalStateException.class)
	public void testGetConfigurationFromTemplate()
	{
		classUnderTest.getConfigurationFromTemplate("PRODUCT_CODE", null);
	}

	@Test(expected = NotImplementedException.class)
	public void testReadDefaultConfiguration()
	{
		classUnderTest.readDefaultConfiguration("PRODUCT_CODE", false);
	}
}

package de.hybris.subscriptionbackoffice.tree;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.navigation.NavigationNode;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SubscriptionNodePopulatorTest
{

	private static final String PROMOTION_LEGACY_MODE_PROP_KEY = "promotions.legacy.mode";
	private static final String PROMOTION_BILLING_TIME_RESTRICTION_NODE_ID = "hmc_typenode_promotionbillingtimerestriction";

	@InjectMocks
	private SubscriptionNodePopulator subscriptionNodePopulator;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private NavigationNode navigationNode;

	@Before
	public void setUp()
	{
		Map<String, Object> map = new HashMap<>();
		map.put("testId", "testCode");
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(navigationNode.getContext().getParameters()).thenReturn(map);
	}

	@Test
	public void shouldReturnSubscriptionBillingTimeRestrictionNodeWhenInLegacyMode() {
		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(configuration.getBoolean(PROMOTION_LEGACY_MODE_PROP_KEY)).thenReturn(true);
		assertThat(subscriptionNodePopulator.getChildren(navigationNode).size()).isEqualTo(2);
		assertThat(subscriptionNodePopulator.getChildren(navigationNode).get(0).getId()).isEqualTo(PROMOTION_BILLING_TIME_RESTRICTION_NODE_ID);
	}

	@Test
	public void shouldNotReturnSubscriptionBillingTimeRestrictionNodeWhenNotinLegacyMode() {
		when(configuration.getBoolean(PROMOTION_LEGACY_MODE_PROP_KEY)).thenReturn(false);
		assertThat(subscriptionNodePopulator.getChildren(navigationNode).size()).isEqualTo(1);
	}
}

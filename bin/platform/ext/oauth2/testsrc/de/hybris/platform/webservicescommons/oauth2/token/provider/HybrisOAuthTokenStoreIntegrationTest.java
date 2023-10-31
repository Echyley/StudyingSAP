package de.hybris.platform.webservicescommons.oauth2.token.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.webservicescommons.model.OAuthClientDetailsModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;

@IntegrationTest
public class HybrisOAuthTokenStoreIntegrationTest extends ServicelayerBaseTest
{
	private static final PropertyConfigSwitcher propertyConfigSwitcher = new PropertyConfigSwitcher("oauth2.accesstoken.save.retry");
	private static final Tenant tenant = Registry.getCurrentTenant();
	private static final TimeUnit CYCLIC_BARRIER_TIMEOUT_UNIT = TimeUnit.SECONDS;
	private static final long CYCLIC_BARRIER_TIMEOUT = 10;

	@Resource
	private TokenStore oauthTokenStore;

	@Resource
	private ModelService modelService;

	@Before
	public void setUp()
	{
		propertyConfigSwitcher.switchToValue("true");
	}

	@After
	public void teardown()
	{
		propertyConfigSwitcher.switchBackToDefault();
	}

	@Test
	public void testTwoThreadsTryToSaveTheSameAccessTokenAtTheSameTime() throws BrokenBarrierException, InterruptedException, TimeoutException
	{
		final int numberOfThreads = 2;
		final int numberOfAccessTokens = 1;

		final int exceptionCount = saveAccessTokensInAGivenAmountOfThreadsAndCountExceptionsThrown(numberOfThreads, numberOfAccessTokens);

		assertThat(exceptionCount).isZero();
	}

	@Test
	public void testExceptionsAreThrownWhenTheFlagIsTurnedOffAndOneHundredThreadsTryToSaveTwoAccessTokens() throws BrokenBarrierException, InterruptedException, TimeoutException
	{
		final int numberOfThreads = 10;
		final int numberOfAccessTokens = 2;
		int exceptionCount = 0;

		propertyConfigSwitcher.switchToValue("false");
		for (int i = 0; i < 10; i++)
		{
			exceptionCount += saveAccessTokensInAGivenAmountOfThreadsAndCountExceptionsThrown(numberOfThreads,
					numberOfAccessTokens);
		}
		assertThat(exceptionCount).isPositive();
	}

	@Test
	public void testNoExceptionsAreThrownWhenOneHundredThreadsTryToUpdateTwoAccessTokens() throws BrokenBarrierException, InterruptedException, TimeoutException
	{
		final int numberOfThreads = 100;
		final int numberOfAccessTokens = 2;

		final int countWithPropertyOn = saveAccessTokensInAGivenAmountOfThreadsAndCountExceptionsThrown(numberOfThreads, numberOfAccessTokens);

		assertThat(countWithPropertyOn).isZero();
	}

	@Test
	public void testOAuthTokenStoreDoesntThrowExceptionWhenTryingToRemoveTheSameRefreshTokenTwice()
	{
		final String randomUUID = UUID.randomUUID().toString();
		final DefaultOAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken(randomUUID);
		oauthTokenStore.storeRefreshToken(refreshToken, createAuthorizations(1, List.of(randomUUID)).get(0));

		oauthTokenStore.removeRefreshToken(refreshToken);
		oauthTokenStore.removeRefreshToken(refreshToken);

		assertThat(refreshToken.getValue()).isEqualTo(randomUUID);
	}

	private int saveAccessTokensInAGivenAmountOfThreadsAndCountExceptionsThrown(final int numberOfThreads, final int numberOfAccessTokens) throws BrokenBarrierException, InterruptedException, TimeoutException
	{
		final CyclicBarrier gate = new CyclicBarrier(numberOfThreads + 1);
		final List<OAuthClientDetailsModel> clients = createClientsForAccessTokens(numberOfAccessTokens);
		final List<String> clientsIds = clients.stream().map(OAuthClientDetailsModel::getClientId).toList();
		final List<OAuth2Authentication> authList = createAuthorizations(numberOfAccessTokens, clientsIds);
		final List<DefaultOAuth2AccessToken> tokenList = createAccessTokenList(numberOfAccessTokens);
		final List<ThreadWithExceptionCount> threadList = new ArrayList<>();

		for (int i = 1; i <= numberOfThreads; ++i)
		{
			threadList.add(createThreadForExecution(tokenList.get(i % numberOfAccessTokens), gate, authList.get(i % numberOfAccessTokens)));
			threadList.get(i - 1).start();
		}

		gate.await(CYCLIC_BARRIER_TIMEOUT, CYCLIC_BARRIER_TIMEOUT_UNIT);
		joinThreads(threadList);

		return sumExceptionsAmongThreads(threadList);
	}

	private List<OAuthClientDetailsModel> createClientsForAccessTokens(final int numberOfAccessTokens)
	{
		final List<OAuthClientDetailsModel> clients = new ArrayList<>();
		final String uuid = UUID.randomUUID().toString();
		for (int i = 0; i < numberOfAccessTokens; ++i)
		{
			final OAuthClientDetailsModel client = modelService.create(OAuthClientDetailsModel.class);
			client.setClientId(uuid + i);
			clients.add(client);

			modelService.saveAll(client);
		}
		return clients;
	}

	private List<OAuth2Authentication> createAuthorizations(final int numberOfAccessTokens, final List<String> clientIds)
	{
		final List<OAuth2Authentication> authList = new ArrayList<>();
		for (int i = 0; i < numberOfAccessTokens; ++i)
		{
			final OAuth2Request request = new OAuth2Request(Collections.emptyMap(), clientIds.get(i), null, true, Collections.emptySet(),
					null, "/", null, null);

			authList.add(new OAuth2Authentication(request, null));
		}
		return authList;
	}

	private List<DefaultOAuth2AccessToken> createAccessTokenList(final int numberOfAccessTokens)
	{
		final List<DefaultOAuth2AccessToken> tokenList = new ArrayList<>();
		final String randomTokenId = UUID.randomUUID().toString();
		for (int i = 0; i < numberOfAccessTokens; ++i)
		{
			tokenList.add(new DefaultOAuth2AccessToken(randomTokenId + i));
		}
		return tokenList;
	}

	private ThreadWithExceptionCount createThreadForExecution(final DefaultOAuth2AccessToken accessToken, final CyclicBarrier gate,final OAuth2Authentication auth)
	{
		return new ThreadWithExceptionCount(() -> {
			Registry.setCurrentTenant(tenant);
			try
			{
				gate.await(CYCLIC_BARRIER_TIMEOUT, CYCLIC_BARRIER_TIMEOUT_UNIT);
				oauthTokenStore.storeAccessToken(accessToken, auth);
			}
			catch (final InterruptedException | BrokenBarrierException | TimeoutException e)
			{
				fail("Threads didn't finish properly", e);
			}
		});
	}

	private void joinThreads(final List<ThreadWithExceptionCount> threadList)
	{
		threadList.forEach((thread) -> {
			try
			{
				thread.join();
			}
			catch (final InterruptedException e)
			{
				fail("Threads didn't join properly", e);
			}
		});
	}

	private int sumExceptionsAmongThreads(final List<ThreadWithExceptionCount> threadList)
	{
		return threadList.stream().mapToInt(ThreadWithExceptionCount::getExceptionCount).sum();
	}

	private static class ThreadWithExceptionCount extends Thread
	{
		private int exceptionCount = 0;

		public ThreadWithExceptionCount(final Runnable o)
		{
			super(o);
		}

		@Override
		public void run()
		{
			try
			{
				super.run();
			}
			catch (final ModelSavingException e)
			{
				this.exceptionCount++;
			}
		}

		int getExceptionCount()
		{
			return this.exceptionCount;
		}
	}
}

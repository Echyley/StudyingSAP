/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.audit;

import static org.assertj.core.api.Assertions.allOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.user.listener.AuthenticationFailureEventListener;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;
import de.hybris.platform.spring.security.CoreRememberMeService;
import de.hybris.platform.spring.security.CoreUserDetailsService;
import de.hybris.platform.spring.security.RejectUserPreAuthenticationChecks;
import de.hybris.platform.testframework.seed.TestDataCreator;
import de.hybris.platform.util.Config;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

@IntegrationTest
public class UserRelatedAuditableActionsTest extends ServicelayerBaseTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private SessionService sessionService;
	@Resource
	private UserService userService;

	private User sessionUser;
	private TestDataCreator testDataCreator;
	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;
	private CoreAuthenticationProvider coreAuthenticationProvider;
	private CoreRememberMeService rememberMeService;
	private CoreUserDetailsService userDetailsService;
	@Resource
	private AuthenticationFailureEventListener authenticationFailureEventListener;

	@Before
	public void setUp() throws Exception
	{
		sessionUser = JaloSession.getCurrentSession().getUser();
		testDataCreator = new TestDataCreator(modelService);

		testAuditableActionHandler = mock(AuditableActionHandler.class);
		originalActionHandler = AuditableActions.getAuditableActionHandlerFactory();

		userDetailsService = new CoreUserDetailsService();

		coreAuthenticationProvider = new CoreAuthenticationProvider();
		coreAuthenticationProvider.setPreAuthenticationChecks(new RejectUserPreAuthenticationChecks());
		coreAuthenticationProvider.setUserDetailsService(userDetailsService);

		rememberMeService = new CoreRememberMeService()
		{
			@Override
			protected UserDetailsService lookupUserDetailsService()
			{
				return userDetailsService;
			}
		};
		rememberMeService.setCookieName(Config.getParameter(CookieBasedLoginToken.NAME_KEY));

		AuditableActions.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);
	}

	@After
	public void tearDown() throws Exception
	{
		JaloSession.getCurrentSession().setUser(sessionUser);
		AuditableActions.setAuditableActionHandlerFactory(originalActionHandler);
	}

	@Test
	public void shouldAuditSettingUserInSession()
	{
		final UserModel testUser = createUser();

		final User prevUser = takeJaloSession().getUser();
		takeJaloSession().setUser(modelService.getSource(testUser));


		assertThat(captureActions()).haveExactly(1, allOf(
				nameEquals("session user changed"),
				attributeThat("newUser", testUser.getPk()),
				attributeThat("prevUser", prevUser.getPK())));
	}

	@Test
	public void shouldAuditSettingUserInSessionContext()
	{
		final UserModel testUser = createUser();
		final User prevUser = takeJaloSession().getUser();

		JaloSession.getCurrentSession().getSessionContext().setUser(modelService.getSource(testUser));

		assertThat(captureActions()).haveExactly(1, allOf(
				nameEquals("session user changed"),
				attributeThat("newUser", testUser.getPk()),
				attributeThat("prevUser", prevUser.getPK())));
	}

	@Test
	public void shouldAuditSettingUserInLocalSessionContext()
	{
		final UserModel testUser = createUser();

		final User outerUser = takeJaloSession().getUser();
		final SessionContext localSessionContext = takeJaloSession().createLocalSessionContext();
		try
		{
			localSessionContext.setUser(modelService.getSource(testUser));

			assertThat(captureActions()).haveExactly(1, allOf(
					nameEquals("session user changed"),
					attributeThat("newUser", testUser.getPk()),
					attributeThat("prevUser", outerUser.getPK())));
		}
		finally
		{
			takeJaloSession().removeLocalSessionContext();
		}
	}

	@Test
	public void shouldAuditRemovingLocalSessionContextWithUserSet()
	{
		final UserModel testUser = createUser();
		final User outerUser = takeJaloSession().getUser();

		final SessionContext localSessionContext = takeJaloSession().createLocalSessionContext();
		try
		{
			localSessionContext.setUser(modelService.getSource(testUser));
		}
		finally
		{
			takeJaloSession().removeLocalSessionContext();
		}

		assertThat(captureActions())
				//for switching user in localCtx
				.haveExactly(1, allOf(
						nameEquals("session user changed"),
						attributeThat("newUser", testUser.getPk()),
						attributeThat("prevUser", outerUser.getPK())))
				//for switching back, when localCtx is removed
				.haveExactly(1, allOf(
						nameEquals("session user changed"),
						attributeThat("newUser", outerUser.getPK()),
						attributeThat("prevUser", testUser.getPk())));
	}

	@Test
	public void shouldAuditSettingUserWithUserService()
	{
		final UserModel testUser = createUser();
		final User outerUser = takeJaloSession().getUser();

		userService.setCurrentUser(testUser);

		assertThat(captureActions())
				//for switching user in localCtx
				.haveExactly(1, allOf(
						nameEquals("session user changed"),
						attributeThat("newUser", testUser.getPk()),
						attributeThat("prevUser", outerUser.getPK())));
	}

	@Test
	public void shouldAuditExecutingBodyInLocalSessionCtxWithUser()
	{
		final UserModel testUser = createUser();
		final User outerUser = takeJaloSession().getUser();

		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				// do nothing
			}
		}, testUser);

		assertThat(captureActions())
				//for switching user in localCtx
				.haveExactly(1, allOf(
						nameEquals("session user changed"),
						attributeThat("newUser", testUser.getPk()),
						attributeThat("prevUser", outerUser.getPK())))
				//for switching back, when localCtx is removed
				.haveExactly(1, allOf(
						nameEquals("session user changed"),
						attributeThat("newUser", outerUser.getPK()),
						attributeThat("prevUser", testUser.getPk())));
	}

	private UserModel createUser()
	{
		return testDataCreator.createUser("testUser-" + RandomStringUtils.randomAlphabetic(5), "testUser");
	}

	private UserModel createUserWithPassword(final String password){
		final UserModel user = createUser();
		user.setEncodedPassword(password);
		modelService.save(user);
		return user;
	}

	@Test
	public void shouldNotAuditUserChangeInLocalViewWithSameUser()
	{
		final UserModel outerUser = modelService.get(takeJaloSession().getUser().getPK());
		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				// do nothing
			}
		}, outerUser);

		assertThat(captureActions()).doNotHave(nameEquals("session user changed"));
	}

	@Test
	public void shouldAuditSuccessfulUserAuthentication()
	{
		final String password = RandomStringUtils.randomAlphabetic(15);
		final UserModel testUser = createUser();
		testUser.setEncodedPassword(password);
		modelService.save(testUser);

		final Authentication testAuthentication = createTestAuthentication(testUser.getUid(), password);
		final Authentication authentication = coreAuthenticationProvider.authenticate(testAuthentication);

		assertThat(authentication).isNotNull();

		assertThat(captureActions())
				.haveExactly(1, allOf(
						nameEquals("successful user authentication"),
						attributeThat("user", testUser.getPk())));
	}

	@Test
	public void shouldAuditSuccessfulUserAuthenticationWithLoginToken() throws Exception
	{
		final String password = RandomStringUtils.randomAlphabetic(15);
		final UserModel testUser = createUser();
		testUser.setEncodedPassword(password);
		modelService.save(testUser);

		final MockHttpServletRequest request = new MockHttpServletRequest();
		final MockHttpServletResponse response = new MockHttpServletResponse();
		final Cookie cookieBasedLoginToken = createCookieBasedLoginBaseOnStoredCookieInResponse(testUser, 100);
		request.setCookies(cookieBasedLoginToken);

		final Authentication loginTokenAuth = rememberMeService.autoLogin(request, response);
		assertThat(loginTokenAuth).isNotNull();

		final Authentication authentication = coreAuthenticationProvider.authenticate(loginTokenAuth);
		assertThat(authentication).isNotNull();

		assertThat(captureActions())
				.haveExactly(1, allOf(
						nameEquals("successful user authentication"),
						attributeThat("user", testUser.getPk()),
						attributeThat("loginToken", Boolean.TRUE)));
	}

	private Cookie createCookieBasedLoginBaseOnStoredCookieInResponse(final UserModel user, final int ttl) throws Exception
	{
		final MockHttpServletResponse response = new MockHttpServletResponse();
		UserManager.getInstance()
		           .storeLoginTokenCookie(Config.getParameter(CookieBasedLoginToken.NAME_KEY), user.getUid(), "en", null,
				           "path", "domain", true, ttl, response);
		return response.getCookie(Config.getParameter(CookieBasedLoginToken.NAME_KEY));
	}

	@Test
	public void shouldAuditClosingUserSession()
	{
		final Session newSession = sessionService.createNewSession();

		final UserModel testUser = createUser();
		newSession.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, testUser);

		sessionService.closeSession(newSession);

		assertThat(captureActions())
				.haveExactly(1, nameEquals("session closed"));

	}

	@Test
	public void shouldAuditFailedUserAuthenticationWithExistingUser()
	{
		final UserModel testUser = createUserWithPassword(RandomStringUtils.randomAlphabetic(15));

		testFailedUserAuthentication(testUser.getUid());
		assertThat(captureActions())
				.haveExactly(1, allOf(
						nameEquals("failed user authentication"),
						attributeThat("user", testUser.getPk())));
	}

	@Test
	public void shouldAuditFailedUserAuthenticationWithNonexistentUser()
	{
		final String uid = RandomStringUtils.randomAlphabetic(5);
		testFailedUserAuthentication(uid);
		assertThat(captureActions())
				.haveExactly(1, allOf(
						nameEquals("failed user authentication"),
						attributeThat("unknownUID", uid)));
	}

	@Test
	public void shouldAuditFailedUserAuthenticationWithAdmin()
	{
		final EmployeeModel adminUser = userService.getAdminUser();
		testFailedUserAuthentication(adminUser.getUid());
		assertThat(captureActions())
				.haveExactly(1, allOf(
						nameEquals("failed user authentication"),
						attributeThat("user", adminUser.getPk())));
	}

	private void testFailedUserAuthentication(final String uid)
	{
		final Authentication testAuthentication = createTestAuthentication(uid,
				RandomStringUtils.randomAlphabetic(10));

		try
		{
			coreAuthenticationProvider.authenticate(testAuthentication);
			Assertions.shouldHaveThrown(BadCredentialsException.class);
		}
		catch (final BadCredentialsException e)
		{
			authenticationFailureEventListener.onApplicationEvent(new AuthenticationFailureBadCredentialsEvent(testAuthentication, e));
		}
	}

	@Test
	public void shouldAuditUserPasswordChangeViaModel()
	{
		final UserModel user = createUserWithPassword(RandomStringUtils.randomAlphabetic(10));

		user.setEncodedPassword(RandomStringUtils.randomAlphabetic(15));
		modelService.save(user);

		assertThat(captureActions())
				.haveExactly(1, allOf(
						nameEquals("user's password changed"),
						attributeThat("user", user.getPk())));
	}

	@Test
	public void shouldAuditUserPasswordChangeViaJALO()
	{
		final User user = modelService.getSource(createUserWithPassword(RandomStringUtils.randomAlphabetic(10)));
		user.setPassword(RandomStringUtils.randomAlphabetic(15));

		assertThat(captureActions())
				.haveExactly(1, allOf(
						nameEquals("user's password changed"),
						attributeThat("user", user.getPK())));
	}

	private static Predicate<AuditableActions.Action> hasAttribute(final String attrName, final Object attrValue)
	{
		return action -> action.getActionAttributes().containsKey(attrName) && Objects.equals(
				action.getActionAttributes().get(attrName), attrValue);
	}

	private static Predicate<AuditableActions.Action> hasName(final String expectedName)
	{
		return action -> Objects.equals(action.getActionName(), expectedName);
	}

	private static Condition<AuditableActions.Action> nameEquals(final String expectedName)
	{
		return new Condition<>(hasName(expectedName), "action name equals \"%s\"", expectedName);
	}

	private static Condition<AuditableActions.Action> attributeThat(final String attrName, final Object attrValue)
	{
		return new Condition<>(hasAttribute(attrName, attrValue), "attribute \"%s\" with value equal to \"%s\"", attrName,
				attrValue);
	}


	private List<AuditableActions.Action> captureActions()
	{
		final ArgumentCaptor<AuditableActions.Action> actionArgumentCaptor = ArgumentCaptor.forClass(
				AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(actionArgumentCaptor.capture());
		return actionArgumentCaptor.getAllValues();
	}

	private Authentication createTestAuthentication(final String principal, final String credentials)
	{
		return new UsernamePasswordAuthenticationToken(principal, credentials);
	}
}

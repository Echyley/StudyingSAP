/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofileservices.security;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.security.PrincipalGroup;
import de.hybris.platform.jalo.user.Customer;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserIdDecorationStrategy;
import de.hybris.platform.servicelayer.user.daos.UserDao;
import de.hybris.platform.spring.security.CoreUserDetails;
import de.hybris.platform.util.Config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * Implementation for {@link UserDetailsService}. Delivers main functionality for chinese user details.
 */
public class ChineseUserDetailsService implements UserDetailsService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ChineseUserDetailsService.class);

	private String rolePrefix = "ROLE_";

	private UserDao userDao;

	private CommonI18NService commonI18NService;

	private ModelService modelService;

	private final List<UserIdDecorationStrategy> userIdDecorationStrategies;

	/**
	 * Constructor of ChineseUserDetailsService
	 *
	 * @param userIdDecorationStrategies
	 *           user ID decoration strategy list
	 *
	 */
	public ChineseUserDetailsService(List<UserIdDecorationStrategy> userIdDecorationStrategies) {
		this.userIdDecorationStrategies = userIdDecorationStrategies;
	}

	@Override
	public CoreUserDetails loadUserByUsername(final String username)
	{
		if (username == null)
		{
			return null;
		}

		UserModel user;
		try
		{
			user = userDao.findUserByUID(this.decorateUserId(username));
		}
		catch (final ModelNotFoundException e)
		{
			throw new UsernameNotFoundException("User not found!");
		}
		if (Objects.isNull(user))
		{
			throw new UsernameNotFoundException("User not found!");
		}

		final User sourceUser = modelService.getSource(user);
		final boolean enabled = isNotAnonymousOrAnonymousLoginIsAllowed(sourceUser) && !sourceUser.isLoginDisabledAsPrimitive()
				&& !this.isAccountDeactivated(sourceUser);

		final boolean accountNonExpired = true;
		final boolean credentialsNonExpired = true;
		final boolean accountNonLocked = true;

		String password = sourceUser.getEncodedPassword(JaloSession.getCurrentSession().getSessionContext());

		// a null value has to be transformed to empty string, otherwise the constructor
		// of org.springframework.security.userdetails.User will fail
		if (password == null)
		{
			password = StringUtils.EMPTY;
		}

		return new CoreUserDetails(user.getUid(), password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked,
				getAuthorities(sourceUser), getCommonI18NService().getCurrentLanguage().getIsocode(), null);
	}

	protected boolean isAccountDeactivated(final User user)
	{
		return user.getDeactivationDate() != null && user.getDeactivationDate().toInstant().isBefore(Instant.now());
	}

	protected boolean isNotAnonymousOrAnonymousLoginIsAllowed(final User user)
	{
		return !user.isAnonymousCustomer() || !Config.getBoolean(Customer.LOGIN_ANONYMOUS_ALWAYS_DISABLED, true);
	}

	protected Collection<GrantedAuthority> getAuthorities(final User user)
	{
		final Set<PrincipalGroup> groups = user.getGroups();
		final Collection<GrantedAuthority> authorities = new ArrayList<>(groups.size());
		final Iterator<PrincipalGroup> itr = groups.iterator();
		while (itr.hasNext())
		{
			final PrincipalGroup group = itr.next();
			authorities.add(new SimpleGrantedAuthority(rolePrefix + group.getUid().toUpperCase(Locale.getDefault())));
			for (final PrincipalGroup gr : group.getAllGroups())
			{
				authorities.add(new SimpleGrantedAuthority(rolePrefix + gr.getUid().toUpperCase(Locale.getDefault())));
			}

		}
		return authorities;
	}

	protected String decorateUserId(String userId) {
		return this.canUserIdBeDecorated(userId) ? this.decorate(userId) : userId;
	}

	private boolean canUserIdBeDecorated(String userId) {
		return userId != null && !this.isAdminOrAnonymousUid(userId);
	}

	private boolean isAdminOrAnonymousUid(String userId) {
		return UserConstants.ADMIN_EMPLOYEE_UID.equals(userId) || UserConstants.ANONYMOUS_CUSTOMER_UID.equals(userId);
	}

	public void setUserDao(final UserDao userDao)
	{
		this.userDao = userDao;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public void setRolePrefix(final String rolePrefix)
	{
		this.rolePrefix = rolePrefix;
	}

	public UserDao getUserDao()
	{
		return userDao;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public List<UserIdDecorationStrategy> getUserIdDecorationStrategies() {
		return Collections.unmodifiableList(this.userIdDecorationStrategies);
	}

	private String decorate(String userId) {
		Objects.requireNonNull(userId);
		String decoratedUserId = userId;

		for (final UserIdDecorationStrategy strategy : getUserIdDecorationStrategies())
		{
			decoratedUserId = Optional.ofNullable(strategy.decorateUserId(decoratedUserId))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.orElse(decoratedUserId);
		}

		if (!userId.equals(decoratedUserId)) {
			LOGGER.debug("UserId {} has been decorated to {}", userId, decoratedUserId);
		}

		return decoratedUserId;
	}

}

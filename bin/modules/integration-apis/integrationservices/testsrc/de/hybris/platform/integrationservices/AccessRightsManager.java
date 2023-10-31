/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.UserRightModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.security.permissions.PermissionAssignment;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.rules.ExternalResource;

/**
 * A utility for managing user access rights in the system. The implementation is not thread safe.
 */
public class AccessRightsManager extends ExternalResource
{
	private static final Log LOG = Log.getLogger(AccessRightsManager.class);
	private static final int CHANGE_TYPES_INITIAL_SIZE = 20;

	private final PermissionManagementService permissionManager;
	private final Set<ComposedTypeModel> changedTypes;
	private final Set<ComposedTypeModel> typesToChange;
	private final Set<UserRightModel> preExistingUserRights;
	private boolean userRightsInitialized = false;
	private UserModel user;
	private Boolean create;
	private Boolean read;
	private Boolean update;
	private Boolean delete;

	/**
	 * Instantiates this manager.
	 *
	 * @param mgr a service to use for managing user access permissions.
	 */
	public AccessRightsManager(final PermissionManagementService mgr)
	{
		permissionManager = mgr != null ? mgr : getContextPermissionManager();
		typesToChange = new HashSet<>();
		changedTypes = new HashSet<>(CHANGE_TYPES_INITIAL_SIZE);
		preExistingUserRights = new HashSet<>();
	}

	/**
	 * Creates new instance of this manager with the {@link PermissionManagementService} that is available in the application
	 * context.
	 * @return new access rights manager instance.
	 */
	public static AccessRightsManager accessRights()
	{
		return new AccessRightsManager(null);
	}

	public AccessRightsManager forUser(final String uid)
	{
		final var user = IntegrationTestUtil.findAny(UserModel.class, u -> u.getUid().equals(uid))
		                                    .orElse(null);
		return forUser(user);
	}

	public AccessRightsManager forUser(final UserModel model)
	{
		user = model;
		return this;
	}

	public AccessRightsManager forTypes(final String... typeCodes)
	{
		Stream.of(typeCodes).forEach(this::forType);
		return this;
	}

	public AccessRightsManager forType(final String type)
	{
		final var model = IntegrationTestUtil.findAny(ComposedTypeModel.class, m -> m.getCode().equals(type))
		                                     .orElse(null);
		return forType(model);
	}

	public AccessRightsManager forType(final ComposedTypeModel model)
	{
		typesToChange.add(model);
		return this;
	}

	public AccessRightsManager grantAll()
	{
		return grantRead()
				.grantCreate()
				.grantUpdate()
				.grantDelete();
	}

	public AccessRightsManager revokeAll()
	{
		return revokeRead()
				.revokeCreate()
				.revokeUpdate()
				.revokeDelete();
	}

	public AccessRightsManager grantRead()
	{
		read = true;
		return this;
	}

	public AccessRightsManager revokeRead()
	{
		read = false;
		return this;
	}

	public AccessRightsManager grantCreate()
	{
		create = true;
		return this;
	}

	public AccessRightsManager revokeCreate()
	{
		create = false;
		return this;
	}

	public AccessRightsManager grantUpdate()
	{
		update = true;
		return this;
	}

	public AccessRightsManager revokeUpdate()
	{
		update = false;
		return this;
	}

	public AccessRightsManager grantDelete()
	{
		delete = true;
		return this;
	}

	public AccessRightsManager revokeDelete()
	{
		delete = false;
		return this;
	}

	/**
	 * Applies permissions explicitly set by calling {@code grant...} or {@code revoke...} methods to
	 * the user and type, which were specified by calling {@link #forUser(UserModel)} and {@link #forType(ComposedTypeModel)}
	 * respectively.
	 * <p>Permissions, for which no {@code grant...} or {@code revoke...} methods were not called will be unspecified.</p>
	 */
	public AccessRightsManager apply()
	{
		if (user != null && !typesToChange.isEmpty())
		{
			initializeUserRights();
			final Collection<PermissionAssignment> permissions = permissions();
			typesToChange.forEach(type -> applyTypePermissions(type, permissions));
			changedTypes.addAll(typesToChange);
			typesToChange.clear();
		}
		return this;
	}

	private void applyTypePermissions(final ComposedTypeModel type, final Collection<PermissionAssignment> permissions)
	{
		getContextPermissionManager().setTypePermissions(type, permissions);
		logUserPermissions(type);
	}

	private PermissionManagementService getContextPermissionManager()
	{
		return Registry.getApplicationContext().getBean("permissionManagementService", PermissionManagementService.class);
	}

	private Collection<PermissionAssignment> permissions()
	{
		final PermissionAssignment[] permissions = {
				toPermission(PermissionsConstants.CREATE, create),
				toPermission(PermissionsConstants.READ, read),
				toPermission(PermissionsConstants.CHANGE, update),
				toPermission(PermissionsConstants.REMOVE, delete)
		};
		return Arrays.stream(permissions)
		             .filter(Objects::nonNull)
		             .collect(Collectors.toSet());
	}

	private PermissionAssignment toPermission(final String permission, final Boolean value)
	{
		return value != null
				? new PermissionAssignment(permission, user, !value)
				: null;
	}

	private void logUserPermissions(final ComposedTypeModel type)
	{
		final String c = permissionAsString(type, PermissionsConstants.CREATE);
		final String r = permissionAsString(type, PermissionsConstants.READ);
		final String u = permissionAsString(type, PermissionsConstants.CHANGE);
		final String d = permissionAsString(type, PermissionsConstants.REMOVE);
		LOG.info("{} permissions for {} (CRUD): {}{}{}{}", user.getUid(), type.getCode(), c, r, u, d);
	}

	private String permissionAsString(final ComposedTypeModel type, final String name)
	{
		final PermissionAssignment pa = contextPermission(type, name);
		if (pa == null)
		{
			return "?";
		}
		return pa.isGranted() ? "+" : "-";
	}

	private PermissionAssignment contextPermission(final ComposedTypeModel type,
	                                               final String name)
	{
		return permissionManager.getTypePermissions(type).stream()
		                        .filter(p -> p.getPrincipal().getUid().equals(user.getUid()))
		                        .filter(p -> p.getPermissionName().equals(name))
		                        .findAny()
		                        .orElse(null);
	}

	private void initializeUserRights()
	{
		if (!userRightsInitialized)
		{
			try
			{
				permissionManager.createPermission(PermissionsConstants.CHANGE);
				permissionManager.createPermission(PermissionsConstants.CREATE);
				permissionManager.createPermission(PermissionsConstants.READ);
				permissionManager.createPermission(PermissionsConstants.REMOVE);
				userRightsInitialized = true;
				LOG.debug("Created CRUD permissions");
			}
			catch (final ModelSavingException e)
			{
				LOG.debug("Permissions already initialized", e);
			}
		}
	}

	/**
	 * Resets access rights to the state they were in the system prior to the first call of the {@link #apply()} method
	 * on this manager.
	 */
	public AccessRightsManager reset()
	{
		resetPermissions();
		resetUserRights();
		return this;
	}

	private void resetPermissions()
	{
		changedTypes.forEach(this::resetTypePermissions);
		changedTypes.clear();
	}

	private void resetTypePermissions(final ComposedTypeModel type)
	{
		permissionManager.clearTypePermissions(type);
		LOG.info("Cleared {} permissions", type.getCode());
	}

	private void resetUserRights()
	{
		final var createdInTests = IntegrationTestUtil.findAll(UserRightModel.class, m -> !preExistingUserRights.contains(m));
		if (! createdInTests.isEmpty())
		{
			IntegrationTestUtil.removeAll(createdInTests);
			LOG.debug("Removed all UserRightsModels created in the test: {}", createdInTests);
		}
		userRightsInitialized = false;
	}

	@Override
	protected void before()
	{
		preExistingUserRights.clear();
		preExistingUserRights.addAll(IntegrationTestUtil.findAll(UserRightModel.class));
		initializeUserRights();
		apply();
	}

	@Override
	protected void after()
	{
		reset();
	}
}

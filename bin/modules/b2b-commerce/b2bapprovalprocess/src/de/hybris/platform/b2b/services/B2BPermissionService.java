/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * A service around {@link B2BPermissionResultModel}. This interface evaluates the permissions(credit limits or budget
 * thresholds) on an order to determine if the order needs to be sent for approval. If the order needs to be approved,
 * the associated approver is applied to the permission so that an Approve or Reject decision on the order can be made.
 *
 * @param <T>
 *           the user (subtype of {@link UserModel})
 * @param <P>
 *           the permission result (subtype of {@link B2BPermissionResultModel})
 * @spring.bean b2bPermissionService
 */
public interface B2BPermissionService<T extends UserModel, P extends B2BPermissionResultModel>
{

	/**
	 * Evaluate permissions of an order to determine if a violation has occurred and that an approver needs to intervene
	 * and approve/reject an order.
	 *
	 * @param order
	 *           the order to be evaluated
	 * @param employee
	 *           the person who placed the order
	 * @param permissionTypes
	 *           the permission types that will be checked
	 * @return the set of {@link B2BPermissionResultModel}
	 */
	abstract Set<P> evaluatePermissions(final AbstractOrderModel order, final T employee,
			final List<Class<? extends B2BPermissionModel>> permissionTypes);

	/**
	 * Get the approvers for an order's associated permissions with a status of OPEN. Approvers are assigned to a
	 * customer or it's unit and this evaluates/gathers the approvers for all units up the hierarchy.
	 *
	 * @param order
	 *           the order
	 * @param customer
	 *           the customer whom placed the order
	 * @param openPermissions
	 *           the permissions to be checked for status of OPEN
	 * @return B2BPermissionResult the set of approvers
	 */
	abstract Set<P> getApproversForOpenPermissions(final AbstractOrderModel order, final T customer,
			final Collection<P> openPermissions);

	/**
	 * Gets permissions with OPEN status.
	 *
	 * @param order
	 *           the order
	 * @return the open permissions
	 */
	abstract List<P> getOpenPermissions(final AbstractOrderModel order);

	/**
	 * Gets the b2b permission for the code provided.
	 *
	 * @param code
	 *           the code
	 * @return the b2b permission model
	 */
	abstract B2BPermissionModel getB2BPermissionForCode(final String code);

	/**
	 * Get all b2b permissions.
	 *
	 * @return the set of B2BPermissionModel
	 */
	abstract Set<B2BPermissionModel> getAllB2BPermissions();


	/**
	 * Determine if a b2b permission exists based on a code.
	 *
	 * @param code
	 *           the code
	 * @return True if permission exists
	 */
	boolean permissionExists(String code);

	/**
	 * Checks if the order requires approval by someone other than customer who placed the order.
	 *
	 * @param order
	 *           A b2b order.
	 * @return True if order needs approval.
	 */
	abstract boolean needsApproval(AbstractOrderModel order);


	/**
	 * Get all approvers who have permissions to approve the order.
	 *
	 * @param order
	 *           A b2b order
	 * @return approvers who are eligible to approve the order.
	 */
	abstract Map<T, P> getEligableApprovers(final OrderModel order);

	/**
	 * Get all permission types
	 *
	 * @return permission types list
	 */
	List<String> getAllB2BPermissionTypes();
}

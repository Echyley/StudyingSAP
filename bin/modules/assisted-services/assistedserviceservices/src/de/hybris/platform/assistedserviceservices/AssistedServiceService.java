/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices;

import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;


/**
 * Service layer interface for Assisted Service methods.
 */
public interface AssistedServiceService
{

	/**
	 * Binds customer with provided id to cart if it's anonymous cart.
	 *
	 * @param customerId
	 *           This is id of the customer to whom we want to bind a cart
	 * @param cartId
	 * 			 the id of the customer cart to pick up
	 * @throws AssistedServiceException
	 * 			 in case cart with given cartId is not anonymous
	 */
	void bindCustomerToCart(final String customerId, final String cartId) throws AssistedServiceException;

	/**
	 * Binds customer with provided id to cart if it's anonymous cart, but not emulate the customer.
	 *
	 * @param customerId
	 *           This is id of the customer to whom we want to bind a cart
	 * @param cartId
	 *           the id of anonymous cart to be bound with user
	 * @throws AssistedServiceException
	 *            In case of missing cart with given cartId OR the cart is not anonymous OR missing customer with the
	 *            customerId
	 */
	void bindCustomerToCartWithoutEmulating(final String customerId, final String cartId) throws AssistedServiceException;

	/**
	 * Creates a new customer by it email and name.
	 *
	 * @param customerId
	 *           email of to be newly created customer that is used as uid
	 * @param customerName
	 *           name of to be newly created customer (firstname and surname separated by space symbol)
	 * @return newly created {@link CustomerModel}
	 * @throws DuplicateUidException
	 *            in case customer with given customerId already exists
	 * @throws AssistedServiceException
	 *            in case isolated agent create an non-isolated customer
	 */
	CustomerModel createNewCustomer(final String customerId, final String customerName)
			throws DuplicateUidException, AssistedServiceException;

	/**
	 * Returns collection of a customer's carts
	 *
	 * @param customer
	 *           customer model whose carts to be returned
	 * @return collection of the customer's cart models
	 */
	Collection<CartModel> getCartsForCustomer(final CustomerModel customer);

	/**
	 * returns customer
	 *
	 * @param customerId
	 * 			 email of to be newly created customer that is used as uid
	 * @return
	 *     		 a UserModel for given customerId
	 */
	UserModel getCustomer(final String customerId);

	/**
	 * search for a cart with most resent CartModel::getModifiedtime
	 *
	 * @param customer
	 * 			  customer model whose latest modified cart to be returned
	 * @return
	 *     		 a latest modified cart by given customer
	 */
	CartModel getLatestModifiedCart(final UserModel customer);

	/**
	 * search for order
	 *
	 * @deprecated since 6.6, use {@link AssistedServiceService#getOrderByCode(String, UserModel)} instead
	 *
	 * @param orderCode
	 * 			  the order's code
	 * @param customer
	 * 			  customer model whose order to be returned
	 * @return
	 * 			  an OrderModel with given code and given customer
	 */
	@Deprecated(since = "6.6", forRemoval = true)
	OrderModel gerOrderByCode(final String orderCode, final UserModel customer);

	/**
	 * search for order
	 *
	 * @param orderCode
	 * 			  the order's code
	 * @param customer
	 * 			  customer model whose order to be returned
	 * @return
	 * 			  an OrderModel with given code and given customer
	 */
	OrderModel getOrderByCode(final String orderCode, final UserModel customer);

	/**
	 * Returns true when provided abstractOrderModel relates to current base site
	 *
	 * @param abstractOrderModel
	 * 			   order model
	 * @return true in case order is made against current {@link de.hybris.platform.basecommerce.jalo.site.BaseSite}
	 */
	boolean isAbstractOrderMatchBaseSite(final AbstractOrderModel abstractOrderModel);

	/**
	 * search cart by code and customer
	 *
	 * @param cartCode
	 * 			  the cart's code
	 * @param customer
	 *      	  customer model whose cart to be returned
	 * @return
	 * 			  a CartModel of cart with given cartCode and customer
	 */
	CartModel getCartByCode(final String cartCode, final UserModel customer);

	/**
	 * Returns ASM session object with all information about current asm session.
	 *
	 * @return asm session object
	 */
	AssistedServiceSession getAsmSession();

	/**
	 * Returns all the stores of the agent.
	 *
	 * @param agentId
	 * 			the agent ID
	 * @return
	 * 			all the stores of the agent.
	 */
	default List<PointOfServiceModel> getAllStoresForAgent(final String agentId)
	{
		throw new NotImplementedException();
	}

	/**
	 * Restore cart to provided user
	 *
	 * @param cart
	 *           cart model to be restored
	 * @param user
	 *           user model which cart to be restored
	 */
	void restoreCartToUser(final CartModel cart, final UserModel user);


	/**
	 * Returns the PointOfServiceModel for the logged in as agent.
	 *
	 * @return PointOfServiceModel
	 */
	PointOfServiceModel getAssistedServiceAgentStore();


	/**
	 * Returns the PointOfServiceModel for the given as agent.
	 *
	 * @param agent
	 * 			  AS agent
	 * @return PointOfServiceModel
	 * 			  a PointOfServiceModel for the given as agent.
	 */
	PointOfServiceModel getAssistedServiceAgentStore(final UserModel agent);

	/**
	 * Get list of customers which username or email starts with provided value.
	 *
	 * @param searchCriteria
	 *           uid or customer's name
	 * @param pageableData
	 *           pageable properties
	 * @return suggested customers
	 */
	SearchPageData<CustomerModel> getCustomers(final String searchCriteria, final PageableData pageableData);

	/**
	 * Verify that if provided user can login
	 * @param agent
	 *           AS agent
	 * @return true in case they can login
	 */
	boolean isAgentCanLogin(final UserModel agent);
}
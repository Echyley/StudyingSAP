/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.impl;

import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.commerceservices.user.UserMatchingService;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Default implementation of {@link UserFacade}.
 */
public class DefaultUserFacade implements UserFacade
{
	private UserService userService;
	private CustomerAccountService customerAccountService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private CommonI18NService commonI18NService;
	private ModelService modelService;
	private CartService cartService;
	private UserMatchingService userMatchingService;
	private CheckoutCustomerStrategy checkoutCustomerStrategy;

	private Populator<AddressData, AddressModel> addressReversePopulator;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	private Converter<TitleModel, TitleData> titleConverter;
	private Populator<CCPaymentInfoData, CreditCardPaymentInfoModel> cardPaymentInfoReversePopulator;

	@Override
	public boolean isAnonymousUser()
	{
		return getUserService().isAnonymousUser(getUserService().getCurrentUser());
	}

	@Override
	public boolean isUserExisting(final String id)
	{
		return getUserMatchingService().isUserExisting(id);
	}

	@Override
	public String getUserUID(final String id)
	{
		validateParameterNotNullStandardMessage("id", id);
		final UserModel user = getUserMatchingService().getUserByProperty(id, UserModel.class);
		return user.getUid();
	}

	@Override
	public void setCurrentUser(final String id)
	{
		final UserModel user = userMatchingService.getUserByProperty(id, UserModel.class);
		userService.setCurrentUser(user);
	}

	@Override
	public List<TitleData> getTitles()
	{
		return Converters.convertAll(getCustomerAccountService().getTitles(), getTitleConverter());
	}

	/**
	 * @deprecated Since 6.5. Use {@link org.apache.commons.collections.CollectionUtils#isEmpty(Collection)} with
	 *             this.getAddressBook() as paramenter instead.
	 */
	@Deprecated(since = "6.5", forRemoval = true)
	@Override
	public boolean isAddressBookEmpty()
	{
		// Get the current customer's addresses
		final List<AddressData> addresses = this.getAddressBook();

		return addresses == null || addresses.isEmpty();
	}

	@Override
	public List<AddressData> getAddressBook()
	{
		// Get the current customer's addresses
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final Collection<AddressModel> addresses = getCustomerAccountService().getAddressBookDeliveryEntries(currentUser);

		if (CollectionUtils.isNotEmpty(addresses))
		{
			final List<AddressData> result = new ArrayList<AddressData>();
			final AddressData defaultAddress = getDefaultAddress();

			for (final AddressModel address : addresses)
			{
				final AddressData addressData = getAddressConverter().convert(address);

				if(addressData == null)
				{
					continue;
				}

				if (defaultAddress != null && defaultAddress.getId() != null && defaultAddress.getId().equals(addressData.getId()))
				{
					addressData.setDefaultAddress(true);
					result.add(0, addressData);
				}
				else
				{
					result.add(addressData);
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

	@Override
	public void addAddress(final AddressData addressData)
	{
		validateParameterNotNullStandardMessage("addressData", addressData);

		final CustomerModel currentCustomer = getCurrentUserForCheckout();

		final boolean makeThisAddressTheDefault = addressData.isDefaultAddress()
				|| (currentCustomer.getDefaultShipmentAddress() == null && addressData.isVisibleInAddressBook());

		// Create the new address model
		final AddressModel newAddress = getModelService().create(AddressModel.class);
		getAddressReversePopulator().populate(addressData, newAddress);

		// Store the address against the user
		getCustomerAccountService().saveAddressEntry(currentCustomer, newAddress);

		// Update the address ID in the newly created address
		addressData.setId(newAddress.getPk().toString());

		if (makeThisAddressTheDefault)
		{
			getCustomerAccountService().setDefaultAddressEntry(currentCustomer, newAddress);
		}
	}

	@Override
	public void editAddress(final AddressData addressData)
	{
		validateParameterNotNullStandardMessage("addressData", addressData);
		final CustomerModel currentCustomer = getCurrentUserForCheckout();
		final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, addressData.getId());
		addressModel.setRegion(null);
		getAddressReversePopulator().populate(addressData, addressModel);
		getCustomerAccountService().saveAddressEntry(currentCustomer, addressModel);
		if (addressData.isDefaultAddress())
		{
			getCustomerAccountService().setDefaultAddressEntry(currentCustomer, addressModel);
		}
		else if (addressModel.equals(currentCustomer.getDefaultShipmentAddress()))
		{
			getCustomerAccountService().clearDefaultAddressEntry(currentCustomer);
		}
	}

	@Override
	public void removeAddress(final AddressData addressData)
	{
		validateParameterNotNullStandardMessage("addressData", addressData);
		final CustomerModel currentCustomer = getCurrentUserForCheckout();
		for (final AddressModel addressModel : getCustomerAccountService().getAddressBookEntries(currentCustomer))
		{
			if (addressData.getId().equals(addressModel.getPk().getLongValueAsString()))
			{
				getCustomerAccountService().deleteAddressEntry(currentCustomer, addressModel);
				break;
			}
		}
	}

	@Override
	public AddressData getDefaultAddress()
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		AddressData defaultAddressData = null;

		final AddressModel defaultAddress = getCustomerAccountService().getDefaultAddress(currentCustomer);
		if (defaultAddress != null)
		{
			defaultAddressData = getAddressConverter().convert(defaultAddress);
		}
		else
		{
			final List<AddressModel> addresses = getCustomerAccountService().getAddressBookEntries(currentCustomer);
			if (CollectionUtils.isNotEmpty(addresses))
			{
				defaultAddressData = getAddressConverter().convert(addresses.get(0));
			}
		}
		return defaultAddressData;
	}

	@Override
	public void setDefaultAddress(final AddressData addressData)
	{
		validateParameterNotNullStandardMessage("addressData", addressData);
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final AddressModel addressModel = getCustomerAccountService().getAddressForCode(currentCustomer, addressData.getId());
		if (addressModel != null)
		{
			getCustomerAccountService().setDefaultAddressEntry(currentCustomer, addressModel);
		}
	}

	@Override
	public AddressData getAddressForCode(final String code)
	{
		final AddressModel address = getCustomerAccountService()
				.getAddressForCode((CustomerModel) getUserService().getCurrentUser(), code);

		return address == null ? null : getAddressConverter().convert(address);
	}

	@Override
	public boolean isDefaultAddress(final String addressId)
	{
		final AddressData defaultAddress = getDefaultAddress();
		return (defaultAddress != null && defaultAddress.getId() != null && defaultAddress.getId().equals(addressId));
	}

	@Override
	public List<CCPaymentInfoData> getCCPaymentInfos(final boolean saved)
	{
		final CustomerModel currentCustomer = getCurrentUserForCheckout();
		final List<CreditCardPaymentInfoModel> creditCards = getCustomerAccountService().getCreditCardPaymentInfos(currentCustomer,
				saved);
		final List<CCPaymentInfoData> ccPaymentInfos = new ArrayList<CCPaymentInfoData>();
		final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
		for (final CreditCardPaymentInfoModel ccPaymentInfoModel : creditCards)
		{
			final CCPaymentInfoData paymentInfoData = getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);

			if(paymentInfoData == null)
			{
				continue;
			}

			if (ccPaymentInfoModel.equals(defaultPaymentInfoModel))
			{
				paymentInfoData.setDefaultPaymentInfo(true);
				ccPaymentInfos.add(0, paymentInfoData);
			}
			else
			{
				ccPaymentInfos.add(paymentInfoData);
			}
		}
		return ccPaymentInfos;
	}

	@Override
	public CCPaymentInfoData getCCPaymentInfoForCode(final String code)
	{
		if (StringUtils.isNotBlank(code))
		{
			final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
			final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService()
					.getCreditCardPaymentInfoForCode(currentCustomer, code);
			if (ccPaymentInfoModel != null)
			{
				final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
				final CCPaymentInfoData paymentInfoData = getCreditCardPaymentInfoConverter().convert(ccPaymentInfoModel);
				if (ccPaymentInfoModel.equals(defaultPaymentInfoModel) && paymentInfoData != null)
				{
					paymentInfoData.setDefaultPaymentInfo(true);
				}
				return paymentInfoData;
			}
		}

		return null;
	}


	@Override
	public void setDefaultPaymentInfo(final CCPaymentInfoData paymentInfoData)
	{
		validateParameterNotNullStandardMessage("paymentInfoData", paymentInfoData);
		final CustomerModel currentCustomer = getCurrentUserForCheckout();
		final CreditCardPaymentInfoModel ccPaymentInfoModel = getCustomerAccountService()
				.getCreditCardPaymentInfoForCode(currentCustomer, paymentInfoData.getId());
		if (ccPaymentInfoModel != null)
		{
			getCustomerAccountService().setDefaultPaymentInfo(currentCustomer, ccPaymentInfoModel);
		}
	}

	@Override
	public void removeCCPaymentInfo(final String id)
	{
		validateParameterNotNullStandardMessage("id", id);
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		for (final CreditCardPaymentInfoModel creditCardPaymentInfo : getCustomerAccountService()
				.getCreditCardPaymentInfos(currentCustomer, false))
		{
			if (creditCardPaymentInfo.getPk().toString().equals(id))
			{
				getCustomerAccountService().deleteCCPaymentInfo(currentCustomer, creditCardPaymentInfo);
				break;
			}
		}
		updateDefaultPaymentInfo(currentCustomer);
	}


	/**
	 * @deprecated since 6.7. Use {@link UserFacade#removeCCPaymentInfo(String)} instead
	 */
	@Deprecated(since = "6.7", forRemoval = true)
	@Override
	public void unlinkCCPaymentInfo(final String id)
	{
		validateParameterNotNullStandardMessage("id", id);
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		for (final CreditCardPaymentInfoModel creditCardPaymentInfo : getCustomerAccountService()
				.getCreditCardPaymentInfos(currentCustomer, false))
		{
			if (creditCardPaymentInfo.getPk().toString().equals(id))
			{
				getCustomerAccountService().unlinkCCPaymentInfo(currentCustomer, creditCardPaymentInfo);
				break;
			}
		}
		updateDefaultPaymentInfo(currentCustomer);
	}



	@Override
	public void updateCCPaymentInfo(final CCPaymentInfoData paymentInfo)
	{
		validateParameterNotNullStandardMessage("paymentInfo", paymentInfo);
		validateParameterNotNullStandardMessage("paymentInfoID", paymentInfo.getId());
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final CreditCardPaymentInfoModel paymentInfoModel = getCustomerAccountService()
				.getCreditCardPaymentInfoForCode(currentCustomer, paymentInfo.getId());
		getCardPaymentInfoReversePopulator().populate(paymentInfo, paymentInfoModel);
		getModelService().save(paymentInfoModel);
		if (paymentInfoModel.getBillingAddress() != null)
		{
			getModelService().save(paymentInfoModel.getBillingAddress());
			getModelService().refresh(paymentInfoModel);
		}
	}

	@Override
	public void syncSessionLanguage()
	{
		final UserModel user = getUserService().getCurrentUser();
		user.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		getModelService().save(user);
	}

	@Override
	public void syncSessionCurrency()
	{
		final UserModel user = getUserService().getCurrentUser();
		user.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		getModelService().save(user);
	}

	protected void updateDefaultPaymentInfo(final CustomerModel currentCustomer)
	{
		if (currentCustomer.getDefaultPaymentInfo() == null)
		{
			final List<CreditCardPaymentInfoModel> ccPaymentInfoModelList = getCustomerAccountService()
					.getCreditCardPaymentInfos(currentCustomer, true);
			if (CollectionUtils.isNotEmpty(ccPaymentInfoModelList))
			{
				getCustomerAccountService().setDefaultPaymentInfo(currentCustomer,
						ccPaymentInfoModelList.get(ccPaymentInfoModelList.size() - 1));
			}
		}
	}

	protected CustomerModel getCurrentUserForCheckout()
	{
		return getCheckoutCustomerStrategy().getCurrentUserForCheckout();
	}


	protected Populator<CCPaymentInfoData, CreditCardPaymentInfoModel> getCardPaymentInfoReversePopulator()
	{
		return cardPaymentInfoReversePopulator;
	}

	@Required
	public void setCardPaymentInfoReversePopulator(
			final Populator<CCPaymentInfoData, CreditCardPaymentInfoModel> cardPaymentInfoReversePopulator)
	{
		this.cardPaymentInfoReversePopulator = cardPaymentInfoReversePopulator;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
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

	protected Populator<AddressData, AddressModel> getAddressReversePopulator()
	{
		return addressReversePopulator;
	}

	@Required
	public void setAddressReversePopulator(final Populator<AddressData, AddressModel> addressReversePopulator)
	{
		this.addressReversePopulator = addressReversePopulator;
	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCreditCardPaymentInfoConverter()
	{
		return creditCardPaymentInfoConverter;
	}

	@Required
	public void setCreditCardPaymentInfoConverter(
			final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter)
	{
		this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
	}

	protected Converter<TitleModel, TitleData> getTitleConverter()
	{
		return titleConverter;
	}

	@Required
	public void setTitleConverter(final Converter<TitleModel, TitleData> titleConverter)
	{
		this.titleConverter = titleConverter;
	}

	protected CheckoutCustomerStrategy getCheckoutCustomerStrategy()
	{
		return checkoutCustomerStrategy;
	}

	@Required
	public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy)
	{
		this.checkoutCustomerStrategy = checkoutCustomerStrategy;
	}

	protected UserMatchingService getUserMatchingService()
	{
		return userMatchingService;
	}

	@Required
	public void setUserMatchingService(final UserMatchingService userMatchingService)
	{
		this.userMatchingService = userMatchingService;
	}
}

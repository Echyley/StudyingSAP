/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.chineseprofilefacades.strategies.impl;

import de.hybris.platform.chineseprofilefacades.data.MobileNumberVerificationData;
import de.hybris.platform.chineseprofilefacades.strategies.VerificationCodeStrategy;
import de.hybris.platform.chineseprofileservices.customer.ChineseCustomerAccountService;
import de.hybris.platform.chineseprofileservices.model.MobileNumberVerificationModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserIdDecorationService;

import java.util.Objects;
import java.util.Optional;


public class ChineseVerificationCodeStrategy implements VerificationCodeStrategy
{
	private final ModelService modelService;
	private final ChineseCustomerAccountService chineseCustomerAccountService;

	private final Converter<MobileNumberVerificationData, MobileNumberVerificationModel> verificationCodeReverseConverter;

	private final Converter<MobileNumberVerificationModel, MobileNumberVerificationData> verificationConverter;

	private UserIdDecorationService userIdDecorationService;

	public ChineseVerificationCodeStrategy(final ModelService modelService,
			final ChineseCustomerAccountService chineseCustomerAccountService,
			final Converter<MobileNumberVerificationData, MobileNumberVerificationModel> verificationCodeReverseConverter,
			final Converter<MobileNumberVerificationModel, MobileNumberVerificationData> verificationConverter,
			final UserIdDecorationService userIdDecorationService)
	{
		this.modelService = modelService;
		this.chineseCustomerAccountService = chineseCustomerAccountService;
		this.verificationCodeReverseConverter = verificationCodeReverseConverter;
		this.verificationConverter = verificationConverter;
		this.userIdDecorationService = userIdDecorationService;
	}

	@Override
	public void saveVerificationCode(final MobileNumberVerificationData data)
	{
		final Optional<MobileNumberVerificationModel> verificationModel = getChineseCustomerAccountService()
				.getMobileNumberVerificationCode(data.getMobileNumber());

		verificationModel.ifPresent(model -> {
			if (getChineseCustomerAccountService().isVerificationCodeExpired(model.getCreationtime()))
			{
				modelService.remove(model);
			}
		});

		data.setMobileNumber(this.decorateMobileNumber(data.getMobileNumber()));
		modelService.save(getVerificationCodeReverseConverter().convert(data));
	}

	@Override
	public Optional<MobileNumberVerificationData> getVerificationCode(final String mobileNumber)
	{

		final Optional<MobileNumberVerificationModel> verificationModel = getChineseCustomerAccountService()
				.getMobileNumberVerificationCode(mobileNumber);

		if (verificationModel.isPresent())
		{
			return Optional.of(getVerificationConverter().convert(verificationModel.get()));
		}
		return Optional.empty();

	}

	@Override
	public void removeVerificationCode(final String mobileNumber)
	{
		final Optional<MobileNumberVerificationModel> verificationModel = getChineseCustomerAccountService()
				.getMobileNumberVerificationCode(mobileNumber);

		verificationModel.ifPresent(model -> getModelService().remove(model));
		
	}

	protected ChineseCustomerAccountService getChineseCustomerAccountService()
	{
		return this.chineseCustomerAccountService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected Converter<MobileNumberVerificationData, MobileNumberVerificationModel> getVerificationCodeReverseConverter()
	{
		return this.verificationCodeReverseConverter;
	}

	protected Converter<MobileNumberVerificationModel, MobileNumberVerificationData> getVerificationConverter()
	{
		return this.verificationConverter;
	}

	public void setUserIdDecorationService(UserIdDecorationService userIdDecorationService) {
		this.userIdDecorationService = Objects.requireNonNull(userIdDecorationService);
	}

	protected UserIdDecorationService getUserIdDecorationService() {
		return this.userIdDecorationService;
	}
	private String decorateMobileNumber(String mobileNumber) {
		return this.canUserIdBeDecorated(mobileNumber) ? this.getUserIdDecorationService().decorateUserId(mobileNumber) : mobileNumber;
	}

	private boolean canUserIdBeDecorated(String userId) {
		return this.getUserIdDecorationService() != null && userId != null;
	}
}

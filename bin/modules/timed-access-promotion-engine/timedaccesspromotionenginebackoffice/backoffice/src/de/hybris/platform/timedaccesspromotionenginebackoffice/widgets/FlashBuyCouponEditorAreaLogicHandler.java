/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.timedaccesspromotionenginebackoffice.widgets;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsRegistry;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsService;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;
import de.hybris.platform.util.localization.Localization;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.Interval;

import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectSavingException;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.validation.ValidationContext;
import com.hybris.cockpitng.validation.impl.DefaultValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationSeverity;
import com.hybris.cockpitng.widgets.baseeditorarea.DefaultEditorAreaLogicHandler;



/**
 * Check the promotion assigned with flash buy coupon before saving.
 */
public class FlashBuyCouponEditorAreaLogicHandler extends DefaultEditorAreaLogicHandler
{
	private static final String MSG_PUBLISHED = "published";
	private static final String MSG_ASSIGNEDCOUPON = "assignedCoupon";
	private static final String MSG_PRODUCT_LIMITATION = "product.limitation";
	private static final String MSG_PROMOTION_OCCUPATION = "promotion.occupied";
	private static final String MSG_PROMOTION_EXPIRED = "promotion.expired";
	private static final String MSG_PROMOTION_ENDDATE_ERROR = "promotion.enddate.error";
	private static final int PRODUCTS_LIMITATION = 1;


	private FlashBuyService flashBuyService;
	private RuleConditionsRegistry ruleConditionsRegistry;
	private RuleConditionsService ruleConditionsService;

	@Override
	public List<ValidationInfo> performValidation(final WidgetInstanceManager widgetInstanceManager, final Object currentObject,
			final ValidationContext validationContext)
	{
		final FlashBuyCouponModel currentFlashBuyCoupon = (FlashBuyCouponModel) currentObject;
		final PromotionSourceRuleModel currentPromotionSourceRule = currentFlashBuyCoupon.getRule();
		if (currentPromotionSourceRule == null)
		{
			currentFlashBuyCoupon.setProduct(null);
			currentFlashBuyCoupon.setStartDate(null);
			currentFlashBuyCoupon.setEndDate(null);
		}
		List<ValidationInfo> validationInfos = new ArrayList<>(
				super.performValidation(widgetInstanceManager, currentObject, validationContext));
		if (!isNull(currentPromotionSourceRule))
		{
			validationInfos = validationInfos.stream()
					.filter(validationInfo -> !AbstractCouponModel.ENDDATE.equals(validationInfo.getInvalidPropertyPath()))
					.collect(Collectors.toList());
			validatePromotionSourceRule(validationInfos, currentPromotionSourceRule, currentFlashBuyCoupon);
		}
		return validationInfos;
	}

	/**
	 * Check if the promotion is available for the flash-buy coupon.If available, then check product(s) quantity for the
	 * promotion
	 *
	 * @param validationInfos
	 *           validation information.
	 * @param promotionSourceRule
	 *           promotion source rule
	 * @param currentFlashBuyCoupon
	 *           current edit FlashBuyCoupon
	 */
	protected void validatePromotionSourceRule(final List<ValidationInfo> validationInfos,
			final PromotionSourceRuleModel promotionSourceRule, final FlashBuyCouponModel currentFlashBuyCoupon)
	{
		if (RuleStatus.PUBLISHED.equals(promotionSourceRule.getStatus()))
		{
			validatePromotionExpiryDate(validationInfos, promotionSourceRule);
			if (!validationInfos.isEmpty())
			{
				return;
			}

			final Optional<FlashBuyCouponModel> flashBuyCoupon = getFlashBuyService()
					.getFlashBuyCouponByPromotionCode(promotionSourceRule.getCode());
			if (flashBuyCoupon.isPresent() && !flashBuyCoupon.get().getCouponId().equals(currentFlashBuyCoupon.getCouponId()))
			{
				validationInfos.add(createValidationInfo(promotionSourceRule.getName(), MSG_ASSIGNEDCOUPON));
			}
			else
			{
				validateRuleConditions(validationInfos, promotionSourceRule, currentFlashBuyCoupon);
			}
		}
		else
		{
			validationInfos.add(createValidationInfo(promotionSourceRule.getName(), MSG_PUBLISHED));
		}

	}

	protected void validateRuleConditions(final List<ValidationInfo> validationInfos,
			final PromotionSourceRuleModel promotionSourceRule, final FlashBuyCouponModel currentFlashBuyCoupon)
	{
		final Map<String, RuleConditionDefinitionData> conditionDefinitions = ruleConditionsRegistry
				.getConditionDefinitionsForRuleTypeAsMap(promotionSourceRule.getClass());
		final List<RuleConditionData> ruleCondition = ruleConditionsService
				.convertConditionsFromString(promotionSourceRule.getConditions(), conditionDefinitions);

		if (ruleCondition.size() > PRODUCTS_LIMITATION)
		{
			validationInfos.add(
					createValidationInfo(promotionSourceRule.getName(), MSG_PRODUCT_LIMITATION, String.valueOf(PRODUCTS_LIMITATION)));
		}
		else
		{
			validateProduct(validationInfos, promotionSourceRule, currentFlashBuyCoupon);
		}
	}

	/**
	 * Verify promotion source rule expiry date
	 *
	 * @param validationInfos
	 *           validation information.
	 * @param promotionSourceRule
	 *           promotion source rule.
	 */
	protected void validatePromotionExpiryDate(final List<ValidationInfo> validationInfos,
			final PromotionSourceRuleModel promotionSourceRule)
	{
		if (nonNull(promotionSourceRule.getEndDate()) && promotionSourceRule.getEndDate().before(Calendar.getInstance().getTime()))
		{
			validationInfos.add(createValidationInfo(promotionSourceRule.getName(), MSG_PROMOTION_EXPIRED));
			return;
		}
		if (nonNull(promotionSourceRule.getStartDate()) && nonNull(promotionSourceRule.getEndDate())
				&& promotionSourceRule.getStartDate().after(promotionSourceRule.getEndDate()))
		{
			validationInfos.add(createValidationInfo(promotionSourceRule.getName(), MSG_PROMOTION_ENDDATE_ERROR));
			return;
		}
	}

	/**
	 * Verify product quantity and coupon overlaps
	 *
	 * @param validationInfos
	 *           validation information.
	 * @param promotionSourceRule
	 *           promotion source rule
	 * @param currentFlashBuyCoupon
	 *           current selected flashBuyCoupon
	 */
	protected void validateProduct(final List<ValidationInfo> validationInfos, final PromotionSourceRuleModel promotionSourceRule,
			final FlashBuyCouponModel currentFlashBuyCoupon)
	{
		final List<ProductModel> products = getFlashBuyService().getAllProductsByPromotionSourceRule(promotionSourceRule);

		if (CollectionUtils.isEmpty((products)) || products.size() > PRODUCTS_LIMITATION)
		{
			validationInfos.add(createValidationInfo(promotionSourceRule.getName(), MSG_PRODUCT_LIMITATION,
					String.valueOf(PRODUCTS_LIMITATION)));
		}
		else
		{
			getFlashBuyService()
					.getFlashBuyCouponByProduct(products.get(0))
					.stream()
					.filter(areCouponsDuplicated(currentFlashBuyCoupon))
					.forEach(
							flashBuyCoupon ->
								validationInfos.add(createValidationInfo(promotionSourceRule.getName(), MSG_PROMOTION_OCCUPATION,
									products.get(0).getCode(), flashBuyCoupon.getCouponId())));

		}
	}

	/**
	 * Check if 2 flash-buy coupons' valid period are overlapping
	 *
	 * @param currentFlashBuyCoupon
	 *           current selected FlashBuyCoupon
	 * @return Predicate if two coupon is duplicated.
	 *
	 */
	protected Predicate<FlashBuyCouponModel> areCouponsDuplicated(final FlashBuyCouponModel currentFlashBuyCoupon)
	{
		return exsitingFlashBuyCoupon -> {
			if (exsitingFlashBuyCoupon.getCouponId().equals(currentFlashBuyCoupon.getCouponId()))
			{
				return false;
			}

			final long exsitingCouponStartTs = exsitingFlashBuyCoupon.getStartDate() != null ? exsitingFlashBuyCoupon.getStartDate()
					.getTime() : Long.MIN_VALUE;
			final long existingCouponEndTs = exsitingFlashBuyCoupon.getStartDate() != null ? exsitingFlashBuyCoupon.getEndDate()
					.getTime() : Long.MAX_VALUE;
			final long currentCouponStartTs = currentFlashBuyCoupon.getRule().getStartDate() != null
					? currentFlashBuyCoupon.getRule().getStartDate()
					.getTime() : Long.MIN_VALUE;
			final long currentCouponEndTs = currentFlashBuyCoupon.getRule().getStartDate() != null
					? currentFlashBuyCoupon.getRule().getEndDate()
					.getTime() : Long.MAX_VALUE;
			final Interval existingCouponPeriod = new Interval(exsitingCouponStartTs, existingCouponEndTs);
			final Interval currentCouponPeriod = new Interval(currentCouponStartTs, currentCouponEndTs);

			return existingCouponPeriod.overlaps(currentCouponPeriod);
		};
	}

	@Override
	public Object performSave(final WidgetInstanceManager widgetInstanceManager, final Object currentObject)
			throws ObjectSavingException
	{
		final FlashBuyCouponModel flashBuyCoupon = (FlashBuyCouponModel) currentObject;
		final PromotionSourceRuleModel promotionSourceRule = flashBuyCoupon.getRule();

		if (promotionSourceRule != null)
		{
			getFlashBuyService().getAllProductsByPromotionSourceRule(promotionSourceRule)
					.forEach(product -> flashBuyCoupon.setProduct(product));

			flashBuyCoupon.setStartDate(promotionSourceRule.getStartDate());
			flashBuyCoupon.setEndDate(promotionSourceRule.getEndDate());
		}

		final Object result = super.performSave(widgetInstanceManager, flashBuyCoupon);
		getFlashBuyService().createCronJobForFlashBuyCoupon((FlashBuyCouponModel) result);

		return result;
	}

	/**
	 * Create validation info
	 *
	 * @param promotionSourceRuleName
	 *           promotion source rule name
	 * @param message
	 *           error msg
	 * @return ValidationInfo Validation info
	 */
	protected ValidationInfo createValidationInfo(final String promotionSourceRuleName, final String message,
			final String... arguments)
	{
		final DefaultValidationInfo validationInfo = new DefaultValidationInfo();

		final String msgKey = "type.validation.promotionSourceRule." + message + ".text";
		final String msg = arguments.length > 0 ? Localization.getLocalizedString(msgKey, arguments) : Localization
				.getLocalizedString(msgKey);
		validationInfo.setValidationMessage(msg);
		validationInfo.setInvalidPropertyPath("rule");
		validationInfo.setConfirmed(false);
		validationInfo.setInvalidValue(promotionSourceRuleName);
		validationInfo.setValidationSeverity(ValidationSeverity.ERROR);

		return validationInfo;
	}

	protected FlashBuyService getFlashBuyService()
	{
		return flashBuyService;
	}


	public void setFlashBuyService(final FlashBuyService flashBuyService)
	{
		this.flashBuyService = flashBuyService;
	}

	protected RuleConditionsService getRuleConditionsService()
	{
		return ruleConditionsService;
	}


	public void setRuleConditionsService(final RuleConditionsService ruleConditionsService)
	{
		this.ruleConditionsService = ruleConditionsService;
	}

	protected RuleConditionsRegistry getRuleConditionsRegistry()
	{
		return ruleConditionsRegistry;
	}


	public void setRuleConditionsRegistry(final RuleConditionsRegistry ruleConditionsRegistry)
	{
		this.ruleConditionsRegistry = ruleConditionsRegistry;
	}


}

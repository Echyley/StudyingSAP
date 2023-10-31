# LineItemDomainSpecific

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**any** | **Object** | This is currently not supported |  [optional]
**sale** | [**SaleBase**](SaleBase.md) |  |  [optional]
**saleForDelivery** | [**SaleForDeliveryBase**](SaleForDeliveryBase.md) |  |  [optional]
**saleForPickup** | [**SaleForPickupBase**](SaleForPickupBase.md) |  |  [optional]
**_return** | [**ReturnBase**](ReturnBase.md) |  |  [optional]
**returnForDelivery** | [**ReturnForDeliveryBase**](ReturnForDeliveryBase.md) |  |  [optional]
**returnForPickup** | [**ReturnForPickupBase**](ReturnForPickupBase.md) |  |  [optional]
**customerOrderForDelivery** | [**CustomerOrderForDeliveryBase**](CustomerOrderForDeliveryBase.md) |  |  [optional]
**customerOrderForPickup** | [**CustomerOrderForPickupBase**](CustomerOrderForPickupBase.md) |  |  [optional]
**discount** | [**DiscountBase**](DiscountBase.md) |  |  [optional]
**loyaltyReward** | [**LoyaltyRewardBase**](LoyaltyRewardBase.md) |  |  [optional]
**coupon** | [**TenderCouponBase**](TenderCouponBase.md) |  |  [optional]
**promotionManualTrigger** | [**PromotionManualTriggerType**](PromotionManualTriggerType.md) |  |  [optional]
**promotionExternalTrigger** | [**PromotionExternalTriggerType**](PromotionExternalTriggerType.md) |  |  [optional]
**sequenceNumber** | **List&lt;Integer&gt;** | Identifies the line item. Although this is an array, exactly 1 entry is required. |  [optional]
**merchandiseHierarchy** | [**List&lt;MerchandiseHierarchyCommonData&gt;**](MerchandiseHierarchyCommonData.md) | This information is taken for checking eligibilities on item hierarchy level. |  [optional]
**additionalBonusLineItem** | [**AdditionalBonusType**](AdditionalBonusType.md) |  |  [optional]
**additionalBonusDiscountLineItem** | [**AdditionalBonusDiscountType**](AdditionalBonusDiscountType.md) |  |  [optional]

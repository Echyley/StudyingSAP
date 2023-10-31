# LoyaltyRewardBase

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**promotionID** | **String** | Identifies the promotion that triggered the loyalty reward. Decimal representation of a 64 bit integer value. | 
**pointsAwarded** | [**List&lt;PointsAwarded&gt;**](PointsAwarded.md) | The number of points awarded by the line item. |  [optional]
**manualTriggerSequenceNumber** | **Integer** | The sequence number for the trigger to be assigned to a single LoyaltyReward. It references the ManualTriggerSequenceNumber of PromotionManualTriggerType that triggered this reward.  |  [optional]
**pointsAwardedAmount** | [**AmountCommonData**](AmountCommonData.md) |  |  [optional]
**computationBaseAmount** | [**AmountCommonData**](AmountCommonData.md) |  |  [optional]
**externalSystemOriginatorFlag** | **Boolean** | Determines if this LoyaltyReward was created by an external system. If yes, it must not be changed, but its own modifications with higher sequence may be applied.  |  [optional]
**priceDerivationRule** | [**PriceDerivationRuleBase**](PriceDerivationRuleBase.md) |  |  [optional]
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**typeCode** | [**TypeCodeEnum**](#TypeCodeEnum) | Type code of the loyalty reward |  [optional]

<a name="TypeCodeEnum"></a>
## Enum: TypeCodeEnum
Name | Value
---- | -----
AWARD | &quot;Award&quot;

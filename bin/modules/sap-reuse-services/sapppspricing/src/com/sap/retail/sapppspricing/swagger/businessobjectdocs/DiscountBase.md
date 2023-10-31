# DiscountBase

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**priceDerivationRule** | [**List&lt;PriceDerivationRuleBase&gt;**](PriceDerivationRuleBase.md) | Although this is an array, onyl exactly 1 entry is suppored | 
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**manualTriggerSequenceNumber** | **Integer** | The sequence number for the trigger that allows more than one trigger to be assigned to a single RetailPriceModifier. It references the ManualTriggerSequenceNumber of PromotionManualTriggerType that triggered this price modifier.  |  [optional]
**extraAmount** | [**AmountCommonData**](AmountCommonData.md) |  |  [optional]
**externalSystemOriginatorFlag** | **Boolean** | Determines if this discount was created by an external system. If yes, it must not be changed, but its own modifications with higher sequence may be applied. Default value is false.  |  [optional]
**sequenceNumber** | **Integer** | Identifies this retail price modifier in the corresponding line item. | 
**amount** | [**Amount**](Amount.md) |  |  [optional]
**percent** | [**Percent**](Percent.md) |  |  [optional]
**previousPrice** | [**PreviousPrice**](PreviousPrice.md) |  |  [optional]
**newPrice** | [**NewPrice**](NewPrice.md) |  |  [optional]
**promotionID** | [**PromotionID**](PromotionID.md) |  |  [optional]
**itemLink** | **List&lt;Integer&gt;** | The link to the discount line item that triggered the creation of this RetailPriceModifier in order to keep the prorated transaction-related discount for the current line item. Is not used in the context of the modification of the price of a single line item (item-related discount). Although this is an array, it contains at most one entry.  |  [optional]
**quantity** | [**QuantityCommonData**](QuantityCommonData.md) |  |  [optional]
**rounding** | [**RoundingCommonData**](RoundingCommonData.md) |  |  [optional]
**computationBaseAmount** | [**AmountCommonData**](AmountCommonData.md) |  |  [optional]
**proratedFlag** | **Boolean** | Specifies whether the discount amount applies to the whole transaction or is prorated to the discountable line items of the transaction. Currently always true.  |  [optional]

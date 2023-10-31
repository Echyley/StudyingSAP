# CustomerOrderForDeliveryBase

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**itemID** | [**List&lt;ItemID&gt;**](ItemID.md) | Although this is an array, only 1 entry is supported. |  [optional]
**merchandiseHierarchy** | [**List&lt;MerchandiseHierarchyCommonData&gt;**](MerchandiseHierarchyCommonData.md) | For information purposes only. The relevant list of hierarchy nodes is expected on the level of LineItemDomainSpecific. |  [optional]
**regularSalesUnitPrice** | [**UnitPriceCommonData**](UnitPriceCommonData.md) |  |  [optional]
**extendedAmount** | [**ExtendedAmountType**](ExtendedAmountType.md) |  |  [optional]
**extendedDiscountAmount** | [**AmountCommonData**](AmountCommonData.md) |  |  [optional]
**quantity** | [**List&lt;QuantityCommonData&gt;**](QuantityCommonData.md) | The quantity of this line item. Although this is an array, only 1 entry is allowed. |  [optional]
**retailPriceModifier** | [**List&lt;RetailPriceModifierDomainSpecific&gt;**](RetailPriceModifierDomainSpecific.md) | The list of modifiers of the sales price triggered by applied price derivation rules. |  [optional]
**itemLink** | [**List&lt;ItemLink&gt;**](ItemLink.md) | Reserved for future use. |  [optional]
**itemType** | [**ItemTypeEnum**](#ItemTypeEnum) | Reserved for future use. |  [optional]
**nonDiscountableFlag** | **Boolean** | Specifies whether this line item may be discounted or not. If set to true, the line item may still be used as an eligibility for promotions. However, such an item does not contribute to fulfill the amount or quantity threshold that is needed for a promotion to become effective.  |  [optional]
**fixedPriceFlag** | **Boolean** | If set to true, the price calculation uses the provided value of RegularSalesUnitPrice as regular price and does not perform a price lookup for this line item. |  [optional]
**taxIncludedInPriceFlag** | **Boolean** | Reserved for future use. |  [optional]
**nonPieceGoodFlag** | **Boolean** | Set this flag to true, if the specified quantity has a different dimension than \&quot;piece\&quot;, for example, if fractional quantities are possible. |  [optional]
**frequentShopperPointsEligibilityFlag** | **Boolean** | Set this flag to true if the corresponding line item is eligible for receiving loyalty points. |  [optional]
**discountTypeCode** | **String** | Reserved for future use. |  [optional]
**priceTypeCode** | [**PriceTypeCodeEnum**](#PriceTypeCodeEnum) | Reserved for future use. Value 00 corresponds to \&quot;regular price\&quot;. |  [optional]
**notConsideredByPriceEngineFlag** | **Boolean** | Determines if the price calculation should consider the line item as a trigger. This information is needed if another process (e.g. a return regarding to an existing transaction) applies rules for the line item that should not be applied. Assuming that the line item is discountable in general, the line item does not trigger a line-item-releated price derivation rule, get discount or get bonus points, if you set this flag to true. However, it can do so for a transaction-related price derivation rule.  |  [optional]
**frequentShopperPointsModifier** | [**List&lt;FrequentShopperPointsModifierType&gt;**](FrequentShopperPointsModifierType.md) | The list of modifiers for loyalty points determined by applied price rules. |  [optional]
**promotionPriceDerivationRuleReference** | [**List&lt;PromotionPriceDerivationRuleReferenceType&gt;**](PromotionPriceDerivationRuleReferenceType.md) | The list of promotions for which the corresponding line item contributed as an eligibility. |  [optional]
**promotionManualTrigger** | [**List&lt;PromotionManualTriggerType&gt;**](PromotionManualTriggerType.md) | Manual triggers created by the client in order to make the transaction eligible for promotions that contain eligibilities of type manual trigger. |  [optional]
**lineItemAttribute** | [**List&lt;LineItemAttributeType&gt;**](LineItemAttributeType.md) | Attribute Value pairs on line item level. This information is  taken for checking eligibilities on item level. |  [optional]

<a name="ItemTypeEnum"></a>
## Enum: ItemTypeEnum
Name | Value
---- | -----
STOCK | &quot;Stock&quot;
GIFTCERTIFICATE | &quot;GiftCertificate&quot;

<a name="PriceTypeCodeEnum"></a>
## Enum: PriceTypeCodeEnum
Name | Value
---- | -----
_00 | &quot;00&quot;
_01 | &quot;01&quot;

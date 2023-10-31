# PriceCalculateBase

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**transactionID** | [**IDCommonData**](IDCommonData.md) |  |  [optional]
**dateTime** | [**DateTimeCommonData**](DateTimeCommonData.md) |  |  [optional]
**loyalty** | [**List&lt;LoyaltyAccountCommonData&gt;**](LoyaltyAccountCommonData.md) | Refers to the end customer. |  [optional]
**shoppingBasket** | [**ShoppingBasketBase**](ShoppingBasketBase.md) |  | 
**regularSalesUnitPriceRoundingRule** | [**RoundingRuleType**](RoundingRuleType.md) |  |  [optional]
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**transactionType** | [**TransactionTypeEnum**](#TransactionTypeEnum) | Indicates whether in this transaction items are sold or returned. |  [optional]
**netPriceFlag** | **Boolean** | If set to true, the net regular prices are used for the price calculation. Otherwise, the gross prices are considered. | 
**calculationMode** | [**CalculationModeEnum**](#CalculationModeEnum) | Controls if the items of the ShoppingBasket element are considered as part of a shared basket (\&quot;Basket\&quot;) or if they are treated as separate items (\&quot;LineItem\&quot;). If not specified, \&quot;Basket\&quot; is used as default.  |  [optional]
**calculationScheme** | [**CalculationSchemeEnum**](#CalculationSchemeEnum) | Controls which price derivation rules are used for the price calculation. This is defined by the transaction control break code/price rule control code and the point in time for the application of price derivation rules (immediately or after subtotal).  For the calculation scheme type &#x27;CalcAll&#x27;, all transaction control break codes/price rule control codes can be applied (PC, PO, SP, SU). For the calculation scheme type \&quot;CalcForItem\&quot;, only rules with transaction control break code/price rule control code PO and SP can be applied. If not specified, \&quot;CalcAll\&quot; is used as default. The calculation scheme is available as of version 4.0.  |  [optional]
**txAttribute** | [**List&lt;TransactionAttributeType&gt;**](TransactionAttributeType.md) | Generic attributes on transaction level in the price calculation request. These generic attributes can be used  as trigger for a promotional rule. |  [optional]

<a name="TransactionTypeEnum"></a>
## Enum: TransactionTypeEnum
Name | Value
---- | -----
SALETRANSACTION | &quot;SaleTransaction&quot;

<a name="CalculationModeEnum"></a>
## Enum: CalculationModeEnum
Name | Value
---- | -----
BASKET | &quot;Basket&quot;
LINEITEM | &quot;LineItem&quot;

<a name="CalculationSchemeEnum"></a>
## Enum: CalculationSchemeEnum
Name | Value
---- | -----
CALCALL | &quot;CalcAll&quot;
CALCFORITEM | &quot;CalcForItem&quot;

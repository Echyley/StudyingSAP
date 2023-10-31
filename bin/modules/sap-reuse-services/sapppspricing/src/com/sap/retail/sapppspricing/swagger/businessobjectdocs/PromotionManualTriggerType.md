# PromotionManualTriggerType

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**manualTriggerSequenceNumber** | **Integer** | Identifies the manual trigger per line item. Must be a positive small integer. | 
**manualTriggerType** | **String** | Type of the manual trigger as defined in the promotion master data. This type is to be set by the client and used to determine relevant promotions. | 
**manualTriggerValue** | **String** | Value for the manual trigger as defined in the promotion master data. This value is to be set by the client and used to determine relevant promotions. | 
**privilegeType** | [**PrivilegeTypeEnum**](#PrivilegeTypeEnum) | Specifies the type of the price adjustment. Valid values are: Absolute discount (RS), percentage discount (RP), new price (PS) or the reduction specified in the promotion master data (AM).  | 
**privilegeValue** | [**AmountCommonData**](AmountCommonData.md) |  | 
**manualTriggerSequenceAddend** | **Long** | A value that is to be added to the sequence of the promotion price derivation rule. This allows to apply multiple manual discounts with the same price derivation rule.  | 
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]

<a name="PrivilegeTypeEnum"></a>
## Enum: PrivilegeTypeEnum
Name | Value
---- | -----
AM | &quot;AM&quot;
RS | &quot;RS&quot;
RP | &quot;RP&quot;
PS | &quot;PS&quot;

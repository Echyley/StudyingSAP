# RoundingRuleType

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**roundingMethod** | [**RoundingMethodEnum**](#RoundingMethodEnum) | Specifies the rounding direction. Default value is Commercial. |  [optional]
**multiple** | [**BigDecimal**](BigDecimal.md) | The rounding result must be an integer multiple of the specified value. Example: If the value to be rounded is 12.345, rounding method is Commercial and Multple is 0.05, then the rounding result is 12.35. Default value is 0.01.  |  [optional]

<a name="RoundingMethodEnum"></a>
## Enum: RoundingMethodEnum
Name | Value
---- | -----
UP | &quot;Up&quot;
DOWN | &quot;Down&quot;
COMMERCIAL | &quot;Commercial&quot;

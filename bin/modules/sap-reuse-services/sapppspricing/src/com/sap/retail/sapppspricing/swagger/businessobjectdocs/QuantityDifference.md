# QuantityDifference

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**value** | [**BigDecimal**](BigDecimal.md) | Must be an integer. The overall quantity is the product of value and units. Can be positive as well as negative. |  [optional]
**units** | [**BigDecimal**](BigDecimal.md) | May be fractional. Set this to one for piece like items. The overall quantity is the product of value and units. |  [optional]
**unitOfMeasureCode** | **String** | Must match with the information specified in the promotions and regular prices. No conversion of unit of measures is done. Uppercase format. |  [optional]

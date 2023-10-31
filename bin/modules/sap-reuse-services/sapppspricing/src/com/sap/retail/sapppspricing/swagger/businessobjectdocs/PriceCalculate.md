# PriceCalculate

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**arTSHeader** | [**ARTSCommonHeaderType**](ARTSCommonHeaderType.md) |  | 
**priceCalculateBody** | [**List&lt;PriceCalculateBase&gt;**](PriceCalculateBase.md) | Contains the items for which a price calculation should be performed and additional information about how it is performed. | 
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**internalMajorVersion** | **Integer** | Major version of the Client API version to be used. For Client API version X.Y, this field corresponds to the X part. Currently allowed versions of the Client API are 1.0, 2.0, 2.1., 3.0, 4.0, 5.0, 6.0. In a cloud environment, only version 2.1 or higher may be used.  | 
**internalMinorVersion** | **Integer** | Minor version of the Client API version to be used. For Client API version X.Y, this field corresponds to the Y part. |  [optional]

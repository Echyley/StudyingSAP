# BusinessUnitCommonData

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**value** | **String** | Identifies the business unit in combination with business unit type and master data source system identifier. | 
**typeCode** | [**TypeCodeEnum**](#TypeCodeEnum) | Specifies the type of a business unit. There may be several business units with the same identifier but different types. Default value is RetailStore.  |  [optional]

<a name="TypeCodeEnum"></a>
## Enum: TypeCodeEnum
Name | Value
---- | -----
RETAILSTORE | &quot;RetailStore&quot;
CUSTOMER | &quot;Customer&quot;
FACTORY | &quot;Factory&quot;
DISTRIBUTIONCENTER | &quot;DistributionCenter&quot;
VENDOR | &quot;Vendor&quot;
DISTRIBUTIONCHAIN | &quot;DistributionChain&quot;

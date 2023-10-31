# MerchSetNodeTypeL0

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | [**TypeEnum**](#TypeEnum) |  |  [optional]
**operation** | [**OperationEnum**](#OperationEnum) |  |  [optional]
**itemID** | [**ItemID**](ItemID.md) |  |  [optional]
**merchandiseHierarchy** | [**MerchandiseHierarchyCommonData**](MerchandiseHierarchyCommonData.md) |  |  [optional]
**genericAttribute** | [**LineItemAttributeType**](LineItemAttributeType.md) |  |  [optional]
**include** | [**List&lt;MerchSetNodeTypeL1&gt;**](MerchSetNodeTypeL1.md) |  |  [optional]
**exclude** | [**List&lt;MerchSetNodeTypeL1&gt;**](MerchSetNodeTypeL1.md) |  |  [optional]
**filter** | [**List&lt;MerchSetNodeTypeL1&gt;**](MerchSetNodeTypeL1.md) |  |  [optional]

<a name="TypeEnum"></a>
## Enum: TypeEnum
Name | Value
---- | -----
OPERATION | &quot;Operation&quot;
ITEM | &quot;Item&quot;
MERCHANDISEHIERARCHYNODE | &quot;MerchandiseHierarchyNode&quot;
GENERICATTRIBUTE | &quot;GenericAttribute&quot;

<a name="OperationEnum"></a>
## Enum: OperationEnum
Name | Value
---- | -----
DIFFERENCE | &quot;Difference&quot;

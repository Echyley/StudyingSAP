# BusinessError

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**errorID** | **String** | Can be used for client side checks. For a complete list of possible error codes see the Troubleshooting section in the administriation guide for SAP Omnichannel Promotion Pricing under https://help.sap.com/viewer/7c87270e23c64c2aa922ce297a6df23d/Cloud/en-US/16b90f4819b546f39f29b664d6259641.html  |  [optional]
**description** | [**DescriptionCommonData**](DescriptionCommonData.md) |  |  [optional]
**severity** | [**SeverityEnum**](#SeverityEnum) | Issues with severity error or higher lead to an HTTP response code 400. |  [optional]
**errorSerialNumber** | **Integer** | The error serial number, unique for every single business error within one specific Error Number. Decimal representation of a 16 bit integer value.  |  [optional]
**lineItemSequenceNumber** | **Integer** | Identifies the Sequence Number of the line item that caused the business error. |  [optional]
**messageVariables** | [**MessageVariables**](MessageVariables.md) |  |  [optional]

<a name="SeverityEnum"></a>
## Enum: SeverityEnum
Name | Value
---- | -----
INFORMATION | &quot;Information&quot;
WARNING | &quot;Warning&quot;
ERROR | &quot;Error&quot;

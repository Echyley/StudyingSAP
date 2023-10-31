# ARTSCommonHeaderType

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**messageID** | [**MessageID**](MessageID.md) |  | 
**dateTime** | [**List&lt;HeaderDateTime&gt;**](HeaderDateTime.md) | The date and time when the request was created. Although this is an array, only 1 entry is allowed. |  [optional]
**response** | [**Response**](Response.md) |  |  [optional]
**requestor** | **String** | Identifies the agent which sent this message. |  [optional]
**businessUnit** | [**List&lt;BusinessUnitCommonData&gt;**](BusinessUnitCommonData.md) | Business units for which prices and promotions should be determined. Until client API version 5.0, exactly 1 entry is allowed. Starting with client  API version 6.0, maximal 2 entries are allowed. If there is more than 1 business unit, one of them must be a distribution chain. In case it is a distribution chain, there must be a pipe (&#x27;|&#x27;) as delimiter to split between sales organisation and distribution channel. | 
**workstationID** | [**WorkstationIDCommonData**](WorkstationIDCommonData.md) |  |  [optional]
**requestedLanguage** | **String** | Uppercase ISO code of the language in which language-dependent texts like a promotion description should be returned. Uppercase representation. RequestedLanguage is not applied in conjunction with RequestedMultiLanguage. If not set, any language found will be used with Client API version 1.0. As of Client API version 2.0, use RequestedMultiLanguage instead.  |  [optional]
**requestedMultiLanguage** | **List&lt;String&gt;** | Uppercase ISO codes of the languages in which language dependent-texts like a promotion description should be returned. Not to be used in conjunction with RequestedLanguage. Requires at least API version 2.0. If not set, all languages found will be used. |  [optional]
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**masterDataSourceSystemID** | **String** | Part of the compound key for items, item hierarchy nodes and business units. Each price record and each promotion refers to exactly one master data source system. Supported with Client API version 2.1 or higher. This field is required when using the cloud version of the price calculation.  | 
**actionCode** | [**ActionCodeEnum**](#ActionCodeEnum) | Describes what to do with the provided data. | 
**messageType** | [**MessageTypeEnum**](#MessageTypeEnum) | Defines whether this is a calculation request or a calculation response if the consumer only sends type \&quot;Request\&quot;. | 

<a name="ActionCodeEnum"></a>
## Enum: ActionCodeEnum
Name | Value
---- | -----
CALCULATE | &quot;Calculate&quot;

<a name="MessageTypeEnum"></a>
## Enum: MessageTypeEnum
Name | Value
---- | -----
REQUEST | &quot;Request&quot;
RESPONSE | &quot;Response&quot;

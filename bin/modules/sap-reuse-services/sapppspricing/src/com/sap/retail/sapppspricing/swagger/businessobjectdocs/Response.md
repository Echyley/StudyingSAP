# Response

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**any** | **List&lt;Object&gt;** | This is currently not supported. |  [optional]
**requestID** | **String** | Identfier of the request to which this response relates. | 
**responseTimestamp** | **String** | Timestamp of the request to which this response relates. |  [optional]
**businessError** | [**List&lt;BusinessError&gt;**](BusinessError.md) | The list of business errors created during the processing of the corresponding request. |  [optional]
**responseCode** | [**ResponseCodeEnum**](#ResponseCodeEnum) | Indicates if the message was successful (OK) or not (Rejected). |  [optional]

<a name="ResponseCodeEnum"></a>
## Enum: ResponseCodeEnum
Name | Value
---- | -----
REJECTED | &quot;Rejected&quot;
OK | &quot;OK&quot;

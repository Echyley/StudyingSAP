# CalculationApi

All URIs are relative to *//serverRoot/*

Method | HTTP request | Description
------------- | ------------- | -------------
[**calculateViaRestWithTenant**](CalculationApi.md#calculateViaRestWithTenant) | **POST** /restapi/{tenantName} | Calculates a PriceCalculateTransaction

<a name="calculateViaRestWithTenant"></a>
# **calculateViaRestWithTenant**
> calculateViaRestWithTenant(tenantName)

Calculates a PriceCalculateTransaction

Determines regular prices by applying promotional rules for the provided list of items.

### Example
```java
// Import classes:
//import com.sap.retail.opps.v1.ApiException;
//import com.sap.retail.opps.v1.api.CalculationApi;


CalculationApi apiInstance = new CalculationApi();
String tenantName = "tenantName_example"; // String | The name of the subdomain in which the service instance/subscription is created. **For the sandbox environment, enter 'oppsapihub'.**
try {
    apiInstance.calculateViaRestWithTenant(tenantName);
} catch (ApiException e) {
    System.err.println("Exception when calling CalculationApi#calculateViaRestWithTenant");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenantName** | **String**| The name of the subdomain in which the service instance/subscription is created. **For the sandbox environment, enter &#x27;oppsapihub&#x27;.** |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined


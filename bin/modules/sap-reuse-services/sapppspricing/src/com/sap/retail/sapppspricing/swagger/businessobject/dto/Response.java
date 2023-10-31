/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Holds response specific information of an ARTSHeader.
 */
@Schema(description = "Holds response specific information of an ARTSHeader.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class Response {
  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("RequestID")
  private String requestID = null;

  @JsonProperty("ResponseTimestamp")
  private String responseTimestamp = null;

  @JsonProperty("BusinessError")
  private List<BusinessError> businessError = null;

  /**
   * Indicates if the message was successful (OK) or not (Rejected).
   */
  public enum ResponseCodeEnum {
    REJECTED("Rejected"),
    OK("OK");

    private String value;

    ResponseCodeEnum(String value) {
      this.value = value;
    }
    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
    @JsonCreator
    public static ResponseCodeEnum fromValue(String input) {
      for (ResponseCodeEnum b : ResponseCodeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("ResponseCode")
  private ResponseCodeEnum responseCode = null;

  public Response any(List<Object> any) {
    this.any = any;
    return this;
  }

  public Response addAnyItem(Object anyItem) {
    if (this.any == null) {
      this.any = new ArrayList<Object>();
    }
    this.any.add(anyItem);
    return this;
  }

   /**
   * This is currently not supported.
   * @return any
  **/
  @Schema(description = "This is currently not supported.")
  public List<Object> getAny() {
    return any;
  }

  public void setAny(List<Object> any) {
    this.any = any;
  }

  public Response requestID(String requestID) {
    this.requestID = requestID;
    return this;
  }

   /**
   * Identfier of the request to which this response relates.
   * @return requestID
  **/
  @Schema(required = true, description = "Identfier of the request to which this response relates.")
  public String getRequestID() {
    return requestID;
  }

  public void setRequestID(String requestID) {
    this.requestID = requestID;
  }

  public Response responseTimestamp(String responseTimestamp) {
    this.responseTimestamp = responseTimestamp;
    return this;
  }

   /**
   * Timestamp of the request to which this response relates.
   * @return responseTimestamp
  **/
  @Schema(description = "Timestamp of the request to which this response relates.")
  public String getResponseTimestamp() {
    return responseTimestamp;
  }

  public void setResponseTimestamp(String responseTimestamp) {
    this.responseTimestamp = responseTimestamp;
  }

  public Response businessError(List<BusinessError> businessError) {
    this.businessError = businessError;
    return this;
  }

  public Response addBusinessErrorItem(BusinessError businessErrorItem) {
    if (this.businessError == null) {
      this.businessError = new ArrayList<BusinessError>();
    }
    this.businessError.add(businessErrorItem);
    return this;
  }

   /**
   * The list of business errors created during the processing of the corresponding request.
   * @return businessError
  **/
  @Schema(description = "The list of business errors created during the processing of the corresponding request.")
  public List<BusinessError> getBusinessError() {
    return businessError;
  }

  public void setBusinessError(List<BusinessError> businessError) {
    this.businessError = businessError;
  }

  public Response responseCode(ResponseCodeEnum responseCode) {
    this.responseCode = responseCode;
    return this;
  }

   /**
   * Indicates if the message was successful (OK) or not (Rejected).
   * @return responseCode
  **/
  @Schema(description = "Indicates if the message was successful (OK) or not (Rejected).")
  public ResponseCodeEnum getResponseCode() {
    return responseCode;
  }

  public void setResponseCode(ResponseCodeEnum responseCode) {
    this.responseCode = responseCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Response response = (Response) o;
    return Objects.equals(this.any, response.any) &&
        Objects.equals(this.requestID, response.requestID) &&
        Objects.equals(this.responseTimestamp, response.responseTimestamp) &&
        Objects.equals(this.businessError, response.businessError) &&
        Objects.equals(this.responseCode, response.responseCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, requestID, responseTimestamp, businessError, responseCode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Response {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    requestID: ").append(toIndentedString(requestID)).append("\n");
    sb.append("    responseTimestamp: ").append(toIndentedString(responseTimestamp)).append("\n");
    sb.append("    businessError: ").append(toIndentedString(businessError)).append("\n");
    sb.append("    responseCode: ").append(toIndentedString(responseCode)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

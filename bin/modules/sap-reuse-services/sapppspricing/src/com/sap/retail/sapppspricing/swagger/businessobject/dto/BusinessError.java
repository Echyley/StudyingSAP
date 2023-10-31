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
 * Additional information for the consumer about issues during the price calculation.
 */
@Schema(description = "Additional information for the consumer about issues during the price calculation.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class BusinessError {
  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("ErrorID")
  private String errorID = null;

  @JsonProperty("Description")
  private DescriptionCommonData description = null;

  /**
   * Issues with severity error or higher lead to an HTTP response code 400.
   */
  public enum SeverityEnum {
    INFORMATION("Information"),
    WARNING("Warning"),
    ERROR("Error");

    private String value;

    SeverityEnum(String value) {
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
    public static SeverityEnum fromValue(String input) {
      for (SeverityEnum b : SeverityEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Severity")
  private SeverityEnum severity = null;

  @JsonProperty("ErrorSerialNumber")
  private Integer errorSerialNumber = null;

  @JsonProperty("LineItemSequenceNumber")
  private Integer lineItemSequenceNumber = null;

  @JsonProperty("MessageVariables")
  private MessageVariables messageVariables = null;

  public BusinessError any(List<Object> any) {
    this.any = any;
    return this;
  }

  public BusinessError addAnyItem(Object anyItem) {
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

  public BusinessError errorID(String errorID) {
    this.errorID = errorID;
    return this;
  }

   /**
   * Can be used for client side checks. For a complete list of possible error codes see the Troubleshooting section in the administriation guide for SAP Omnichannel Promotion Pricing under https://help.sap.com/viewer/7c87270e23c64c2aa922ce297a6df23d/Cloud/en-US/16b90f4819b546f39f29b664d6259641.html 
   * @return errorID
  **/
  @Schema(description = "Can be used for client side checks. For a complete list of possible error codes see the Troubleshooting section in the administriation guide for SAP Omnichannel Promotion Pricing under https://help.sap.com/viewer/7c87270e23c64c2aa922ce297a6df23d/Cloud/en-US/16b90f4819b546f39f29b664d6259641.html ")
  public String getErrorID() {
    return errorID;
  }

  public void setErrorID(String errorID) {
    this.errorID = errorID;
  }

  public BusinessError description(DescriptionCommonData description) {
    this.description = description;
    return this;
  }

   /**
   * Get description
   * @return description
  **/
  @Schema(description = "")
  public DescriptionCommonData getDescription() {
    return description;
  }

  public void setDescription(DescriptionCommonData description) {
    this.description = description;
  }

  public BusinessError severity(SeverityEnum severity) {
    this.severity = severity;
    return this;
  }

   /**
   * Issues with severity error or higher lead to an HTTP response code 400.
   * @return severity
  **/
  @Schema(description = "Issues with severity error or higher lead to an HTTP response code 400.")
  public SeverityEnum getSeverity() {
    return severity;
  }

  public void setSeverity(SeverityEnum severity) {
    this.severity = severity;
  }

  public BusinessError errorSerialNumber(Integer errorSerialNumber) {
    this.errorSerialNumber = errorSerialNumber;
    return this;
  }

   /**
   * The error serial number, unique for every single business error within one specific Error Number. Decimal representation of a 16 bit integer value. 
   * minimum: 0
   * maximum: 32767
   * @return errorSerialNumber
  **/
  @Schema(description = "The error serial number, unique for every single business error within one specific Error Number. Decimal representation of a 16 bit integer value. ")
  public Integer getErrorSerialNumber() {
    return errorSerialNumber;
  }

  public void setErrorSerialNumber(Integer errorSerialNumber) {
    this.errorSerialNumber = errorSerialNumber;
  }

  public BusinessError lineItemSequenceNumber(Integer lineItemSequenceNumber) {
    this.lineItemSequenceNumber = lineItemSequenceNumber;
    return this;
  }

   /**
   * Identifies the Sequence Number of the line item that caused the business error.
   * minimum: 0
   * maximum: 32767
   * @return lineItemSequenceNumber
  **/
  @Schema(description = "Identifies the Sequence Number of the line item that caused the business error.")
  public Integer getLineItemSequenceNumber() {
    return lineItemSequenceNumber;
  }

  public void setLineItemSequenceNumber(Integer lineItemSequenceNumber) {
    this.lineItemSequenceNumber = lineItemSequenceNumber;
  }

  public BusinessError messageVariables(MessageVariables messageVariables) {
    this.messageVariables = messageVariables;
    return this;
  }

   /**
   * Get messageVariables
   * @return messageVariables
  **/
  @Schema(description = "")
  public MessageVariables getMessageVariables() {
    return messageVariables;
  }

  public void setMessageVariables(MessageVariables messageVariables) {
    this.messageVariables = messageVariables;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BusinessError businessError = (BusinessError) o;
    return Objects.equals(this.any, businessError.any) &&
        Objects.equals(this.errorID, businessError.errorID) &&
        Objects.equals(this.description, businessError.description) &&
        Objects.equals(this.severity, businessError.severity) &&
        Objects.equals(this.errorSerialNumber, businessError.errorSerialNumber) &&
        Objects.equals(this.lineItemSequenceNumber, businessError.lineItemSequenceNumber) &&
        Objects.equals(this.messageVariables, businessError.messageVariables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, errorID, description, severity, errorSerialNumber, lineItemSequenceNumber, messageVariables);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BusinessError {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    errorID: ").append(toIndentedString(errorID)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    severity: ").append(toIndentedString(severity)).append("\n");
    sb.append("    errorSerialNumber: ").append(toIndentedString(errorSerialNumber)).append("\n");
    sb.append("    lineItemSequenceNumber: ").append(toIndentedString(lineItemSequenceNumber)).append("\n");
    sb.append("    messageVariables: ").append(toIndentedString(messageVariables)).append("\n");
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

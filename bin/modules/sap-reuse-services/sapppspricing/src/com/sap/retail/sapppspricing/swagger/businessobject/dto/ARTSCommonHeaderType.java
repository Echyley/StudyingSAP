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
 * Contains basic information for the request or the response.
 */
@Schema(description = "Contains basic information for the request or the response.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class ARTSCommonHeaderType {
  @JsonProperty("MessageID")
  private MessageID messageID = null;

  @JsonProperty("DateTime")
  private List<HeaderDateTime> dateTime = null;

  @JsonProperty("Response")
  private Response response = null;

  @JsonProperty("Requestor")
  private String requestor = null;

  @JsonProperty("BusinessUnit")
  private List<BusinessUnitCommonData> businessUnit = new ArrayList<BusinessUnitCommonData>();

  @JsonProperty("WorkstationID")
  private WorkstationIDCommonData workstationID = null;

  @JsonProperty("RequestedLanguage")
  private String requestedLanguage = null;

  @JsonProperty("RequestedMultiLanguage")
  private List<String> requestedMultiLanguage = null;

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("MasterDataSourceSystemID")
  private String masterDataSourceSystemID = null;

  /**
   * Describes what to do with the provided data.
   */
  public enum ActionCodeEnum {
    CALCULATE("Calculate");

    private String value;

    ActionCodeEnum(String value) {
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
    public static ActionCodeEnum fromValue(String input) {
      for (ActionCodeEnum b : ActionCodeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("ActionCode")
  private ActionCodeEnum actionCode = null;

  /**
   * Defines whether this is a calculation request or a calculation response if the consumer only sends type \&quot;Request\&quot;.
   */
  public enum MessageTypeEnum {
    REQUEST("Request"),
    RESPONSE("Response");

    private String value;

    MessageTypeEnum(String value) {
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
    public static MessageTypeEnum fromValue(String input) {
      for (MessageTypeEnum b : MessageTypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("MessageType")
  private MessageTypeEnum messageType = null;

  public ARTSCommonHeaderType messageID(MessageID messageID) {
    this.messageID = messageID;
    return this;
  }

   /**
   * Get messageID
   * @return messageID
  **/
  @Schema(required = true, description = "")
  public MessageID getMessageID() {
    return messageID;
  }

  public void setMessageID(MessageID messageID) {
    this.messageID = messageID;
  }

  public ARTSCommonHeaderType dateTime(List<HeaderDateTime> dateTime) {
    this.dateTime = dateTime;
    return this;
  }

  public ARTSCommonHeaderType addDateTimeItem(HeaderDateTime dateTimeItem) {
    if (this.dateTime == null) {
      this.dateTime = new ArrayList<HeaderDateTime>();
    }
    this.dateTime.add(dateTimeItem);
    return this;
  }

   /**
   * The date and time when the request was created. Although this is an array, only 1 entry is allowed.
   * @return dateTime
  **/
  @Schema(description = "The date and time when the request was created. Although this is an array, only 1 entry is allowed.")
  public List<HeaderDateTime> getDateTime() {
    return dateTime;
  }

  public void setDateTime(List<HeaderDateTime> dateTime) {
    this.dateTime = dateTime;
  }

  public ARTSCommonHeaderType response(Response response) {
    this.response = response;
    return this;
  }

   /**
   * Get response
   * @return response
  **/
  @Schema(description = "")
  public Response getResponse() {
    return response;
  }

  public void setResponse(Response response) {
    this.response = response;
  }

  public ARTSCommonHeaderType requestor(String requestor) {
    this.requestor = requestor;
    return this;
  }

   /**
   * Identifies the agent which sent this message.
   * @return requestor
  **/
  @Schema(description = "Identifies the agent which sent this message.")
  public String getRequestor() {
    return requestor;
  }

  public void setRequestor(String requestor) {
    this.requestor = requestor;
  }

  public ARTSCommonHeaderType businessUnit(List<BusinessUnitCommonData> businessUnit) {
    this.businessUnit = businessUnit;
    return this;
  }

  public ARTSCommonHeaderType addBusinessUnitItem(BusinessUnitCommonData businessUnitItem) {
    this.businessUnit.add(businessUnitItem);
    return this;
  }

   /**
   * Business units for which prices and promotions should be determined. Until client API version 5.0, exactly 1 entry is allowed. Starting with client  API version 6.0, maximal 2 entries are allowed. If there is more than 1 business unit, one of them must be a distribution chain. In case it is a distribution chain, there must be a pipe (&#x27;|&#x27;) as delimiter to split between sales organisation and distribution channel.
   * @return businessUnit
  **/
  @Schema(required = true, description = "Business units for which prices and promotions should be determined. Until client API version 5.0, exactly 1 entry is allowed. Starting with client  API version 6.0, maximal 2 entries are allowed. If there is more than 1 business unit, one of them must be a distribution chain. In case it is a distribution chain, there must be a pipe ('|') as delimiter to split between sales organisation and distribution channel.")
  public List<BusinessUnitCommonData> getBusinessUnit() {
    return businessUnit;
  }

  public void setBusinessUnit(List<BusinessUnitCommonData> businessUnit) {
    this.businessUnit = businessUnit;
  }

  public ARTSCommonHeaderType workstationID(WorkstationIDCommonData workstationID) {
    this.workstationID = workstationID;
    return this;
  }

   /**
   * Get workstationID
   * @return workstationID
  **/
  @Schema(description = "")
  public WorkstationIDCommonData getWorkstationID() {
    return workstationID;
  }

  public void setWorkstationID(WorkstationIDCommonData workstationID) {
    this.workstationID = workstationID;
  }

  public ARTSCommonHeaderType requestedLanguage(String requestedLanguage) {
    this.requestedLanguage = requestedLanguage;
    return this;
  }

   /**
   * Uppercase ISO code of the language in which language-dependent texts like a promotion description should be returned. Uppercase representation. RequestedLanguage is not applied in conjunction with RequestedMultiLanguage. If not set, any language found will be used with Client API version 1.0. As of Client API version 2.0, use RequestedMultiLanguage instead. 
   * @return requestedLanguage
  **/
  @Schema(description = "Uppercase ISO code of the language in which language-dependent texts like a promotion description should be returned. Uppercase representation. RequestedLanguage is not applied in conjunction with RequestedMultiLanguage. If not set, any language found will be used with Client API version 1.0. As of Client API version 2.0, use RequestedMultiLanguage instead. ")
  public String getRequestedLanguage() {
    return requestedLanguage;
  }

  public void setRequestedLanguage(String requestedLanguage) {
    this.requestedLanguage = requestedLanguage;
  }

  public ARTSCommonHeaderType requestedMultiLanguage(List<String> requestedMultiLanguage) {
    this.requestedMultiLanguage = requestedMultiLanguage;
    return this;
  }

  public ARTSCommonHeaderType addRequestedMultiLanguageItem(String requestedMultiLanguageItem) {
    if (this.requestedMultiLanguage == null) {
      this.requestedMultiLanguage = new ArrayList<String>();
    }
    this.requestedMultiLanguage.add(requestedMultiLanguageItem);
    return this;
  }

   /**
   * Uppercase ISO codes of the languages in which language dependent-texts like a promotion description should be returned. Not to be used in conjunction with RequestedLanguage. Requires at least API version 2.0. If not set, all languages found will be used.
   * @return requestedMultiLanguage
  **/
  @Schema(description = "Uppercase ISO codes of the languages in which language dependent-texts like a promotion description should be returned. Not to be used in conjunction with RequestedLanguage. Requires at least API version 2.0. If not set, all languages found will be used.")
  public List<String> getRequestedMultiLanguage() {
    return requestedMultiLanguage;
  }

  public void setRequestedMultiLanguage(List<String> requestedMultiLanguage) {
    this.requestedMultiLanguage = requestedMultiLanguage;
  }

  public ARTSCommonHeaderType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public ARTSCommonHeaderType addAnyItem(Object anyItem) {
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

  public ARTSCommonHeaderType masterDataSourceSystemID(String masterDataSourceSystemID) {
    this.masterDataSourceSystemID = masterDataSourceSystemID;
    return this;
  }

   /**
   * Part of the compound key for items, item hierarchy nodes and business units. Each price record and each promotion refers to exactly one master data source system. Supported with Client API version 2.1 or higher. This field is required when using the cloud version of the price calculation. 
   * @return masterDataSourceSystemID
  **/
  @Schema(required = true, description = "Part of the compound key for items, item hierarchy nodes and business units. Each price record and each promotion refers to exactly one master data source system. Supported with Client API version 2.1 or higher. This field is required when using the cloud version of the price calculation. ")
  public String getMasterDataSourceSystemID() {
    return masterDataSourceSystemID;
  }

  public void setMasterDataSourceSystemID(String masterDataSourceSystemID) {
    this.masterDataSourceSystemID = masterDataSourceSystemID;
  }

  public ARTSCommonHeaderType actionCode(ActionCodeEnum actionCode) {
    this.actionCode = actionCode;
    return this;
  }

   /**
   * Describes what to do with the provided data.
   * @return actionCode
  **/
  @Schema(required = true, description = "Describes what to do with the provided data.")
  public ActionCodeEnum getActionCode() {
    return actionCode;
  }

  public void setActionCode(ActionCodeEnum actionCode) {
    this.actionCode = actionCode;
  }

  public ARTSCommonHeaderType messageType(MessageTypeEnum messageType) {
    this.messageType = messageType;
    return this;
  }

   /**
   * Defines whether this is a calculation request or a calculation response if the consumer only sends type \&quot;Request\&quot;.
   * @return messageType
  **/
  @Schema(required = true, description = "Defines whether this is a calculation request or a calculation response if the consumer only sends type \"Request\".")
  public MessageTypeEnum getMessageType() {
    return messageType;
  }

  public void setMessageType(MessageTypeEnum messageType) {
    this.messageType = messageType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ARTSCommonHeaderType arTSCommonHeaderType = (ARTSCommonHeaderType) o;
    return Objects.equals(this.messageID, arTSCommonHeaderType.messageID) &&
        Objects.equals(this.dateTime, arTSCommonHeaderType.dateTime) &&
        Objects.equals(this.response, arTSCommonHeaderType.response) &&
        Objects.equals(this.requestor, arTSCommonHeaderType.requestor) &&
        Objects.equals(this.businessUnit, arTSCommonHeaderType.businessUnit) &&
        Objects.equals(this.workstationID, arTSCommonHeaderType.workstationID) &&
        Objects.equals(this.requestedLanguage, arTSCommonHeaderType.requestedLanguage) &&
        Objects.equals(this.requestedMultiLanguage, arTSCommonHeaderType.requestedMultiLanguage) &&
        Objects.equals(this.any, arTSCommonHeaderType.any) &&
        Objects.equals(this.masterDataSourceSystemID, arTSCommonHeaderType.masterDataSourceSystemID) &&
        Objects.equals(this.actionCode, arTSCommonHeaderType.actionCode) &&
        Objects.equals(this.messageType, arTSCommonHeaderType.messageType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(messageID, dateTime, response, requestor, businessUnit, workstationID, requestedLanguage, requestedMultiLanguage, any, masterDataSourceSystemID, actionCode, messageType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ARTSCommonHeaderType {\n");
    
    sb.append("    messageID: ").append(toIndentedString(messageID)).append("\n");
    sb.append("    dateTime: ").append(toIndentedString(dateTime)).append("\n");
    sb.append("    response: ").append(toIndentedString(response)).append("\n");
    sb.append("    requestor: ").append(toIndentedString(requestor)).append("\n");
    sb.append("    businessUnit: ").append(toIndentedString(businessUnit)).append("\n");
    sb.append("    workstationID: ").append(toIndentedString(workstationID)).append("\n");
    sb.append("    requestedLanguage: ").append(toIndentedString(requestedLanguage)).append("\n");
    sb.append("    requestedMultiLanguage: ").append(toIndentedString(requestedMultiLanguage)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    masterDataSourceSystemID: ").append(toIndentedString(masterDataSourceSystemID)).append("\n");
    sb.append("    actionCode: ").append(toIndentedString(actionCode)).append("\n");
    sb.append("    messageType: ").append(toIndentedString(messageType)).append("\n");
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

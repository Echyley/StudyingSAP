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
 * Information provided by the client for a manual promotion that is to be calculated. Contains eligibility relevant information and protentially also the specification of a discount (depending on the privilege type). 
 */
@Schema(description = "Information provided by the client for a manual promotion that is to be calculated. Contains eligibility relevant information and protentially also the specification of a discount (depending on the privilege type). ")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PromotionManualTriggerType {
  @JsonProperty("ManualTriggerSequenceNumber")
  private Integer manualTriggerSequenceNumber = null;

  @JsonProperty("ManualTriggerType")
  private String manualTriggerType = null;

  @JsonProperty("ManualTriggerValue")
  private String manualTriggerValue = null;

  /**
   * Specifies the type of the price adjustment. Valid values are: Absolute discount (RS), percentage discount (RP), new price (PS) or the reduction specified in the promotion master data (AM). 
   */
  public enum PrivilegeTypeEnum {
    AM("AM"),
    RS("RS"),
    RP("RP"),
    PS("PS");

    private String value;

    PrivilegeTypeEnum(String value) {
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
    public static PrivilegeTypeEnum fromValue(String input) {
      for (PrivilegeTypeEnum b : PrivilegeTypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("PrivilegeType")
  private PrivilegeTypeEnum privilegeType = null;

  @JsonProperty("PrivilegeValue")
  private AmountCommonData privilegeValue = null;

  @JsonProperty("ManualTriggerSequenceAddend")
  private Long manualTriggerSequenceAddend = null;

  @JsonProperty("any")
  private List<Object> any = null;

  public PromotionManualTriggerType manualTriggerSequenceNumber(Integer manualTriggerSequenceNumber) {
    this.manualTriggerSequenceNumber = manualTriggerSequenceNumber;
    return this;
  }

   /**
   * Identifies the manual trigger per line item. Must be a positive small integer.
   * minimum: 0
   * maximum: 32767
   * @return manualTriggerSequenceNumber
  **/
  @Schema(required = true, description = "Identifies the manual trigger per line item. Must be a positive small integer.")
  public Integer getManualTriggerSequenceNumber() {
    return manualTriggerSequenceNumber;
  }

  public void setManualTriggerSequenceNumber(Integer manualTriggerSequenceNumber) {
    this.manualTriggerSequenceNumber = manualTriggerSequenceNumber;
  }

  public PromotionManualTriggerType manualTriggerType(String manualTriggerType) {
    this.manualTriggerType = manualTriggerType;
    return this;
  }

   /**
   * Type of the manual trigger as defined in the promotion master data. This type is to be set by the client and used to determine relevant promotions.
   * @return manualTriggerType
  **/
  @Schema(required = true, description = "Type of the manual trigger as defined in the promotion master data. This type is to be set by the client and used to determine relevant promotions.")
  public String getManualTriggerType() {
    return manualTriggerType;
  }

  public void setManualTriggerType(String manualTriggerType) {
    this.manualTriggerType = manualTriggerType;
  }

  public PromotionManualTriggerType manualTriggerValue(String manualTriggerValue) {
    this.manualTriggerValue = manualTriggerValue;
    return this;
  }

   /**
   * Value for the manual trigger as defined in the promotion master data. This value is to be set by the client and used to determine relevant promotions.
   * @return manualTriggerValue
  **/
  @Schema(required = true, description = "Value for the manual trigger as defined in the promotion master data. This value is to be set by the client and used to determine relevant promotions.")
  public String getManualTriggerValue() {
    return manualTriggerValue;
  }

  public void setManualTriggerValue(String manualTriggerValue) {
    this.manualTriggerValue = manualTriggerValue;
  }

  public PromotionManualTriggerType privilegeType(PrivilegeTypeEnum privilegeType) {
    this.privilegeType = privilegeType;
    return this;
  }

   /**
   * Specifies the type of the price adjustment. Valid values are: Absolute discount (RS), percentage discount (RP), new price (PS) or the reduction specified in the promotion master data (AM). 
   * @return privilegeType
  **/
  @Schema(required = true, description = "Specifies the type of the price adjustment. Valid values are: Absolute discount (RS), percentage discount (RP), new price (PS) or the reduction specified in the promotion master data (AM). ")
  public PrivilegeTypeEnum getPrivilegeType() {
    return privilegeType;
  }

  public void setPrivilegeType(PrivilegeTypeEnum privilegeType) {
    this.privilegeType = privilegeType;
  }

  public PromotionManualTriggerType privilegeValue(AmountCommonData privilegeValue) {
    this.privilegeValue = privilegeValue;
    return this;
  }

   /**
   * Get privilegeValue
   * @return privilegeValue
  **/
  @Schema(required = true, description = "")
  public AmountCommonData getPrivilegeValue() {
    return privilegeValue;
  }

  public void setPrivilegeValue(AmountCommonData privilegeValue) {
    this.privilegeValue = privilegeValue;
  }

  public PromotionManualTriggerType manualTriggerSequenceAddend(Long manualTriggerSequenceAddend) {
    this.manualTriggerSequenceAddend = manualTriggerSequenceAddend;
    return this;
  }

   /**
   * A value that is to be added to the sequence of the promotion price derivation rule. This allows to apply multiple manual discounts with the same price derivation rule. 
   * minimum: 0
   * @return manualTriggerSequenceAddend
  **/
  @Schema(required = true, description = "A value that is to be added to the sequence of the promotion price derivation rule. This allows to apply multiple manual discounts with the same price derivation rule. ")
  public Long getManualTriggerSequenceAddend() {
    return manualTriggerSequenceAddend;
  }

  public void setManualTriggerSequenceAddend(Long manualTriggerSequenceAddend) {
    this.manualTriggerSequenceAddend = manualTriggerSequenceAddend;
  }

  public PromotionManualTriggerType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PromotionManualTriggerType addAnyItem(Object anyItem) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PromotionManualTriggerType promotionManualTriggerType = (PromotionManualTriggerType) o;
    return Objects.equals(this.manualTriggerSequenceNumber, promotionManualTriggerType.manualTriggerSequenceNumber) &&
        Objects.equals(this.manualTriggerType, promotionManualTriggerType.manualTriggerType) &&
        Objects.equals(this.manualTriggerValue, promotionManualTriggerType.manualTriggerValue) &&
        Objects.equals(this.privilegeType, promotionManualTriggerType.privilegeType) &&
        Objects.equals(this.privilegeValue, promotionManualTriggerType.privilegeValue) &&
        Objects.equals(this.manualTriggerSequenceAddend, promotionManualTriggerType.manualTriggerSequenceAddend) &&
        Objects.equals(this.any, promotionManualTriggerType.any);
  }

  @Override
  public int hashCode() {
    return Objects.hash(manualTriggerSequenceNumber, manualTriggerType, manualTriggerValue, privilegeType, privilegeValue, manualTriggerSequenceAddend, any);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PromotionManualTriggerType {\n");
    
    sb.append("    manualTriggerSequenceNumber: ").append(toIndentedString(manualTriggerSequenceNumber)).append("\n");
    sb.append("    manualTriggerType: ").append(toIndentedString(manualTriggerType)).append("\n");
    sb.append("    manualTriggerValue: ").append(toIndentedString(manualTriggerValue)).append("\n");
    sb.append("    privilegeType: ").append(toIndentedString(privilegeType)).append("\n");
    sb.append("    privilegeValue: ").append(toIndentedString(privilegeValue)).append("\n");
    sb.append("    manualTriggerSequenceAddend: ").append(toIndentedString(manualTriggerSequenceAddend)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
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

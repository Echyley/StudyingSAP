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
 * Contains information about the consumed eligibility if this was a coupon eligibility.
 */
@Schema(description = "Contains information about the consumed eligibility if this was a coupon eligibility.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PriceDerivationRuleEligibility {
  @JsonProperty("ReferenceID")
  private String referenceID = null;

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("ReferenceSequenceNumber")
  private Integer referenceSequenceNumber = null;

  /**
   * Gets or Sets type
   */
  public enum TypeEnum {
    STORECOUPON("StoreCoupon");

    private String value;

    TypeEnum(String value) {
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
    public static TypeEnum fromValue(String input) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Type")
  private TypeEnum type = null;

  public PriceDerivationRuleEligibility referenceID(String referenceID) {
    this.referenceID = referenceID;
    return this;
  }

   /**
   * Contains the coupon code for a coupon eligibility.
   * @return referenceID
  **/
  @Schema(description = "Contains the coupon code for a coupon eligibility.")
  public String getReferenceID() {
    return referenceID;
  }

  public void setReferenceID(String referenceID) {
    this.referenceID = referenceID;
  }

  public PriceDerivationRuleEligibility any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PriceDerivationRuleEligibility addAnyItem(Object anyItem) {
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

  public PriceDerivationRuleEligibility referenceSequenceNumber(Integer referenceSequenceNumber) {
    this.referenceSequenceNumber = referenceSequenceNumber;
    return this;
  }

   /**
   * The sequence number of the line item containing the coupon for a coupon eligibility.
   * minimum: 0
   * maximum: 32767
   * @return referenceSequenceNumber
  **/
  @Schema(description = "The sequence number of the line item containing the coupon for a coupon eligibility.")
  public Integer getReferenceSequenceNumber() {
    return referenceSequenceNumber;
  }

  public void setReferenceSequenceNumber(Integer referenceSequenceNumber) {
    this.referenceSequenceNumber = referenceSequenceNumber;
  }

  public PriceDerivationRuleEligibility type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  @Schema(example = "StoreCoupon", required = true, description = "")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceDerivationRuleEligibility priceDerivationRuleEligibility = (PriceDerivationRuleEligibility) o;
    return Objects.equals(this.referenceID, priceDerivationRuleEligibility.referenceID) &&
        Objects.equals(this.any, priceDerivationRuleEligibility.any) &&
        Objects.equals(this.referenceSequenceNumber, priceDerivationRuleEligibility.referenceSequenceNumber) &&
        Objects.equals(this.type, priceDerivationRuleEligibility.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(referenceID, any, referenceSequenceNumber, type);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceDerivationRuleEligibility {\n");
    
    sb.append("    referenceID: ").append(toIndentedString(referenceID)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    referenceSequenceNumber: ").append(toIndentedString(referenceSequenceNumber)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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

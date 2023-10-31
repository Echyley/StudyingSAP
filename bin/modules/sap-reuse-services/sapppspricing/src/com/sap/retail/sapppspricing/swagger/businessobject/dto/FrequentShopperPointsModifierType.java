/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Information about changes to loyalty points triggered by applied price rules.
 */
@Schema(description = "Information about changes to loyalty points triggered by applied price rules.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class FrequentShopperPointsModifierType {
  @JsonProperty("PromotionID")
  private String promotionID = null;

  @JsonProperty("PointsAwarded")
  private List<PointsAwarded> pointsAwarded = null;

  @JsonProperty("ManualTriggerSequenceNumber")
  private Integer manualTriggerSequenceNumber = null;

  @JsonProperty("PointsAwardedAmount")
  private AmountCommonData pointsAwardedAmount = null;

  @JsonProperty("ComputationBaseAmount")
  private AmountCommonData computationBaseAmount = null;

  @JsonProperty("ExternalSystemOriginatorFlag")
  private Boolean externalSystemOriginatorFlag = null;

  @JsonProperty("PriceDerivationRule")
  private PriceDerivationRuleBase priceDerivationRule = null;

  @JsonProperty("any")
  private List<Object> any = null;

  /**
   * Type code of the loyalty reward
   */
  public enum TypeCodeEnum {
    AWARD("Award");

    private String value;

    TypeCodeEnum(String value) {
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
    public static TypeCodeEnum fromValue(String input) {
      for (TypeCodeEnum b : TypeCodeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("TypeCode")
  private TypeCodeEnum typeCode = null;

  @JsonProperty("SequenceNumber")
  private Integer sequenceNumber = null;

  @JsonProperty("AppliedQuantity")
  private BigDecimal appliedQuantity = null;

  @JsonProperty("ItemLink")
  private Integer itemLink = null;

  public FrequentShopperPointsModifierType promotionID(String promotionID) {
    this.promotionID = promotionID;
    return this;
  }

   /**
   * Identifies the promotion that triggered the loyalty reward. Decimal representation of a 64 bit integer value.
   * @return promotionID
  **/
  @Schema(required = true, description = "Identifies the promotion that triggered the loyalty reward. Decimal representation of a 64 bit integer value.")
  public String getPromotionID() {
    return promotionID;
  }

  public void setPromotionID(String promotionID) {
    this.promotionID = promotionID;
  }

  public FrequentShopperPointsModifierType pointsAwarded(List<PointsAwarded> pointsAwarded) {
    this.pointsAwarded = pointsAwarded;
    return this;
  }

  public FrequentShopperPointsModifierType addPointsAwardedItem(PointsAwarded pointsAwardedItem) {
    if (this.pointsAwarded == null) {
      this.pointsAwarded = new ArrayList<PointsAwarded>();
    }
    this.pointsAwarded.add(pointsAwardedItem);
    return this;
  }

   /**
   * The number of points awarded by the line item.
   * @return pointsAwarded
  **/
  @Schema(description = "The number of points awarded by the line item.")
  public List<PointsAwarded> getPointsAwarded() {
    return pointsAwarded;
  }

  public void setPointsAwarded(List<PointsAwarded> pointsAwarded) {
    this.pointsAwarded = pointsAwarded;
  }

  public FrequentShopperPointsModifierType manualTriggerSequenceNumber(Integer manualTriggerSequenceNumber) {
    this.manualTriggerSequenceNumber = manualTriggerSequenceNumber;
    return this;
  }

   /**
   * The sequence number for the trigger to be assigned to a single LoyaltyReward. It references the ManualTriggerSequenceNumber of PromotionManualTriggerType that triggered this reward. 
   * minimum: 0
   * maximum: 32767
   * @return manualTriggerSequenceNumber
  **/
  @Schema(description = "The sequence number for the trigger to be assigned to a single LoyaltyReward. It references the ManualTriggerSequenceNumber of PromotionManualTriggerType that triggered this reward. ")
  public Integer getManualTriggerSequenceNumber() {
    return manualTriggerSequenceNumber;
  }

  public void setManualTriggerSequenceNumber(Integer manualTriggerSequenceNumber) {
    this.manualTriggerSequenceNumber = manualTriggerSequenceNumber;
  }

  public FrequentShopperPointsModifierType pointsAwardedAmount(AmountCommonData pointsAwardedAmount) {
    this.pointsAwardedAmount = pointsAwardedAmount;
    return this;
  }

   /**
   * Get pointsAwardedAmount
   * @return pointsAwardedAmount
  **/
  @Schema(description = "")
  public AmountCommonData getPointsAwardedAmount() {
    return pointsAwardedAmount;
  }

  public void setPointsAwardedAmount(AmountCommonData pointsAwardedAmount) {
    this.pointsAwardedAmount = pointsAwardedAmount;
  }

  public FrequentShopperPointsModifierType computationBaseAmount(AmountCommonData computationBaseAmount) {
    this.computationBaseAmount = computationBaseAmount;
    return this;
  }

   /**
   * Get computationBaseAmount
   * @return computationBaseAmount
  **/
  @Schema(description = "")
  public AmountCommonData getComputationBaseAmount() {
    return computationBaseAmount;
  }

  public void setComputationBaseAmount(AmountCommonData computationBaseAmount) {
    this.computationBaseAmount = computationBaseAmount;
  }

  public FrequentShopperPointsModifierType externalSystemOriginatorFlag(Boolean externalSystemOriginatorFlag) {
    this.externalSystemOriginatorFlag = externalSystemOriginatorFlag;
    return this;
  }

   /**
   * Determines if this LoyaltyReward was created by an external system. If yes, it must not be changed, but its own modifications with higher sequence may be applied. 
   * @return externalSystemOriginatorFlag
  **/
  @Schema(description = "Determines if this LoyaltyReward was created by an external system. If yes, it must not be changed, but its own modifications with higher sequence may be applied. ")
  public Boolean isExternalSystemOriginatorFlag() {
    return externalSystemOriginatorFlag;
  }

  public void setExternalSystemOriginatorFlag(Boolean externalSystemOriginatorFlag) {
    this.externalSystemOriginatorFlag = externalSystemOriginatorFlag;
  }

  public FrequentShopperPointsModifierType priceDerivationRule(PriceDerivationRuleBase priceDerivationRule) {
    this.priceDerivationRule = priceDerivationRule;
    return this;
  }

   /**
   * Get priceDerivationRule
   * @return priceDerivationRule
  **/
  @Schema(description = "")
  public PriceDerivationRuleBase getPriceDerivationRule() {
    return priceDerivationRule;
  }

  public void setPriceDerivationRule(PriceDerivationRuleBase priceDerivationRule) {
    this.priceDerivationRule = priceDerivationRule;
  }

  public FrequentShopperPointsModifierType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public FrequentShopperPointsModifierType addAnyItem(Object anyItem) {
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

  public FrequentShopperPointsModifierType typeCode(TypeCodeEnum typeCode) {
    this.typeCode = typeCode;
    return this;
  }

   /**
   * Type code of the loyalty reward
   * @return typeCode
  **/
  @Schema(description = "Type code of the loyalty reward")
  public TypeCodeEnum getTypeCode() {
    return typeCode;
  }

  public void setTypeCode(TypeCodeEnum typeCode) {
    this.typeCode = typeCode;
  }

  public FrequentShopperPointsModifierType sequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
    return this;
  }

   /**
   * Identifies this loyalty points modifier in the current line item.
   * minimum: 0
   * maximum: 32767
   * @return sequenceNumber
  **/
  @Schema(required = true, description = "Identifies this loyalty points modifier in the current line item.")
  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public FrequentShopperPointsModifierType appliedQuantity(BigDecimal appliedQuantity) {
    this.appliedQuantity = appliedQuantity;
    return this;
  }

   /**
   * The line item quantity contributing to this modifier.
   * minimum: 0
   * @return appliedQuantity
  **/
  @Schema(description = "The line item quantity contributing to this modifier.")
  public BigDecimal getAppliedQuantity() {
    return appliedQuantity;
  }

  public void setAppliedQuantity(BigDecimal appliedQuantity) {
    this.appliedQuantity = appliedQuantity;
  }

  public FrequentShopperPointsModifierType itemLink(Integer itemLink) {
    this.itemLink = itemLink;
    return this;
  }

   /**
   * Link to the LoyaltyReward line item that forced the creation of this FrequentShopperPointsPriceModifier in order to keep the share of the prorated transaction-related points for the current line item. It is not used in the context of modifying points for a dedicated line item. 
   * minimum: 0
   * maximum: 32767
   * @return itemLink
  **/
  @Schema(description = "Link to the LoyaltyReward line item that forced the creation of this FrequentShopperPointsPriceModifier in order to keep the share of the prorated transaction-related points for the current line item. It is not used in the context of modifying points for a dedicated line item. ")
  public Integer getItemLink() {
    return itemLink;
  }

  public void setItemLink(Integer itemLink) {
    this.itemLink = itemLink;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FrequentShopperPointsModifierType frequentShopperPointsModifierType = (FrequentShopperPointsModifierType) o;
    return Objects.equals(this.promotionID, frequentShopperPointsModifierType.promotionID) &&
        Objects.equals(this.pointsAwarded, frequentShopperPointsModifierType.pointsAwarded) &&
        Objects.equals(this.manualTriggerSequenceNumber, frequentShopperPointsModifierType.manualTriggerSequenceNumber) &&
        Objects.equals(this.pointsAwardedAmount, frequentShopperPointsModifierType.pointsAwardedAmount) &&
        Objects.equals(this.computationBaseAmount, frequentShopperPointsModifierType.computationBaseAmount) &&
        Objects.equals(this.externalSystemOriginatorFlag, frequentShopperPointsModifierType.externalSystemOriginatorFlag) &&
        Objects.equals(this.priceDerivationRule, frequentShopperPointsModifierType.priceDerivationRule) &&
        Objects.equals(this.any, frequentShopperPointsModifierType.any) &&
        Objects.equals(this.typeCode, frequentShopperPointsModifierType.typeCode) &&
        Objects.equals(this.sequenceNumber, frequentShopperPointsModifierType.sequenceNumber) &&
        Objects.equals(this.appliedQuantity, frequentShopperPointsModifierType.appliedQuantity) &&
        Objects.equals(this.itemLink, frequentShopperPointsModifierType.itemLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(promotionID, pointsAwarded, manualTriggerSequenceNumber, pointsAwardedAmount, computationBaseAmount, externalSystemOriginatorFlag, priceDerivationRule, any, typeCode, sequenceNumber, appliedQuantity, itemLink);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FrequentShopperPointsModifierType {\n");
    
    sb.append("    promotionID: ").append(toIndentedString(promotionID)).append("\n");
    sb.append("    pointsAwarded: ").append(toIndentedString(pointsAwarded)).append("\n");
    sb.append("    manualTriggerSequenceNumber: ").append(toIndentedString(manualTriggerSequenceNumber)).append("\n");
    sb.append("    pointsAwardedAmount: ").append(toIndentedString(pointsAwardedAmount)).append("\n");
    sb.append("    computationBaseAmount: ").append(toIndentedString(computationBaseAmount)).append("\n");
    sb.append("    externalSystemOriginatorFlag: ").append(toIndentedString(externalSystemOriginatorFlag)).append("\n");
    sb.append("    priceDerivationRule: ").append(toIndentedString(priceDerivationRule)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    typeCode: ").append(toIndentedString(typeCode)).append("\n");
    sb.append("    sequenceNumber: ").append(toIndentedString(sequenceNumber)).append("\n");
    sb.append("    appliedQuantity: ").append(toIndentedString(appliedQuantity)).append("\n");
    sb.append("    itemLink: ").append(toIndentedString(itemLink)).append("\n");
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

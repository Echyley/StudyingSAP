/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Contains information about the modification of a sales prices resulting from an applied promotion.
 */
@Schema(description = "Contains information about the modification of a sales prices resulting from an applied promotion.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class RetailPriceModifierDomainSpecific {
  @JsonProperty("PriceDerivationRule")
  private List<PriceDerivationRuleBase> priceDerivationRule = new ArrayList<PriceDerivationRuleBase>();

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("ManualTriggerSequenceNumber")
  private Integer manualTriggerSequenceNumber = null;

  @JsonProperty("ExtraAmount")
  private AmountCommonData extraAmount = null;

  @JsonProperty("ExternalSystemOriginatorFlag")
  private Boolean externalSystemOriginatorFlag = null;

  @JsonProperty("SequenceNumber")
  private Integer sequenceNumber = null;

  @JsonProperty("Amount")
  private Amount amount = null;

  @JsonProperty("Percent")
  private Percent percent = null;

  @JsonProperty("PreviousPrice")
  private PreviousPrice previousPrice = null;

  @JsonProperty("NewPrice")
  private NewPrice newPrice = null;

  @JsonProperty("PromotionID")
  private PromotionID promotionID = null;

  @JsonProperty("ItemLink")
  private List<Integer> itemLink = null;

  @JsonProperty("Quantity")
  private QuantityCommonData quantity = null;

  @JsonProperty("Rounding")
  private RoundingCommonData rounding = null;

  @JsonProperty("ComputationBaseAmount")
  private AmountCommonData computationBaseAmount = null;

  public RetailPriceModifierDomainSpecific priceDerivationRule(List<PriceDerivationRuleBase> priceDerivationRule) {
    this.priceDerivationRule = priceDerivationRule;
    return this;
  }

  public RetailPriceModifierDomainSpecific addPriceDerivationRuleItem(PriceDerivationRuleBase priceDerivationRuleItem) {
    this.priceDerivationRule.add(priceDerivationRuleItem);
    return this;
  }

   /**
   * Although this is an array, onyl exactly 1 entry is suppored
   * @return priceDerivationRule
  **/
  @Schema(required = true, description = "Although this is an array, onyl exactly 1 entry is suppored")
  public List<PriceDerivationRuleBase> getPriceDerivationRule() {
    return priceDerivationRule;
  }

  public void setPriceDerivationRule(List<PriceDerivationRuleBase> priceDerivationRule) {
    this.priceDerivationRule = priceDerivationRule;
  }

  public RetailPriceModifierDomainSpecific any(List<Object> any) {
    this.any = any;
    return this;
  }

  public RetailPriceModifierDomainSpecific addAnyItem(Object anyItem) {
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

  public RetailPriceModifierDomainSpecific manualTriggerSequenceNumber(Integer manualTriggerSequenceNumber) {
    this.manualTriggerSequenceNumber = manualTriggerSequenceNumber;
    return this;
  }

   /**
   * The sequence number for the trigger that allows more than one trigger to be assigned to a single RetailPriceModifier. It references the ManualTriggerSequenceNumber of PromotionManualTriggerType that triggered this price modifier. 
   * minimum: 0
   * maximum: 32767
   * @return manualTriggerSequenceNumber
  **/
  @Schema(description = "The sequence number for the trigger that allows more than one trigger to be assigned to a single RetailPriceModifier. It references the ManualTriggerSequenceNumber of PromotionManualTriggerType that triggered this price modifier. ")
  public Integer getManualTriggerSequenceNumber() {
    return manualTriggerSequenceNumber;
  }

  public void setManualTriggerSequenceNumber(Integer manualTriggerSequenceNumber) {
    this.manualTriggerSequenceNumber = manualTriggerSequenceNumber;
  }

  public RetailPriceModifierDomainSpecific extraAmount(AmountCommonData extraAmount) {
    this.extraAmount = extraAmount;
    return this;
  }

   /**
   * Get extraAmount
   * @return extraAmount
  **/
  @Schema(description = "")
  public AmountCommonData getExtraAmount() {
    return extraAmount;
  }

  public void setExtraAmount(AmountCommonData extraAmount) {
    this.extraAmount = extraAmount;
  }

  public RetailPriceModifierDomainSpecific externalSystemOriginatorFlag(Boolean externalSystemOriginatorFlag) {
    this.externalSystemOriginatorFlag = externalSystemOriginatorFlag;
    return this;
  }

   /**
   * Determines if this discount was created by an external system. If yes, it must not be changed, but its own modifications with higher sequence may be applied. Default value is false. 
   * @return externalSystemOriginatorFlag
  **/
  @Schema(description = "Determines if this discount was created by an external system. If yes, it must not be changed, but its own modifications with higher sequence may be applied. Default value is false. ")
  public Boolean isExternalSystemOriginatorFlag() {
    return externalSystemOriginatorFlag;
  }

  public void setExternalSystemOriginatorFlag(Boolean externalSystemOriginatorFlag) {
    this.externalSystemOriginatorFlag = externalSystemOriginatorFlag;
  }

  public RetailPriceModifierDomainSpecific sequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
    return this;
  }

   /**
   * Identifies this retail price modifier in the corresponding line item.
   * minimum: 0
   * maximum: 32767
   * @return sequenceNumber
  **/
  @Schema(required = true, description = "Identifies this retail price modifier in the corresponding line item.")
  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public RetailPriceModifierDomainSpecific amount(Amount amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Get amount
   * @return amount
  **/
  @Schema(description = "")
  public Amount getAmount() {
    return amount;
  }

  public void setAmount(Amount amount) {
    this.amount = amount;
  }

  public RetailPriceModifierDomainSpecific percent(Percent percent) {
    this.percent = percent;
    return this;
  }

   /**
   * Get percent
   * @return percent
  **/
  @Schema(description = "")
  public Percent getPercent() {
    return percent;
  }

  public void setPercent(Percent percent) {
    this.percent = percent;
  }

  public RetailPriceModifierDomainSpecific previousPrice(PreviousPrice previousPrice) {
    this.previousPrice = previousPrice;
    return this;
  }

   /**
   * Get previousPrice
   * @return previousPrice
  **/
  @Schema(description = "")
  public PreviousPrice getPreviousPrice() {
    return previousPrice;
  }

  public void setPreviousPrice(PreviousPrice previousPrice) {
    this.previousPrice = previousPrice;
  }

  public RetailPriceModifierDomainSpecific newPrice(NewPrice newPrice) {
    this.newPrice = newPrice;
    return this;
  }

   /**
   * Get newPrice
   * @return newPrice
  **/
  @Schema(description = "")
  public NewPrice getNewPrice() {
    return newPrice;
  }

  public void setNewPrice(NewPrice newPrice) {
    this.newPrice = newPrice;
  }

  public RetailPriceModifierDomainSpecific promotionID(PromotionID promotionID) {
    this.promotionID = promotionID;
    return this;
  }

   /**
   * Get promotionID
   * @return promotionID
  **/
  @Schema(description = "")
  public PromotionID getPromotionID() {
    return promotionID;
  }

  public void setPromotionID(PromotionID promotionID) {
    this.promotionID = promotionID;
  }

  public RetailPriceModifierDomainSpecific itemLink(List<Integer> itemLink) {
    this.itemLink = itemLink;
    return this;
  }

  public RetailPriceModifierDomainSpecific addItemLinkItem(Integer itemLinkItem) {
    if (this.itemLink == null) {
      this.itemLink = new ArrayList<Integer>();
    }
    this.itemLink.add(itemLinkItem);
    return this;
  }

   /**
   * The link to the discount line item that triggered the creation of this RetailPriceModifier in order to keep the prorated transaction-related discount for the current line item. Is not used in the context of the modification of the price of a single line item (item-related discount). Although this is an array, it contains at most one entry. 
   * @return itemLink
  **/
  @Schema(description = "The link to the discount line item that triggered the creation of this RetailPriceModifier in order to keep the prorated transaction-related discount for the current line item. Is not used in the context of the modification of the price of a single line item (item-related discount). Although this is an array, it contains at most one entry. ")
  public List<Integer> getItemLink() {
    return itemLink;
  }

  public void setItemLink(List<Integer> itemLink) {
    this.itemLink = itemLink;
  }

  public RetailPriceModifierDomainSpecific quantity(QuantityCommonData quantity) {
    this.quantity = quantity;
    return this;
  }

   /**
   * Get quantity
   * @return quantity
  **/
  @Schema(description = "")
  public QuantityCommonData getQuantity() {
    return quantity;
  }

  public void setQuantity(QuantityCommonData quantity) {
    this.quantity = quantity;
  }

  public RetailPriceModifierDomainSpecific rounding(RoundingCommonData rounding) {
    this.rounding = rounding;
    return this;
  }

   /**
   * Get rounding
   * @return rounding
  **/
  @Schema(description = "")
  public RoundingCommonData getRounding() {
    return rounding;
  }

  public void setRounding(RoundingCommonData rounding) {
    this.rounding = rounding;
  }

  public RetailPriceModifierDomainSpecific computationBaseAmount(AmountCommonData computationBaseAmount) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RetailPriceModifierDomainSpecific retailPriceModifierDomainSpecific = (RetailPriceModifierDomainSpecific) o;
    return Objects.equals(this.priceDerivationRule, retailPriceModifierDomainSpecific.priceDerivationRule) &&
        Objects.equals(this.any, retailPriceModifierDomainSpecific.any) &&
        Objects.equals(this.manualTriggerSequenceNumber, retailPriceModifierDomainSpecific.manualTriggerSequenceNumber) &&
        Objects.equals(this.extraAmount, retailPriceModifierDomainSpecific.extraAmount) &&
        Objects.equals(this.externalSystemOriginatorFlag, retailPriceModifierDomainSpecific.externalSystemOriginatorFlag) &&
        Objects.equals(this.sequenceNumber, retailPriceModifierDomainSpecific.sequenceNumber) &&
        Objects.equals(this.amount, retailPriceModifierDomainSpecific.amount) &&
        Objects.equals(this.percent, retailPriceModifierDomainSpecific.percent) &&
        Objects.equals(this.previousPrice, retailPriceModifierDomainSpecific.previousPrice) &&
        Objects.equals(this.newPrice, retailPriceModifierDomainSpecific.newPrice) &&
        Objects.equals(this.promotionID, retailPriceModifierDomainSpecific.promotionID) &&
        Objects.equals(this.itemLink, retailPriceModifierDomainSpecific.itemLink) &&
        Objects.equals(this.quantity, retailPriceModifierDomainSpecific.quantity) &&
        Objects.equals(this.rounding, retailPriceModifierDomainSpecific.rounding) &&
        Objects.equals(this.computationBaseAmount, retailPriceModifierDomainSpecific.computationBaseAmount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priceDerivationRule, any, manualTriggerSequenceNumber, extraAmount, externalSystemOriginatorFlag, sequenceNumber, amount, percent, previousPrice, newPrice, promotionID, itemLink, quantity, rounding, computationBaseAmount);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RetailPriceModifierDomainSpecific {\n");
    
    sb.append("    priceDerivationRule: ").append(toIndentedString(priceDerivationRule)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    manualTriggerSequenceNumber: ").append(toIndentedString(manualTriggerSequenceNumber)).append("\n");
    sb.append("    extraAmount: ").append(toIndentedString(extraAmount)).append("\n");
    sb.append("    externalSystemOriginatorFlag: ").append(toIndentedString(externalSystemOriginatorFlag)).append("\n");
    sb.append("    sequenceNumber: ").append(toIndentedString(sequenceNumber)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("    percent: ").append(toIndentedString(percent)).append("\n");
    sb.append("    previousPrice: ").append(toIndentedString(previousPrice)).append("\n");
    sb.append("    newPrice: ").append(toIndentedString(newPrice)).append("\n");
    sb.append("    promotionID: ").append(toIndentedString(promotionID)).append("\n");
    sb.append("    itemLink: ").append(toIndentedString(itemLink)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    rounding: ").append(toIndentedString(rounding)).append("\n");
    sb.append("    computationBaseAmount: ").append(toIndentedString(computationBaseAmount)).append("\n");
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

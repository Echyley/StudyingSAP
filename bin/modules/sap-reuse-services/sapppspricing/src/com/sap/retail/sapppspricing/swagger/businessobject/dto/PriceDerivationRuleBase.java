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
 * Contains information of an applied price derivation rule.
 */
@Schema(description = "Contains information of an applied price derivation rule.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PriceDerivationRuleBase {
  @JsonProperty("PriceDerivationRuleID")
  private String priceDerivationRuleID = null;

  @JsonProperty("Eligibility")
  private List<PriceDerivationRuleEligibility> eligibility = null;

  @JsonProperty("any")
  private List<Object> any = null;

  @JsonProperty("PromotionDescription")
  private String promotionDescription = null;

  @JsonProperty("PromotionDescriptionMultiLanguage")
  private List<DescriptionCommonData> promotionDescriptionMultiLanguage = null;

  @JsonProperty("PromotionPriceDerivationRuleSequence")
  private Long promotionPriceDerivationRuleSequence = null;

  @JsonProperty("PromotionPriceDerivationRuleResolution")
  private Long promotionPriceDerivationRuleResolution = null;

  @JsonProperty("PromotionPriceDerivationRuleTypeCode")
  private String promotionPriceDerivationRuleTypeCode = null;

  /**
   * Indicates whether this is a line-item-related price derivation rule (PO/PC) or a transaction-related price derivation rule (SU/SP).
   */
  public enum TransactionControlBreakCodeEnum {
    PO("PO"),
    SU("SU"),
    PC("PC"),
    SP("SP");

    private String value;

    TransactionControlBreakCodeEnum(String value) {
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
    public static TransactionControlBreakCodeEnum fromValue(String input) {
      for (TransactionControlBreakCodeEnum b : TransactionControlBreakCodeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("TransactionControlBreakCode")
  private TransactionControlBreakCodeEnum transactionControlBreakCode = null;

  @JsonProperty("PriceDerivationRuleDescription")
  private String priceDerivationRuleDescription = null;

  @JsonProperty("PromotionOriginatorTypeCode")
  private String promotionOriginatorTypeCode = null;

  @JsonProperty("TriggerQuantity")
  private BigDecimal triggerQuantity = null;

  @JsonProperty("DiscountMethodCode")
  private String discountMethodCode = null;

  @JsonProperty("FrequentShopperPointsFlag")
  private Boolean frequentShopperPointsFlag = null;

  @JsonProperty("CustomerGroupLoyaltyPointsDefaultQuantity")
  private BigDecimal customerGroupLoyaltyPointsDefaultQuantity = null;

  @JsonProperty("ProhibitPrintFlag")
  private Boolean prohibitPrintFlag = null;

  @JsonProperty("TenderTypeCode")
  private String tenderTypeCode = null;

  @JsonProperty("PointsConversionAmount")
  private AmountCommonData pointsConversionAmount = null;

  @JsonProperty("NoEffectOnSubsequentPriceDerivationRulesFlag")
  private Boolean noEffectOnSubsequentPriceDerivationRulesFlag = null;

  @JsonProperty("ProhibitTransactionRelatedPriceDerivationRulesFlag")
  private Boolean prohibitTransactionRelatedPriceDerivationRulesFlag = null;

  @JsonProperty("ExclusiveFlag")
  private Boolean exclusiveFlag = null;

  @JsonProperty("ConcurrenceControlVector")
  private String concurrenceControlVector = null;

  @JsonProperty("AppliedCount")
  private BigDecimal appliedCount = null;

  @JsonProperty("ReceiptLine")
  private String receiptLine = null;

  @JsonProperty("ReceiptLineMultiLanguage")
  private List<DescriptionCommonData> receiptLineMultiLanguage = null;

  @JsonProperty("ExternalPromotionID")
  private String externalPromotionID = null;

  @JsonProperty("ExternalPriceDerivationRuleID")
  private String externalPriceDerivationRuleID = null;

  @JsonProperty("CouponPrintoutID")
  private String couponPrintoutID = null;

  @JsonProperty("CouponPrintoutRule")
  private String couponPrintoutRule = null;

  @JsonProperty("CouponPrintoutText")
  private String couponPrintoutText = null;

  @JsonProperty("PrintoutValidityPeriod")
  private Integer printoutValidityPeriod = null;

  @JsonProperty("GiftCertificateExpirationDate")
  private String giftCertificateExpirationDate = null;

  @JsonProperty("ExternalAction")
  private ExternalActionType externalAction = null;

  @JsonProperty("OperatorDisplayText")
  private List<DescriptionCommonData> operatorDisplayText = null;

  @JsonProperty("CustomerDisplayText")
  private List<DescriptionCommonData> customerDisplayText = null;

  @JsonProperty("PromotionType")
  private String promotionType = null;

  @JsonProperty("CalculationBaseSequence")
  private String calculationBaseSequence = null;

  @JsonProperty("PromotionID")
  private String promotionID = null;

  /**
   * Describes the type of the applied price derivation rule.
   */
  public enum ApplicationTypeEnum {
    FIXEDPRICE("FixedPrice"),
    FIXPRICETOTAL("FixPriceTotal"),
    DISCOUNTSINGLE("DiscountSingle"),
    DISCOUNTTOTAL("DiscountTotal"),
    DISCOUNTPERCENT("DiscountPercent"),
    DISCOUNTPERCENTTOTAL("DiscountPercentTotal"),
    SETPRICETOTAL("SetPriceTotal"),
    MIXMATCH("MixMatch"),
    EXTERNALACTION("ExternalAction"),
    MANUAL("Manual"),
    ADDITIONALBONUS("AdditionalBonus");

    private String value;

    ApplicationTypeEnum(String value) {
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
    public static ApplicationTypeEnum fromValue(String input) {
      for (ApplicationTypeEnum b : ApplicationTypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("ApplicationType")
  private ApplicationTypeEnum applicationType = null;

  public PriceDerivationRuleBase priceDerivationRuleID(String priceDerivationRuleID) {
    this.priceDerivationRuleID = priceDerivationRuleID;
    return this;
  }

   /**
   * Identifies the price derivation rule. Decimal representation of a 64 bit integer value
   * @return priceDerivationRuleID
  **/
  @Schema(required = true, description = "Identifies the price derivation rule. Decimal representation of a 64 bit integer value")
  public String getPriceDerivationRuleID() {
    return priceDerivationRuleID;
  }

  public void setPriceDerivationRuleID(String priceDerivationRuleID) {
    this.priceDerivationRuleID = priceDerivationRuleID;
  }

  public PriceDerivationRuleBase eligibility(List<PriceDerivationRuleEligibility> eligibility) {
    this.eligibility = eligibility;
    return this;
  }

  public PriceDerivationRuleBase addEligibilityItem(PriceDerivationRuleEligibility eligibilityItem) {
    if (this.eligibility == null) {
      this.eligibility = new ArrayList<PriceDerivationRuleEligibility>();
    }
    this.eligibility.add(eligibilityItem);
    return this;
  }

   /**
   * Only relevant for of coupon eligibilities. Each coupon that is used results in a separate eligibility element.
   * @return eligibility
  **/
  @Schema(description = "Only relevant for of coupon eligibilities. Each coupon that is used results in a separate eligibility element.")
  public List<PriceDerivationRuleEligibility> getEligibility() {
    return eligibility;
  }

  public void setEligibility(List<PriceDerivationRuleEligibility> eligibility) {
    this.eligibility = eligibility;
  }

  public PriceDerivationRuleBase any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PriceDerivationRuleBase addAnyItem(Object anyItem) {
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

  public PriceDerivationRuleBase promotionDescription(String promotionDescription) {
    this.promotionDescription = promotionDescription;
    return this;
  }

   /**
   * Exists for compatibility reasons with earlier versions of the Client API (1.0). As of Client API 2.0, use PromotionDescriptionMultiLanguage instead of PromotionDescription.
   * @return promotionDescription
  **/
  @Schema(description = "Exists for compatibility reasons with earlier versions of the Client API (1.0). As of Client API 2.0, use PromotionDescriptionMultiLanguage instead of PromotionDescription.")
  public String getPromotionDescription() {
    return promotionDescription;
  }

  public void setPromotionDescription(String promotionDescription) {
    this.promotionDescription = promotionDescription;
  }

  public PriceDerivationRuleBase promotionDescriptionMultiLanguage(List<DescriptionCommonData> promotionDescriptionMultiLanguage) {
    this.promotionDescriptionMultiLanguage = promotionDescriptionMultiLanguage;
    return this;
  }

  public PriceDerivationRuleBase addPromotionDescriptionMultiLanguageItem(DescriptionCommonData promotionDescriptionMultiLanguageItem) {
    if (this.promotionDescriptionMultiLanguage == null) {
      this.promotionDescriptionMultiLanguage = new ArrayList<DescriptionCommonData>();
    }
    this.promotionDescriptionMultiLanguage.add(promotionDescriptionMultiLanguageItem);
    return this;
  }

   /**
   * Promotion descriptions in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.
   * @return promotionDescriptionMultiLanguage
  **/
  @Schema(description = "Promotion descriptions in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.")
  public List<DescriptionCommonData> getPromotionDescriptionMultiLanguage() {
    return promotionDescriptionMultiLanguage;
  }

  public void setPromotionDescriptionMultiLanguage(List<DescriptionCommonData> promotionDescriptionMultiLanguage) {
    this.promotionDescriptionMultiLanguage = promotionDescriptionMultiLanguage;
  }

  public PriceDerivationRuleBase promotionPriceDerivationRuleSequence(Long promotionPriceDerivationRuleSequence) {
    this.promotionPriceDerivationRuleSequence = promotionPriceDerivationRuleSequence;
    return this;
  }

   /**
   * This is specified in the promotion master data and controls in which order rules are applied. Line-item-related price derivation rules and transaction-related price derivation rules are treated independently (all line-item-related promotions are applied first). 
   * @return promotionPriceDerivationRuleSequence
  **/
  @Schema(description = "This is specified in the promotion master data and controls in which order rules are applied. Line-item-related price derivation rules and transaction-related price derivation rules are treated independently (all line-item-related promotions are applied first). ")
  public Long getPromotionPriceDerivationRuleSequence() {
    return promotionPriceDerivationRuleSequence;
  }

  public void setPromotionPriceDerivationRuleSequence(Long promotionPriceDerivationRuleSequence) {
    this.promotionPriceDerivationRuleSequence = promotionPriceDerivationRuleSequence;
  }

  public PriceDerivationRuleBase promotionPriceDerivationRuleResolution(Long promotionPriceDerivationRuleResolution) {
    this.promotionPriceDerivationRuleResolution = promotionPriceDerivationRuleResolution;
    return this;
  }

   /**
   * This is specified in the promotion master data and controls which rule is applied if several rules have the same sequence value. If there are price derivation rules with an identical sequence number and identical highest resolution, the service tries to perform a best-price calculation. It is not guaranteed that this will succeed. 
   * minimum: 0
   * @return promotionPriceDerivationRuleResolution
  **/
  @Schema(description = "This is specified in the promotion master data and controls which rule is applied if several rules have the same sequence value. If there are price derivation rules with an identical sequence number and identical highest resolution, the service tries to perform a best-price calculation. It is not guaranteed that this will succeed. ")
  public Long getPromotionPriceDerivationRuleResolution() {
    return promotionPriceDerivationRuleResolution;
  }

  public void setPromotionPriceDerivationRuleResolution(Long promotionPriceDerivationRuleResolution) {
    this.promotionPriceDerivationRuleResolution = promotionPriceDerivationRuleResolution;
  }

  public PriceDerivationRuleBase promotionPriceDerivationRuleTypeCode(String promotionPriceDerivationRuleTypeCode) {
    this.promotionPriceDerivationRuleTypeCode = promotionPriceDerivationRuleTypeCode;
    return this;
  }

   /**
   * Holds the condition type if this promotion discount should be mapped into an SAP ERP or SAP S/4HANA condition record.
   * @return promotionPriceDerivationRuleTypeCode
  **/
  @Schema(description = "Holds the condition type if this promotion discount should be mapped into an SAP ERP or SAP S/4HANA condition record.")
  public String getPromotionPriceDerivationRuleTypeCode() {
    return promotionPriceDerivationRuleTypeCode;
  }

  public void setPromotionPriceDerivationRuleTypeCode(String promotionPriceDerivationRuleTypeCode) {
    this.promotionPriceDerivationRuleTypeCode = promotionPriceDerivationRuleTypeCode;
  }

  public PriceDerivationRuleBase transactionControlBreakCode(TransactionControlBreakCodeEnum transactionControlBreakCode) {
    this.transactionControlBreakCode = transactionControlBreakCode;
    return this;
  }

   /**
   * Indicates whether this is a line-item-related price derivation rule (PO/PC) or a transaction-related price derivation rule (SU/SP).
   * @return transactionControlBreakCode
  **/
  @Schema(description = "Indicates whether this is a line-item-related price derivation rule (PO/PC) or a transaction-related price derivation rule (SU/SP).")
  public TransactionControlBreakCodeEnum getTransactionControlBreakCode() {
    return transactionControlBreakCode;
  }

  public void setTransactionControlBreakCode(TransactionControlBreakCodeEnum transactionControlBreakCode) {
    this.transactionControlBreakCode = transactionControlBreakCode;
  }

  public PriceDerivationRuleBase priceDerivationRuleDescription(String priceDerivationRuleDescription) {
    this.priceDerivationRuleDescription = priceDerivationRuleDescription;
    return this;
  }

   /**
   * Reserved for future use.
   * @return priceDerivationRuleDescription
  **/
  @Schema(description = "Reserved for future use.")
  public String getPriceDerivationRuleDescription() {
    return priceDerivationRuleDescription;
  }

  public void setPriceDerivationRuleDescription(String priceDerivationRuleDescription) {
    this.priceDerivationRuleDescription = priceDerivationRuleDescription;
  }

  public PriceDerivationRuleBase promotionOriginatorTypeCode(String promotionOriginatorTypeCode) {
    this.promotionOriginatorTypeCode = promotionOriginatorTypeCode;
    return this;
  }

   /**
   * Contains information about the origin of the promotion as specifed in the promotion master data.
   * @return promotionOriginatorTypeCode
  **/
  @Schema(description = "Contains information about the origin of the promotion as specifed in the promotion master data.")
  public String getPromotionOriginatorTypeCode() {
    return promotionOriginatorTypeCode;
  }

  public void setPromotionOriginatorTypeCode(String promotionOriginatorTypeCode) {
    this.promotionOriginatorTypeCode = promotionOriginatorTypeCode;
  }

  public PriceDerivationRuleBase triggerQuantity(BigDecimal triggerQuantity) {
    this.triggerQuantity = triggerQuantity;
    return this;
  }

   /**
   * The total quantity that needs to be purchased in order to trigger the price derivation rule.
   * minimum: 0
   * @return triggerQuantity
  **/
  @Schema(description = "The total quantity that needs to be purchased in order to trigger the price derivation rule.")
  public BigDecimal getTriggerQuantity() {
    return triggerQuantity;
  }

  public void setTriggerQuantity(BigDecimal triggerQuantity) {
    this.triggerQuantity = triggerQuantity;
  }

  public PriceDerivationRuleBase discountMethodCode(String discountMethodCode) {
    this.discountMethodCode = discountMethodCode;
    return this;
  }

   /**
   * Determines how the discount impacts the calculation of the transaction. Possible values are: 00 - The discount reduces the transaction total. 01 - The discount does not influence the transaction total or the amount that the customer has to pay. However, the customer gets a gift certificate for the discount amount that he can use to pay for the next purchase. 02 - The discount is used as a tender for the current transaction, for example it reduces the remaining amount that the customer has to pay. 03 - The discount reduces the transaction total but is counterbalanced by some gift certificate sale. 04 - A coupon is given to the customer instead of a discount. Transaction total is not reduced. 
   * @return discountMethodCode
  **/
  @Schema(description = "Determines how the discount impacts the calculation of the transaction. Possible values are: 00 - The discount reduces the transaction total. 01 - The discount does not influence the transaction total or the amount that the customer has to pay. However, the customer gets a gift certificate for the discount amount that he can use to pay for the next purchase. 02 - The discount is used as a tender for the current transaction, for example it reduces the remaining amount that the customer has to pay. 03 - The discount reduces the transaction total but is counterbalanced by some gift certificate sale. 04 - A coupon is given to the customer instead of a discount. Transaction total is not reduced. ")
  public String getDiscountMethodCode() {
    return discountMethodCode;
  }

  public void setDiscountMethodCode(String discountMethodCode) {
    this.discountMethodCode = discountMethodCode;
  }

  public PriceDerivationRuleBase frequentShopperPointsFlag(Boolean frequentShopperPointsFlag) {
    this.frequentShopperPointsFlag = frequentShopperPointsFlag;
    return this;
  }

   /**
   * Determines whether a discount (false) or loyalty points (true) are granted by the price derivation rule
   * @return frequentShopperPointsFlag
  **/
  @Schema(description = "Determines whether a discount (false) or loyalty points (true) are granted by the price derivation rule")
  public Boolean isFrequentShopperPointsFlag() {
    return frequentShopperPointsFlag;
  }

  public void setFrequentShopperPointsFlag(Boolean frequentShopperPointsFlag) {
    this.frequentShopperPointsFlag = frequentShopperPointsFlag;
  }

  public PriceDerivationRuleBase customerGroupLoyaltyPointsDefaultQuantity(BigDecimal customerGroupLoyaltyPointsDefaultQuantity) {
    this.customerGroupLoyaltyPointsDefaultQuantity = customerGroupLoyaltyPointsDefaultQuantity;
    return this;
  }

   /**
   * Reserved for future use.
   * @return customerGroupLoyaltyPointsDefaultQuantity
  **/
  @Schema(description = "Reserved for future use.")
  public BigDecimal getCustomerGroupLoyaltyPointsDefaultQuantity() {
    return customerGroupLoyaltyPointsDefaultQuantity;
  }

  public void setCustomerGroupLoyaltyPointsDefaultQuantity(BigDecimal customerGroupLoyaltyPointsDefaultQuantity) {
    this.customerGroupLoyaltyPointsDefaultQuantity = customerGroupLoyaltyPointsDefaultQuantity;
  }

  public PriceDerivationRuleBase prohibitPrintFlag(Boolean prohibitPrintFlag) {
    this.prohibitPrintFlag = prohibitPrintFlag;
    return this;
  }

   /**
   * Determines if the result of the applied price derivation rule is to be suppressed on displays and accordingly not printed on the receipt. If this element is missing in the request, the default value is set to false and the applied price derivation is printed on the receipt. 
   * @return prohibitPrintFlag
  **/
  @Schema(description = "Determines if the result of the applied price derivation rule is to be suppressed on displays and accordingly not printed on the receipt. If this element is missing in the request, the default value is set to false and the applied price derivation is printed on the receipt. ")
  public Boolean isProhibitPrintFlag() {
    return prohibitPrintFlag;
  }

  public void setProhibitPrintFlag(Boolean prohibitPrintFlag) {
    this.prohibitPrintFlag = prohibitPrintFlag;
  }

  public PriceDerivationRuleBase tenderTypeCode(String tenderTypeCode) {
    this.tenderTypeCode = tenderTypeCode;
    return this;
  }

   /**
   * Type code of the tender. This value is needed only for DiscountMethodCode &#x3D; 02.
   * @return tenderTypeCode
  **/
  @Schema(description = "Type code of the tender. This value is needed only for DiscountMethodCode = 02.")
  public String getTenderTypeCode() {
    return tenderTypeCode;
  }

  public void setTenderTypeCode(String tenderTypeCode) {
    this.tenderTypeCode = tenderTypeCode;
  }

  public PriceDerivationRuleBase pointsConversionAmount(AmountCommonData pointsConversionAmount) {
    this.pointsConversionAmount = pointsConversionAmount;
    return this;
  }

   /**
   * Get pointsConversionAmount
   * @return pointsConversionAmount
  **/
  @Schema(description = "")
  public AmountCommonData getPointsConversionAmount() {
    return pointsConversionAmount;
  }

  public void setPointsConversionAmount(AmountCommonData pointsConversionAmount) {
    this.pointsConversionAmount = pointsConversionAmount;
  }

  public PriceDerivationRuleBase noEffectOnSubsequentPriceDerivationRulesFlag(Boolean noEffectOnSubsequentPriceDerivationRulesFlag) {
    this.noEffectOnSubsequentPriceDerivationRulesFlag = noEffectOnSubsequentPriceDerivationRulesFlag;
    return this;
  }

   /**
   * Determines whether applying this price derivation rule influences the calculation base of subsequent price derivation rules (false) or not (true). This is only relevant for line-item-related monetary price derivation rules. 
   * @return noEffectOnSubsequentPriceDerivationRulesFlag
  **/
  @Schema(description = "Determines whether applying this price derivation rule influences the calculation base of subsequent price derivation rules (false) or not (true). This is only relevant for line-item-related monetary price derivation rules. ")
  public Boolean isNoEffectOnSubsequentPriceDerivationRulesFlag() {
    return noEffectOnSubsequentPriceDerivationRulesFlag;
  }

  public void setNoEffectOnSubsequentPriceDerivationRulesFlag(Boolean noEffectOnSubsequentPriceDerivationRulesFlag) {
    this.noEffectOnSubsequentPriceDerivationRulesFlag = noEffectOnSubsequentPriceDerivationRulesFlag;
  }

  public PriceDerivationRuleBase prohibitTransactionRelatedPriceDerivationRulesFlag(Boolean prohibitTransactionRelatedPriceDerivationRulesFlag) {
    this.prohibitTransactionRelatedPriceDerivationRulesFlag = prohibitTransactionRelatedPriceDerivationRulesFlag;
    return this;
  }

   /**
   * Determines whether applying this price derivation rule influences the calculation base of subsequent transaction-related price derivation rules (false) or not (true). This is only relevant for line-item-related monetary price derivation rules. 
   * @return prohibitTransactionRelatedPriceDerivationRulesFlag
  **/
  @Schema(description = "Determines whether applying this price derivation rule influences the calculation base of subsequent transaction-related price derivation rules (false) or not (true). This is only relevant for line-item-related monetary price derivation rules. ")
  public Boolean isProhibitTransactionRelatedPriceDerivationRulesFlag() {
    return prohibitTransactionRelatedPriceDerivationRulesFlag;
  }

  public void setProhibitTransactionRelatedPriceDerivationRulesFlag(Boolean prohibitTransactionRelatedPriceDerivationRulesFlag) {
    this.prohibitTransactionRelatedPriceDerivationRulesFlag = prohibitTransactionRelatedPriceDerivationRulesFlag;
  }

  public PriceDerivationRuleBase exclusiveFlag(Boolean exclusiveFlag) {
    this.exclusiveFlag = exclusiveFlag;
    return this;
  }

   /**
   * Determines whether this price derivation rule is an exclusive one. This impacts the application of further price derivation rules with a higher sequence.  
   * @return exclusiveFlag
  **/
  @Schema(description = "Determines whether this price derivation rule is an exclusive one. This impacts the application of further price derivation rules with a higher sequence.  ")
  public Boolean isExclusiveFlag() {
    return exclusiveFlag;
  }

  public void setExclusiveFlag(Boolean exclusiveFlag) {
    this.exclusiveFlag = exclusiveFlag;
  }

  public PriceDerivationRuleBase concurrenceControlVector(String concurrenceControlVector) {
    this.concurrenceControlVector = concurrenceControlVector;
    return this;
  }

   /**
   * This is currently not supported.
   * @return concurrenceControlVector
  **/
  @Schema(description = "This is currently not supported.")
  public String getConcurrenceControlVector() {
    return concurrenceControlVector;
  }

  public void setConcurrenceControlVector(String concurrenceControlVector) {
    this.concurrenceControlVector = concurrenceControlVector;
  }

  public PriceDerivationRuleBase appliedCount(BigDecimal appliedCount) {
    this.appliedCount = appliedCount;
    return this;
  }

   /**
   * Describes how often the current price derivation rule was applied.
   * @return appliedCount
  **/
  @Schema(description = "Describes how often the current price derivation rule was applied.")
  public BigDecimal getAppliedCount() {
    return appliedCount;
  }

  public void setAppliedCount(BigDecimal appliedCount) {
    this.appliedCount = appliedCount;
  }

  public PriceDerivationRuleBase receiptLine(String receiptLine) {
    this.receiptLine = receiptLine;
    return this;
  }

   /**
   * Exists for compatibility reasons with earlier versions of the Client API (1.0). As of Client API 2.0, use ReceiptLineMultiLanguage instead of ReceiptLine.
   * @return receiptLine
  **/
  @Schema(description = "Exists for compatibility reasons with earlier versions of the Client API (1.0). As of Client API 2.0, use ReceiptLineMultiLanguage instead of ReceiptLine.")
  public String getReceiptLine() {
    return receiptLine;
  }

  public void setReceiptLine(String receiptLine) {
    this.receiptLine = receiptLine;
  }

  public PriceDerivationRuleBase receiptLineMultiLanguage(List<DescriptionCommonData> receiptLineMultiLanguage) {
    this.receiptLineMultiLanguage = receiptLineMultiLanguage;
    return this;
  }

  public PriceDerivationRuleBase addReceiptLineMultiLanguageItem(DescriptionCommonData receiptLineMultiLanguageItem) {
    if (this.receiptLineMultiLanguage == null) {
      this.receiptLineMultiLanguage = new ArrayList<DescriptionCommonData>();
    }
    this.receiptLineMultiLanguage.add(receiptLineMultiLanguageItem);
    return this;
  }

   /**
   * The texts to be printed on the receipt in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.
   * @return receiptLineMultiLanguage
  **/
  @Schema(description = "The texts to be printed on the receipt in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.")
  public List<DescriptionCommonData> getReceiptLineMultiLanguage() {
    return receiptLineMultiLanguage;
  }

  public void setReceiptLineMultiLanguage(List<DescriptionCommonData> receiptLineMultiLanguage) {
    this.receiptLineMultiLanguage = receiptLineMultiLanguage;
  }

  public PriceDerivationRuleBase externalPromotionID(String externalPromotionID) {
    this.externalPromotionID = externalPromotionID;
    return this;
  }

   /**
   * Identifies an external promotion that was the origin for the corresponding OPP promotion.
   * @return externalPromotionID
  **/
  @Schema(description = "Identifies an external promotion that was the origin for the corresponding OPP promotion.")
  public String getExternalPromotionID() {
    return externalPromotionID;
  }

  public void setExternalPromotionID(String externalPromotionID) {
    this.externalPromotionID = externalPromotionID;
  }

  public PriceDerivationRuleBase externalPriceDerivationRuleID(String externalPriceDerivationRuleID) {
    this.externalPriceDerivationRuleID = externalPriceDerivationRuleID;
    return this;
  }

   /**
   * Decimal representation of a 64 bit integer value. Not used.
   * @return externalPriceDerivationRuleID
  **/
  @Schema(description = "Decimal representation of a 64 bit integer value. Not used.")
  public String getExternalPriceDerivationRuleID() {
    return externalPriceDerivationRuleID;
  }

  public void setExternalPriceDerivationRuleID(String externalPriceDerivationRuleID) {
    this.externalPriceDerivationRuleID = externalPriceDerivationRuleID;
  }

  public PriceDerivationRuleBase couponPrintoutID(String couponPrintoutID) {
    this.couponPrintoutID = couponPrintoutID;
    return this;
  }

   /**
   * The coupon number of the printout coupon. Only relevant for DiscountMethodCode &#x3D; 04.
   * @return couponPrintoutID
  **/
  @Schema(description = "The coupon number of the printout coupon. Only relevant for DiscountMethodCode = 04.")
  public String getCouponPrintoutID() {
    return couponPrintoutID;
  }

  public void setCouponPrintoutID(String couponPrintoutID) {
    this.couponPrintoutID = couponPrintoutID;
  }

  public PriceDerivationRuleBase couponPrintoutRule(String couponPrintoutRule) {
    this.couponPrintoutRule = couponPrintoutRule;
    return this;
  }

   /**
   * Describes how the printout of the coupon is to be done. Only relevant for DiscountMethodCode &#x3D; 04. Possible values are: 00 - A separate receipt is to be printed. 01 - The coupon is to be printed at the end of the receipt of the current transaction. 
   * @return couponPrintoutRule
  **/
  @Schema(description = "Describes how the printout of the coupon is to be done. Only relevant for DiscountMethodCode = 04. Possible values are: 00 - A separate receipt is to be printed. 01 - The coupon is to be printed at the end of the receipt of the current transaction. ")
  public String getCouponPrintoutRule() {
    return couponPrintoutRule;
  }

  public void setCouponPrintoutRule(String couponPrintoutRule) {
    this.couponPrintoutRule = couponPrintoutRule;
  }

  public PriceDerivationRuleBase couponPrintoutText(String couponPrintoutText) {
    this.couponPrintoutText = couponPrintoutText;
    return this;
  }

   /**
   * The (formatted) text to be printed. Only relevant for DiscountMethodCode &#x3D; 04.
   * @return couponPrintoutText
  **/
  @Schema(description = "The (formatted) text to be printed. Only relevant for DiscountMethodCode = 04.")
  public String getCouponPrintoutText() {
    return couponPrintoutText;
  }

  public void setCouponPrintoutText(String couponPrintoutText) {
    this.couponPrintoutText = couponPrintoutText;
  }

  public PriceDerivationRuleBase printoutValidityPeriod(Integer printoutValidityPeriod) {
    this.printoutValidityPeriod = printoutValidityPeriod;
    return this;
  }

   /**
   * Describes for how many days the coupon will be valid. Only relevant for DiscountMethodCode &#x3D; 04.
   * minimum: 0
   * @return printoutValidityPeriod
  **/
  @Schema(description = "Describes for how many days the coupon will be valid. Only relevant for DiscountMethodCode = 04.")
  public Integer getPrintoutValidityPeriod() {
    return printoutValidityPeriod;
  }

  public void setPrintoutValidityPeriod(Integer printoutValidityPeriod) {
    this.printoutValidityPeriod = printoutValidityPeriod;
  }

  public PriceDerivationRuleBase giftCertificateExpirationDate(String giftCertificateExpirationDate) {
    this.giftCertificateExpirationDate = giftCertificateExpirationDate;
    return this;
  }

   /**
   * Reserved for future use.
   * @return giftCertificateExpirationDate
  **/
  @Schema(description = "Reserved for future use.")
  public String getGiftCertificateExpirationDate() {
    return giftCertificateExpirationDate;
  }

  public void setGiftCertificateExpirationDate(String giftCertificateExpirationDate) {
    this.giftCertificateExpirationDate = giftCertificateExpirationDate;
  }

  public PriceDerivationRuleBase externalAction(ExternalActionType externalAction) {
    this.externalAction = externalAction;
    return this;
  }

   /**
   * Get externalAction
   * @return externalAction
  **/
  @Schema(description = "")
  public ExternalActionType getExternalAction() {
    return externalAction;
  }

  public void setExternalAction(ExternalActionType externalAction) {
    this.externalAction = externalAction;
  }

  public PriceDerivationRuleBase operatorDisplayText(List<DescriptionCommonData> operatorDisplayText) {
    this.operatorDisplayText = operatorDisplayText;
    return this;
  }

  public PriceDerivationRuleBase addOperatorDisplayTextItem(DescriptionCommonData operatorDisplayTextItem) {
    if (this.operatorDisplayText == null) {
      this.operatorDisplayText = new ArrayList<DescriptionCommonData>();
    }
    this.operatorDisplayText.add(operatorDisplayTextItem);
    return this;
  }

   /**
   * Texts shown to the operator in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.
   * @return operatorDisplayText
  **/
  @Schema(description = "Texts shown to the operator in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.")
  public List<DescriptionCommonData> getOperatorDisplayText() {
    return operatorDisplayText;
  }

  public void setOperatorDisplayText(List<DescriptionCommonData> operatorDisplayText) {
    this.operatorDisplayText = operatorDisplayText;
  }

  public PriceDerivationRuleBase customerDisplayText(List<DescriptionCommonData> customerDisplayText) {
    this.customerDisplayText = customerDisplayText;
    return this;
  }

  public PriceDerivationRuleBase addCustomerDisplayTextItem(DescriptionCommonData customerDisplayTextItem) {
    if (this.customerDisplayText == null) {
      this.customerDisplayText = new ArrayList<DescriptionCommonData>();
    }
    this.customerDisplayText.add(customerDisplayTextItem);
    return this;
  }

   /**
   * Texts shown to the customer in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.
   * @return customerDisplayText
  **/
  @Schema(description = "Texts shown to the customer in the languages requested by the client via RequestedMultiLanguage in the ARTSHeader. Only provided with Client API version 2.0 or higher.")
  public List<DescriptionCommonData> getCustomerDisplayText() {
    return customerDisplayText;
  }

  public void setCustomerDisplayText(List<DescriptionCommonData> customerDisplayText) {
    this.customerDisplayText = customerDisplayText;
  }

  public PriceDerivationRuleBase promotionType(String promotionType) {
    this.promotionType = promotionType;
    return this;
  }

   /**
   * Can be used to control further processing on client side. Only provided with Client API version 2.0 or higher.
   * @return promotionType
  **/
  @Schema(description = "Can be used to control further processing on client side. Only provided with Client API version 2.0 or higher.")
  public String getPromotionType() {
    return promotionType;
  }

  public void setPromotionType(String promotionType) {
    this.promotionType = promotionType;
  }

  public PriceDerivationRuleBase calculationBaseSequence(String calculationBaseSequence) {
    this.calculationBaseSequence = calculationBaseSequence;
    return this;
  }

   /**
   * The sequence value of the price derivation rule of which the result is taken as the basis for this price derivation rule. Only provided with Client API version 2.0 or higher. The type of this element is a string although its content has a numeric value. It could also contain a negative value. In that case the corresponding sign is reflected as a postfix. For example value  \&quot;-1\&quot; is reflected in this element by type string as follows: \&quot;1-\&quot;. 
   * @return calculationBaseSequence
  **/
  @Schema(description = "The sequence value of the price derivation rule of which the result is taken as the basis for this price derivation rule. Only provided with Client API version 2.0 or higher. The type of this element is a string although its content has a numeric value. It could also contain a negative value. In that case the corresponding sign is reflected as a postfix. For example value  \"-1\" is reflected in this element by type string as follows: \"1-\". ")
  public String getCalculationBaseSequence() {
    return calculationBaseSequence;
  }

  public void setCalculationBaseSequence(String calculationBaseSequence) {
    this.calculationBaseSequence = calculationBaseSequence;
  }

  public PriceDerivationRuleBase promotionID(String promotionID) {
    this.promotionID = promotionID;
    return this;
  }

   /**
   * Promotion identifier. This is filled if the provided Client API version of the calculation request is 5.0 or higher. Has the same content as the PromotionID of the RetailPriceModifier etc. but should be preferred over the other fields to support cases where an applied promotion does not lead to the creation of a RetailPriceModifier, FrequentShopperPointsModifier etc. 
   * @return promotionID
  **/
  @Schema(description = "Promotion identifier. This is filled if the provided Client API version of the calculation request is 5.0 or higher. Has the same content as the PromotionID of the RetailPriceModifier etc. but should be preferred over the other fields to support cases where an applied promotion does not lead to the creation of a RetailPriceModifier, FrequentShopperPointsModifier etc. ")
  public String getPromotionID() {
    return promotionID;
  }

  public void setPromotionID(String promotionID) {
    this.promotionID = promotionID;
  }

  public PriceDerivationRuleBase applicationType(ApplicationTypeEnum applicationType) {
    this.applicationType = applicationType;
    return this;
  }

   /**
   * Describes the type of the applied price derivation rule.
   * @return applicationType
  **/
  @Schema(description = "Describes the type of the applied price derivation rule.")
  public ApplicationTypeEnum getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationTypeEnum applicationType) {
    this.applicationType = applicationType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceDerivationRuleBase priceDerivationRuleBase = (PriceDerivationRuleBase) o;
    return Objects.equals(this.priceDerivationRuleID, priceDerivationRuleBase.priceDerivationRuleID) &&
        Objects.equals(this.eligibility, priceDerivationRuleBase.eligibility) &&
        Objects.equals(this.any, priceDerivationRuleBase.any) &&
        Objects.equals(this.promotionDescription, priceDerivationRuleBase.promotionDescription) &&
        Objects.equals(this.promotionDescriptionMultiLanguage, priceDerivationRuleBase.promotionDescriptionMultiLanguage) &&
        Objects.equals(this.promotionPriceDerivationRuleSequence, priceDerivationRuleBase.promotionPriceDerivationRuleSequence) &&
        Objects.equals(this.promotionPriceDerivationRuleResolution, priceDerivationRuleBase.promotionPriceDerivationRuleResolution) &&
        Objects.equals(this.promotionPriceDerivationRuleTypeCode, priceDerivationRuleBase.promotionPriceDerivationRuleTypeCode) &&
        Objects.equals(this.transactionControlBreakCode, priceDerivationRuleBase.transactionControlBreakCode) &&
        Objects.equals(this.priceDerivationRuleDescription, priceDerivationRuleBase.priceDerivationRuleDescription) &&
        Objects.equals(this.promotionOriginatorTypeCode, priceDerivationRuleBase.promotionOriginatorTypeCode) &&
        Objects.equals(this.triggerQuantity, priceDerivationRuleBase.triggerQuantity) &&
        Objects.equals(this.discountMethodCode, priceDerivationRuleBase.discountMethodCode) &&
        Objects.equals(this.frequentShopperPointsFlag, priceDerivationRuleBase.frequentShopperPointsFlag) &&
        Objects.equals(this.customerGroupLoyaltyPointsDefaultQuantity, priceDerivationRuleBase.customerGroupLoyaltyPointsDefaultQuantity) &&
        Objects.equals(this.prohibitPrintFlag, priceDerivationRuleBase.prohibitPrintFlag) &&
        Objects.equals(this.tenderTypeCode, priceDerivationRuleBase.tenderTypeCode) &&
        Objects.equals(this.pointsConversionAmount, priceDerivationRuleBase.pointsConversionAmount) &&
        Objects.equals(this.noEffectOnSubsequentPriceDerivationRulesFlag, priceDerivationRuleBase.noEffectOnSubsequentPriceDerivationRulesFlag) &&
        Objects.equals(this.prohibitTransactionRelatedPriceDerivationRulesFlag, priceDerivationRuleBase.prohibitTransactionRelatedPriceDerivationRulesFlag) &&
        Objects.equals(this.exclusiveFlag, priceDerivationRuleBase.exclusiveFlag) &&
        Objects.equals(this.concurrenceControlVector, priceDerivationRuleBase.concurrenceControlVector) &&
        Objects.equals(this.appliedCount, priceDerivationRuleBase.appliedCount) &&
        Objects.equals(this.receiptLine, priceDerivationRuleBase.receiptLine) &&
        Objects.equals(this.receiptLineMultiLanguage, priceDerivationRuleBase.receiptLineMultiLanguage) &&
        Objects.equals(this.externalPromotionID, priceDerivationRuleBase.externalPromotionID) &&
        Objects.equals(this.externalPriceDerivationRuleID, priceDerivationRuleBase.externalPriceDerivationRuleID) &&
        Objects.equals(this.couponPrintoutID, priceDerivationRuleBase.couponPrintoutID) &&
        Objects.equals(this.couponPrintoutRule, priceDerivationRuleBase.couponPrintoutRule) &&
        Objects.equals(this.couponPrintoutText, priceDerivationRuleBase.couponPrintoutText) &&
        Objects.equals(this.printoutValidityPeriod, priceDerivationRuleBase.printoutValidityPeriod) &&
        Objects.equals(this.giftCertificateExpirationDate, priceDerivationRuleBase.giftCertificateExpirationDate) &&
        Objects.equals(this.externalAction, priceDerivationRuleBase.externalAction) &&
        Objects.equals(this.operatorDisplayText, priceDerivationRuleBase.operatorDisplayText) &&
        Objects.equals(this.customerDisplayText, priceDerivationRuleBase.customerDisplayText) &&
        Objects.equals(this.promotionType, priceDerivationRuleBase.promotionType) &&
        Objects.equals(this.calculationBaseSequence, priceDerivationRuleBase.calculationBaseSequence) &&
        Objects.equals(this.promotionID, priceDerivationRuleBase.promotionID) &&
        Objects.equals(this.applicationType, priceDerivationRuleBase.applicationType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(priceDerivationRuleID, eligibility, any, promotionDescription, promotionDescriptionMultiLanguage, promotionPriceDerivationRuleSequence, promotionPriceDerivationRuleResolution, promotionPriceDerivationRuleTypeCode, transactionControlBreakCode, priceDerivationRuleDescription, promotionOriginatorTypeCode, triggerQuantity, discountMethodCode, frequentShopperPointsFlag, customerGroupLoyaltyPointsDefaultQuantity, prohibitPrintFlag, tenderTypeCode, pointsConversionAmount, noEffectOnSubsequentPriceDerivationRulesFlag, prohibitTransactionRelatedPriceDerivationRulesFlag, exclusiveFlag, concurrenceControlVector, appliedCount, receiptLine, receiptLineMultiLanguage, externalPromotionID, externalPriceDerivationRuleID, couponPrintoutID, couponPrintoutRule, couponPrintoutText, printoutValidityPeriod, giftCertificateExpirationDate, externalAction, operatorDisplayText, customerDisplayText, promotionType, calculationBaseSequence, promotionID, applicationType);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceDerivationRuleBase {\n");
    
    sb.append("    priceDerivationRuleID: ").append(toIndentedString(priceDerivationRuleID)).append("\n");
    sb.append("    eligibility: ").append(toIndentedString(eligibility)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    promotionDescription: ").append(toIndentedString(promotionDescription)).append("\n");
    sb.append("    promotionDescriptionMultiLanguage: ").append(toIndentedString(promotionDescriptionMultiLanguage)).append("\n");
    sb.append("    promotionPriceDerivationRuleSequence: ").append(toIndentedString(promotionPriceDerivationRuleSequence)).append("\n");
    sb.append("    promotionPriceDerivationRuleResolution: ").append(toIndentedString(promotionPriceDerivationRuleResolution)).append("\n");
    sb.append("    promotionPriceDerivationRuleTypeCode: ").append(toIndentedString(promotionPriceDerivationRuleTypeCode)).append("\n");
    sb.append("    transactionControlBreakCode: ").append(toIndentedString(transactionControlBreakCode)).append("\n");
    sb.append("    priceDerivationRuleDescription: ").append(toIndentedString(priceDerivationRuleDescription)).append("\n");
    sb.append("    promotionOriginatorTypeCode: ").append(toIndentedString(promotionOriginatorTypeCode)).append("\n");
    sb.append("    triggerQuantity: ").append(toIndentedString(triggerQuantity)).append("\n");
    sb.append("    discountMethodCode: ").append(toIndentedString(discountMethodCode)).append("\n");
    sb.append("    frequentShopperPointsFlag: ").append(toIndentedString(frequentShopperPointsFlag)).append("\n");
    sb.append("    customerGroupLoyaltyPointsDefaultQuantity: ").append(toIndentedString(customerGroupLoyaltyPointsDefaultQuantity)).append("\n");
    sb.append("    prohibitPrintFlag: ").append(toIndentedString(prohibitPrintFlag)).append("\n");
    sb.append("    tenderTypeCode: ").append(toIndentedString(tenderTypeCode)).append("\n");
    sb.append("    pointsConversionAmount: ").append(toIndentedString(pointsConversionAmount)).append("\n");
    sb.append("    noEffectOnSubsequentPriceDerivationRulesFlag: ").append(toIndentedString(noEffectOnSubsequentPriceDerivationRulesFlag)).append("\n");
    sb.append("    prohibitTransactionRelatedPriceDerivationRulesFlag: ").append(toIndentedString(prohibitTransactionRelatedPriceDerivationRulesFlag)).append("\n");
    sb.append("    exclusiveFlag: ").append(toIndentedString(exclusiveFlag)).append("\n");
    sb.append("    concurrenceControlVector: ").append(toIndentedString(concurrenceControlVector)).append("\n");
    sb.append("    appliedCount: ").append(toIndentedString(appliedCount)).append("\n");
    sb.append("    receiptLine: ").append(toIndentedString(receiptLine)).append("\n");
    sb.append("    receiptLineMultiLanguage: ").append(toIndentedString(receiptLineMultiLanguage)).append("\n");
    sb.append("    externalPromotionID: ").append(toIndentedString(externalPromotionID)).append("\n");
    sb.append("    externalPriceDerivationRuleID: ").append(toIndentedString(externalPriceDerivationRuleID)).append("\n");
    sb.append("    couponPrintoutID: ").append(toIndentedString(couponPrintoutID)).append("\n");
    sb.append("    couponPrintoutRule: ").append(toIndentedString(couponPrintoutRule)).append("\n");
    sb.append("    couponPrintoutText: ").append(toIndentedString(couponPrintoutText)).append("\n");
    sb.append("    printoutValidityPeriod: ").append(toIndentedString(printoutValidityPeriod)).append("\n");
    sb.append("    giftCertificateExpirationDate: ").append(toIndentedString(giftCertificateExpirationDate)).append("\n");
    sb.append("    externalAction: ").append(toIndentedString(externalAction)).append("\n");
    sb.append("    operatorDisplayText: ").append(toIndentedString(operatorDisplayText)).append("\n");
    sb.append("    customerDisplayText: ").append(toIndentedString(customerDisplayText)).append("\n");
    sb.append("    promotionType: ").append(toIndentedString(promotionType)).append("\n");
    sb.append("    calculationBaseSequence: ").append(toIndentedString(calculationBaseSequence)).append("\n");
    sb.append("    promotionID: ").append(toIndentedString(promotionID)).append("\n");
    sb.append("    applicationType: ").append(toIndentedString(applicationType)).append("\n");
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

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
 * Contains relevant information for the price calculation like the items, date and time, and more.
 */
@Schema(description = "Contains relevant information for the price calculation like the items, date and time, and more.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PriceCalculateBase {
  @JsonProperty("TransactionID")
  private IDCommonData transactionID = null;

  @JsonProperty("DateTime")
  private DateTimeCommonData dateTime = null;

  @JsonProperty("Loyalty")
  private List<LoyaltyAccountCommonData> loyalty = null;

  @JsonProperty("ShoppingBasket")
  private ShoppingBasketBase shoppingBasket = null;

  @JsonProperty("RegularSalesUnitPriceRoundingRule")
  private RoundingRuleType regularSalesUnitPriceRoundingRule = null;

  @JsonProperty("any")
  private List<Object> any = null;

  /**
   * Indicates whether in this transaction items are sold or returned.
   */
  public enum TransactionTypeEnum {
    SALETRANSACTION("SaleTransaction");

    private String value;

    TransactionTypeEnum(String value) {
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
    public static TransactionTypeEnum fromValue(String input) {
      for (TransactionTypeEnum b : TransactionTypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("TransactionType")
  private TransactionTypeEnum transactionType = null;

  @JsonProperty("NetPriceFlag")
  private Boolean netPriceFlag = null;

  /**
   * Controls if the items of the ShoppingBasket element are considered as part of a shared basket (\&quot;Basket\&quot;) or if they are treated as separate items (\&quot;LineItem\&quot;). If not specified, \&quot;Basket\&quot; is used as default. 
   */
  public enum CalculationModeEnum {
    BASKET("Basket"),
    LINEITEM("LineItem");

    private String value;

    CalculationModeEnum(String value) {
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
    public static CalculationModeEnum fromValue(String input) {
      for (CalculationModeEnum b : CalculationModeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("CalculationMode")
  private CalculationModeEnum calculationMode = null;

  /**
   * Controls which price derivation rules are used for the price calculation. This is defined by the transaction control break code/price rule control code and the point in time for the application of price derivation rules (immediately or after subtotal).  For the calculation scheme type &#x27;CalcAll&#x27;, all transaction control break codes/price rule control codes can be applied (PC, PO, SP, SU). For the calculation scheme type \&quot;CalcForItem\&quot;, only rules with transaction control break code/price rule control code PO and SP can be applied. If not specified, \&quot;CalcAll\&quot; is used as default. The calculation scheme is available as of version 4.0. 
   */
  public enum CalculationSchemeEnum {
    CALCALL("CalcAll"),
    CALCFORITEM("CalcForItem");

    private String value;

    CalculationSchemeEnum(String value) {
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
    public static CalculationSchemeEnum fromValue(String input) {
      for (CalculationSchemeEnum b : CalculationSchemeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("CalculationScheme")
  private CalculationSchemeEnum calculationScheme = null;

  @JsonProperty("TxAttribute")
  private List<TransactionAttributeType> txAttribute = null;

  public PriceCalculateBase transactionID(IDCommonData transactionID) {
    this.transactionID = transactionID;
    return this;
  }

   /**
   * Get transactionID
   * @return transactionID
  **/
  @Schema(description = "")
  public IDCommonData getTransactionID() {
    return transactionID;
  }

  public void setTransactionID(IDCommonData transactionID) {
    this.transactionID = transactionID;
  }

  public PriceCalculateBase dateTime(DateTimeCommonData dateTime) {
    this.dateTime = dateTime;
    return this;
  }

   /**
   * Get dateTime
   * @return dateTime
  **/
  @Schema(description = "")
  public DateTimeCommonData getDateTime() {
    return dateTime;
  }

  public void setDateTime(DateTimeCommonData dateTime) {
    this.dateTime = dateTime;
  }

  public PriceCalculateBase loyalty(List<LoyaltyAccountCommonData> loyalty) {
    this.loyalty = loyalty;
    return this;
  }

  public PriceCalculateBase addLoyaltyItem(LoyaltyAccountCommonData loyaltyItem) {
    if (this.loyalty == null) {
      this.loyalty = new ArrayList<LoyaltyAccountCommonData>();
    }
    this.loyalty.add(loyaltyItem);
    return this;
  }

   /**
   * Refers to the end customer.
   * @return loyalty
  **/
  @Schema(description = "Refers to the end customer.")
  public List<LoyaltyAccountCommonData> getLoyalty() {
    return loyalty;
  }

  public void setLoyalty(List<LoyaltyAccountCommonData> loyalty) {
    this.loyalty = loyalty;
  }

  public PriceCalculateBase shoppingBasket(ShoppingBasketBase shoppingBasket) {
    this.shoppingBasket = shoppingBasket;
    return this;
  }

   /**
   * Get shoppingBasket
   * @return shoppingBasket
  **/
  @Schema(required = true, description = "")
  public ShoppingBasketBase getShoppingBasket() {
    return shoppingBasket;
  }

  public void setShoppingBasket(ShoppingBasketBase shoppingBasket) {
    this.shoppingBasket = shoppingBasket;
  }

  public PriceCalculateBase regularSalesUnitPriceRoundingRule(RoundingRuleType regularSalesUnitPriceRoundingRule) {
    this.regularSalesUnitPriceRoundingRule = regularSalesUnitPriceRoundingRule;
    return this;
  }

   /**
   * Get regularSalesUnitPriceRoundingRule
   * @return regularSalesUnitPriceRoundingRule
  **/
  @Schema(description = "")
  public RoundingRuleType getRegularSalesUnitPriceRoundingRule() {
    return regularSalesUnitPriceRoundingRule;
  }

  public void setRegularSalesUnitPriceRoundingRule(RoundingRuleType regularSalesUnitPriceRoundingRule) {
    this.regularSalesUnitPriceRoundingRule = regularSalesUnitPriceRoundingRule;
  }

  public PriceCalculateBase any(List<Object> any) {
    this.any = any;
    return this;
  }

  public PriceCalculateBase addAnyItem(Object anyItem) {
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

  public PriceCalculateBase transactionType(TransactionTypeEnum transactionType) {
    this.transactionType = transactionType;
    return this;
  }

   /**
   * Indicates whether in this transaction items are sold or returned.
   * @return transactionType
  **/
  @Schema(description = "Indicates whether in this transaction items are sold or returned.")
  public TransactionTypeEnum getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(TransactionTypeEnum transactionType) {
    this.transactionType = transactionType;
  }

  public PriceCalculateBase netPriceFlag(Boolean netPriceFlag) {
    this.netPriceFlag = netPriceFlag;
    return this;
  }

   /**
   * If set to true, the net regular prices are used for the price calculation. Otherwise, the gross prices are considered.
   * @return netPriceFlag
  **/
  @Schema(required = true, description = "If set to true, the net regular prices are used for the price calculation. Otherwise, the gross prices are considered.")
  public Boolean isNetPriceFlag() {
    return netPriceFlag;
  }

  public void setNetPriceFlag(Boolean netPriceFlag) {
    this.netPriceFlag = netPriceFlag;
  }

  public PriceCalculateBase calculationMode(CalculationModeEnum calculationMode) {
    this.calculationMode = calculationMode;
    return this;
  }

   /**
   * Controls if the items of the ShoppingBasket element are considered as part of a shared basket (\&quot;Basket\&quot;) or if they are treated as separate items (\&quot;LineItem\&quot;). If not specified, \&quot;Basket\&quot; is used as default. 
   * @return calculationMode
  **/
  @Schema(example = "Basket", description = "Controls if the items of the ShoppingBasket element are considered as part of a shared basket (\"Basket\") or if they are treated as separate items (\"LineItem\"). If not specified, \"Basket\" is used as default. ")
  public CalculationModeEnum getCalculationMode() {
    return calculationMode;
  }

  public void setCalculationMode(CalculationModeEnum calculationMode) {
    this.calculationMode = calculationMode;
  }

  public PriceCalculateBase calculationScheme(CalculationSchemeEnum calculationScheme) {
    this.calculationScheme = calculationScheme;
    return this;
  }

   /**
   * Controls which price derivation rules are used for the price calculation. This is defined by the transaction control break code/price rule control code and the point in time for the application of price derivation rules (immediately or after subtotal).  For the calculation scheme type &#x27;CalcAll&#x27;, all transaction control break codes/price rule control codes can be applied (PC, PO, SP, SU). For the calculation scheme type \&quot;CalcForItem\&quot;, only rules with transaction control break code/price rule control code PO and SP can be applied. If not specified, \&quot;CalcAll\&quot; is used as default. The calculation scheme is available as of version 4.0. 
   * @return calculationScheme
  **/
  @Schema(example = "CalcAll", description = "Controls which price derivation rules are used for the price calculation. This is defined by the transaction control break code/price rule control code and the point in time for the application of price derivation rules (immediately or after subtotal).  For the calculation scheme type 'CalcAll', all transaction control break codes/price rule control codes can be applied (PC, PO, SP, SU). For the calculation scheme type \"CalcForItem\", only rules with transaction control break code/price rule control code PO and SP can be applied. If not specified, \"CalcAll\" is used as default. The calculation scheme is available as of version 4.0. ")
  public CalculationSchemeEnum getCalculationScheme() {
    return calculationScheme;
  }

  public void setCalculationScheme(CalculationSchemeEnum calculationScheme) {
    this.calculationScheme = calculationScheme;
  }

  public PriceCalculateBase txAttribute(List<TransactionAttributeType> txAttribute) {
    this.txAttribute = txAttribute;
    return this;
  }

  public PriceCalculateBase addTxAttributeItem(TransactionAttributeType txAttributeItem) {
    if (this.txAttribute == null) {
      this.txAttribute = new ArrayList<TransactionAttributeType>();
    }
    this.txAttribute.add(txAttributeItem);
    return this;
  }

   /**
   * Generic attributes on transaction level in the price calculation request. These generic attributes can be used  as trigger for a promotional rule.
   * @return txAttribute
  **/
  @Schema(description = "Generic attributes on transaction level in the price calculation request. These generic attributes can be used  as trigger for a promotional rule.")
  public List<TransactionAttributeType> getTxAttribute() {
    return txAttribute;
  }

  public void setTxAttribute(List<TransactionAttributeType> txAttribute) {
    this.txAttribute = txAttribute;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceCalculateBase priceCalculateBase = (PriceCalculateBase) o;
    return Objects.equals(this.transactionID, priceCalculateBase.transactionID) &&
        Objects.equals(this.dateTime, priceCalculateBase.dateTime) &&
        Objects.equals(this.loyalty, priceCalculateBase.loyalty) &&
        Objects.equals(this.shoppingBasket, priceCalculateBase.shoppingBasket) &&
        Objects.equals(this.regularSalesUnitPriceRoundingRule, priceCalculateBase.regularSalesUnitPriceRoundingRule) &&
        Objects.equals(this.any, priceCalculateBase.any) &&
        Objects.equals(this.transactionType, priceCalculateBase.transactionType) &&
        Objects.equals(this.netPriceFlag, priceCalculateBase.netPriceFlag) &&
        Objects.equals(this.calculationMode, priceCalculateBase.calculationMode) &&
        Objects.equals(this.calculationScheme, priceCalculateBase.calculationScheme) &&
        Objects.equals(this.txAttribute, priceCalculateBase.txAttribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transactionID, dateTime, loyalty, shoppingBasket, regularSalesUnitPriceRoundingRule, any, transactionType, netPriceFlag, calculationMode, calculationScheme, txAttribute);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PriceCalculateBase {\n");
    
    sb.append("    transactionID: ").append(toIndentedString(transactionID)).append("\n");
    sb.append("    dateTime: ").append(toIndentedString(dateTime)).append("\n");
    sb.append("    loyalty: ").append(toIndentedString(loyalty)).append("\n");
    sb.append("    shoppingBasket: ").append(toIndentedString(shoppingBasket)).append("\n");
    sb.append("    regularSalesUnitPriceRoundingRule: ").append(toIndentedString(regularSalesUnitPriceRoundingRule)).append("\n");
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    transactionType: ").append(toIndentedString(transactionType)).append("\n");
    sb.append("    netPriceFlag: ").append(toIndentedString(netPriceFlag)).append("\n");
    sb.append("    calculationMode: ").append(toIndentedString(calculationMode)).append("\n");
    sb.append("    calculationScheme: ").append(toIndentedString(calculationScheme)).append("\n");
    sb.append("    txAttribute: ").append(toIndentedString(txAttribute)).append("\n");
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

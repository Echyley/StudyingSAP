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
 * Specifies how the regular sales price should be rounded after multiplying the unit price with the provided quantity.
 */
@Schema(description = "Specifies how the regular sales price should be rounded after multiplying the unit price with the provided quantity.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class RoundingRuleType {
  @JsonProperty("any")
  private List<Object> any = null;

  /**
   * Specifies the rounding direction. Default value is Commercial.
   */
  public enum RoundingMethodEnum {
    UP("Up"),
    DOWN("Down"),
    COMMERCIAL("Commercial");

    private String value;

    RoundingMethodEnum(String value) {
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
    public static RoundingMethodEnum fromValue(String input) {
      for (RoundingMethodEnum b : RoundingMethodEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("RoundingMethod")
  private RoundingMethodEnum roundingMethod = null;

  @JsonProperty("Multiple")
  private BigDecimal multiple = null;

  public RoundingRuleType any(List<Object> any) {
    this.any = any;
    return this;
  }

  public RoundingRuleType addAnyItem(Object anyItem) {
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

  public RoundingRuleType roundingMethod(RoundingMethodEnum roundingMethod) {
    this.roundingMethod = roundingMethod;
    return this;
  }

   /**
   * Specifies the rounding direction. Default value is Commercial.
   * @return roundingMethod
  **/
  @Schema(description = "Specifies the rounding direction. Default value is Commercial.")
  public RoundingMethodEnum getRoundingMethod() {
    return roundingMethod;
  }

  public void setRoundingMethod(RoundingMethodEnum roundingMethod) {
    this.roundingMethod = roundingMethod;
  }

  public RoundingRuleType multiple(BigDecimal multiple) {
    this.multiple = multiple;
    return this;
  }

   /**
   * The rounding result must be an integer multiple of the specified value. Example: If the value to be rounded is 12.345, rounding method is Commercial and Multple is 0.05, then the rounding result is 12.35. Default value is 0.01. 
   * minimum: 0
   * @return multiple
  **/
  @Schema(example = "0.01", description = "The rounding result must be an integer multiple of the specified value. Example: If the value to be rounded is 12.345, rounding method is Commercial and Multple is 0.05, then the rounding result is 12.35. Default value is 0.01. ")
  public BigDecimal getMultiple() {
    return multiple;
  }

  public void setMultiple(BigDecimal multiple) {
    this.multiple = multiple;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoundingRuleType roundingRuleType = (RoundingRuleType) o;
    return Objects.equals(this.any, roundingRuleType.any) &&
        Objects.equals(this.roundingMethod, roundingRuleType.roundingMethod) &&
        Objects.equals(this.multiple, roundingRuleType.multiple);
  }

  @Override
  public int hashCode() {
    return Objects.hash(any, roundingMethod, multiple);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoundingRuleType {\n");
    
    sb.append("    any: ").append(toIndentedString(any)).append("\n");
    sb.append("    roundingMethod: ").append(toIndentedString(roundingMethod)).append("\n");
    sb.append("    multiple: ").append(toIndentedString(multiple)).append("\n");
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

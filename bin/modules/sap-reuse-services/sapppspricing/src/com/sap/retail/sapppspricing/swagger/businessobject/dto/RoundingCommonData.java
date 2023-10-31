/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.swagger.businessobject.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Objects;
/**
 * Contains information about the rounding of discounts.
 */
@Schema(description = "Contains information about the rounding of discounts.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class RoundingCommonData {
  @JsonProperty("value")
  private BigDecimal value = null;

  @JsonProperty("Currency")
  private String currency = null;

  /**
   * Describes whether the discount was rounded up or down.
   */
  public enum RoundingDirectionEnum {
    UP("Up"),
    DOWN("Down");

    private String value;

    RoundingDirectionEnum(String value) {
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
    public static RoundingDirectionEnum fromValue(String input) {
      for (RoundingDirectionEnum b : RoundingDirectionEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("RoundingDirection")
  private RoundingDirectionEnum roundingDirection = null;

  public RoundingCommonData value(BigDecimal value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * minimum: 0
   * @return value
  **/
  @Schema(description = "")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public RoundingCommonData currency(String currency) {
    this.currency = currency;
    return this;
  }

   /**
   * Uppercase currency is expected in uppercase ISO format.
   * @return currency
  **/
  @Schema(description = "Uppercase currency is expected in uppercase ISO format.")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public RoundingCommonData roundingDirection(RoundingDirectionEnum roundingDirection) {
    this.roundingDirection = roundingDirection;
    return this;
  }

   /**
   * Describes whether the discount was rounded up or down.
   * @return roundingDirection
  **/
  @Schema(description = "Describes whether the discount was rounded up or down.")
  public RoundingDirectionEnum getRoundingDirection() {
    return roundingDirection;
  }

  public void setRoundingDirection(RoundingDirectionEnum roundingDirection) {
    this.roundingDirection = roundingDirection;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RoundingCommonData roundingCommonData = (RoundingCommonData) o;
    return Objects.equals(this.value, roundingCommonData.value) &&
        Objects.equals(this.currency, roundingCommonData.currency) &&
        Objects.equals(this.roundingDirection, roundingCommonData.roundingDirection);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, currency, roundingDirection);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RoundingCommonData {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    roundingDirection: ").append(toIndentedString(roundingDirection)).append("\n");
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

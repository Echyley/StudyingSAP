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
 * Specifies how many points were used by the price derivation rule.
 */
@Schema(description = "Specifies how many points were used by the price derivation rule.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class PointsAwarded {
  @JsonProperty("value")
  private BigDecimal value = null;

  /**
   * Specifies whether the points were earned or something else.
   */
  public enum TypeEnum {
    POINTSEARNED("PointsEarned");

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

  public PointsAwarded value(BigDecimal value) {
    this.value = value;
    return this;
  }

   /**
   * Number of points
   * minimum: 0
   * @return value
  **/
  @Schema(description = "Number of points")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public PointsAwarded type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * Specifies whether the points were earned or something else.
   * @return type
  **/
  @Schema(description = "Specifies whether the points were earned or something else.")
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
    PointsAwarded pointsAwarded = (PointsAwarded) o;
    return Objects.equals(this.value, pointsAwarded.value) &&
        Objects.equals(this.type, pointsAwarded.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, type);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PointsAwarded {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

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
 * Percentage discount and the action how it was applied.
 */
@Schema(description = "Percentage discount and the action how it was applied.")
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-09-26T19:16:22.658615400+05:30[Asia/Calcutta]")
public class Percent {
  @JsonProperty("value")
  private BigDecimal value = null;

  /**
   * Specifies whether the percentage was added or subtracted.
   */
  public enum ActionEnum {
    ADD("Add"),
    SUBTRACT("Subtract");

    private String value;

    ActionEnum(String value) {
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
    public static ActionEnum fromValue(String input) {
      for (ActionEnum b : ActionEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Action")
  private ActionEnum action = null;

  public Percent value(BigDecimal value) {
    this.value = value;
    return this;
  }

   /**
   * The value that is, for example, used for a percentage discount. It has to be a positive number.
   * minimum: 0
   * @return value
  **/
  @Schema(description = "The value that is, for example, used for a percentage discount. It has to be a positive number.")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public Percent action(ActionEnum action) {
    this.action = action;
    return this;
  }

   /**
   * Specifies whether the percentage was added or subtracted.
   * @return action
  **/
  @Schema(required = true, description = "Specifies whether the percentage was added or subtracted.")
  public ActionEnum getAction() {
    return action;
  }

  public void setAction(ActionEnum action) {
    this.action = action;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Percent percent = (Percent) o;
    return Objects.equals(this.value, percent.value) &&
        Objects.equals(this.action, percent.action);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, action);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Percent {\n");
    
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    action: ").append(toIndentedString(action)).append("\n");
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

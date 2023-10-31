/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.filter.dto;

public class FilterData {
	private final String key;	
	private final String operator;
	private final String value;
	private final String separator;
	
	private FilterData(FilterDataBuilder builder) {
		this.key = builder.key;
		this.operator = builder.operator;
		this.value = builder.value;
		this.separator = builder.separator;
	}

	public String getKey() {
		return key;
	}

	public String getOperator() {
		return operator;
	}

	public String getValue() {
		return value;
	}

	public String getSeparator() {
		return separator;
	}
	
	@Override
	public String toString() {
		return "FilterData: "+this.key+", "+this.operator+", "+this.value+", "+this.separator;
	}
	
	public static class FilterDataBuilder {
		private static final String SPACE_CHARACTER = " ";
		private final String key;	
		private String operator;
		private String value;
		private String separator;
		
		public FilterDataBuilder(String key) {
			this.key = key;
		}
		
		public FilterDataBuilder filterDataOperator(String operator) {
			this.operator = operator;
			return this;
		}
		public FilterDataBuilder filterDataValue(String value) {
			this.value = value;
			return this;
		}
		public FilterDataBuilder filterDataSeparator(String separator) {
			this.separator = separator;
			return this;
		}
		
		public FilterDataBuilder operatorWithSpacePrefix(String operator) {
			this.operator = SPACE_CHARACTER.concat(operator);
			return this;
		}
		public FilterDataBuilder valueWithSpacePrefix(String value) {
			this.value = SPACE_CHARACTER.concat(value);
			return this;
		}
		public FilterDataBuilder separatorWithSpacePrefix(String separator) {
			this.separator = SPACE_CHARACTER.concat(separator);
			return this;
		}
		
		
		
		public FilterData build() {
			return new FilterData(this);
		}
	}
}

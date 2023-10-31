/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Mock implementation for a configurable pipe
 */
public class ConfPipeMockImpl extends BaseRunTimeConfigMockImpl
{
	protected static final String PLASTIC_TYPE = "PL";
	protected static final String STEEL_TYPE = "ST";
	protected static final String ROOT_INSTANCE_LANG_DEP_NAME = "Pipe";
	protected static final String ROOT_INSTANCE_NAME = "CONF_M_PIPE";
	protected static final String TYPE_NAME = "CPQ_PIPE_TYPE";
	protected static final String LENGTH_NAME = "CPQ_PIPE_LENGTH";
	protected static final String OUTER_DIA_NAME = "CPQ_PIPE_OUTER";
	protected static final String INNER_DIA_NAME = "CPQ_PIPE_INNER";
	protected static final String NAME = "name";
	protected static final String VALUE_15 = "15";
	protected static final String VALUE_20 = "20";
	protected static final String VALUE_25 = "25";
	protected static final String VALUE_30 = "30";
	protected static final String VALUE_40 = "40";

	public static final int VARIANT_PARAM_NAME = 0;
	public static final int VARIANT_PARAM_OUTER_DIA_NAME = 1;
	public static final int VARIANT_PARAM_INNER_DIA_NAME = 2;
	public static final int VARIANT_PARAM_TYPE_NAME = 3;
	public static final int DEFAULT_PIPE_LENGTH_TYPE_LENGTH = 4;
	private static final int NUMBER_OF_PARTS_IN_VARIANTS = 4;

	private final String variantProuctCode;

	public ConfPipeMockImpl(final String variantProuctCode)
	{
		super();
		this.variantProuctCode = variantProuctCode;
	}

	@Override
	public ConfigModel createDefaultConfiguration()
	{
		// Model
		final ConfigModel model = createDefaultConfigModel();
		// root instance
		final InstanceModel rootInstance = createDefaultRootInstance(model, ROOT_INSTANCE_NAME, ROOT_INSTANCE_LANG_DEP_NAME);

		// Characteristics and Values
		final List<CsticModel> cstics = new ArrayList<>();
		final Map<String, String> variantParams = createVariantParams(variantProuctCode);
		cstics.add(createType(variantParams));
		cstics.add(createOuterDiameter(variantParams));
		cstics.add(createInnerDiameter(variantParams));
		cstics.add(createLength());
		rootInstance.setCstics(cstics);

		// groups
		final List<CsticGroupModel> groups = new ArrayList<>();
		addCsticGroup(groups, InstanceModel.GENERAL_GROUP_NAME, null, TYPE_NAME, OUTER_DIA_NAME, INNER_DIA_NAME, LENGTH_NAME);
		rootInstance.setCsticGroups(groups);

		return model;
	}

	protected Map<String,String> createVariantParams(final String productCode)
	{
		final Map<String, String> variantParams = new HashMap<>(6);
		if (null != productCode)
		{
			// CONF_M_PIPE-OD-ID-TN ==> OD = outer diamater; ID = inner diameter; TN = type name
			// CONF_M_PIPE-30-15-PL ==> OD = 30; ID = 15; PN = PL (plastic)
			final String[] components = productCode.split("-");
			if (components.length != NUMBER_OF_PARTS_IN_VARIANTS)
			{
				throw new IllegalStateException("We expect a variant format of 'CONF_M_PIPE-outerDiameter-innerDiameter-typeName'");
			}
			variantParams.put(NAME, components[VARIANT_PARAM_NAME]);
			variantParams.put(OUTER_DIA_NAME, components[VARIANT_PARAM_OUTER_DIA_NAME]);
			variantParams.put(INNER_DIA_NAME, components[VARIANT_PARAM_INNER_DIA_NAME]);
			variantParams.put(TYPE_NAME, components[VARIANT_PARAM_TYPE_NAME]);
		}
		return Collections.unmodifiableMap(variantParams);
	}

	protected CsticModel createType(final Map<String, String> variantParams)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(TYPE_NAME, "type of pipe");
		builder.stringType().singleSelection();
		builder.addOption(STEEL_TYPE, "Steel", STEEL_TYPE.equals(variantParams.get(TYPE_NAME)));
		builder.addOption(PLASTIC_TYPE, "Plastic", PLASTIC_TYPE.equals(variantParams.get(TYPE_NAME)));
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createOuterDiameter(final Map<String, String> variantParams)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(OUTER_DIA_NAME, "Outer Diamater in cm");
		builder.stringType().singleSelection();
		builder.addOption(VALUE_30, VALUE_30, VALUE_30.equals(variantParams.get(OUTER_DIA_NAME)));
		builder.addOption(VALUE_40, VALUE_40, VALUE_40.equals(variantParams.get(OUTER_DIA_NAME)));
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createInnerDiameter(final Map<String, String> variantParams)
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(INNER_DIA_NAME, "Inner Diamater in cm");
		builder.stringType().singleSelection();
		builder.addOption(VALUE_15, VALUE_15, VALUE_15.equals(variantParams.get(INNER_DIA_NAME)));
		builder.addOption(VALUE_20, VALUE_20, VALUE_20.equals(variantParams.get(INNER_DIA_NAME)));
		builder.addOption(VALUE_25, VALUE_25, VALUE_25.equals(variantParams.get(INNER_DIA_NAME)));
		builder.withDefaultUIState().required();
		return builder.build();
	}

	protected CsticModel createLength()
	{
		final CsticModelBuilder builder = new CsticModelBuilder().withInstance(ROOT_INST_ID, ROOT_INSTANCE_NAME);
		builder.withName(LENGTH_NAME, "Pipe Length in cm");
		builder.numericType(0, DEFAULT_PIPE_LENGTH_TYPE_LENGTH).simpleInput();
		builder.withDefaultUIState();
		return builder.build();
	}

	@Override
	public boolean isChangeableVariant()
	{
		return true;
	}


	protected void setBasePrices(final ConfigModel model, final Double price)
	{
		//Take over price from
		final PriceModel basePrice = createPrice(BigDecimal.valueOf(price));
		model.setBasePrice(basePrice);

		final PriceModel selectedOptionsPrice = createPrice(0);
		model.setSelectedOptionsPrice(selectedOptionsPrice);

		model.setCurrentTotalPrice(basePrice);
	}

	@Override
	public void addProductAttributes(final ConfigModel model, final ProductModel productModel)
	{
		if (productModel != null)
		{
			final Collection<PriceRowModel>europe1Prices = productModel.getEurope1Prices();
			if (CollectionUtils.isNotEmpty(europe1Prices))
			{
				setBasePrices(model, europe1Prices.iterator().next().getPrice());
			}
		}

	}

}

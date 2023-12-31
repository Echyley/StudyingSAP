/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4ccpiquote.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;

public class DefaultC4CCpiCommerceCartCalculationStrategy extends DefaultCommerceCartCalculationStrategy {

        private DefaultC4CCpiQuoteRequiresCalculationStrategy quoteRequiresCalculationStrategy;
        /**
         * @deprecated Since 5.2. 
         */
        @Override
        @Deprecated(since = "5.2" ,forRemoval = true)
        public boolean calculateCart(final CartModel cartModel) {
                final CommerceCartParameter parameter = new CommerceCartParameter();
                parameter.setEnableHooks(true);
                parameter.setCart(cartModel);
                return this.calculateCart(parameter);
        }

        @Override
        public boolean calculateCart(final CommerceCartParameter parameter) {
                final CartModel cartModel = parameter.getCart();

                validateParameterNotNull(cartModel, "Cart model cannot be null");

                final CalculationService calcService = getCalculationService();
                boolean recalculated = false;
                if (getQuoteRequiresCalculationStrategy().shouldCalculateAllValues(cartModel)) {
                        super.calculateCart(parameter);
                } else if (calcService.requiresCalculation(cartModel)) {

                        try {
                                calcService.calculate(cartModel);
                        } catch (final CalculationException calculationException) {
                                throw new IllegalStateException("Cart model " + cartModel.getCode() + " was not calculated due to: "
                                                + calculationException.getMessage(), calculationException);
                        } 
                        recalculated = true;
                }

                if (isCalculateExternalTaxes()) {
                        getExternalTaxesService().calculateExternalTaxes(cartModel);
                }
                return recalculated;
        }

        public DefaultC4CCpiQuoteRequiresCalculationStrategy getQuoteRequiresCalculationStrategy() {
                return quoteRequiresCalculationStrategy;
        }

        public void setQuoteRequiresCalculationStrategy(DefaultC4CCpiQuoteRequiresCalculationStrategy quoteRequiresCalculationStrategy) {
                this.quoteRequiresCalculationStrategy = quoteRequiresCalculationStrategy;
        }
}
/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.promotionengineservices.sampleaddonspocktest

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.order.CartModel
import org.junit.Test
import spock.lang.Unroll

@IntegrationTest
class PromotionRulesSampleAddOnSpec extends AbstractPromotionEngineAddOnServicesSpockTest
{
	@Test
	@Unroll
	def "#testId : Adding #productsToAddToCart to Cart(#forCurrency) with #promotionRules promotions should have discoount and amount to #promotedAmount with the promotions and #baseAmount without it"()
	{
		given:
		compileSourceRules(promotionRules, "promotions-module-junit")

		when:
		CartModel cart = createCart("cart", forCurrency)
		productsToAddToCart.each
		{ code, quantity ->
			addProductToCart(code, quantity, cart)
		}
		double cartTotalPriceWithoutPromo = cart.totalPrice
		evaluatePromotionForCart(cart.getCode())
		double cartTotalAfterPromo = cart.totalPrice

		boolean giftsPresentAsExpected = gifts.every
		{ code, quantity ->
			checkProductQuantity(code, cart, quantity)
		}
		then:
		cartTotalPriceWithoutPromo == baseAmount
		cartTotalAfterPromo == promotedAmount
		giftsPresentAsExpected == true

		where:
		testId | productsToAddToCart | promotionRules | gifts | promotedAmount | baseAmount | forCurrency

        //QA-5430,product_buy_x_get_y_free_main
		'Test_Apply_product_buy_x_get_y_free_main_USD' | ['834955': 1, '779864': 1, '779848': 2] | ['product_buy_x_get_y_free_main']| [] | 32.45 | 32.45 | 'USD'
		'Test_Apply_product_buy_x_get_y_free_mainwithgifts_USD' | ['834955': 1,'779864': 2, '779848': 2] | ['product_buy_x_get_y_free_main']| ['779864': 2] | 24.5 | 40.4 | 'USD'
		//QA-5425
		'Test_order_threshold_fixed_price_products_main_USD' | ['325414': 2, '805693': 1] | ['order_threshold_fixed_price_products_main']| []| 344.08 | 430.58 | 'USD'

		//QA-5424, order_threshold_percentage_discount_products_main,case2 failed, need debug
		'Test_order_threshold_percentage_discount_products_main1_USD' | ['1776948': 2] | ['order_threshold_percentage_discount_products_main']| []| 293.76 | 293.76 | 'USD'
		//'Test_order_threshold_percentage_discount_products_mainwithdiscount_USD' | ['1776948': 2, '834955': 1] | ['order_threshold_percentage_discount_products_main']| []| 300.89 | 301.26 | 'USD'

		//QA-5426,order_threshold_free_gift_main
	    'Test_Apply_order_threshold_free_gift_mainwithnoFree_USD' | ['1776948': 2] | ['order_threshold_free_gift_main']| [] | 293.76 | 293.76 | 'USD'
		'Test_Apply_order_threshold_free_gift_main_USD' | ['1776948': 4] | ['order_threshold_free_gift_main']| ['1687508': 1] | 587.52| 587.52 | 'USD'
		// QA-5427,product_percentage_discount
		//'Test_roduct_percentage_discount_USD' | ['1382080': 1, '932577': 1] | ['product_percentage_discount']| []| 541.87 | 599.35 | 'USD'
		'Test_roduct_percentage_discount_USD' | ['1382080': 2] | ['product_percentage_discount']| []| 1034.80 | 1149.76 | 'USD'

		//QA-5428,product_fixed_discount
		'Test_product_fixed_discount_USD' | ['1776948': 1] | ['product_fixed_discount']| []| 146.88 | 146.88 | 'USD'
       //;1986316;584, brand_10;Canon;3571B001AA;pieces;8714574531724
		'Test_product_fixed_discount_USD' | ['1776948': 1, '1986316': 1] | ['product_fixed_discount']| []| 2066.11 | 2081.11 | 'USD'

		//product_perfect_partner_fixed_price_main,QA-5431
		'Test_product_perfect_partner_fixed_price_main_USD' | ['816780': 1, '482105': 1] | ['product_perfect_partner_fixed_price_main']| []| 1161 | 1235.69 | 'USD'

		//product_price_threshold_fixed_discount,QA-5619
		'Test_product_price_threshold_fixed_discount_main_USD' | ['1981415': 1] | ['product_price_threshold_fixed_discount']| []| 212.26 | 212.26 | 'USD'
		'Test_product_price_threshold_fixed_discount_main1_USD' | ['1981415': 1, '1986316': 1] | ['product_price_threshold_fixed_discount']| []| 2046.49 | 2146.49 | 'USD'
		'Test_product_price_threshold_fixed_discount_main2_USD' | ['1981415': 1, '1986316': 2] | ['product_price_threshold_fixed_discount']| []| 3880.72 | 4080.72 | 'USD'

		//percentage_discount_on_sony_camera,QA-9363, need enable Promotion Planning, campaign = start_your_photo_journey_with_sony
		'Test_percentage_discount_on_sony_camera_USD' | ['3357888': 1] | ['percentage_discount_on_sony_camera']| []| 117.59 | 130.65 | 'USD'



	}

	@Test
	@Unroll
	def "#testId : Adding PowerToolsproductsToAddToCart to Cart(#forCurrency) with #promotionRules promotions should have discoount and amount to #promotedAmount with the promotions and #baseAmount without it"()
	{
		given:
		compileSourceRules(promotionRules, "promotions-module-junit")

		when:
		CartModel cart = createCart("cart", forCurrency)
		productsToAddToCart.each
				{ code, quantity ->
					addProductToCart(code, quantity, cart)
				}
		double cartTotalPriceWithoutPromo = cart.totalPrice
		evaluatePromotionForCart(cart.getCode())
		double cartTotalAfterPromo = cart.totalPrice

		boolean giftsPresentAsExpected = gifts.every
				{ code, quantity ->
					checkProductQuantity(code, cart, quantity)
				}
		then:
		cartTotalPriceWithoutPromo == baseAmount
		cartTotalAfterPromo == promotedAmount
		giftsPresentAsExpected == true

		where:
		testId | productsToAddToCart | promotionRules | gifts | promotedAmount | baseAmount | forCurrency


		//750_USD_off_orders_over_10000_USD (B2B),QA-9392
		'Test_750_USD_off_orders_over_10000_USD' | ['3880499': 1] | ['750_USD_off_orders_over_10000_USD']| []| 236.0 | 236.0 | 'USD'
		'Test_750_USD_off_orders_over_10000_USD11withdisocunt' | ['3880499': 50] | ['750_USD_off_orders_over_10000_USD']| []| 11050.0 | 11800.0 | 'USD'

		//3_percent_off_orders_over_500_USD (B2B),QA-9390
		'Test_73_percent_off_orders_over_500_USD' | ['3880499': 1] | ['3_percent_off_orders_over_500_USD']| []| 236.0 | 236.0 | 'USD'
		'Test_73_percent_off_orders_over_500_USD11withdisocunt' | ['3880499': 3] | ['3_percent_off_orders_over_500_USD']| []| 686.76 | 708.0 | 'USD'
		//10_percent_off_on_Sanders,QA-9389


		//product_buy_3_power_drills_get_1_free (B2B),QA-9393
		'Test_product_buy_3_power_drills_get_1_free_USD' | ['3880499': 1] | ['product_buy_3_power_drills_get_1_free']| []| 236.0 | 236.0 | 'USD'
		//'Test_product_buy_3_power_drills_get_1_freewithdiscount_USD' | ['3880499': 3] | ['product_buy_3_power_drills_get_1_free']| ['3880499': 1]| 472.0 | 472.0 | 'USD'

		//product_price_threshold_fixed_discount,QA-9395
		'Test_product_price_threshold_750_fixed_discount_USD' | ['3880499': 1] | ['product_price_threshold_750_fixed_discount']| []| 236.0 | 236.0 | 'USD'
		'Test_product_price_threshold_750_fixed_discount_USD' | ['3880499': 1,'3887130': 1] | ['product_price_threshold_750_fixed_discount']| []| 1058.0 | 1108.0 | 'USD'

		//drill_screwdriver_sander_for_fixed_price，QA-9394
		//'Test_drill_screwdriver_sander_for_fixed_price_USD' | ['3880499': 1] | ['product_price_threshold_fixed_discount']| []| 236.0 | 236.0 | 'USD'




	}



}

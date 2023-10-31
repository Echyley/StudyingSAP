/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */


ACC.sapdigitalpayment = {
submit_billingAddressDetailsPostForm: ".submit_billingAddressDetailsPostForm",
bindAll : function() {
	
	this.bindSubmitBillingAddressDetailsPostForm();
	this.bindSavedPayments();
	
},
	
bindSubmitBillingAddressDetailsPostForm: function ()
{
	$(this.submit_billingAddressDetailsPostForm).click(function ()
	{
		$('#billingAddressDetailsForm').submit();
	});
},

bindSavedPayments:function()
{
	
	$(this.submit_billingAddressDetailsPostForm).attr("disabled", true);
	$(document).on("click",".js-checkout-payment-add-newcard",function(e)
	{
		e.preventDefault();
		$('.js-saved-payments').hide();
		$('.js-checkout-payment-add-newcard').hide();
		$('.submit_billingAddressDetailsPostForm').attr("disabled", false);
		$('#js-new-card-register-form').attr("target","_blank");
		$('#js-new-card-register-form').submit();	
		});
}

};

$(document).ready(function ()
{
	ACC.sapdigitalpayment.bindAll();
});
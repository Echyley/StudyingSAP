/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
ACC.invoice = {
		

	$errorMessage : $("#invoiceErrorMessage"),

	bindAll : function() {
		
		this.bindInvoicePDF();
		this.backToInvoices();
		
	},
	
	

	backToInvoices: function(){
		$(".invoiceTopBackBtn").on("click", function(){
			var sUrl = $(this).data("backToInvoicelist");
			window.location = sUrl;
		});
	},

	bindInvoicePDF : function() {
		$(document).on("click", '.invoiceClass', function(event) {
			$('.global-alerts').html("");
			ACC.invoice.$errorMessage.hide();
			ACC.invoice.doAjaxCallForInvoice(this, event);

		});
	},

	doAjaxCallForInvoice : function(element, event) {
		event.preventDefault();
		var invoiceCode = $(element).data("invoicenumber");
		var erorrmsg= $(element).data("errormsg");
		
		$.ajax({
			url : ACC.config.contextPath + '/my-company/organization-management/invoicedocument/invoicedownload',
			data : {invoiceCode : invoiceCode},
			type : 'GET',
			contentType : 'application/pdf',
			sync : true,
			success : function(data) {
				window.open(this.url,'_blank',"width=1024,height=768,resizable=yes,scrollbars=yes,toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
			},
			error : function(data, textStatus, ex) {
				ACC.invoice.$errorMessage.text(erorrmsg+' : '+invoiceCode).css('color','#c90400').show();
				var body = $("html, body");
				body.stop().animate({
					scrollTop : 0
				}, '500', 'swing', function foo() {
    						// This is intentional
						});
			}
		});
	}
};

$(document).ready(function() {
	ACC.invoice.bindAll();
});

/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */





$(document).ready(function() {
	
	$(document).on("click",".js-open-org-mgmt-portal", function(e){
		
		gigya.accounts.b2b.openDelegatedAdminLogin({
			orgId : orgId,
			partnerID : partnerId, 
			callback : function(r) 
			{
				//
			}
		});
    });
});

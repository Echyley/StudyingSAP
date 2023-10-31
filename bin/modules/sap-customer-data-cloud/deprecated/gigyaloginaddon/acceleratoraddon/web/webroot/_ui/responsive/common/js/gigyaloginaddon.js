/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */




var gigyaHybris = window.gigyaHybris || {};
gigyaHybris.gigyaFunctions = gigyaHybris.gigyaFunctions || {};

// Request Id Token from CDC 
window.__gigyaConf = {include:'id_token'};

gigyaHybris.gigyaFunctions.raasLogin = function(response) {
    //Show loading Indicator
    showLoadingIndicator();
    jQuery.ajax(ACC.config.contextPath + "/gigyaraas/login", {
        data: {
            gigyaData: JSON.stringify(response)
        },
        dataType: "json",
        type: "post"
    }).done(function(data, textStatus, jqXHR) {
        if (data.code !== 0) {
            ACC.colorbox.open(data.message,{
              html : $(document).find("#dialog").html(),
              maxWidth:"100%",
              width:"420px",
              initialWidth :"420px",
              height:"300px"
            });
        } else {
            window.location = ACC.config.contextPath + '/';
        }
    });
};


gigyaHybris.gigyaFunctions.raasEditProfile = function(response) {
    //Show loading Indicator
    showLoadingIndicator();
    $.ajax(ACC.config.contextPath + "/gigyaraas/profile", {
        data: {
            gigyaData: JSON.stringify(response.response)
        },
        dataType: "json",
        type: "post"
    }).done(function(data, textStatus, jqXHR) {
        if (data.code !== 0) {
            ACC.colorbox.open(data.message,{
                html : $(document).find("#dialog").html(),
                maxWidth:"100%",
                width:"420px",
                initialWidth :"420px",
                height:"300px"
              });
        } else {
            window.location.reload(false);
        }
    });
};


gigyaHybris.gigyaFunctions.raasClick = function() {
    $(".gigya-raas-link").click(
        function(event) {
            event.preventDefault();
            let id = $(this).attr("data-gigya-id");

            gigya.accounts.showScreenSet(window.gigyaHybris.raas[id]);
        });
};


gigyaHybris.gigyaFunctions.raasEmbed = function() {
    if (gigyaHybris.raas) {
        $.each(gigyaHybris.raas, function(name, params) {
			
            if(!params.profileEdit && params.containerID){
                //Show loading Indicator
                showLoadingIndicator();
                gigya.accounts.showScreenSet(params);
                setLoadingIndicatorInterval(params.containerID);
            }
            
            if(params.profileEdit && params.containerID){
                //Show loading Indicator
                showLoadingIndicator();
                gigya.accounts.showScreenSet({
                    screenSet : params.screenSet,
                    startScreen : params.startScreen,
                    containerID : params.containerID,
                    onAfterSubmit : gigyaHybris.gigyaFunctions.raasEditProfile
                });
                setLoadingIndicatorInterval(params.containerID);
            }
        });
    }
};

gigyaHybris.gigyaFunctions.setAccountResidency = function(accountResidency) {
    gigya.setAccountResidency(accountResidency);
};

/*
 * Register login events
 */
function gigyaRegister() {
    if (ACC.gigyaUserMode === "raas") {
        gigya.accounts.addEventHandlers({
            onLogin: gigyaHybris.gigyaFunctions.raasLogin
        });
    }
}

function interceptLogoutClickEvent(e) {
    let target = e.target || e.srcElement;
    if (target.tagName === 'A' && target.getAttribute('href').endsWith('/logout')) {
        gigya.accounts.logout();
    }
}


function showLoadingIndicator() {
    //Show loading Indicator
    ACC.colorbox.open("<img id='screen-set-spinner' src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />", {
        html: "<span></span>",
        width: "140px",
        initialWidth: "140px",
        height: "50px",
        close: "",
        escKey: false,
        overlayClose: false
    });
}

function setLoadingIndicatorInterval(containerID) {
	//Keep loading Indicator in view till screenset loads
    let showLoadingInterval = setInterval(function() {
        if (!$('#'+containerID).is(':empty')) {
            clearInterval(showLoadingInterval);
            ACC.colorbox.close();
         }
    }, 1000); // check every second
}

$(document).ready(function() {
    gigyaRegister();
    gigyaHybris.gigyaFunctions.raasClick();
    gigyaHybris.gigyaFunctions.raasEmbed();
    if (document.addEventListener) {
        document.addEventListener('click', interceptLogoutClickEvent);
    } else if (document.attachEvent) {
        document.attachEvent('onclick', interceptLogoutClickEvent);
    }
});

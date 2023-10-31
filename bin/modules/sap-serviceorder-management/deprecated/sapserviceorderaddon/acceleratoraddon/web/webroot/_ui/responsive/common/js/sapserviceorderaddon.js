/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

$(function(){
    var elem = document.querySelector('[data-scheduleleaddays]'); 
    if(elem){
    var date = new Date();
    var minDate = new Date(date.getFullYear(), date.getMonth(), date.getDate() + parseInt(elem.dataset.scheduleleaddays));
    $("#scheduleDate").datepicker({
        changeMonth: true,
        changeYear: true,
        minDate: minDate,
        dateFormat: 'dd-mm-yy' 
     });
    }
}); 
/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */




 var isAddNewBtnClicked = false;
 const btnAddNewCard = "#btnAddNewCard";
 

 $(document).ready(function() {

     $(document).on("click","#btnSavedCards", function(e){
             e.preventDefault();
             var url = $(this).data("savedCardsUrl");
             ACC.colorbox.open('Saved Cards',{
                 href: url,
                 maxWidth:"100%",
                 width:"380px",
                 initialWidth :"380px"
             });
         });

     $(document).on("click",btnAddNewCard, function(e){
         e.preventDefault();
         isAddNewBtnClicked = true;
         $(btnAddNewCard).html('Checking...');
         $(btnAddNewCard).add('disabled', true);

         var url = $(this).data("newCardUrl");
         var win = window.open(url, '_blank');
         win.focus();
      });

      var checkCardRegistration = function (){
          //If Add New Card button exists
          if ($(btnAddNewCard).length && isAddNewBtnClicked){
              var url = $(btnAddNewCard).data("checkCardUrl");
              ACC.colorbox.open('Payment Details',{
                  href: url,
                  maxWidth:"100%",
                  width:"380px",
                  initialWidth :"380px"
              });
          }
      };

     $(window).on("focus", checkCardRegistration);
 });
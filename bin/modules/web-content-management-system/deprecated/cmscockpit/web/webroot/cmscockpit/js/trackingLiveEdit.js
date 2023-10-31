const LIVE_EDIT_PERSPECTIVE = "Live Edit Perspective";

function trackingLiveEditPerspective() {
    trackingAddComponentsActions();
    trackingSelectUser();
    trackingSelectUserGroup();
    trackingSetCategoryCode();
    trackingAddRestriction();
}

function trackingAddComponentsActions() {
    $(document).on('click', 'div.livePopupMainPanel table.contentEditorGroupHeader a', function () {
        if (isAddItemAction($(this)) && isActions($(this))) {
            pushRequest(LIVE_EDIT_PERSPECTIVE, "Add Action Item");
        }
    });
}

function isAddItemAction(element) {
     return element.find('[src$="add_btn.gif" ]').length > 0;
}

function isActions(element) {
    return element.parents('table.contentEditorGroupHeader').find('span:contains("Actions")').length > 0
}

function trackingSelectUser() {
    $(document).on('click', 'div.referenceSelector span.autoCompleteCmp', function () {
        if (isSelectUser($(this))) {
            pushRequest(LIVE_EDIT_PERSPECTIVE, "Select User");
        }
    });
}

function trackingSelectUserGroup() {
    $(document).on('click', 'div.referenceSelector span.autoCompleteCmp', function () {
        if (isSelectUserGroup($(this))) {
            pushRequest(LIVE_EDIT_PERSPECTIVE, "Select User Group");
        }
    });
}

function isSelectUser(element) {
    let labelElement =  element.parents('table.referenceSelector_hbox').parents('td').find('span:contains("User")');
    return labelElement.text() == 'User'; //label "User group" also be found, so use equal to filter.
}

function isSelectUserGroup(element) {
    let labelElement =  element.parents('table.referenceSelector_hbox').parents('td').find('span:contains("User group")');
    return labelElement.text() == 'User group';
}

function trackingSetCategoryCode() {
    $(document).on('click', 'div.contentEditorGroupbox div.contentEditorEntry:contains("Category Code")', function (){
        pushRequest(LIVE_EDIT_PERSPECTIVE, "Set Category Code");
    })
}

function trackingAddRestriction() {
    $(document).on('click', 'table.contentElementBox:contains(Restriction)', function (){
        let contentElementDiv = $(this).find('div.contentElementName:contains("Restriction")');
        if (contentElementDiv.length == 1) {
            let text = contentElementDiv.children('span').text();
            pushRequest(LIVE_EDIT_PERSPECTIVE, 'Add ' + text);
        }
    })
}



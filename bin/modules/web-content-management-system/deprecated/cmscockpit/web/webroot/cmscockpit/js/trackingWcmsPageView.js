const PAGE_VIEW_PERSPECTIVE = "WCMS Page View Perspective";
function trackingWcmsPageViewPerspective() {
    trackingLockPageByAdministrator();
    trackingUnLockPageByAdministrator();
    trackingPageUnapproved();
    trackingEditPagesNavigationNodes();
    trackingAdvancedSearch();
    trackingLockUnlockPageTemplate();
    trackingReverseOverriding();
    trackingEditContentSlot();
    trackPopUpSelector();
}

function trackingLockPageByAdministrator() {
    $(document).on('click', 'div.browserMainContentBox div.cms_page_actions img[src$="icon_unlocked.png"]', function () {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Lock Page By Administrator');
    });
}

function trackingUnLockPageByAdministrator() {
    $(document).on('click', 'div.browserMainContentBox div.cms_page_actions span span:contains("Locked by User"), div.cms_page_actions img[src$="icon-lock-red.gif"]', function () {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'UnLock Page By Administrator');
    });
}

function trackingPageUnapproved() {
    $(document).on('click', 'div.browserMainContentBox div.cms_page_actions div.z-menu-popup a:contains("unapproved")', function () {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Unapproved Page');
    });
}

function trackingEditPagesNavigationNodes() {
    $(document).on('click', 'div.sectionPanelComponent div.referenceSelector span.autoCompleteCmp input', function () {
        if (isNavigationNodes($(this))) {
            pushRequest(PAGE_VIEW_PERSPECTIVE, 'Edit Navigation Nodes');
        }
    })
}

function  isNavigationNodes(element) {
    return element.parents('table').length > 3
             && $(element.parents('table').get(3)).find('span:contains("Navigation nodes:")').length > 0;
}

function trackingAdvancedSearch() {
    trackingPageAdvancedSearch();
    trackingCustomizeSearch();
    trackingKeepOpen();
    trackingSetDefaultPage();
    trackingExcludeSubtypes();
}

function trackingPageAdvancedSearch(){
    $(document).on('click', 'div.contentBrowserComponent a.openAdvSearchBtn', function() {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Advanced Page Search - Open');
    })
}

function trackingCustomizeSearch() {
    $(document).on('click', 'div.advanceSearchContainer div.advSearchBottomToolbar a.editbtnEnabled img[src$="icon_func_search_edit_white.png"],  div.advanceSearchContainer div.advSearchBottomToolbar a img[src$="icon_func_search_edit.png"]',function() {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Advanced Page Search - Customize');
    })
}

function trackingKeepOpen() {
    $(document).on('click', 'div.advanceSearchContainer div.advSearchBottomToolbar span.z-checkbox',function() {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Advanced Page Search - Keep Open');
    })
}

function trackingSetDefaultPage() {
    $(document).on('click', 'div.advanceSearchContainer div.advancedSearchMainArea table:contains("Is default page") span.z-radio', function (){
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Advanced Page Search - Is Default Page');
    })
}

function trackingExcludeSubtypes() {
    $(document).on('click', 'div.advanceSearchContainer input[type="checkbox"]', function (){
        if(isExcludeSubTypes($(this))) {
            pushRequest(PAGE_VIEW_PERSPECTIVE, 'Advanced Page Search - Exclude Subtypes');
        }
    })
}

function trackingReverseOverriding() {
    $(document).on('click', 'div.cmsStructManArea td.structureViewSection img[src$="icon_func_override_reverse.png"]', function () {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Reverse override');
    })
}

function trackingLockUnlockPageTemplate() {
    $(document).on('click', 'div.component_editor_component_center div.lockableGroupbox img[src$="icon_locked.png"]', function () {
        pushRequest(PAGE_VIEW_PERSPECTIVE, 'Unlock Page Template');
    })
}

function trackingEditContentSlot() {
    $(document).on('click', 'div.sectionPanelComponent div.section_component_container input.textEditor', function() {
        if(isEditSlotName($(this))) {
            pushRequest(PAGE_VIEW_PERSPECTIVE, "Edit Slot Name");
        }

        if (isEditSlotID($(this))) {
            pushRequest(PAGE_VIEW_PERSPECTIVE, "Edit Slot ID");
        }
    })
}

function isEditSlotName(element) {
   return  element.closest('div.popupwindow').find('td:contains("Edit ContentSlot")').length > 0
     && element.closest('tr').find('span:contains("Name")').text().startsWith('Name:');
}

function isEditSlotID(element) {
    return  element.closest('div.popupwindow').find('td:contains("Edit ContentSlot")').length > 0
        && element.closest('tr').find('span:contains("ID")').text().startsWith('ID:');
}

function trackPopUpSelector() {
    $(document).on('click', 'div.cmsStructManArea div.contentEditorEntry input[type="radio"]', function () {
        if (isPopUp($(this))) {
            pushRequest(PAGE_VIEW_PERSPECTIVE, 'Set Is Pop Up');
        }
    })
}

function isPopUp(element) {
   return element.closest('table').closest('tr').find('span:contains("Popup:")').length > 0;
}

function isExcludeSubTypes(element) {
    return element.closest('td').prev().prev('td:contains("Exclude subtypes")').length > 0
}

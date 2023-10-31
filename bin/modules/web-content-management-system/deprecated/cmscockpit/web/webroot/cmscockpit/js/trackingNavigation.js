const NAVIGATION_PERSPECTIVE = "Navigation Perspective";

function trackingNavigationPerspective() {
    trackingCategoriesSync();
    trackingAddItems();
}

function trackingCategoriesSync() {
    $(document).on('click', 'div.navigationSectionContainerLeft a img[src$="icon_status_sync.png"]', function () {
        pushRequest(NAVIGATION_PERSPECTIVE, "Navigation Tree Synchronization");
    });
}

function trackingAddItems() {
    $(document).on('click', 'div.wizardContentFrame div.assignmentTypeList div.z-list-cell-cnt', function () {
        if (isMedia($(this))) {
            pushRequest(NAVIGATION_PERSPECTIVE, "Add Media Item");
        }

        if (isContentPage($(this))) {
            pushRequest(NAVIGATION_PERSPECTIVE, "Add Content Page Item");
        }
    });
}

function isMedia(element) {
    return element.find('[src$="navigation_resource_Media.gif"]').length > 0;
}

function isContentPage(element) {
    return element.find('[src$="navigation_resource_ContentPage.gif" ]').length > 0;
}


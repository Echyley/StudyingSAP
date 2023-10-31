const TRACKING_URL = "https://license.hybris.com/collect";
const FEATURE_NAME = "CMSCockpit";
const EVENT_TYPE = "trackEvent";
const NAVIGATION_AREA = "Navigation Area";


var _paq = _paq || [];
_paq.push(["trackPageView"]);
_paq.push(["enableLinkTracking"]);

function embedTrackingCode() {
    _paq.push(["setTrackerUrl", TRACKING_URL]);
    _paq.push(["setSiteId", siteId]);

    loadPiwikLib();
    trackingNavigationArea();
    trackingNavigationPerspective();
    trackingLiveEditPerspective();
    trackingWcmsPageViewPerspective();
}

function trackingNavigationArea() {
    trackingPerspectives();
    trackingQueries();
    trackingHistory();
}

function trackingPerspectives() {
    $(document).on('click', 'div.navigation_north div.perspectiveButton td.z-button-cm', function () {
        pushRequest(NAVIGATION_AREA, 'Perspective - ' + $(this).text());
    });
}

function trackingQueries() {
    $(document).on('click', 'div.navigation_area a ', function () {
        if (isSavedQuery($(this))) {
            pushRequest(NAVIGATION_AREA, 'Queries - ' + $(this).attr("title"));
        }
    });
}

function isSavedQuery(element) {
    let isUnsharedSavedQueries = element.find('[src$="button_view_savedquery_active.png"]').length > 0;
    let isSharedSavedQueries = element.find('[src$="button_view_savedqueryshared_default.png"]').length > 0;
    return isUnsharedSavedQueries || isSharedSavedQueries;
}

function trackingHistory() {
    $(document).on('click', 'div.navigationarea div.undoRedoButtons', function () {
        pushRequest(NAVIGATION_AREA, "History - UndoRedo");
    });
}

function pushRequest(functionality, element) {
    _paq.push([EVENT_TYPE, FEATURE_NAME, functionality, element]);
}

function loadPiwikLib() {
    let piwikScript = document.createElement("script");
    piwikScript.type = "text/javascript";
    piwikScript.src = "cmscockpit/js/piwik.js";
    document.head.appendChild(piwikScript);
}

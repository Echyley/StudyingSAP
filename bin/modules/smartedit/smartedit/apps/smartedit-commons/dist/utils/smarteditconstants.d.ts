import { LoginDialogResource } from '@smart/utils';
export declare const DOMAIN_TOKEN = "domain";
export declare const SMARTEDITLOADER_COMPONENT_NAME = "smarteditloader-component";
export declare const SMARTEDITCONTAINER_COMPONENT_NAME = "smarteditcontainer-component";
export declare const SMARTEDIT_COMPONENT_NAME = "smartedit-component";
export declare const ELEMENT_UUID_ATTRIBUTE = "data-smartedit-element-uuid";
export declare const ID_ATTRIBUTE = "data-smartedit-component-id";
export declare const TYPE_ATTRIBUTE = "data-smartedit-component-type";
export declare const NG_ROUTE_PREFIX = "ng";
export declare const NG_ROUTE_WILDCARD = "**";
export declare const LOGOUT = "logout";
export declare const EXTENDED_VIEW_PORT_MARGIN = 1000;
export declare const CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
export declare const CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";
export declare const CONTEXT_SITE_ID = "CURRENT_CONTEXT_SITE_ID";
export declare const PAGE_CONTEXT_CATALOG = "CURRENT_PAGE_CONTEXT_CATALOG";
export declare const PAGE_CONTEXT_CATALOG_VERSION = "CURRENT_PAGE_CONTEXT_CATALOG_VERSION";
/**
 * Constant containing the name of the current page site uid placeholder in URLs
 */
export declare const PAGE_CONTEXT_SITE_ID = "CURRENT_PAGE_CONTEXT_SITE_ID";
export declare const SHOW_SLOT_MENU = "_SHOW_SLOT_MENU";
export declare const HIDE_SLOT_MENU = "HIDE_SLOT_MENU";
export declare const OVERLAY_DISABLED_EVENT = "OVERLAY_DISABLED";
export declare const DEFAULT_LANGUAGE = "en_US";
export declare const CLOSE_CTX_MENU = "CLOSE_CTX_MENU";
export declare const CTX_MENU_DROPDOWN_IS_OPEN = "CTX_MENU_DROPDOWN_IS_OPEN";
export declare enum MUTATION_CHILD_TYPES {
    ADD_OPERATION = "addedNodes",
    REMOVE_OPERATION = "removedNodes"
}
export declare const MUTATION_TYPES: {
    CHILD_LIST: {
        NAME: string;
        ADD_OPERATION: MUTATION_CHILD_TYPES;
        REMOVE_OPERATION: MUTATION_CHILD_TYPES;
    };
    ATTRIBUTES: {
        NAME: string;
    };
};
/**
 * **Deprecated since 2105.**
 *
 * Path to fetch permissions of a given catalog version.
 *
 * @deprecated
 */
export declare const CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT = "/permissionswebservices/v1/permissions/catalogs/search";
export declare const OPERATION_CONTEXT: {
    BACKGROUND_TASKS: string;
    INTERACTIVE: string;
    NON_INTERACTIVE: string;
    BATCH_OPERATIONS: string;
    TOOLING: string;
    CMS: string;
};
export declare const I18N_RESOURCE_URI = "/smarteditwebservices/v1/i18n/translations";
/**
 * Resource URI of the WhoAmI REST service used to retrieve information on the
 * current logged-in user.
 */
export declare const WHO_AM_I_RESOURCE_URI = "/authorizationserver/oauth/whoami";
/**
 * The default OAuth 2 client id to use during authentication.
 */
export declare const DEFAULT_AUTHENTICATION_CLIENT_ID = "smartedit";
export declare const SSO_AUTHENTICATION_ENTRY_POINT = "/samlsinglesignon/saml";
export declare const SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT = "/smartedit/authenticate";
export declare const SSO_LOGOUT_ENTRY_POINT = "/samlsinglesignon/saml/logout";
/**
 * Path of the preview ticket API
 */
export declare const PREVIEW_RESOURCE_URI = "/previewwebservices/v1/preview";
/**
 * Regular expression identifying CMS related URIs
 */
export declare const CMSWEBSERVICES_PATH: RegExp;
/**
 * To calculate platform domain URI, this regular expression will be used
 */
export declare const SMARTEDIT_RESOURCE_URI_REGEXP: RegExp;
/**
 * The name of the webapp root context
 */
export declare const SMARTEDIT_ROOT = "smartedit";
/**
 * The SmartEdit configuration API root
 */
export declare const CONFIGURATION_URI = "/smartedit/configuration";
export declare const SETTINGS_URI = "/smartedit/settings";
export declare const EVENT_NOTIFICATION_CHANGED = "EVENT_NOTIFICATION_CHANGED";
export declare enum SortDirections {
    Ascending = "asc",
    Descending = "desc"
}
export declare const REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT = "REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT";
export declare const PREVIOUS_USERNAME_HASH = "previousUsername";
export declare const SMARTEDIT_LOGIN_DIALOG_RESOURCES: LoginDialogResource;
export { DEFAULT_AUTHENTICATION_ENTRY_POINT, EVENTS, I18N_ROOT_RESOURCE_URI, DEFAULT_AUTH_MAP, DEFAULT_CREDENTIALS_MAP, DEFAULT_LANGUAGE_ISO, LANDING_PAGE_PATH, SELECTED_LANGUAGE, SWITCH_LANGUAGE_EVENT } from '@smart/utils';
export declare const EVENT_PERSPECTIVE_CHANGED = "EVENT_PERSPECTIVE_CHANGED";
export declare const EVENT_PERSPECTIVE_UNLOADING = "EVENT_PERSPECTIVE_UNLOADING";
export declare const EVENT_PERSPECTIVE_REFRESHED = "EVENT_PERSPECTIVE_REFRESHED";
export declare const EVENT_PERSPECTIVE_ADDED = "EVENT_PERSPECTIVE_ADDED";
export declare const EVENT_PERSPECTIVE_UPDATED = "EVENT_PERSPECTIVE_UPDATED";
export declare const EVENT_THEME_SAVED = "EVENT_THEME_SAVED";
export declare const EVENT_THEME_CHANGED = "EVENT_THEME_CHANGED";
export declare const EVENT_STRICT_PREVIEW_MODE_REQUESTED = "EVENT_STRICT_PREVIEW_MODE_REQUESTED";
export declare const PERSPECTIVE_SELECTOR_WIDGET_KEY = "perspectiveToolbar.perspectiveSelectorTemplate";
export declare const EVENT_SMARTEDIT_COMPONENT_UPDATED = "EVENT_SMARTEDIT_COMPONENT_UPDATED";
export declare const OVERLAY_ID = "smarteditoverlay";
export declare const EVENT_OUTER_FRAME_CLICKED = "EVENT_OUTER_FRAME_CLICKED";
export declare const EVENT_INNER_FRAME_CLICKED = "EVENT_INNER_FRAME_CLICKED";
export declare const CATALOG_VERSION_UUID_ATTRIBUTE = "data-smartedit-catalog-version-uuid";
export declare const COMPONENT_CLASS = "smartEditComponent";
export declare const CONTAINER_ID_ATTRIBUTE = "data-smartedit-container-id";
export declare const CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS: {
    PROCESS: string;
    REMOVE: string;
    KEEP_VISIBLE: string;
};
export declare const CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS: {
    PROCESS_COMPONENTS: string;
    RESTART_PROCESS: string;
};
export declare const OVERLAY_RERENDERED_EVENT = "overlayRerendered";
export declare const SMARTEDIT_ATTRIBUTE_PREFIX = "data-smartedit-";
export declare const SMARTEDIT_COMPONENT_PROCESS_STATUS = "smartEditComponentProcessStatus";
export declare const UUID_ATTRIBUTE = "data-smartedit-component-uuid";
export declare const OVERLAY_COMPONENT_CLASS = "smartEditComponentX";
export declare const CONTENT_SLOT_TYPE = "ContentSlot";
export declare const CONTAINER_TYPE_ATTRIBUTE = "data-smartedit-container-type";
export declare const LANGUAGE_RESOURCE_URI = "/cmswebservices/v1/sites/:siteUID/languages";
export declare const I18N_LANGUAGES_RESOURCE_URI = "/smarteditwebservices/v1/i18n/languages";
export declare const GENERIC_EDITOR_LOADED_EVENT = "genericEditorLoadedEvent";
export declare const GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT = "UnrelatedValidationMessagesEvent";
export declare const VALIDATION_MESSAGE_TYPES: {
    VALIDATION_ERROR: string;
    WARNING: string;
};
export declare const ENUM_RESOURCE_URI = "/cmswebservices/v1/enums";
export declare const DROPDOWN_IMPLEMENTATION_SUFFIX = "DROPDOWN_IMPLEMENTATION_SUFFIX";
export declare const LINKED_DROPDOWN = "LinkedDropdown";
export declare const CLICK_DROPDOWN = "ClickDropdown";
export declare const DROPDOWN_POPULATOR = "DropdownPopulator";
export declare const SITES_RESOURCE_URI = "/cmswebservices/v1/sites";
export declare const DATE_CONSTANTS: {
    ANGULAR_FORMAT: string;
    MOMENT_FORMAT: string;
    MOMENT_ISO: string;
    ISO: string;
    ANGULAR_SHORT: string;
};
export declare const CATALOG_DETAILS_COLUMNS: {
    LEFT: string;
    RIGHT: string;
};
export declare const TYPES_RESOURCE_URI = "/cmswebservices/v1/types";
export declare const STORE_FRONT_CONTEXT = "/storefront";
export declare const PRODUCT_RESOURCE_API = "/cmssmarteditwebservices/v1/sites/:siteUID/products/:productUID";
export declare const PRODUCT_LIST_RESOURCE_API = "/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/products";
export declare const HIDE_TOOLBAR_ITEM_CONTEXT = "HIDE_TOOLBAR_ITEM_CONTEXT";
export declare const SHOW_TOOLBAR_ITEM_CONTEXT = "SHOW_TOOLBAR_ITEM_CONTEXT";
export declare const SMARTEDIT_DRAG_AND_DROP_EVENTS: {
    DRAG_DROP_CROSS_ORIGIN_START: string;
    DRAG_DROP_START: string;
    DRAG_DROP_END: string;
    TRACK_MOUSE_POSITION: string;
    DROP_ELEMENT: string;
};
export declare const NONE_PERSPECTIVE = "se.none";
export declare const ALL_PERSPECTIVE = "se.all";
export declare const SEND_MOUSE_POSITION_THROTTLE = 100;
export declare const THROTTLE_SCROLLING_DELAY = 70;
export declare const SMARTEDIT_ELEMENT_HOVERED = "smartedit-element-hovered";
export declare const SCROLL_AREA_CLASS = "ySECmsScrollArea";
export declare const SMARTEDIT_IFRAME_DRAG_AREA = "ySmartEditFrameDragArea";
export declare const DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME: {
    START: string;
    END: string;
};
export declare const SMARTEDIT_IFRAME_WRAPPER_ID = "#js_iFrameWrapper";
export declare const HEART_BEAT_TIMEOUT_THRESHOLD_MS = 10000;
export declare const EVENT_CONTENT_CATALOG_UPDATE = "EVENT_CONTENT_CATALOG_UPDATE";
export declare const SMARTEDIT_INNER_FILES: any[];
export declare const SMARTEDIT_INNER_FILES_POST: any[];
export declare const MEDIA_RESOURCE_URI: string;
export declare const MEDIA_PATH = "/cmswebservices/v1/media";
export declare const MEDIAS_PATH: string;
export declare const EXPERIENCE_STORAGE_KEY = "experience";
export declare const MEDIA_FOLDER_PATH = "/cmswebservices/v1/mediafolders";
export declare const OPEN_PAGE_WORKFLOW_MENU = "OPEN_PAGE_WORKFLOW_MENU";
export declare const CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU = "CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU";
export declare const CMSITEMS_UPDATE_EVENT = "CMSITEMS_UPDATE";
export declare const PAGES_CONTENT_SLOT_RESOURCE_URI: string;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGE_TEMPLATES_URI
 *
 * @description
 * Resource URI of the page templates REST service
 */
export declare const EVENT_PAGE_TREE_PANEL_SWITCH = "EVENT_PAGE_TREE_PANEL_SWITCH";
export declare const EVENT_PAGE_TREE_SLOT_SELECTED = "EVENT_PAGE_TREE_SLOT_SELECTED";
export declare const EVENT_PAGE_TREE_COMPONENT_SELECTED = "EVENT_PAGE_TREE_COMPONENT_SELECTED";
export declare const EVENT_OPEN_IN_PAGE_TREE = "EVENT_OPEN_IN_PAGE_TREE";
export declare const EVENT_PART_REFRESH_TREE_NODE = "EVENT_PART_REFRESH_TREE_NODE";
export declare const EVENT_OVERALL_REFRESH_TREE_NODE = "EVENT_OVERALL_REFRESH_TREE_NODE";
export declare const EVENT_PAGE_TREE_SLOT_NEED_UPDATE = "EVENT_PAGE_TREE_SLOT_NEED_UPDATE";
export declare const PAGE_TREE_PANEL_WIDTH_COOKIE_NAME = "PAGE_TREE_PANEL_WIDTH_COOKIE_NAME";
export declare const PAGE_TREE_SLOT_EXPANDED_EVENT = "PAGE_TREE_SLOT_EXPANDED_EVENT";
export declare const PAGE_TREE_NODE_CLASS = "se-page-tree-component-node";
/**
 * @description
 * Theme related APIs
 */
export declare const CURRENT_USER_THEME_URI = "/smarteditwebservices/v1/themes/currentUser/theme";
export declare const ALL_ACTIVE_THEMES_URI = "/smarteditwebservices/v1/themes";
export declare const USER_TRACKING_FUNCTIONALITY: {
    NAVIGATION: string;
    PAGE_MANAGEMENT: string;
    NAVIGATION_MANAGEMENT: string;
    HEADER_TOOL: string;
    SELECT_PERSPECTIVE: string;
    TOOL_BAR: string;
    VERSION_OPERATION: string;
    INFLECTION: string;
    ADD_COMPONENT: string;
    VERSION_MANAGEMENT: string;
    CONTEXT_MENU: string;
    PAGE_STRUCTURE: string;
};
export declare const USER_TRACKING_KEY_MAP: Map<string, string>;

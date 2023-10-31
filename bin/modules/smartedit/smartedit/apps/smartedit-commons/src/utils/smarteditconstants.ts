/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { LoginDialogResource } from '@smart/utils';

export const DOMAIN_TOKEN = 'domain';

export const SMARTEDITLOADER_COMPONENT_NAME = 'smarteditloader-component';
export const SMARTEDITCONTAINER_COMPONENT_NAME = 'smarteditcontainer-component';
export const SMARTEDIT_COMPONENT_NAME = 'smartedit-component';

export const ELEMENT_UUID_ATTRIBUTE = 'data-smartedit-element-uuid';
export const ID_ATTRIBUTE = 'data-smartedit-component-id';
export const TYPE_ATTRIBUTE = 'data-smartedit-component-type';
export const NG_ROUTE_PREFIX = 'ng';
export const NG_ROUTE_WILDCARD = '**';
export const LOGOUT = 'logout';
export const EXTENDED_VIEW_PORT_MARGIN = 1000;

export const CONTEXT_CATALOG = 'CURRENT_CONTEXT_CATALOG';
export const CONTEXT_CATALOG_VERSION = 'CURRENT_CONTEXT_CATALOG_VERSION';
export const CONTEXT_SITE_ID = 'CURRENT_CONTEXT_SITE_ID';

export const PAGE_CONTEXT_CATALOG = 'CURRENT_PAGE_CONTEXT_CATALOG';
export const PAGE_CONTEXT_CATALOG_VERSION = 'CURRENT_PAGE_CONTEXT_CATALOG_VERSION';
/**
 * Constant containing the name of the current page site uid placeholder in URLs
 */
export const PAGE_CONTEXT_SITE_ID = 'CURRENT_PAGE_CONTEXT_SITE_ID';

export const SHOW_SLOT_MENU = '_SHOW_SLOT_MENU';
export const HIDE_SLOT_MENU = 'HIDE_SLOT_MENU';

export const OVERLAY_DISABLED_EVENT = 'OVERLAY_DISABLED';

export const DEFAULT_LANGUAGE = 'en_US';
export const CLOSE_CTX_MENU = 'CLOSE_CTX_MENU';
export const CTX_MENU_DROPDOWN_IS_OPEN = 'CTX_MENU_DROPDOWN_IS_OPEN';

export enum MUTATION_CHILD_TYPES {
    ADD_OPERATION = 'addedNodes',
    REMOVE_OPERATION = 'removedNodes'
}
/*
 * Mutation object (return in a list of mutations in mutation event) can be of different types.
 * We are here only interested in type attributes (used for onPageChanged and onComponentChanged events) and childList (used for onComponentAdded events)
 */
export const MUTATION_TYPES = {
    CHILD_LIST: {
        NAME: 'childList',
        ADD_OPERATION: MUTATION_CHILD_TYPES.ADD_OPERATION,
        REMOVE_OPERATION: MUTATION_CHILD_TYPES.REMOVE_OPERATION
    },
    ATTRIBUTES: {
        NAME: 'attributes'
    }
};

/**
 * **Deprecated since 2105.**
 *
 * Path to fetch permissions of a given catalog version.
 *
 * @deprecated
 */
export const CATALOG_VERSION_PERMISSIONS_RESOURCE_URI_CONSTANT =
    '/permissionswebservices/v1/permissions/catalogs/search';

export const OPERATION_CONTEXT = {
    BACKGROUND_TASKS: 'Background Tasks',
    INTERACTIVE: 'Interactive',
    NON_INTERACTIVE: 'Non-Interactive',
    BATCH_OPERATIONS: 'Batch Operations',
    TOOLING: 'Tooling',
    CMS: 'CMS'
};

export const I18N_RESOURCE_URI = '/smarteditwebservices/v1/i18n/translations';

/**
 * Resource URI of the WhoAmI REST service used to retrieve information on the
 * current logged-in user.
 */
export const WHO_AM_I_RESOURCE_URI = '/authorizationserver/oauth/whoami';

/**
 * The default OAuth 2 client id to use during authentication.
 */
export const DEFAULT_AUTHENTICATION_CLIENT_ID = 'smartedit';
export const SSO_AUTHENTICATION_ENTRY_POINT = '/samlsinglesignon/saml';
export const SSO_OAUTH2_AUTHENTICATION_ENTRY_POINT = '/smartedit/authenticate';
export const SSO_LOGOUT_ENTRY_POINT = '/samlsinglesignon/saml/logout';

/**
 * Path of the preview ticket API
 */
export const PREVIEW_RESOURCE_URI = '/previewwebservices/v1/preview';

/**
 * Regular expression identifying CMS related URIs
 */
export const CMSWEBSERVICES_PATH = /\/cmssmarteditwebservices|\/cmswebservices/;

/**
 * To calculate platform domain URI, this regular expression will be used
 */
export const SMARTEDIT_RESOURCE_URI_REGEXP = /^(.*)\/smartedit/;

/**
 * The name of the webapp root context
 */
export const SMARTEDIT_ROOT = 'smartedit';

/**
 * The SmartEdit configuration API root
 */
export const CONFIGURATION_URI = '/smartedit/configuration';

export const SETTINGS_URI = '/smartedit/settings';
export const EVENT_NOTIFICATION_CHANGED = 'EVENT_NOTIFICATION_CHANGED';

export enum SortDirections {
    Ascending = 'asc',
    Descending = 'desc'
}

export const REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT = 'REFRESH_CONTEXTUAL_MENU_ITEMS_EVENT';
export const PREVIOUS_USERNAME_HASH = 'previousUsername';

export const SMARTEDIT_LOGIN_DIALOG_RESOURCES: LoginDialogResource = {
    topLogoURL: 'static-resources/images/SAP_R_grad.svg',
    bottomLogoURL: 'static-resources/images/best-run-sap-logo.svg'
};
export {
    DEFAULT_AUTHENTICATION_ENTRY_POINT,
    EVENTS,
    I18N_ROOT_RESOURCE_URI,
    DEFAULT_AUTH_MAP,
    DEFAULT_CREDENTIALS_MAP,
    DEFAULT_LANGUAGE_ISO,
    LANDING_PAGE_PATH,
    SELECTED_LANGUAGE,
    SWITCH_LANGUAGE_EVENT
} from '@smart/utils';

export const EVENT_PERSPECTIVE_CHANGED = 'EVENT_PERSPECTIVE_CHANGED';
export const EVENT_PERSPECTIVE_UNLOADING = 'EVENT_PERSPECTIVE_UNLOADING';
export const EVENT_PERSPECTIVE_REFRESHED = 'EVENT_PERSPECTIVE_REFRESHED';
export const EVENT_PERSPECTIVE_ADDED = 'EVENT_PERSPECTIVE_ADDED';
export const EVENT_PERSPECTIVE_UPDATED = 'EVENT_PERSPECTIVE_UPDATED';
export const EVENT_THEME_SAVED = 'EVENT_THEME_SAVED';
export const EVENT_THEME_CHANGED = 'EVENT_THEME_CHANGED';

export const EVENT_STRICT_PREVIEW_MODE_REQUESTED = 'EVENT_STRICT_PREVIEW_MODE_REQUESTED';

export const PERSPECTIVE_SELECTOR_WIDGET_KEY = 'perspectiveToolbar.perspectiveSelectorTemplate';

export const EVENT_SMARTEDIT_COMPONENT_UPDATED = 'EVENT_SMARTEDIT_COMPONENT_UPDATED';

export const OVERLAY_ID = 'smarteditoverlay';

export const EVENT_OUTER_FRAME_CLICKED = 'EVENT_OUTER_FRAME_CLICKED';
export const EVENT_INNER_FRAME_CLICKED = 'EVENT_INNER_FRAME_CLICKED';
export const CATALOG_VERSION_UUID_ATTRIBUTE = 'data-smartedit-catalog-version-uuid';
export const COMPONENT_CLASS = 'smartEditComponent';
export const CONTAINER_ID_ATTRIBUTE = 'data-smartedit-container-id';
export const CONTRACT_CHANGE_LISTENER_COMPONENT_PROCESS_STATUS = {
    PROCESS: 'processComponent',
    REMOVE: 'removeComponent',
    KEEP_VISIBLE: 'keepComponentVisible'
};
export const CONTRACT_CHANGE_LISTENER_PROCESS_EVENTS = {
    PROCESS_COMPONENTS: 'contractChangeListenerProcessComponents',
    RESTART_PROCESS: 'contractChangeListenerRestartProcess'
};
export const OVERLAY_RERENDERED_EVENT = 'overlayRerendered';
export const SMARTEDIT_ATTRIBUTE_PREFIX = 'data-smartedit-';
export const SMARTEDIT_COMPONENT_PROCESS_STATUS = 'smartEditComponentProcessStatus';
export const UUID_ATTRIBUTE = 'data-smartedit-component-uuid';
export const OVERLAY_COMPONENT_CLASS = 'smartEditComponentX';
export const CONTENT_SLOT_TYPE = 'ContentSlot';
export const CONTAINER_TYPE_ATTRIBUTE = 'data-smartedit-container-type';
export const LANGUAGE_RESOURCE_URI = '/cmswebservices/v1/sites/:siteUID/languages';
export const I18N_LANGUAGES_RESOURCE_URI = '/smarteditwebservices/v1/i18n/languages';

// Generic Editor

export const GENERIC_EDITOR_LOADED_EVENT = 'genericEditorLoadedEvent';
export const GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT =
    'UnrelatedValidationMessagesEvent';
export const VALIDATION_MESSAGE_TYPES = {
    VALIDATION_ERROR: 'ValidationError',
    WARNING: 'Warning'
};

export const ENUM_RESOURCE_URI = '/cmswebservices/v1/enums';

// Dropdown
export const DROPDOWN_IMPLEMENTATION_SUFFIX = 'DROPDOWN_IMPLEMENTATION_SUFFIX';
export const LINKED_DROPDOWN = 'LinkedDropdown';
export const CLICK_DROPDOWN = 'ClickDropdown';
export const DROPDOWN_POPULATOR = 'DropdownPopulator';
export const SITES_RESOURCE_URI = '/cmswebservices/v1/sites';

export const DATE_CONSTANTS = {
    ANGULAR_FORMAT: 'short',
    MOMENT_FORMAT: 'M/D/YY h:mm A',
    MOMENT_ISO: 'YYYY-MM-DDTHH:mm:00ZZ',
    ISO: 'yyyy-MM-ddTHH:mm:00Z',
    ANGULAR_SHORT: 'M/d/yy h:mm a'
};

export const CATALOG_DETAILS_COLUMNS = {
    LEFT: 'left',
    RIGHT: 'right'
};

export const TYPES_RESOURCE_URI = '/cmswebservices/v1/types';
export const STORE_FRONT_CONTEXT = '/storefront';
export const PRODUCT_RESOURCE_API =
    '/cmssmarteditwebservices/v1/sites/:siteUID/products/:productUID';
export const PRODUCT_LIST_RESOURCE_API =
    '/cmssmarteditwebservices/v1/productcatalogs/:catalogId/versions/:catalogVersion/products';

export const HIDE_TOOLBAR_ITEM_CONTEXT = 'HIDE_TOOLBAR_ITEM_CONTEXT';
export const SHOW_TOOLBAR_ITEM_CONTEXT = 'SHOW_TOOLBAR_ITEM_CONTEXT';

export const SMARTEDIT_DRAG_AND_DROP_EVENTS = {
    DRAG_DROP_CROSS_ORIGIN_START: 'DRAG_DROP_CROSS_ORIGIN_START',
    DRAG_DROP_START: 'EVENT_DRAG_DROP_START',
    DRAG_DROP_END: 'EVENT_DRAG_DROP_END',
    TRACK_MOUSE_POSITION: 'EVENT_TRACK_MOUSE_POSITION',
    DROP_ELEMENT: 'EVENT_DROP_ELEMENT'
};

export const NONE_PERSPECTIVE = 'se.none';
export const ALL_PERSPECTIVE = 'se.all';

export const SEND_MOUSE_POSITION_THROTTLE = 100;
export const THROTTLE_SCROLLING_DELAY = 70;

export const SMARTEDIT_ELEMENT_HOVERED = 'smartedit-element-hovered';
export const SCROLL_AREA_CLASS = 'ySECmsScrollArea';
export const SMARTEDIT_IFRAME_DRAG_AREA = 'ySmartEditFrameDragArea';
export const DRAG_AND_DROP_CROSS_ORIGIN_BEFORE_TIME = {
    START: 'START',
    END: 'END'
};

export const SMARTEDIT_IFRAME_WRAPPER_ID = '#js_iFrameWrapper';
export const HEART_BEAT_TIMEOUT_THRESHOLD_MS = 10000;

export const EVENT_CONTENT_CATALOG_UPDATE = 'EVENT_CONTENT_CATALOG_UPDATE';

// These two constants are only used to load new files in E2E tests. In production code they
// are completely ignored.
export const SMARTEDIT_INNER_FILES = [];
export const SMARTEDIT_INNER_FILES_POST = [];

export const MEDIA_RESOURCE_URI = `/cmswebservices/v1/catalogs/${CONTEXT_CATALOG}/versions/${CONTEXT_CATALOG_VERSION}/media`;
export const MEDIA_PATH = `/cmswebservices/v1/media`;
export const MEDIAS_PATH = `/cmswebservices/v1/catalogs/${CONTEXT_CATALOG}/versions/${CONTEXT_CATALOG_VERSION}/medias`;

export const EXPERIENCE_STORAGE_KEY = 'experience';

export const MEDIA_FOLDER_PATH = `/cmswebservices/v1/mediafolders`;

export const OPEN_PAGE_WORKFLOW_MENU = 'OPEN_PAGE_WORKFLOW_MENU';
export const CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU = 'CMS_EVENT_OPEN_PAGE_WORKFLOW_MENU';
export const CMSITEMS_UPDATE_EVENT = 'CMSITEMS_UPDATE';

export const PAGES_CONTENT_SLOT_RESOURCE_URI = `/cmswebservices/v1/sites/${PAGE_CONTEXT_SITE_ID}/catalogs/${PAGE_CONTEXT_CATALOG}/versions/${PAGE_CONTEXT_CATALOG_VERSION}/pagescontentslots`;
/**
 * @ngdoc object
 * @name resourceLocationsModule.object:PAGE_TEMPLATES_URI
 *
 * @description
 * Resource URI of the page templates REST service
 */

// page tree event
export const EVENT_PAGE_TREE_PANEL_SWITCH = 'EVENT_PAGE_TREE_PANEL_SWITCH';
export const EVENT_PAGE_TREE_SLOT_SELECTED = 'EVENT_PAGE_TREE_SLOT_SELECTED';
export const EVENT_PAGE_TREE_COMPONENT_SELECTED = 'EVENT_PAGE_TREE_COMPONENT_SELECTED';
export const EVENT_OPEN_IN_PAGE_TREE = 'EVENT_OPEN_IN_PAGE_TREE';
export const EVENT_PART_REFRESH_TREE_NODE = 'EVENT_PART_REFRESH_TREE_NODE';
export const EVENT_OVERALL_REFRESH_TREE_NODE = 'EVENT_OVERALL_REFRESH_TREE_NODE';
export const EVENT_PAGE_TREE_SLOT_NEED_UPDATE = 'EVENT_PAGE_TREE_SLOT_NEED_UPDATE';

export const PAGE_TREE_PANEL_WIDTH_COOKIE_NAME = 'PAGE_TREE_PANEL_WIDTH_COOKIE_NAME';
export const PAGE_TREE_SLOT_EXPANDED_EVENT = 'PAGE_TREE_SLOT_EXPANDED_EVENT';
export const PAGE_TREE_NODE_CLASS = 'se-page-tree-component-node';

/**
 * @description
 * Theme related APIs
 */
export const CURRENT_USER_THEME_URI = '/smarteditwebservices/v1/themes/currentUser/theme';
export const ALL_ACTIVE_THEMES_URI = '/smarteditwebservices/v1/themes';
// user tracking functionality area
export const USER_TRACKING_FUNCTIONALITY = {
    NAVIGATION: 'Function Navigation',
    PAGE_MANAGEMENT: 'Page Management',
    NAVIGATION_MANAGEMENT: 'Navigation Management',
    HEADER_TOOL: 'Header Tool Bar',
    SELECT_PERSPECTIVE: 'Perspective Select',
    TOOL_BAR: 'Tool Bar',
    VERSION_OPERATION: 'Version Operation',
    INFLECTION: 'Inflection Select',
    ADD_COMPONENT: 'Add Component',
    VERSION_MANAGEMENT: 'Version Management',
    CONTEXT_MENU: 'Contextual Menu',
    PAGE_STRUCTURE: 'Page Structure'
};

export const USER_TRACKING_KEY_MAP = new Map<string, string>([
    ['se.cms.toolbaritem.navigationmenu.name', 'Navigation'],
    ['se.cms.pagelist.title', 'Pages'],
    ['se.route.storefront.title', 'Storefront'],
    ['headerToolbar.configurationTemplate', 'Configuration'],
    ['headerToolbar.languageSelectorTemplate', 'Language Select'],
    ['headerToolbar.userAccountTemplate', 'User Account'],
    ['se.cms.perspective.basic', 'Basic Edit'],
    ['se.cms.perspective.versioning', 'Versioning'],
    ['personalizationsmartedit.perspective', 'Personalization'],
    ['se.cms.perspective.advanced', 'Advanced Edit'],
    ['se.none', 'Preview'],
    ['se.cms.componentMenuTemplate', 'Component'],
    ['se.cms.pageTreeMenu', USER_TRACKING_FUNCTIONALITY.PAGE_STRUCTURE],
    [
        'personalizationsmartedit.container.pagecustomizations.toolbar',
        'Personalize - Customization'
    ],
    ['personalizationsmartedit.container.combinedview.toolbar', 'Personlization - CombinedView'],
    ['personalizationsmartedit.container.manager.toolbar', 'Personlization - Library'],
    ['se.cms.createVersionMenu', 'Add Version'],
    ['se.cms.clonePageMenu', 'Clone'],
    ['se.cms.compomentmenu.tabs.componenttypes', 'Component Types'],
    ['se.cms.compomentmenu.tabs.customizedcomp', 'Saved Components'],
    ['se.cms.contextmenu.title.dragndrop', 'Drag and Drop'],
    ['se.cms.contextmenu.title.edit', 'Edit'],
    ['se.cms.contextmenu.title.remove', 'Remove'],
    ['se.cms.contextmenu.title.clone.component', 'Clone'],
    ['se.cms.pageVersionsMenu', 'Versions'],
    ['se.cms.contextmenu.title.open.in.page.tree', 'Open In Page Tree'],
    ['se.cms.version.item.menu.view.label', 'View'],
    ['se.cms.version.item.menu.edit.label', 'Edit Details'],
    ['se.cms.version.item.menu.rollback.label', 'Rollback To This Version'],
    ['se.cms.version.item.menu.delete.label', 'Delete'],
    ['se.cms.rollbackVersionMenu', 'Version Rollback']
]);

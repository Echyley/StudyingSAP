/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export const PERSONALIZATION_DATE_FORMATS = {
    SHORT_DATE_FORMAT: 'M/D/YY',
    MODEL_DATE_FORMAT: 'YYYY-MM-DDTHH:mm:SSZ'
};

export const DATE_CONSTANTS = {
    ANGULAR_FORMAT: 'short',
    MOMENT_FORMAT: 'M/D/YY h:mm A',
    MOMENT_ISO: 'YYYY-MM-DDTHH:mm:00ZZ',
    ISO: 'yyyy-MM-ddTHH:mm:00Z'
};

export enum PERSONALIZATION_MODEL_STATUS_CODES {
    ENABLED = 'ENABLED',
    DISABLED = 'DISABLED',
    DELETED = 'DELETED'
}

export const PERSONALIZATION_VIEW_STATUS_MAPPING_CODES: {
    ALL: 'ALL';
    ENABLED: 'ENABLED';
    DISABLED: 'DISABLED';
} = {
    ALL: 'ALL',
    ENABLED: 'ENABLED',
    DISABLED: 'DISABLED'
};

export const PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING: {
    [index: number]: { borderClass: string; listClass: string };
} = {
    0: {
        borderClass: 'personalizationsmarteditComponentSelected0',
        listClass: 'personalizationsmarteditComponentSelectedList0'
    },
    1: {
        borderClass: 'personalizationsmarteditComponentSelected1',
        listClass: 'personalizationsmarteditComponentSelectedList1'
    },
    2: {
        borderClass: 'personalizationsmarteditComponentSelected2',
        listClass: 'personalizationsmarteditComponentSelectedList2'
    },
    3: {
        borderClass: 'personalizationsmarteditComponentSelected3',
        listClass: 'personalizationsmarteditComponentSelectedList3'
    },
    4: {
        borderClass: 'personalizationsmarteditComponentSelected4',
        listClass: 'personalizationsmarteditComponentSelectedList4'
    },
    5: {
        borderClass: 'personalizationsmarteditComponentSelected5',
        listClass: 'personalizationsmarteditComponentSelectedList5'
    },
    6: {
        borderClass: 'personalizationsmarteditComponentSelected6',
        listClass: 'personalizationsmarteditComponentSelectedList6'
    },
    7: {
        borderClass: 'personalizationsmarteditComponentSelected7',
        listClass: 'personalizationsmarteditComponentSelectedList7'
    },
    8: {
        borderClass: 'personalizationsmarteditComponentSelected8',
        listClass: 'personalizationsmarteditComponentSelectedList8'
    },
    9: {
        borderClass: 'personalizationsmarteditComponentSelected9',
        listClass: 'personalizationsmarteditComponentSelectedList9'
    },
    10: {
        borderClass: 'personalizationsmarteditComponentSelected10',
        listClass: 'personalizationsmarteditComponentSelectedList10'
    },
    11: {
        borderClass: 'personalizationsmarteditComponentSelected11',
        listClass: 'personalizationsmarteditComponentSelectedList11'
    },
    12: {
        borderClass: 'personalizationsmarteditComponentSelected12',
        listClass: 'personalizationsmarteditComponentSelectedList12'
    },
    13: {
        borderClass: 'personalizationsmarteditComponentSelected13',
        listClass: 'personalizationsmarteditComponentSelectedList13'
    },
    14: {
        borderClass: 'personalizationsmarteditComponentSelected14',
        listClass: 'personalizationsmarteditComponentSelectedList14'
    }
};

export const COMBINED_VIEW_TOOLBAR_ITEM_KEY =
    'personalizationsmartedit.container.combinedview.toolbar';

export const CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY =
    'personalizationsmartedit.container.pagecustomizations.toolbar';

export const PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES = {
    OLD: 'old',
    NEW: 'new',
    DELETE: 'delete',
    UPDATE: 'update'
};

export const PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER = {
    ALL: 'all',
    ONLY_THIS_PAGE: 'onlythispage'
};

export const PERSONALIZATION_CATALOG_FILTER = {
    ALL: 'all',
    CURRENT: 'current',
    PARENTS: 'parents'
};

export const COMPONENT_CONTAINER_TYPE = 'CxCmsComponentContainer';
export const CONTAINER_SOURCE_ID_ATTR = 'data-smartedit-container-source-id';

export const CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS = {
    BASIC_INFO_TAB_NAME: 'basicinfotab',
    BASIC_INFO_TAB_FORM_NAME: 'form.basicinfotab',
    TARGET_GROUP_TAB_NAME: 'targetgrptab',
    TARGET_GROUP_TAB_FORM_NAME: 'form.targetgrptab'
};

export const CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS = {
    CONFIRM_OK: 'confirmOk',
    CONFIRM_CANCEL: 'confirmCancel',
    CONFIRM_NEXT: 'confirmNext'
};

export const CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY = {
    CRITERIA_AND: 'AND',
    CRITERIA_OR: 'OR'
};

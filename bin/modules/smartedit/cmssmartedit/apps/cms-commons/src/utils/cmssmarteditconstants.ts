/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Constants identifying CMS drag and drop events.
 */
export enum DRAG_AND_DROP_EVENTS {
    /**
     * Name of event executed when a drag and drop event starts.
     */
    DRAG_STARTED = 'CMS_DRAG_STARTED',
    /**
     * Name of event executed when a drag and drop event stops.
     */
    DRAG_STOPPED = 'CMS_DRAG_STOPPED',
    /**
     * Name of event executed when onDragOver is triggered.
     */
    DRAG_OVER = 'CMS_DRAG_OVER',
    /**
     * Name of event executed when onDragLeave is triggered.
     */
    DRAG_LEAVE = 'CMS_DRAG_LEAVE',
    /**
     * Name of event executed when onDrop is finished.
     */
    SCROLL_TO_MODIFIED_SLOT = 'SCROLL_TO_MODIFIED_SLOT'
}

export const COMPONENT_CREATED_EVENT = 'COMPONENT_CREATED_EVENT';
export const COMPONENT_REMOVED_EVENT = 'COMPONENT_REMOVED_EVENT';
export const COMPONENT_UPDATED_EVENT = 'COMPONENT_UPDATED_EVENT';

export const CMSITEMS_UPDATE_EVENT = 'CMSITEMS_UPDATE';

export const EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV = 'EVENT_PAGE_STATUS_UPDATED_IN_ACTIVE_CV';

export const NAVIGATION_NODE_TYPECODE = 'CMSNavigationNode';
export const NAVIGATION_NODE_ROOT_NODE_UID = 'root';

export const IMAGES_URL = '/cmssmartedit/images';

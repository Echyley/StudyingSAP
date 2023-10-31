/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Pageable } from '@smart/utils';
/**
 * Represents a page version.
 */
export interface IPageVersion {
    /**
     * uid of the version.
     */
    uid: string;
    /**
     * uuid of the item.
     */
    itemUUID: string;
    /**
     * date time when the page was created.
     */
    creationtime: Date;
    /**
     * user friendly name of the page version.
     */
    label: string;
    /**
     * optional string that describes the page version.
     */
    description?: string;
}

/**
 * Represents a payload to query page versions.
 */
export interface PageVersionSearchPayload extends Pageable {
    /**
     * uuid of the page whose versions to retrieve
     */
    pageUuid: string;
}

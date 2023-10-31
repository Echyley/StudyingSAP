/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IPageInfoService } from './interfaces';
export class PageInfoService extends IPageInfoService {
    public static PATTERN_SMARTEDIT_CATALOG_VERSION_UUID = /smartedit-catalog-version-uuid\-(\S+)/;
    getPageUID(): Promise<string> {
        return null;
    }

    getPageUUID(): Promise<string> {
        return null;
    }

    getCatalogVersionUUIDFromPage(): Promise<string> {
        return null;
    }
}

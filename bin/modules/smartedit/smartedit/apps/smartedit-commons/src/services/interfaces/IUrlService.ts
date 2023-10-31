/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Provides an abstract extensible url service, Used to open a given URL
 * in a new browser url upon invocation.
 *
 * This class serves as an interface and should be extended, not instantiated.
 */
import {
    CONTEXT_CATALOG,
    CONTEXT_CATALOG_VERSION,
    CONTEXT_SITE_ID,
    PAGE_CONTEXT_CATALOG,
    PAGE_CONTEXT_CATALOG_VERSION,
    PAGE_CONTEXT_SITE_ID
} from 'smarteditcommons/utils/smarteditconstants';
import { IUriContext } from './IUriContext';

export abstract class IUrlService {
    /**
     * Opens a given URL in a new browser pop up without authentication.
     */
    openUrlInPopup(url: string): void {
        'proxyFunction';
        return null;
    }

    /**
     * Navigates to the given path in the same browser tab.
     */
    path(path: string): void {
        'proxyFunction';
        return null;
    }

    /**
     * Returns a uri context array populated with the given siteId, catalogId and catalogVersion information
     */
    buildUriContext(siteId: string, catalogId: string, catalogVersion: string): IUriContext {
        const uriContext: IUriContext = {};
        uriContext[CONTEXT_SITE_ID] = siteId;
        uriContext[CONTEXT_CATALOG] = catalogId;
        uriContext[CONTEXT_CATALOG_VERSION] = catalogVersion;
        return uriContext;
    }

    /**
     * Returns a page uri context array populated with the given siteId, catalogId and catalogVersion information
     */
    buildPageUriContext(siteId: string, catalogId: string, catalogVersion: string): IUriContext {
        const uriContext: IUriContext = {};
        uriContext[PAGE_CONTEXT_SITE_ID] = siteId;
        uriContext[PAGE_CONTEXT_CATALOG] = catalogId;
        uriContext[PAGE_CONTEXT_CATALOG_VERSION] = catalogVersion;
        return uriContext;
    }
}

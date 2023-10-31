/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import {
    IGenericEditorDropdownSelectedOptionEventData,
    LINKED_DROPDOWN,
    SystemEventService
} from 'smarteditcommons';
import { ISelectedSite, SITES_ID } from '../../../../components/pages/landingPage';

/**
 * @ignore
 * @internal
 *
 * Navigates to a site with the given site id.
 */
@Injectable()
export class CatalogNavigateToSite {
    constructor(private systemEvent: SystemEventService) {}

    navigate(siteId: string): void {
        this.systemEvent.publishAsync(SITES_ID + LINKED_DROPDOWN, {
            qualifier: 'site',
            optionObject: {
                contentCatalogs: [],
                uid: siteId,
                id: siteId,
                label: {},
                name: {},
                previewUrl: ''
            }
        } as IGenericEditorDropdownSelectedOptionEventData<ISelectedSite>);
    }
}

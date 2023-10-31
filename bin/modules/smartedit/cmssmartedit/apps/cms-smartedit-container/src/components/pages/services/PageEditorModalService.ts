/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { DEFAULT_SYNCHRONIZATION_POLLING as SYNCHRONIZATION_POLLING } from 'cmscommons';
import {
    CrossFrameEventService,
    EVENT_CONTENT_CATALOG_UPDATE,
    LogService,
    SystemEventService,
    ICMSPage
} from 'smarteditcommons';
import { GenericEditorModalService } from '../../../services';
import { PageEditorModalConfigService } from './PageEditorModalConfigService';

/**
 * The page editor modal service module provides a service that allows opening an editor modal for a given page. The editor modal is populated with a save and cancel button, and is loaded with the
 * editorTabset of cmssmarteditContainer as its content, providing a way to edit
 * various fields of the given page.
 *
 * Convenience service to open an editor modal window for a given page's data.
 */
@Injectable()
export class PageEditorModalService {
    constructor(
        private genericEditorModalService: GenericEditorModalService,
        private crossFrameEventService: CrossFrameEventService,
        private systemEventService: SystemEventService,
        private pageEditorModalConfigService: PageEditorModalConfigService,
        private logService: LogService
    ) {}

    /**
     * Opens Editor Modal.
     *
     * The editor modal is initialized with a title in the format '<TypeName> Editor', ie: 'Paragraph Editor'.
     * The editor modal is also wired with a save and cancel button.
     *
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    public async open(page: ICMSPage): Promise<any> {
        const config = await this.pageEditorModalConfigService.create(page);
        try {
            const updatedCmsPage = await this.genericEditorModalService.open<ICMSPage>(
                config,
                () => {
                    this.crossFrameEventService.publish(
                        SYNCHRONIZATION_POLLING.FETCH_SYNC_STATUS_ONCE,
                        page.uuid
                    );
                }
            );
            this.systemEventService.publishAsync(EVENT_CONTENT_CATALOG_UPDATE, updatedCmsPage);
        } catch (error) {
            this.logService.debug('Page Editor Modal dismissed', error);
        }
    }
}

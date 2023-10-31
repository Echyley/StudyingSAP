/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { CrossFrameEventService, EVENT_OPEN_IN_PAGE_TREE } from 'smarteditcommons';

@Injectable()
export class OpenNodeInPageTreeService {
    private timeout: any = null;
    constructor(private crossFrameEventService: CrossFrameEventService) {}

    /*
     * When user click the 'Open In Page Tree' button, it will publish the event and the page tree
     * will open the component node on tree.
     * But if the pagetree is not open firstly, EVENT_OPEN_IN_PAGE_TREE will
     * inform storefrontpage to open the page tree. And it should publish the event second time
     * to open the component node on tree.
     * */
    public publishOpenEvent(elementUuid: string): void {
        this.timeout && clearTimeout(this.timeout);
        this.crossFrameEventService.publish(EVENT_OPEN_IN_PAGE_TREE, elementUuid);

        this.timeout = setTimeout(() => {
            this.crossFrameEventService.publish(EVENT_OPEN_IN_PAGE_TREE, elementUuid);
        }, 1000);
    }
}

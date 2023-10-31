/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { DRAG_AND_DROP_EVENTS } from 'cmscommons';
import { CLOSE_CTX_MENU, CTX_MENU_DROPDOWN_IS_OPEN, SystemEventService } from 'smarteditcommons';

/**
 * ContextualMenuDropdownService is an internal service that provides methods for interaction between
 * Drag and Drop Service and the Contextual Menu.
 *
 * Note: The contextualMenuDropdownService functions are as a glue between the Drag and Drop Service and the Contextual Menu.
 *  The service was created to solve the issue of closing any contextual menu that is open whenever a drag operation is started.
 *  It does so while keeping the DnD and Contextual Menu services decoupled.
 */
@Injectable()
export class ContextualMenuDropdownService {
    private unsubscribeFn: () => void;
    constructor(private systemEventService: SystemEventService) {}

    public registerIsOpenEvent(): void {
        this.systemEventService.subscribe(CTX_MENU_DROPDOWN_IS_OPEN, () => {
            this.registerDragStarted();
        });
    }

    private registerDragStarted(): void {
        this.unsubscribeFn = this.systemEventService.subscribe(
            DRAG_AND_DROP_EVENTS.DRAG_STARTED,
            () => {
                this.triggerCloseOperation();
            }
        );
    }

    private triggerCloseOperation(): void {
        this.systemEventService.publishAsync(CLOSE_CTX_MENU);
        if (this.unsubscribeFn) {
            this.unsubscribeFn();
        }
    }
}

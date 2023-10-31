/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { CdkDragMove } from '@angular/cdk/drag-drop';
import { DOCUMENT } from '@angular/common';
import { Inject, Injectable } from '@angular/core';

// Detailed explanation of this solution is here: https://stackoverflow.com/questions/67337934/angular-nested-drag-and-drop-cdk-material-cdkdroplistgroup-cdkdroplist-nested
@Injectable()
export class SegmentDragAndDropService {
    currentHoverDropListId?: string;

    constructor(@Inject(DOCUMENT) private document: Document) {}

    dragMoved(event: CdkDragMove<HTMLElement>): void {
        const elementFromPoint = this.document.elementFromPoint(
            event.pointerPosition.x,
            event.pointerPosition.y
        );

        if (!elementFromPoint) {
            this.currentHoverDropListId = null;
            return;
        }

        const dropList = elementFromPoint.classList.contains('cdk-drop-list')
            ? elementFromPoint
            : elementFromPoint.closest('.cdk-drop-list');

        if (!dropList) {
            this.currentHoverDropListId = null;
            return;
        }

        this.currentHoverDropListId = dropList.id;
    }

    dragReleased(): void {
        this.currentHoverDropListId = null;
    }
}

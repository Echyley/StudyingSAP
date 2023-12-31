/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { forwardRef, Component, Inject } from '@angular/core';
import { GenericEditorComponent } from '../../GenericEditorComponent';
import { GenericEditorStackService } from '../../services';
import { GenericEditorInfo } from '../../types';

import './genericEditorBreadcrumb.scss';

/**
 * Component responsible for rendering a breadcrumb on top of the generic editor
 * when there is more than one editor opened on top of each other.
 * This will happen when editing nested components.
 *
 * @param editorStackId The string that identifies the stack of editors being edited together.
 */
@Component({
    selector: 'se-generic-editor-breadcrumb',
    template: `
        <div class="se-ge-breadcrumb">
            <ng-container *ngIf="showBreadcrumb()">
                <div
                    *ngFor="let breadcrumbItem of getEditorsStack(); let i = index"
                    class="se-ge-breadcrumb__item">
                    <div class="se-ge-breadcrumb__data">
                        <span class="se-ge-breadcrumb__title">
                            {{ getComponentName(breadcrumbItem) | translate }}
                        </span>
                        <span class="se-ge-breadcrumb__info">
                            {{ breadcrumbItem.componentType | translate }}
                        </span>
                    </div>

                    <div class="se-ge-breadcrumb__divider" *ngIf="i < getEditorsStack().length - 1">
                        <i class="sap-icon--navigation-right-arrow"></i>
                    </div>
                </div>
            </ng-container>
        </div>
    `
})
export class GenericEditorBreadcrumbComponent {
    private editorsStack: GenericEditorInfo[];

    constructor(
        private genericEditorStackService: GenericEditorStackService,
        @Inject(forwardRef(() => GenericEditorComponent)) private ge: GenericEditorComponent
    ) {}

    getEditorsStack(): GenericEditorInfo[] {
        if (!this.editorsStack) {
            this.editorsStack = this.genericEditorStackService.getEditorsStack(
                this.ge.editorStackId
            );
        }

        return this.editorsStack;
    }

    showBreadcrumb(): boolean {
        return this.getEditorsStack() && this.getEditorsStack().length > 1;
    }

    getComponentName(breadcrumbItem: GenericEditorInfo): any {
        if (!breadcrumbItem.component.name) {
            return 'se.breadcrumb.name.empty';
        }

        return breadcrumbItem.component.name;
    }
}

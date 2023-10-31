/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { IUriContext, CMSItem } from 'smarteditcommons';
// TODO: When restriction module is migrated check if can get rid off 2-way binding for isDirty and submit

@Component({
    selector: 'se-item-manager',
    templateUrl: './ItemManagerComponent.html'
})
export class ItemManagementComponent implements OnInit {
    @Input() item: CMSItem;
    @Input() uriContext: IUriContext;
    @Input() mode: string; // 'edit', 'add', or 'create'
    @Input() contentApi: string;
    @Input() structureApi: string;
    @Input() componentType?: string;
    @Input() isDirty: () => boolean;
    @Input() submitFunction: () => Promise<CMSItem>;

    @Output() isDirtyChange: EventEmitter<() => boolean>;
    @Output() submitFunctionChange: EventEmitter<() => Promise<CMSItem>>;

    public editorId: string;
    public itemId: string;
    public submit: () => Promise<CMSItem>;
    public isDirtyInternal: () => boolean;
    // Passing reset to GenericEditor hides extra cancel button when form is dirty
    public reset: boolean;
    private readonly supportedModes: string[];

    constructor() {
        this.supportedModes = ['add', 'edit', 'create'];
        this.editorId = 'se-item-management-editor';
        this.isDirtyChange = new EventEmitter();
        this.submitFunctionChange = new EventEmitter();
        this.reset = true;
    }

    ngOnInit(): void {
        this.validateMode();

        this.submitFunctionChange.emit(() => this.submitInternal());
        this.isDirtyChange.emit(() => this.isDirtyLocal());

        if (!this.componentType && this.item) {
            this.componentType = this.item.typeCode;
        }

        if (!this.item) {
            this.itemId = null;
        }

        if (this.item && this.item.uuid) {
            this.itemId = this.item.uuid;
        } else if (this.item && this.item.uid) {
            this.itemId = this.item.uid;
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        const itemChange = changes.item;
        if (itemChange) {
            this.itemId = null;

            if (this.item && this.item.uuid) {
                this.itemId = this.item.uuid;
            } else if (this.item && this.item.uid) {
                this.itemId = this.item.uid;
            }
        }
    }

    private validateMode(): void {
        if (!this.supportedModes.includes(this.mode)) {
            throw 'ItemManagementComponent.ngOnInit() - Mode not supported: ' + this.mode;
        }
    }

    private submitInternal(): Promise<CMSItem> {
        switch (this.mode.toLowerCase()) {
            case 'add':
                return Promise.resolve(this.item);
            case 'edit':
                return this.submit();
            case 'create':
                return this.submit().then((itemResponse) => itemResponse);

            default:
                throw `ItemManagementController - The given mode [${this.mode}] has not been implemented for this component`;
        }
    }

    private isDirtyLocal(): boolean {
        if (this.isDirtyInternal) {
            return this.isDirtyInternal();
        }
        return false;
    }
}

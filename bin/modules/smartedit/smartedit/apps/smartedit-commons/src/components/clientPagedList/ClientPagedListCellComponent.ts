/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import { ChangeDetectionStrategy, Component, Injector, Input, OnInit } from '@angular/core';
import {
    ClientPagedListColumnKey,
    ClientPagedListItem,
    CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN
} from './interfaces';

/** @internal */
@Component({
    selector: 'se-client-paged-list-cell',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: './ClientPagedListCellComponent.html'
})
export class ClientPagedListCellComponent implements OnInit {
    @Input() item: ClientPagedListItem;
    @Input() key: ClientPagedListColumnKey;

    public componentInjector: Injector;

    constructor(private injector: Injector) {}

    ngOnInit(): void {
        this.createComponentInjector();
    }

    /**
     * Creates Injector for ClientPagedListColumnKey.component
     */
    private createComponentInjector(): void {
        this.componentInjector = Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: CLIENT_PAGED_LIST_CELL_COMPONENT_DATA_TOKEN,
                    useValue: {
                        item: this.item,
                        key: this.key
                    }
                }
            ]
        });
    }
}

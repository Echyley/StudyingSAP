/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* forbiddenNameSpaces useValue:false */
import {
    ChangeDetectionStrategy,
    Component,
    Injector,
    Input,
    SimpleChanges,
    Type
} from '@angular/core';

import { SelectItem } from '../interfaces';
import { SelectComponent } from '../SelectComponent';
import { ItemComponentData, ITEM_COMPONENT_DATA_TOKEN } from './interfaces';

/**
 * Component represents options and selected options of `SelectComponent`.
 *
 * Displays an Item Component which can have access to exposed values via {@link ITEM_COMPONENT_DATA_TOKEN}.
 *
 * @internal
 */
@Component({
    selector: 'se-item-printer',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: {
        '[class.se-item-printer]': 'true'
    },
    templateUrl: './ItemPrinterComponent.html'
})
export class ItemPrinterComponent<T extends SelectItem> {
    @Input() item: T;
    @Input() component: Type<any>;
    @Input() selectComponentCtx: SelectComponent<T>;
    @Input() isSelected = true;

    public componentInjector: Injector;
    public itemComponentData: ItemComponentData;

    constructor(private injector: Injector) {}

    ngOnChanges(changes: SimpleChanges): void {
        if (changes.item) {
            this.itemComponentData = this.createItemComponentData();
            if (this.component) {
                this.componentInjector = this.createItemComponentInjector();
            }
        }
    }

    private createItemComponentData(): ItemComponentData {
        return {
            item: this.item,
            selected: this.isSelected,
            select: this.selectComponentCtx
        };
    }

    private createItemComponentInjector(): Injector {
        return Injector.create({
            parent: this.injector,
            providers: [
                {
                    provide: ITEM_COMPONENT_DATA_TOKEN,
                    useValue: this.itemComponentData
                }
            ]
        });
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Inject,
    OnInit,
    ViewRef
} from '@angular/core';
import { ContextualMenuItemData, CONTEXTUAL_MENU_ITEM_DATA } from 'smarteditcommons';
import { IContextAwareEditableItemService } from '../../services';

@Component({
    selector: 'shared-component-button', // At some point it should be prefixed with "se-". I leave it as it is because I am not sure about the impact of changing the selector.
    templateUrl: './SharedComponentButtonComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SharedComponentButtonComponent implements OnInit {
    public isReady: boolean;
    public message: string;
    private smarteditComponentId: string;

    constructor(
        @Inject(CONTEXTUAL_MENU_ITEM_DATA) item: ContextualMenuItemData,
        private contextAwareEditableItemService: IContextAwareEditableItemService,
        private cdr: ChangeDetectorRef
    ) {
        this.isReady = false;
        ({
            componentAttributes: { smarteditComponentId: this.smarteditComponentId }
        } = item);
    }

    async ngOnInit(): Promise<void> {
        const isEditable = await this.contextAwareEditableItemService.isItemEditable(
            this.smarteditComponentId
        );
        this.message = `se.cms.contextmenu.shared.component.info.msg${
            isEditable ? '.editable' : ''
        }`;
        this.isReady = true;

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }
}

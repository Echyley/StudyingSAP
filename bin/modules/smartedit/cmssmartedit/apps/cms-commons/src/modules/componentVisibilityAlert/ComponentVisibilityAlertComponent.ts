/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { AlertRef } from '@fundamental-ngx/core';
import { stringUtils, IEditorModalService } from 'smarteditcommons';
import { IComponentVisibilityAlertService } from './IComponentVisibilityAlertService';
import { SlotComponent } from './types';

@Component({
    selector: 'se-component-visibility-alert',
    template: `<div>
        <p [translate]="message"></p>
        <div>
            <a fd-link style="cursor:pointer" (click)="onClick()" [translate]="hyperlinkLabel"></a>
        </div>
    </div>`,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentVisibilityAlertComponent {
    public hyperlinkLabel = 'se.cms.component.visibility.alert.hyperlink';
    public message: string;

    private component: SlotComponent;

    constructor(
        private editorModalService: IEditorModalService,
        private componentVisibilityAlertService: IComponentVisibilityAlertService,
        private alertRef: AlertRef
    ) {
        ({ component: this.component, message: this.message } = this.alertRef.data);
    }

    public async onClick(): Promise<void> {
        this.alertRef.dismiss();
        this.checkProvidedArguments(this.component);
        const item = await this.editorModalService.openAndRerenderSlot(
            this.component.itemType,
            this.component.itemId,
            'visibilityTab'
            // config
        );

        this.componentVisibilityAlertService.checkAndAlertOnComponentVisibility({
            itemId: item.uuid,
            itemType: item.itemtype,
            catalogVersion: item.catalogVersion,
            restricted: item.restricted,
            slotId: this.component.slotId,
            visible: item.visible
        });
    }

    // TODO: since we can leverage TypeScript it could possibly be removed once we migrate each consumer.
    private checkProvidedArguments(component: SlotComponent): void | never {
        const checkedArguments = [component.itemId, component.itemType, component.slotId];
        const nonEmptyArguments = checkedArguments.filter(
            (value) => value && !stringUtils.isBlank(value)
        );

        if (nonEmptyArguments.length !== checkedArguments.length) {
            throw new Error(
                'componentVisibilityAlertService.checkAndAlertOnComponentVisibility - missing properly typed parameters'
            );
        }
    }
}

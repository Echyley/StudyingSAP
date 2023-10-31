/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject } from '@angular/core';
import {
    GENERIC_EDITOR_WIDGET_DATA,
    GenericEditorWidgetData,
    GenericEditorField
} from 'smarteditcommons';

export interface LinkToggleDTO {
    linkToggle?: {
        urlLink?: string;
        external?: boolean;
    };
}

@Component({
    selector: 'se-link-toggle',
    templateUrl: './LinkToggleComponent.html',
    styleUrls: ['./LinkToggleComponent.scss']
})
export class LinkToggleComponent {
    public field: GenericEditorField;
    public model: LinkToggleDTO;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA)
        data: GenericEditorWidgetData<LinkToggleDTO>
    ) {
        ({ field: this.field, model: this.model } = data);

        if (!this.model.linkToggle) {
            this.model.linkToggle = {};
        }

        if (this.model.linkToggle.external === undefined) {
            this.model.linkToggle.external = true;
        }
    }

    public clearUrlLink(): void {
        this.model.linkToggle.urlLink = null;
    }

    public checkUrlLink(): void {
        if (this.model.linkToggle.urlLink === '') {
            this.model.linkToggle.urlLink = null;
        }
    }
}

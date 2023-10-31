/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ItemComponentData, ITEM_COMPONENT_DATA_TOKEN } from 'smarteditcommons';

@Component({
    selector: 'se-restriction-editor-criteria-select-item',
    changeDetection: ChangeDetectionStrategy.OnPush,
    template: `<span [translate]="data.item.editLabel"></span>`
})
export class RestrictionEditorCriteriaSelectItemComponent {
    constructor(@Inject(ITEM_COMPONENT_DATA_TOKEN) public data: ItemComponentData) {}
}

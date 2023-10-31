/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import {
    GenericEditorField,
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    IGenericEditor,
    CMSItem
} from 'smarteditcommons';
import { RestrictionsDTO } from '../../../../components/pages/pageWizard';
import { IRestrictionType } from '../../../../dao/RestrictionTypesRestService';
import { RestrictionTypesService } from '../../../../services/pageRestrictions/RestrictionTypesService';
import { RestrictionsService } from '../../../../services/RestrictionsService';

interface ComponentItem extends CMSItem {
    restrictions: string[];
}

/** Wrapper for Restrictions Editor Component */
@Component({
    selector: 'se-component-restrictions-editor',
    templateUrl: './ComponentRestrictionsEditorComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ComponentRestrictionsEditorComponent implements OnInit {
    public field: GenericEditorField;
    public model: ComponentItem;
    public isEditable: boolean;
    public getRestrictionTypes: () => Promise<IRestrictionType[]>;
    public getSupportedRestrictionTypes: () => Promise<string[]>;
    private editor: IGenericEditor;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<ComponentItem>,
        private restrictionsService: RestrictionsService,
        private restrictionTypesService: RestrictionTypesService
    ) {
        ({ editor: this.editor, field: this.field, model: this.model } = data);
        this.isEditable = !data.isFieldDisabled();
    }

    ngOnInit(): void {
        this.initGetRestrictionTypes();
        this.initGetSupportedRestrictionTypes();

        if (this.model.restrictions === undefined) {
            this.editor.form.pristine.restrictions = [];
            this.editor.form.pristine.onlyOneRestrictionMustApply = false;
        }
    }

    public onRestrictionsChange({
        onlyOneRestrictionMustApply,
        restrictionUuids,
        alwaysEnableSubmit
    }: RestrictionsDTO): void {
        this.model.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
        this.model.restrictions = restrictionUuids;
        this.editor.api.setAlwaysEnableSubmit(alwaysEnableSubmit);
    }

    public initGetRestrictionTypes(): void {
        this.getRestrictionTypes = (): Promise<IRestrictionType[]> =>
            this.restrictionTypesService.getRestrictionTypes();
    }

    public initGetSupportedRestrictionTypes(): void {
        this.getSupportedRestrictionTypes = (): Promise<string[]> =>
            this.restrictionsService.getSupportedRestrictionTypeCodes();
    }
}

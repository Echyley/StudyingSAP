/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, Inject, OnInit } from '@angular/core';
import {
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    IGenericEditor,
    ICMSPage
} from 'smarteditcommons';
import { RestrictionsDTO } from '../../../../../components/pages/pageWizard';
import { IRestrictionType } from '../../../../../dao/RestrictionTypesRestService';
import { RestrictionTypesService } from '../../../../../services/pageRestrictions/RestrictionTypesService';
import { RestrictionsService } from '../../../../../services/RestrictionsService';

/** Wrapper for Restrictions Editor that can access Generic Editor.  */
@Component({
    selector: 'se-page-restrictions-editor',
    templateUrl: './PageRestrictionsEditorComponent.html'
})
export class PageRestrictionsEditorComponent implements OnInit {
    public page: ICMSPage;
    public isEditable: boolean;
    public getRestrictionTypes: () => Promise<IRestrictionType[]>;
    public getSupportedRestrictionTypes: () => Promise<string[]>;
    public onRemoveValidationMessages: () => void;

    private editor: IGenericEditor;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<any>,
        private restrictionsService: RestrictionsService,
        private restrictionTypesService: RestrictionTypesService
    ) {
        ({ editor: this.editor, model: this.page } = data);
        this.isEditable = !data.isFieldDisabled();
    }

    ngOnInit(): void {
        this.initGetRestrictionTypes();
        this.initSupportedRestrictionTypes();
        this.initOnRemoveValidationMessages();
    }

    /**
     * Whenever a restriction is Added, Edited or Removed.
     * The method is called also when Restrictions Editor is initialized.
     */
    public onRestrictionsChange({
        onlyOneRestrictionMustApply,
        restrictionUuids,
        alwaysEnableSubmit
    }: RestrictionsDTO): void {
        this.page.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
        this.page.restrictions = restrictionUuids;
    }

    /** Data to populate Restriction Management Select dropdown.  */
    private initGetRestrictionTypes(): void {
        this.getRestrictionTypes = (): Promise<IRestrictionType[]> =>
            this.restrictionTypesService.getRestrictionTypesByPageType(this.page.typeCode);
    }

    /** Data to populate Restriction Management Select dropdown. */
    private initSupportedRestrictionTypes(): void {
        this.getSupportedRestrictionTypes = (): Promise<string[]> =>
            this.restrictionsService.getSupportedRestrictionTypeCodes();
    }

    /** Callback when "Clear All" button is clicked. */
    private initOnRemoveValidationMessages(): void {
        this.onRemoveValidationMessages = (): void => this.editor.api.clearMessages();
    }
}

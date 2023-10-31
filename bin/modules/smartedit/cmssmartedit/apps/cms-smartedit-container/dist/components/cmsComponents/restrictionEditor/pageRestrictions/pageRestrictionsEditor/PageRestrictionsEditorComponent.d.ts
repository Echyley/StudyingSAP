import { OnInit } from '@angular/core';
import { GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';
import { RestrictionsDTO } from '../../../../../components/pages/pageWizard';
import { IRestrictionType } from '../../../../../dao/RestrictionTypesRestService';
import { RestrictionTypesService } from '../../../../../services/pageRestrictions/RestrictionTypesService';
import { RestrictionsService } from '../../../../../services/RestrictionsService';
export declare class PageRestrictionsEditorComponent implements OnInit {
    data: GenericEditorWidgetData<any>;
    private restrictionsService;
    private restrictionTypesService;
    page: ICMSPage;
    isEditable: boolean;
    getRestrictionTypes: () => Promise<IRestrictionType[]>;
    getSupportedRestrictionTypes: () => Promise<string[]>;
    onRemoveValidationMessages: () => void;
    private editor;
    constructor(data: GenericEditorWidgetData<any>, restrictionsService: RestrictionsService, restrictionTypesService: RestrictionTypesService);
    ngOnInit(): void;
    onRestrictionsChange({ onlyOneRestrictionMustApply, restrictionUuids, alwaysEnableSubmit }: RestrictionsDTO): void;
    private initGetRestrictionTypes;
    private initSupportedRestrictionTypes;
    private initOnRemoveValidationMessages;
}

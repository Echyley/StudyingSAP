import { OnInit } from '@angular/core';
import { GenericEditorField, GenericEditorWidgetData, CMSItem } from 'smarteditcommons';
import { RestrictionsDTO } from '../../../../components/pages/pageWizard';
import { IRestrictionType } from '../../../../dao/RestrictionTypesRestService';
import { RestrictionTypesService } from '../../../../services/pageRestrictions/RestrictionTypesService';
import { RestrictionsService } from '../../../../services/RestrictionsService';
interface ComponentItem extends CMSItem {
    restrictions: string[];
}
export declare class ComponentRestrictionsEditorComponent implements OnInit {
    data: GenericEditorWidgetData<ComponentItem>;
    private restrictionsService;
    private restrictionTypesService;
    field: GenericEditorField;
    model: ComponentItem;
    isEditable: boolean;
    getRestrictionTypes: () => Promise<IRestrictionType[]>;
    getSupportedRestrictionTypes: () => Promise<string[]>;
    private editor;
    constructor(data: GenericEditorWidgetData<ComponentItem>, restrictionsService: RestrictionsService, restrictionTypesService: RestrictionTypesService);
    ngOnInit(): void;
    onRestrictionsChange({ onlyOneRestrictionMustApply, restrictionUuids, alwaysEnableSubmit }: RestrictionsDTO): void;
    initGetRestrictionTypes(): void;
    initGetSupportedRestrictionTypes(): void;
}
export {};

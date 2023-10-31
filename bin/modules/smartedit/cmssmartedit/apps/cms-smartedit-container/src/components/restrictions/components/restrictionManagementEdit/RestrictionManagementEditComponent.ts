/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IUriContext, URIBuilder, CMSItem, cmsitemsUri } from 'smarteditcommons';
import { RestrictionsService } from '../../../../services/RestrictionsService';

@Component({
    selector: 'se-restriction-management-edit',
    templateUrl: './RestrictionManagementEditComponent.html',
    styleUrls: ['./RestrictionManagementEditComponent.scss', '../../restrictions.scss']
})
// TODO: When restriction module is migrated check if can get rid off 2-way binding for isDirty and submit
export class RestrictionManagementEditComponent implements OnInit {
    @Input() restriction: CMSItem;
    @Input() getSupportedRestrictionTypes: () => Promise<string[]>;
    @Input() uriContext: IUriContext;
    @Input() isDirtyFn: () => boolean;
    @Input() submitFn: () => Promise<CMSItem>;

    @Output() isDirtyFnChange: EventEmitter<() => boolean>;
    @Output() submitFnChange: EventEmitter<() => Promise<CMSItem>>;

    public submitInternal: () => Promise<CMSItem>;
    public isDirtyInternal: () => boolean;
    public ready: boolean;
    public itemManagementMode: string;
    public contentApi: string;
    public structureApi: string;
    public isTypeSupported: boolean;

    constructor(private restrictionsService: RestrictionsService) {
        this.isDirtyFnChange = new EventEmitter();
        this.submitFnChange = new EventEmitter();
        this.ready = false;
        this.itemManagementMode = 'edit';
        this.structureApi = this.restrictionsService.getStructureApiUri(this.itemManagementMode);
    }

    async ngOnInit(): Promise<void> {
        const dryRunCmsItemsUri = cmsitemsUri + '/:identifier';
        this.restriction = this.restriction || ({} as unknown as CMSItem);
        this.contentApi = new URIBuilder(dryRunCmsItemsUri).replaceParams(this.uriContext).build();

        if (typeof this.getSupportedRestrictionTypes !== 'undefined') {
            const supportedTypes = await this.getSupportedRestrictionTypes();
            return this.emitActions(supportedTypes.includes(this.restriction.itemtype));
        } else {
            return this.emitActions(true);
        }
    }

    private emitActions(isRestrictionTypeSupported: boolean): void {
        this.isTypeSupported = isRestrictionTypeSupported;
        if (isRestrictionTypeSupported) {
            this.submitFnChange.emit(
                (): Promise<CMSItem> =>
                    this.submitInternal().then((itemResponse) => Promise.resolve(itemResponse))
            );
            this.isDirtyFnChange.emit(() => this.isDirtyLocal());
        } else {
            // type not supported, disable the save button always
            this.submitFnChange.emit((): Promise<CMSItem> => Promise.resolve(null));
            this.isDirtyFnChange.emit((): boolean => false);
        }
        this.ready = true;
    }

    private isDirtyLocal(): boolean {
        if (this.isDirtyInternal) {
            return this.isDirtyInternal();
        }
        return false;
    }
}

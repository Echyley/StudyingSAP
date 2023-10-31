/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    EventEmitter,
    Input,
    Output,
    Type,
    ViewEncapsulation,
    ViewRef
} from '@angular/core';
import {
    ActionableSearchItem,
    FetchStrategy,
    IUriContext,
    Page,
    SelectReset,
    SystemEventService,
    URIBuilder,
    CMSItem,
    cmsitemsUri
} from 'smarteditcommons';
import { IRestrictionType } from '../../../../dao/RestrictionTypesRestService';
import { RestrictionsService } from '../../../../services/RestrictionsService';
import {
    RestrictionManagementSelectModel,
    RestrictionManagementSelectModelFactory
} from '../../services/RestrictionManagementSelectModelFactory';
import { RestrictionManagementSelectItemComponent } from './restrictionManagementSelectItem/RestrictionManagementSelectItemComponent';

@Component({
    selector: 'se-restriction-management-select',
    templateUrl: './RestrictionManagementSelectComponent.html',
    styleUrls: ['./RestrictionManagementSelectComponent.scss'],
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class RestrictionManagementSelectComponent {
    @Input() uriContext: IUriContext;
    @Input() existingRestrictions: CMSItem[];
    @Input() fetchRestrictionTypes: () => Promise<IRestrictionType[]>;
    @Input() getSupportedRestrictionTypes: () => Promise<string[]>;
    @Input() isDirtyFn: () => boolean;
    @Input() submitFn: () => Promise<CMSItem>;

    @Output() isDirtyFnChange: EventEmitter<() => boolean>;
    @Output() submitFnChange: EventEmitter<() => Promise<CMSItem>>;

    public readonly RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID =
        'RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID';
    /**
     * Resets the second se-select model when parent model changes
     * or when new restriction in being created
     */
    public reset: SelectReset;
    public submitInternal: () => Promise<CMSItem>;
    public isDirtyInternal: () => boolean;
    public disableRestrictionChoice: (restriction: CMSItem) => boolean;
    public itemComponent: Type<RestrictionManagementSelectItemComponent>;
    public resultsHeaderLabel: string;
    public editorHeader: string;
    public selectModel: RestrictionManagementSelectModel;
    public fetchOptions: FetchStrategy<CMSItem>;
    public fetchStrategy: FetchStrategy<IRestrictionType>;
    public actionableSearchItem: ActionableSearchItem;
    public viewConfig: {
        showRestrictionSelector: boolean;
        showRestrictionEditor: boolean;
        mode: string;
        contentApi: string;
        structureApi?: string;
    };

    private createButtonUnsubscribe: () => void;

    constructor(
        private restrictionManagementSelectModelFactory: RestrictionManagementSelectModelFactory,
        private restrictionsService: RestrictionsService,
        private systemEventService: SystemEventService,
        private cdr: ChangeDetectorRef
    ) {
        this.resultsHeaderLabel = 'se.cms.restrictionmanagement.restrictionresults.header';
        this.itemComponent = RestrictionManagementSelectItemComponent;
        this.editorHeader = '';

        this.isDirtyFnChange = new EventEmitter();
        this.submitFnChange = new EventEmitter();
    }

    ngOnInit(): void {
        this.selectModel =
            this.restrictionManagementSelectModelFactory.createRestrictionManagementSelectModel(
                this.fetchRestrictionTypes,
                this.getSupportedRestrictionTypes
            );

        const dryRunCmsItemsUri = cmsitemsUri + '/:identifier';

        this.viewConfig = {
            showRestrictionSelector: false,
            showRestrictionEditor: false,
            mode: 'add',
            contentApi: new URIBuilder(dryRunCmsItemsUri).replaceParams(this.uriContext).build()
        };

        this.fetchOptions = {
            fetchPage: (
                search?: string,
                pageSize?: number,
                currentPage?: number
            ): Promise<Page<CMSItem>> =>
                this.selectModel.getRestrictionsPaged(search, pageSize, currentPage),
            fetchEntity: (): Promise<CMSItem> => this.selectModel.getRestrictionFromBackend()
        };

        this.fetchStrategy = {
            fetchAll: (): Promise<IRestrictionType[]> => this.selectModel.getRestrictionTypes()
        };

        this.disableRestrictionChoice = (restriction: CMSItem): boolean =>
            !!this.existingRestrictions.find(
                (existingRestriction) => restriction.uid === existingRestriction.uid
            );

        // To prevent "ExpressionChangeAfterItHasBeenChecked" error, because we change values
        // for instance in PageRestrictionsStepComponent.ts
        setTimeout(() => {
            this.submitFnChange.emit(async (): Promise<CMSItem> => {
                if (this.selectModel.isTypeSupported()) {
                    return this.submitInternal().then((value) => value);
                }
                return this.selectModel.getRestriction() as CMSItem;
            });

            this.isDirtyFnChange.emit((): boolean => {
                if (this.viewConfig.mode === 'add') {
                    // if we're in adding mode and an editor is displayed then a restriction has been picked
                    return this.viewConfig.showRestrictionEditor;
                } else if (this.isDirtyInternal) {
                    // if we're creating a new restriction the use isDirty from GE
                    return this.isDirtyInternal();
                }
                return false;
            });
        });

        this.createButtonUnsubscribe = this.systemEventService.subscribe(
            this.RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID,
            (_eventId, eventData) => this.createButtonEventHandler(eventData)
        );
    }

    ngOnDestroy(): void {
        if (this.createButtonUnsubscribe) {
            this.createButtonUnsubscribe();
        }
    }

    public selectRestrictionType(): void {
        if (this.selectModel.restrictionTypeSelected()) {
            if (this.viewConfig.showRestrictionSelector) {
                this.resetSelector();
            } else {
                this.viewConfig.showRestrictionSelector = true;
            }
            this.viewConfig.showRestrictionEditor = false;
        }
        this.actionableSearchItem = this.selectModel.isTypeSupported()
            ? { eventId: this.RESTRICTION_CREATE_BUTTON_PRESSED_EVENT_ID }
            : undefined;
    }

    public selectRestriction(): void {
        if (this.selectModel.restrictionSelected()) {
            this.setViewConfig('se.cms.restriction.management.select.editor.header.add', 'add');

            if (!this.viewConfig.showRestrictionEditor) {
                this.viewConfig.showRestrictionEditor = true;
            }
        }
    }

    public async createRestriction(name: string): Promise<void> {
        await this.selectModel.createRestrictionSelected(name, this.uriContext);
        this.setViewConfig('se.cms.restriction.management.select.editor.header.create', 'create');

        if (this.viewConfig.showRestrictionEditor) {
            this.resetSelector();
        } else {
            this.viewConfig.showRestrictionEditor = true;
        }

        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public showWarningMessage(): boolean {
        return this.selectModel.getRestriction() && !this.selectModel.isTypeSupported();
    }

    private createButtonEventHandler(name: string): void {
        this.createRestriction(name);
    }

    private resetSelector(): void {
        if (typeof this.reset === 'function') {
            this.reset(true);
        }
    }

    private setViewConfig(editorHeader: string, mode: string): void {
        this.editorHeader = editorHeader;
        this.viewConfig.mode = mode;
        this.viewConfig.structureApi = this.restrictionsService.getStructureApiUri(
            this.viewConfig.mode
        );
    }
}

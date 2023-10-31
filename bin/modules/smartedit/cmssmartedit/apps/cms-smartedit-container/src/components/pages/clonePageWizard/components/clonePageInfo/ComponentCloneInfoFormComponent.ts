/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PAGE_CONTEXT_CATALOG, PAGE_CONTEXT_CATALOG_VERSION } from 'cmscommons';
import { PageFacade } from 'cmssmarteditcontainer/facades';
import {
    GenericEditorAPI,
    GenericEditorStructure,
    GENERIC_EDITOR_LOADED_EVENT,
    GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
    ICatalogVersion,
    ILanguage,
    IUriContext,
    LanguageService,
    PAGE_CONTEXT_SITE_ID,
    SystemEventService,
    IPageService,
    ICMSPage
} from 'smarteditcommons';

@Component({
    selector: 'se-component-clone-info-form',
    templateUrl: './ComponentCloneInfoComponent.html'
})
export class ComponentCloneInfoFormComponent implements OnInit, OnDestroy {
    @Input() content: ICMSPage;
    @Input() pageTemplate: string;
    @Input() pageTypeCode: string;
    @Input() structure: GenericEditorStructure;
    @Input() targetCatalogVersion: ICatalogVersion;
    @Input() uriContext: IUriContext;
    @Input() submit: () => void;
    @Input() reset: () => void;
    @Input() isDirty: () => void;
    @Input() isValid: () => void;

    @Output() isDirtyChange: EventEmitter<() => void>;
    @Output() isValidChange: EventEmitter<() => void>;
    @Output() submitChange: EventEmitter<() => void>;
    @Output() resetChange: EventEmitter<() => void>;

    public catalogVersionContainsPageWithSameTypeCode: boolean;
    public genericEditorId: string;

    public isDirtyInternal: () => void;
    public isValidInternal: () => void;
    public submitInternal: () => void;
    public resetInternal: () => void;

    private pageLabel: string;
    private pageInfoEditorApi: GenericEditorAPI;
    private genericEditorLoadEventUnsubscribe: () => void;

    constructor(
        private translateService: TranslateService,
        private languageService: LanguageService,
        private pageFacade: PageFacade,
        private pageService: IPageService,
        private systemEventService: SystemEventService
    ) {
        this.genericEditorId = 'COMPONENT_CLONE_INFO_FORM_GENERIC_ID';
        this.isDirtyChange = new EventEmitter();
        this.isValidChange = new EventEmitter();
        this.submitChange = new EventEmitter();
        this.resetChange = new EventEmitter();
    }

    async ngOnInit(): Promise<void> {
        this.pageLabel = null;
        this.catalogVersionContainsPageWithSameTypeCode = false;

        if (
            this.pageTypeCode !== 'ContentPage' &&
            this.targetCatalogVersion &&
            !this.isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)
        ) {
            const uriContextForSelectedCatalogVersion = {
                [PAGE_CONTEXT_SITE_ID]: this.targetCatalogVersion.siteId,
                [PAGE_CONTEXT_CATALOG]: this.targetCatalogVersion.catalogId,
                [PAGE_CONTEXT_CATALOG_VERSION]: this.targetCatalogVersion.version
            };

            const pageExists = await this.pageService.primaryPageForPageTypeExists(
                this.pageTypeCode,
                uriContextForSelectedCatalogVersion
            );
            this.catalogVersionContainsPageWithSameTypeCode = pageExists;
        }

        // To prevent "ExpressionChangeAfterItHasBeenChecked" error
        setTimeout(() => {
            this.isDirtyChange.emit(() => this.isDirtyInternal && this.isDirtyInternal());
            this.isValidChange.emit(() => this.isValidInternal && this.isValidInternal());
            this.submitChange.emit(() => this.submitInternal && this.submitInternal());
            this.resetChange.emit(() => this.resetInternal && this.resetInternal());
        });

        this.genericEditorLoadEventUnsubscribe = this.systemEventService.subscribe(
            GENERIC_EDITOR_LOADED_EVENT,
            (_eventId, editorId) => this.handleWarningMessage(editorId)
        );
    }

    ngOnDestroy(): void {
        if (this.genericEditorLoadEventUnsubscribe) {
            this.genericEditorLoadEventUnsubscribe();
        }
    }

    public setGenericEditorApi(api: GenericEditorAPI): void {
        this.pageInfoEditorApi = api;

        if (
            this.targetCatalogVersion &&
            !this.isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)
        ) {
            this.pageInfoEditorApi.getLanguages = (): Promise<ILanguage[]> =>
                this.languageService.getLanguagesForSite(this.targetCatalogVersion.siteId);
        }
    }

    private isUriContextEqualToCatalogVersion(
        uriContext: IUriContext,
        catalogVersion: ICatalogVersion
    ): boolean {
        return (
            uriContext &&
            catalogVersion &&
            catalogVersion.siteId === uriContext.CURRENT_CONTEXT_SITE_ID &&
            catalogVersion.catalogId === uriContext.CURRENT_CONTEXT_CATALOG &&
            catalogVersion.version === uriContext.CURRENT_CONTEXT_CATALOG_VERSION
        );
    }

    private async handleWarningMessage(editorId: string): Promise<void> {
        if (editorId !== this.genericEditorId) {
            return;
        }

        if (
            this.pageTypeCode === 'ContentPage' &&
            this.targetCatalogVersion &&
            this.pageInfoEditorApi &&
            !this.isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)
        ) {
            const content = this.pageInfoEditorApi.getContent();

            if (content && content.label !== this.pageLabel) {
                this.pageLabel = content.label as string;
                await this.toggleWarningMessage();
            }
        }
    }

    private async toggleWarningMessage(): Promise<void> {
        const pageExists = await this.pageFacade.contentPageWithLabelExists(
            this.pageLabel,
            this.targetCatalogVersion.catalogId,
            this.targetCatalogVersion.version
        );
        if (pageExists) {
            this.systemEventService.publishAsync(
                GENERIC_EDITOR_UNRELATED_VALIDATION_MESSAGES_EVENT,
                {
                    messages: [
                        {
                            subject: 'label',
                            message: this.translateService.instant(
                                'se.cms.clonepagewizard.pageinfo.targetcatalogversion.label.exists.message'
                            ),
                            type: 'Warning'
                        }
                    ]
                }
            );
        } else {
            this.pageInfoEditorApi.clearMessages();
        }
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ChangeDetectionStrategy,
    ChangeDetectorRef,
    Component,
    Input,
    OnChanges,
    OnInit,
    SimpleChanges,
    ViewRef
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { isEqual } from 'lodash';
import { take } from 'rxjs/operators';
import {
    FetchStrategy,
    ICatalogVersion,
    IUriContext,
    IUrlService,
    L10nPipe,
    LogService,
    Page,
    SelectItem,
    TypedMap,
    IPageService,
    ICMSPage
} from 'smarteditcommons';
import { DisplayConditionsFacade } from '../../../../../facades';
import {
    HomepageService,
    ICatalogHomepageDetails,
    IDisplayCondition,
    PageDisplayConditionsService
} from '../../../../../services';

enum CatalogHomepageDetailsStatus {
    PENDING = 'PENDING',
    NO_HOMEPAGE = 'NO_HOMEPAGE',
    LOCAL = 'LOCAL',
    OLD = 'OLD',
    PARENT = 'PARENT'
}

interface DisplayConditionDTO {
    homepage: boolean;
    isPrimary: boolean;
    primaryPage?: ICMSPage;
}

/**
 * Component for selecting the page condition that can be applied to a new page.
 * The component takes a page type and some URI params that it needs to load the necessary information, and outputs
 * a display condition result. See below
 */
@Component({
    selector: 'se-new-page-display-condition',
    templateUrl: './NewPageDisplayConditionComponent.html',
    styleUrls: ['./NewPageDisplayConditionComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [L10nPipe]
})
export class NewPageDisplayConditionComponent implements OnInit, OnChanges {
    /**
     * The page typeCode of a potential new page
     */
    @Input() pageTypeCode: string;

    /**
     * The uri context containing site/catalog information.
     * This is necessary for the component to determine which display conditions can be applied.
     */
    @Input() uriContext: IUriContext;

    /**
     * An optional output function binding. Every time there is a change to the output,
     * or resulting display condition, this function (if it exists) will get executed with a
     * homepage: {Boolean} True if new page should replace current homepage
     * isPrimary: {Boolean} True if the chosen new page display condition is Primary
     * primaryPage: {Object} [Optional] If isPrimary is false (meaning this is a variant page), the value is a page object, representing the primary page that this new page will be a variant of.
     * as the single parameter
     */
    @Input() resultFn?: (data: DisplayConditionDTO) => void;

    @Input() pageUuid?: string;

    /**
     * I18n key to provide if any display condition must be selected by default.
     */
    @Input() initialConditionSelectedKey?: string;

    /**
     * ICatalogVersion object to provide if the target catalog version is selected
     */
    @Input() targetCatalogVersion?: ICatalogVersion;

    public conditions: IDisplayCondition[];
    public conditionSelected: IDisplayCondition;
    public conditionSelectorFetchStrategy: FetchStrategy;

    public currentHomePageName: string;
    public isReady: boolean;
    public homepage: boolean;
    public showReplaceLabel: boolean;

    public primarySelected: ICMSPage;
    public primarySelectedModel: string;
    public primaryPageChoicesFetchStrategy: FetchStrategy;

    public onDataChange: () => void;

    private homepageDetails: ICatalogHomepageDetails;

    constructor(
        private urlService: IUrlService,
        private homepageService: HomepageService,
        private displayConditionsFacade: DisplayConditionsFacade,
        private translateService: TranslateService,
        private pageService: IPageService,
        private logService: LogService,
        private pageDisplayConditions: PageDisplayConditionsService,
        private l10n: L10nPipe,
        private cdr: ChangeDetectorRef
    ) {
        this.conditions = null;

        this.homepage = false;
        this.homepageDetails = {
            status: CatalogHomepageDetailsStatus.PENDING
        };

        this.isReady = false;

        this.primarySelected = null;
        this.primarySelectedModel = null;

        this.showReplaceLabel = false;
    }

    ngOnInit(): void {
        this.onDataChange = (): void => this.dataChanged();

        this.conditionSelected = {} as IDisplayCondition;
        this.conditionSelectorFetchStrategy = {
            fetchAll: (): Promise<SelectItem[]> =>
                Promise.resolve(
                    (this.conditions || []).map((condition) => ({
                        id: condition.label,
                        label: condition.label
                    }))
                )
        };
        this.initialConditionSelectedKey =
            this.initialConditionSelectedKey || 'page.displaycondition.primary';

        this.primaryPageChoicesFetchStrategy = {
            fetchEntity: (): Promise<SelectItem> =>
                Promise.resolve({
                    id: this.primarySelected.uid,
                    label: this.primarySelected.name || this.primarySelected.label
                }),
            fetchPage: (
                search: string,
                pageSize: number,
                currentPage: number
            ): Promise<Page<SelectItem>> =>
                this.displayConditionsFacade.getPrimaryPagesForPageType(
                    this.pageTypeCode,
                    this.uriContext,
                    {
                        search,
                        pageSize,
                        currentPage
                    }
                )
        };
    }

    async ngOnChanges(changes: SimpleChanges): Promise<void> {
        // As typeCode is string we can do weak checking (double equals == instead of triple ===)
        // It's because we don't want to trigger change when e.g. previous value is undefined and current is null
        const hasPageTypeCodeChanged =
            !!changes.pageTypeCode &&
            // eslint-disable-next-line eqeqeq
            changes.pageTypeCode.currentValue != changes.pageTypeCode.previousValue;
        const hasTargetCatalogVersionChanged =
            !!changes.targetCatalogVersion &&
            !isEqual(
                changes.targetCatalogVersion.currentValue,
                changes.targetCatalogVersion.previousValue
            );

        if (hasPageTypeCodeChanged || hasTargetCatalogVersionChanged) {
            if (
                !this.targetCatalogVersion ||
                this.isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)
            ) {
                await this.getSelectedPrimaryPageAndDisplayCondition();
            } else {
                this.getOnlyPrimaryDisplayCondition();
            }
            const context =
                this.targetCatalogVersion === undefined
                    ? this.uriContext
                    : this.urlService.buildUriContext(
                          this.targetCatalogVersion.siteId,
                          this.targetCatalogVersion.catalogId,
                          this.targetCatalogVersion.version
                      );
            const homepageDetails = await this.homepageService.getHomepageDetailsForContext(
                context
            );
            this.homepageDetails = homepageDetails;
            await this.updateHomepageUiProperties();
        } else {
            await this.updateHomepageUiProperties();
        }
        if (!(this.cdr as ViewRef).destroyed) {
            this.cdr.detectChanges();
        }
    }

    public showPrimarySelector(): boolean {
        return !(this.conditionSelected && this.conditionSelected.isPrimary);
    }

    public onConditionChange(selectedConditionLabel: string): void {
        this.conditionSelected = this.conditions.find(
            (condition) => condition.label === selectedConditionLabel
        );
    }

    public onHomePageChange(isHomepage: boolean): void {
        this.homepage = isHomepage;
        this.dataChanged();
    }

    public showHomePageWidget(): boolean {
        return this.isPrimaryContentPage();
    }

    public async primarySelectedModelOnChange(uid: string): Promise<void> {
        const page = await this.pageService.getPageById(uid);
        this.setPrimarySelected(page);
        this.dataChanged();
    }

    private dataChanged(): void {
        if (!this.isPrimaryContentPage()) {
            this.homepage = undefined;
        }
        this.updateHomepageUiProperties();

        if (this.resultFn) {
            this.resultFn(this.getResults());
        }
    }

    private isPrimaryContentPage(): boolean {
        return this.conditionSelected?.isPrimary && this.pageTypeCode === 'ContentPage';
    }

    private async updateHomepageUiProperties(): Promise<void> {
        if (this.homepage) {
            switch (this.homepageDetails.status) {
                case CatalogHomepageDetailsStatus.NO_HOMEPAGE:
                    this.homepageService.sendEventHideReplaceParentHomePageInfo();
                    this.showReplaceLabel = false;
                    break;
                case CatalogHomepageDetailsStatus.PARENT:
                    const [parentCatalogName, targetCatalogName] = await this.getTranslatedNames(
                        this.homepageDetails.parentCatalogName,
                        this.homepageDetails.targetCatalogName
                    );
                    this.homepageService.sendEventShowReplaceParentHomePageInfo({
                        description: this.translateService.instant(
                            'se.cms.display.conditions.homepage.replace.parent.info.header',
                            {
                                parentCatalogName,
                                parentCatalogVersion: this.homepageDetails.parentCatalogVersion,
                                targetCatalogName,
                                targetCatalogVersion: this.homepageDetails.targetCatalogVersion
                            }
                        )
                    });
                    this.showReplaceLabel = false;
                    break;
                case CatalogHomepageDetailsStatus.LOCAL:
                    this.homepageService.sendEventHideReplaceParentHomePageInfo();
                    this.currentHomePageName = this.homepageDetails.currentHomepageName;
                    this.showReplaceLabel = true;
                    break;
                default:
                    // do nothing
                    break;
            }
        } else {
            this.homepageService.sendEventHideReplaceParentHomePageInfo();
            this.showReplaceLabel = false;
        }
    }

    private getTranslatedNames(
        name: TypedMap<string>,
        secondName: TypedMap<string>
    ): Promise<[string, string]> {
        return Promise.all([
            this.l10n.transform(name).pipe(take(1)).toPromise(),
            this.l10n.transform(secondName).pipe(take(1)).toPromise()
        ]);
    }

    private getResults(): DisplayConditionDTO {
        const result = {
            homepage: this.homepage,
            isPrimary: this.conditionSelected && this.conditionSelected.isPrimary,
            primaryPage: null
        };

        if (this.conditionSelected && !this.conditionSelected.isPrimary) {
            result.primaryPage = this.primarySelected;
        }

        return result;
    }

    private async getSelectedPrimaryPageAndDisplayCondition(): Promise<void> {
        if (this.pageTypeCode) {
            try {
                if (this.pageUuid) {
                    const page = await this.pageService.getPageByUuid(this.pageUuid);

                    // defaultPage means whether the page is a Primary Page
                    const primaryPage = await (!page.defaultPage
                        ? this.pageService.getPrimaryPage(page.uid)
                        : page);

                    this.setPrimarySelected(primaryPage);
                }
            } catch (error) {
                this.logService.error(error);
            } finally {
                this.getAllPrimaryDisplayCondition();
            }
        } else {
            this.getAllPrimaryDisplayCondition();
        }
    }

    private async getAllPrimaryDisplayCondition(): Promise<void> {
        try {
            const response = await this.pageDisplayConditions.getNewPageConditions(
                this.pageTypeCode,
                this.uriContext
            );

            if (!response.length) {
                return;
            }

            this.conditions = response;
            this.conditionSelected = this.conditions[0];

            if (this.conditions.length > 1) {
                this.conditionSelected = this.conditions.find(
                    (condition) => condition.label === this.initialConditionSelectedKey
                );
            }
            this.isReady = true;
        } catch (error) {
            this.logService.error(error);
        } finally {
            if (
                this.targetCatalogVersion &&
                !this.isUriContextEqualToCatalogVersion(this.uriContext, this.targetCatalogVersion)
            ) {
                this.getOnlyPrimaryDisplayCondition();
            } else {
                this.dataChanged();
            }
        }
    }

    private getOnlyPrimaryDisplayCondition(): void {
        this.conditions = [
            {
                description: 'page.displaycondition.primary.description',
                isPrimary: true,
                label: 'page.displaycondition.primary'
            }
        ];

        this.conditionSelected = this.conditions[0];

        this.isReady = true;
        this.dataChanged();
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

    private setPrimarySelected(page: ICMSPage): void {
        this.primarySelected = page;
        this.primarySelectedModel = page.uid;
    }
}

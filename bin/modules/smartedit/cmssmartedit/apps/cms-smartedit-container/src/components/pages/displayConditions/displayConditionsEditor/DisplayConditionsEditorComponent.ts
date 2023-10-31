/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    Component,
    ChangeDetectionStrategy,
    Inject,
    OnInit,
    ChangeDetectorRef,
    ViewRef
} from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { take } from 'rxjs/operators';
import {
    GenericEditorWidgetData,
    GENERIC_EDITOR_WIDGET_DATA,
    L10nPipe,
    LogService,
    TypedMap,
    IPageService,
    ICMSPage
} from 'smarteditcommons';
import {
    IDisplayConditionsPageVariation,
    IDisplayConditionsPrimaryPage
} from '../../../../facades';
import {
    HomepageService,
    DisplayConditionsEditorModel,
    ICatalogHomepageDetails
} from '../../../../services';

export enum CatalogHomepageDetailsStatus {
    PENDING = 'PENDING',
    NO_HOMEPAGE = 'NO_HOMEPAGE',
    LOCAL = 'LOCAL',
    OLD = 'OLD',
    PARENT = 'PARENT'
}

@Component({
    selector: 'se-display-conditions-editor',
    templateUrl: './DisplayConditionsEditorComponent.html',
    styleUrls: ['./DisplayConditionsEditorComponent.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [L10nPipe]
})
export class DisplayConditionsEditorComponent implements OnInit {
    public currentHomePageName: string;
    public hasFallback: boolean;
    public isPrimaryPage: boolean;
    public showReplaceLabel: boolean;
    public homepageDetails: ICatalogHomepageDetails;
    public page: ICMSPage;

    constructor(
        @Inject(GENERIC_EDITOR_WIDGET_DATA) public data: GenericEditorWidgetData<ICMSPage>,
        private route: ActivatedRoute,
        private displayConditionsEditorModel: DisplayConditionsEditorModel,
        private homepageService: HomepageService,
        private pageService: IPageService,
        private logService: LogService,
        private l10n: L10nPipe,
        private translateService: TranslateService,
        private cdr: ChangeDetectorRef
    ) {
        ({ model: this.page } = this.data);

        this.hasFallback = false;
        this.isPrimaryPage = false;
        this.showReplaceLabel = false;

        this.homepageDetails = {
            status: CatalogHomepageDetailsStatus.PENDING
        };
    }

    async ngOnInit(): Promise<void> {
        await this.displayConditionsEditorModel.initModel(this.page.uid);
        // Because this component is created dynamically it does not have direct access to params
        // So we need to recursively climb to top parent component and get params from there
        const routeParams = this.getRouteParams();

        try {
            const uriContext = await this.pageService.buildUriContextForCurrentPage(
                routeParams.siteId,
                routeParams.catalogId,
                routeParams.catalogVersion
            );

            const [isPagePrimary, hasFallbackHomePage, homePageDetails] = await Promise.all([
                this.pageService.isPagePrimaryWithContext(this.page.uid, uriContext),
                this.homepageService.hasFallbackHomePage(uriContext),
                this.homepageService.getHomepageDetailsForContext(uriContext)
            ]);

            this.isPrimaryPage = isPagePrimary;
            this.hasFallback = hasFallbackHomePage;
            this.homepageDetails = homePageDetails;
            if (!(this.cdr as ViewRef).destroyed) {
                this.cdr.detectChanges();
            }
        } catch {
            this.logService.error(
                'DisplayConditionsEditorComponent::ngOnInit - unable to retrieve uriContext'
            );
        }
    }

    public disableHomepageCheckbox(): boolean {
        // multi country with parent homepage
        if (this.hasFallback) {
            return false;
        }

        // multi or single with homepage in current catalog
        if (this.homepageDetails.status === CatalogHomepageDetailsStatus.LOCAL) {
            // editing existing local homepage
            if (this.homepageDetails.currentHomepageUid === this.page.uid) {
                return true;
            }
            // editing old local homepage (you can put it back to homepage again)
            if (this.homepageDetails.oldHomepageUid === this.page.uid) {
                return false;
            }

            // editing another page to be the new homepage
            return false;
        }

        // don't think this is reachable?
        // If there is a fallback then we can also check/uncheck
        return true;
    }

    public async homePageChanged(isHomepage: boolean): Promise<void> {
        this.page.homepage = isHomepage;
        if (this.page.homepage) {
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
                    this.showReplaceLabel =
                        this.page.uid !== this.homepageDetails.currentHomepageUid;
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

    public showHomePageWidget(): boolean {
        return (
            this.homepageDetails.status !== CatalogHomepageDetailsStatus.PENDING &&
            this.page.typeCode === 'ContentPage' &&
            this.isPrimaryPage
        );
    }

    public getPageName(): string {
        return this.displayConditionsEditorModel.pageName;
    }

    public getPageType(): string {
        return this.displayConditionsEditorModel.pageType;
    }

    public isPagePrimary(): boolean {
        return this.displayConditionsEditorModel.isPrimary;
    }

    public getVariations(): IDisplayConditionsPageVariation[] {
        return this.displayConditionsEditorModel.variations;
    }

    public getAssociatedPrimaryPage(): Partial<IDisplayConditionsPrimaryPage> {
        return this.displayConditionsEditorModel.associatedPrimaryPage;
    }

    public getIsAssociatedPrimaryReadOnly(): boolean {
        return this.displayConditionsEditorModel.isAssociatedPrimaryReadOnly;
    }

    public onPrimaryPageSelect(primaryPage: ICMSPage): void {
        this.page.label = primaryPage.label;
    }

    private getRouteParams(): Params {
        let demandedRoute = this.route.firstChild ? this.route.firstChild : this.route;

        while (!!demandedRoute.firstChild) {
            demandedRoute = demandedRoute.firstChild;
        }

        return demandedRoute.snapshot.params;
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
}

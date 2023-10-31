import { OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { GenericEditorWidgetData, L10nPipe, LogService, IPageService, ICMSPage } from 'smarteditcommons';
import { IDisplayConditionsPageVariation, IDisplayConditionsPrimaryPage } from '../../../../facades';
import { HomepageService, DisplayConditionsEditorModel, ICatalogHomepageDetails } from '../../../../services';
export declare enum CatalogHomepageDetailsStatus {
    PENDING = "PENDING",
    NO_HOMEPAGE = "NO_HOMEPAGE",
    LOCAL = "LOCAL",
    OLD = "OLD",
    PARENT = "PARENT"
}
export declare class DisplayConditionsEditorComponent implements OnInit {
    data: GenericEditorWidgetData<ICMSPage>;
    private route;
    private displayConditionsEditorModel;
    private homepageService;
    private pageService;
    private logService;
    private l10n;
    private translateService;
    private cdr;
    currentHomePageName: string;
    hasFallback: boolean;
    isPrimaryPage: boolean;
    showReplaceLabel: boolean;
    homepageDetails: ICatalogHomepageDetails;
    page: ICMSPage;
    constructor(data: GenericEditorWidgetData<ICMSPage>, route: ActivatedRoute, displayConditionsEditorModel: DisplayConditionsEditorModel, homepageService: HomepageService, pageService: IPageService, logService: LogService, l10n: L10nPipe, translateService: TranslateService, cdr: ChangeDetectorRef);
    ngOnInit(): Promise<void>;
    disableHomepageCheckbox(): boolean;
    homePageChanged(isHomepage: boolean): Promise<void>;
    showHomePageWidget(): boolean;
    getPageName(): string;
    getPageType(): string;
    isPagePrimary(): boolean;
    getVariations(): IDisplayConditionsPageVariation[];
    getAssociatedPrimaryPage(): Partial<IDisplayConditionsPrimaryPage>;
    getIsAssociatedPrimaryReadOnly(): boolean;
    onPrimaryPageSelect(primaryPage: ICMSPage): void;
    private getRouteParams;
    private getTranslatedNames;
}

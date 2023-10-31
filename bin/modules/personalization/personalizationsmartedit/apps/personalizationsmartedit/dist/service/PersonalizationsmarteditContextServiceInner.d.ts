/// <reference types="jquery" />
import { CombinedView, Customize, Personalization, PersonalizationsmarteditContextUtils, SeData } from 'personalizationcommons';
import { PersonalizationsmarteditContextServiceReverseProxy } from 'personalizationsmartedit/service/PersonalizationsmarteditContextServiceInnerReverseProxy';
import { IContextualMenuService } from 'smarteditcommons';
export declare class PersonalizationsmarteditContextService {
    protected yjQuery: JQueryStatic;
    protected contextualMenuService: IContextualMenuService;
    protected personalizationsmarteditContextServiceReverseProxy: PersonalizationsmarteditContextServiceReverseProxy;
    protected personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils;
    protected personalization: Personalization;
    protected customize: Customize;
    protected combinedView: CombinedView;
    protected seData: SeData;
    constructor(yjQuery: JQueryStatic, contextualMenuService: IContextualMenuService, personalizationsmarteditContextServiceReverseProxy: PersonalizationsmarteditContextServiceReverseProxy, personalizationsmarteditContextUtils: PersonalizationsmarteditContextUtils);
    getPersonalization(): Personalization;
    setPersonalization(personalization: Personalization): void;
    getCustomize(): Customize;
    setCustomize(customize: Customize): void;
    getCombinedView(): CombinedView;
    setCombinedView(combinedView: CombinedView): void;
    getSeData(): SeData;
    setSeData(seData: SeData): void;
    isCurrentPageActiveWorkflowRunning(): Promise<boolean>;
}

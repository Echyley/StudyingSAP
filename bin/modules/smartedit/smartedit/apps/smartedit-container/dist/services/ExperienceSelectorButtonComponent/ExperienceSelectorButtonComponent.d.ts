import './ExperienceSelectorButtonComponent.scss';
import { OnDestroy, OnInit } from '@angular/core';
import { CrossFrameEventService, IExperience, ISharedDataService, SystemEventService, L10nPipe } from 'smarteditcommons';
export declare class ExperienceSelectorButtonComponent implements OnInit, OnDestroy {
    private systemEventService;
    private crossFrameEventService;
    private locale;
    private sharedDataService;
    private l10nPipe;
    resetExperienceSelector: () => void;
    status: {
        isOpen: boolean;
    };
    isCurrentPageFromParent: boolean;
    parentCatalogVersion: string;
    experience: IExperience;
    experienceText: string;
    private unregFn;
    private unRegNewPageContextEventFn;
    constructor(systemEventService: SystemEventService, crossFrameEventService: CrossFrameEventService, locale: string, sharedDataService: ISharedDataService, l10nPipe: L10nPipe);
    ngOnInit(): Promise<void>;
    ngOnDestroy(): void;
    updateExperience(): Promise<void>;
    setPageFromParent(data: IExperience): Promise<void>;
    getNavigationArrowIconIsCollapse(): string;
    private setExperienceText;
    private getProductCatalogVersionTextByUuids;
}

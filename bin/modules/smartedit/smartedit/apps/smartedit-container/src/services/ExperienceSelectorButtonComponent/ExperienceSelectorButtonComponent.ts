/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import './ExperienceSelectorButtonComponent.scss';
import { DatePipe } from '@angular/common';
import { Component, Inject, LOCALE_ID, OnDestroy, OnInit } from '@angular/core';
import moment from 'moment';
import { take } from 'rxjs/operators';
import {
    CrossFrameEventService,
    DATE_CONSTANTS,
    EVENTS,
    IExperience,
    ISharedDataService,
    SystemEventService,
    EXPERIENCE_STORAGE_KEY,
    L10nPipe
} from 'smarteditcommons';

/** @internal  */
@Component({
    selector: 'se-experience-selector-button',
    templateUrl: './ExperienceSelectorButtonComponent.html',
    providers: [L10nPipe]
})
export class ExperienceSelectorButtonComponent implements OnInit, OnDestroy {
    public resetExperienceSelector: () => void;
    public status: { isOpen: boolean } = { isOpen: false };
    public isCurrentPageFromParent = false;
    public parentCatalogVersion: string;
    public experience: IExperience;
    public experienceText: string;

    private unregFn: () => void;
    private unRegNewPageContextEventFn: () => void;

    constructor(
        private systemEventService: SystemEventService,
        private crossFrameEventService: CrossFrameEventService,
        @Inject(LOCALE_ID) private locale: string,
        private sharedDataService: ISharedDataService,
        private l10nPipe: L10nPipe
    ) {}

    async ngOnInit(): Promise<void> {
        await this.updateExperience();
        await this.setExperienceText();
        this.unregFn = this.systemEventService.subscribe(EVENTS.EXPERIENCE_UPDATE, async () => {
            await this.updateExperience();
            await this.setExperienceText();
        });

        this.unRegNewPageContextEventFn = this.crossFrameEventService.subscribe(
            EVENTS.PAGE_CHANGE,
            (eventId: string, data: IExperience) => {
                this.setPageFromParent(data);
            }
        );
    }

    ngOnDestroy(): void {
        this.unregFn();
        this.unRegNewPageContextEventFn();
    }

    public async updateExperience(): Promise<void> {
        this.experience = (await this.sharedDataService.get(EXPERIENCE_STORAGE_KEY)) as IExperience;
    }

    public async setPageFromParent(data: IExperience): Promise<void> {
        const {
            pageContext: {
                catalogName,
                catalogVersion,
                catalogVersionUuid: pageContextCatalogVersionUuid
            },
            catalogDescriptor: { catalogVersionUuid: catalogDescriptorCatalogVersionUuid }
        } = data;

        const translatedName = await this.l10nPipe.transform(catalogName).pipe(take(1)).toPromise();
        this.parentCatalogVersion = `${translatedName} (${catalogVersion})`;

        this.isCurrentPageFromParent =
            catalogDescriptorCatalogVersionUuid !== pageContextCatalogVersionUuid;
    }

    public getNavigationArrowIconIsCollapse(): string {
        return this.status.isOpen ? 'se.arrowicon.collapse.title' : 'se.arrowicon.expand.title';
    }

    private async setExperienceText(): Promise<void> {
        if (!this.experience) {
            this.experienceText = '';
        }

        const {
            catalogDescriptor: { name, catalogVersion },
            languageDescriptor: { nativeName },
            time
        } = this.experience;
        const pipe = new DatePipe(this.locale);

        const transformedTime = time
            ? `  |  ${pipe.transform(
                  moment(time).isValid() ? time : moment.now(),
                  DATE_CONSTANTS.ANGULAR_SHORT
              )}`
            : '';

        const [translatedName, catalogVersions] = await Promise.all([
            this.l10nPipe.transform(name).pipe(take(1)).toPromise(),
            this.getProductCatalogVersionTextByUuids()
        ]);

        this.experienceText = `${translatedName} - ${catalogVersion}  |  ${nativeName}${transformedTime}${catalogVersions}`;
    }

    private async getProductCatalogVersionTextByUuids(): Promise<string> {
        const { productCatalogVersions } = this.experience;
        // Separator is used in map().join to provide "join" value at the beginning e.g.:
        // we join by " | " so the final string WITHOUT could be: `string | string2`
        // what we want is ` | string | string2` that why we add empty string at beginning of array
        const SEPARATOR = '';
        const versionPromises = productCatalogVersions.map(
            async ({ catalogName, catalogVersion }) => {
                const translatedName = await this.l10nPipe
                    .transform(catalogName)
                    .pipe(take(1))
                    .toPromise();
                return `${translatedName} (${catalogVersion})`;
            }
        );

        const versions = await Promise.all(versionPromises);

        return [SEPARATOR].concat(versions).join(' | ');
    }
}

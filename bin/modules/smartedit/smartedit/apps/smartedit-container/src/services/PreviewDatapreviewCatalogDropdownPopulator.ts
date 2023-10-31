/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import * as lo from 'lodash';
import { take } from 'rxjs/operators';
import {
    DropdownPopulatorInterface,
    DropdownPopulatorPayload,
    GenericEditorOption,
    IBaseCatalog,
    IBaseCatalogVersion,
    ICatalogService,
    ISite,
    LanguageService,
    IExperience,
    ISharedDataService,
    EXPERIENCE_STORAGE_KEY,
    L10nPipe,
    IdWithLabel
} from 'smarteditcommons';

/**
 * Implementation of DropdownPopulatorInterface for catalog dropdown in
 * experience selector to populate the list of catalogs by making a REST call to retrieve the sites and then the catalogs based on the site.
 */
@Injectable()
export class PreviewDatapreviewCatalogDropdownPopulator extends DropdownPopulatorInterface {
    constructor(
        private catalogService: ICatalogService,
        private sharedDataService: ISharedDataService,
        private l10nPipe: L10nPipe,
        languageService: LanguageService
    ) {
        super(lo, languageService);
    }

    /**
     *  Returns a promise resolving to a list of site - catalogs to be displayed in the experience selector.
     *
     */
    public fetchAll(payload: DropdownPopulatorPayload): Promise<GenericEditorOption[]> {
        return this.initCatalogVersionDropdownChoices(payload.search);
    }

    /** @internal */
    private async initCatalogVersionDropdownChoices(
        search: string
    ): Promise<GenericEditorOption[]> {
        try {
            const experience: IExperience = (await this.sharedDataService.get(
                EXPERIENCE_STORAGE_KEY
            )) as IExperience;
            const siteDescriptor: ISite = experience.siteDescriptor;
            const dropdownChoices = await this.getDropdownChoices(siteDescriptor, search);

            const ascDropdownChoices = lo
                .flatten(dropdownChoices)
                .sort((e1: GenericEditorOption, e2: GenericEditorOption) =>
                    (e1.label as string).localeCompare(e2.label as string)
                );
            return ascDropdownChoices;
        } catch (e) {
            throw new Error(e);
        }
    }

    /** @internal */
    private async getDropdownChoices(
        siteDescriptor: ISite,
        search: string
    ): Promise<GenericEditorOption[]> {
        const catalogs = await this.catalogService.getContentCatalogsForSite(siteDescriptor.uid);

        const optionPromises = lo
            .flatten(catalogs)
            .map((catalog) => this.getTranslatedCatalogVersionsOptions(siteDescriptor, catalog));
        const options = await Promise.all(lo.flatten(optionPromises));

        return options.filter((option) =>
            search ? option.label.toUpperCase().includes(search.toUpperCase()) : true
        );
    }

    private getTranslatedCatalogVersionsOptions(
        siteDescriptor: ISite,
        catalog: IBaseCatalog
    ): Promise<IdWithLabel>[] {
        return catalog.versions.map(async (catalogVersion: IBaseCatalogVersion) => {
            const catalogName = await this.l10nPipe
                .transform(catalog.name)
                .pipe(take(1))
                .toPromise();
            return {
                id: `${siteDescriptor.uid}|${catalog.catalogId}|${catalogVersion.version}`,
                label: `${catalogName} - ${catalogVersion.version}`
            };
        });
    }
}

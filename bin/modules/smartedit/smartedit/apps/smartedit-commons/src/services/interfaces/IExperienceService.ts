/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Payload } from '@smart/utils';
import * as lo from 'lodash';
import {
    IDefaultExperienceParams,
    IExperience,
    IExperienceCatalogVersion,
    IExperienceParams
} from './IExperience';
import { IPreviewCatalogVersionData, IPreviewData } from './IPreview';

/**
 * ExperienceService deals with building experience objects given a context.
 */
export abstract class IExperienceService {
    updateExperiencePageContext(
        pageCatalogVersionUuid: string,
        pageId: string
    ): Promise<IExperience> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves the active experience.
     */
    getCurrentExperience(): Promise<IExperience> {
        'proxyFunction';
        return null;
    }

    /**
     * Stores a given experience as current experience.
     * Invoking this method ensures that a hard refresh of the application will preserve the experience.
     */
    setCurrentExperience(experience: IExperience): Promise<IExperience> {
        'proxyFunction';
        return null;
    }

    /**
     * Determines whether the catalog version has changed between the previous and current experience
     */
    hasCatalogVersionChanged(): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves the active experience, creates a new preview ticket and returns a new preview url with an updated
     * previewTicketId query param
     *
     * @returns An URL containing the new `previewTicketId`
     */
    buildRefreshedPreviewUrl(): Promise<string> {
        'proxyFunction';
        return null;
    }

    /**
     * Retrieves the active experience, merges it with a new experience, creates a new preview ticket and reloads the
     * preview within the iframeManagerService
     *
     * @param newExperience The object containing new attributes to be merged with the current experience
     *
     * @returns A promise of the updated experience
     */
    updateExperience(newExperience?: Payload): Promise<IExperience> {
        'proxyFunction';
        return null;
    }

    loadExperience(params: IDefaultExperienceParams): Promise<any> {
        'proxyFunction';
        return null;
    }

    /**
     * This method compares all the properties of given experience of type IDefaultExperienceParams with the current experience.
     *
     * @param experience The object containing default experience params such as pageId, catalogId, catalogVersion and siteId
     *
     * @return True if current experience matches with the gien experience. Otherwise false.
     */
    compareWithCurrentExperience(experience: IDefaultExperienceParams): Promise<boolean> {
        'proxyFunction';
        return null;
    }

    /** @internal */
    _convertExperienceToPreviewData(experience: IExperience, resourcePath: string): IPreviewData {
        const previewData = lo.cloneDeep(experience) as any;
        const catalogVersions: IPreviewCatalogVersionData[] = [];

        delete previewData.catalogDescriptor;
        delete previewData.siteDescriptor;
        delete previewData.languageDescriptor;
        delete previewData.pageContext;
        delete previewData.productCatalogVersions;

        if (experience.productCatalogVersions && experience.productCatalogVersions.length) {
            experience.productCatalogVersions.forEach(
                (productCatalogVersion: IExperienceCatalogVersion) => {
                    catalogVersions.push({
                        catalog: productCatalogVersion.catalog,
                        catalogVersion: productCatalogVersion.catalogVersion
                    });
                }
            );
        }
        catalogVersions.push({
            catalog: experience.catalogDescriptor.catalogId,
            catalogVersion: experience.catalogDescriptor.catalogVersion
        });

        previewData.catalogVersions = catalogVersions;
        previewData.language = experience.languageDescriptor.isocode;
        previewData.resourcePath = resourcePath;
        previewData.siteId = experience.siteDescriptor.uid;

        return previewData as IPreviewData;
    }

    /**
     * If an experience is set in the shared data service, this method will load the preview for this experience (such as Catalog, language, date and time).
     * Otherwise, the user will be redirected to the landing page to select an experience.
     * To load a preview, we need to get a preview ticket from an API.
     * Here we set current location to null initially so that the iframe manager loads the provided url and set the location.
     *
     * @returns a promise returning the experience
     */
    initializeExperience(): Promise<IExperience> {
        'proxyFunction';
        return null;
    }

    /**
     * Given an object containing a siteId, catalogId, catalogVersion and catalogVersions (array of product catalog version uuid's), will return a reconstructed experience
     *
     */
    buildAndSetExperience(params: IExperienceParams): Promise<IExperience> {
        'proxyFunction';
        return null;
    }
}

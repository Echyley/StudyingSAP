import { Payload } from '@smart/utils';
import { IDefaultExperienceParams, IExperience, IExperienceParams } from './IExperience';
import { IPreviewData } from './IPreview';
/**
 * ExperienceService deals with building experience objects given a context.
 */
export declare abstract class IExperienceService {
    updateExperiencePageContext(pageCatalogVersionUuid: string, pageId: string): Promise<IExperience>;
    /**
     * Retrieves the active experience.
     */
    getCurrentExperience(): Promise<IExperience>;
    /**
     * Stores a given experience as current experience.
     * Invoking this method ensures that a hard refresh of the application will preserve the experience.
     */
    setCurrentExperience(experience: IExperience): Promise<IExperience>;
    /**
     * Determines whether the catalog version has changed between the previous and current experience
     */
    hasCatalogVersionChanged(): Promise<boolean>;
    /**
     * Retrieves the active experience, creates a new preview ticket and returns a new preview url with an updated
     * previewTicketId query param
     *
     * @returns An URL containing the new `previewTicketId`
     */
    buildRefreshedPreviewUrl(): Promise<string>;
    /**
     * Retrieves the active experience, merges it with a new experience, creates a new preview ticket and reloads the
     * preview within the iframeManagerService
     *
     * @param newExperience The object containing new attributes to be merged with the current experience
     *
     * @returns A promise of the updated experience
     */
    updateExperience(newExperience?: Payload): Promise<IExperience>;
    loadExperience(params: IDefaultExperienceParams): Promise<any>;
    /**
     * This method compares all the properties of given experience of type IDefaultExperienceParams with the current experience.
     *
     * @param experience The object containing default experience params such as pageId, catalogId, catalogVersion and siteId
     *
     * @return True if current experience matches with the gien experience. Otherwise false.
     */
    compareWithCurrentExperience(experience: IDefaultExperienceParams): Promise<boolean>;
    /** @internal */
    _convertExperienceToPreviewData(experience: IExperience, resourcePath: string): IPreviewData;
    /**
     * If an experience is set in the shared data service, this method will load the preview for this experience (such as Catalog, language, date and time).
     * Otherwise, the user will be redirected to the landing page to select an experience.
     * To load a preview, we need to get a preview ticket from an API.
     * Here we set current location to null initially so that the iframe manager loads the provided url and set the location.
     *
     * @returns a promise returning the experience
     */
    initializeExperience(): Promise<IExperience>;
    /**
     * Given an object containing a siteId, catalogId, catalogVersion and catalogVersions (array of product catalog version uuid's), will return a reconstructed experience
     *
     */
    buildAndSetExperience(params: IExperienceParams): Promise<IExperience>;
}

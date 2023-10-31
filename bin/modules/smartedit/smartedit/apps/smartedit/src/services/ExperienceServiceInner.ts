/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    GatewayProxied,
    IExperience,
    IExperienceService,
    IPreviewData,
    IPreviewService,
    LogService,
    SmarteditRoutingService
} from 'smarteditcommons';

/** @internal */
@GatewayProxied(
    'loadExperience',
    'updateExperiencePageContext',
    'getCurrentExperience',
    'setCurrentExperience',
    'hasCatalogVersionChanged',
    'buildRefreshedPreviewUrl',
    'compareWithCurrentExperience'
)
export class ExperienceService extends IExperienceService {
    constructor(
        private routingService: SmarteditRoutingService,
        private logService: LogService,
        private previewService: IPreviewService
    ) {
        super();
    }

    buildRefreshedPreviewUrl(): Promise<string> {
        return this.getCurrentExperience().then(
            (experience: IExperience) => {
                if (!experience) {
                    throw new Error(
                        'ExperienceService.buildRefreshedPreviewUrl() - Invalid experience from ExperienceService.getCurrentExperience()'
                    );
                }

                const promise = this.previewService.getResourcePathFromPreviewUrl(
                    experience.siteDescriptor.previewUrl
                );

                return promise.then(
                    (resourcePath: string) => {
                        const previewData: IPreviewData = this._convertExperienceToPreviewData(
                            experience,
                            resourcePath
                        );

                        return this.previewService.updateUrlWithNewPreviewTicketId(
                            this.routingService.absUrl(),
                            previewData
                        );
                    },
                    (err: any) => {
                        this.logService.error(
                            'ExperienceService.buildRefreshedPreviewUrl() - failed to retrieve resource path'
                        );
                        return Promise.reject(err);
                    }
                );
            },
            (err: any) => {
                this.logService.error(
                    'ExperienceService.buildRefreshedPreviewUrl() - failed to retrieve current experience'
                );
                return Promise.reject(err);
            }
        );
    }
}

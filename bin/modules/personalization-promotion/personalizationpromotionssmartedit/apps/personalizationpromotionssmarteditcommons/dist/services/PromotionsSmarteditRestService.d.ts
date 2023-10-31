import { PersonalizationsmarteditUtils } from 'personalizationcommons';
import { IRestServiceFactory } from 'smarteditcommons';
import { PromotionsPage, PromotionsCatalog } from '../types';
export declare class PromotionsSmarteditRestService {
    protected restServiceFactory: IRestServiceFactory;
    protected smarteditUtils: PersonalizationsmarteditUtils;
    static readonly AVAILABLE_PROMOTIONS: string;
    constructor(restServiceFactory: IRestServiceFactory, smarteditUtils: PersonalizationsmarteditUtils);
    getPromotions(catalogVersions: PromotionsCatalog[]): Promise<PromotionsPage>;
}

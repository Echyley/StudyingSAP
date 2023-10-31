import { PersonalizationsmarteditUtils, Customization } from 'personalizationcommons';
import { PersonalizationsmarteditRestService } from '../service';
export declare class CustomizationDataFactory {
    protected personalizationsmarteditRestService: PersonalizationsmarteditRestService;
    protected personalizationsmarteditUtils: PersonalizationsmarteditUtils;
    defaultSuccessCallbackFunction: (repsonse: any) => void;
    defaultErrorCallbackFunction: (response: any) => void;
    items: Customization[];
    private defaultFilter;
    private defaultDataArrayName;
    constructor(personalizationsmarteditRestService: PersonalizationsmarteditRestService, personalizationsmarteditUtils: PersonalizationsmarteditUtils);
    getCustomizations(filter: any): Promise<void>;
    updateData(params: any, successCallbackFunction: (response: any) => void, errorCallbackFunction: (response: any) => void): any;
    refreshData(): void;
    resetData(): void;
}

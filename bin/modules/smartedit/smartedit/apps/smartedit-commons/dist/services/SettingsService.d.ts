import { RestServiceFactory, TypedMap } from '@smart/utils';
declare type multiType = TypedMap<string | boolean | string[]>;
export declare class SettingsService {
    private readonly restService;
    constructor(restServicefactory: RestServiceFactory);
    load(): Promise<multiType>;
    get(key: string): Promise<string>;
    getBoolean(key: string): Promise<boolean>;
    getStringList(key: string): Promise<string[]>;
}
export {};

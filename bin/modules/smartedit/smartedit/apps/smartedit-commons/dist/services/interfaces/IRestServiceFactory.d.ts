import { IRestService } from '@smart/utils';
export declare abstract class IRestServiceFactory {
    get<T>(uri: string, identifier?: string): IRestService<T>;
    setDomain?(domain: string): void;
    setBasePath?(basePath: string): void;
    setGlobalBasePath?(globalDomain: string): void;
}

import { RestServiceFactory } from '@smart/utils';
import { GenericEditorField } from '../types';
import { IFetchDataHandler } from './IFetchDataHandler';
export declare class FetchEnumDataHandler implements IFetchDataHandler {
    private restServiceFactory;
    private static cache;
    private restServiceForEnum;
    static resetForTests(): void;
    constructor(restServiceFactory: RestServiceFactory);
    findByMask(field: GenericEditorField, search?: string): Promise<string[]>;
    getById(field: GenericEditorField, identifier: string): Promise<string>;
}

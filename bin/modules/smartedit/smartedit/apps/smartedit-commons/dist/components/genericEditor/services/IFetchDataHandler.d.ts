import { GenericEditorField } from '../types';
/**
 * Interface describing the contract of a fetchDataHandler fetched through dependency injection by the
 * {@link GenericEditorFactoryService} to populate dropdowns
 */
export interface IFetchDataHandler {
    /**
     * will returns a promise resolving to an entity, of type defined by field, matching the given identifier
     *
     * @returns an entity
     */
    getById(field: GenericEditorField, identifier: string): Promise<string>;
    /**
     * Will returns a promise resolving to list of entities, of type defined by field, eligible for a given search mask
     *
     * @returns a list of eligible entities
     */
    findByMask(field: GenericEditorField, mask: string): Promise<string[]>;
}

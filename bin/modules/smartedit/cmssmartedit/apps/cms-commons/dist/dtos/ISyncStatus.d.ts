import { TypedMap } from 'smarteditcommons';
/**
 * @description
 * Interface for synchronization information
 */
export interface ISyncStatus {
    catalogVersionUuid: string;
    catalogVersionName: TypedMap<string>;
    dependentItemTypesOutOfSync: TypedMap<string>[];
    itemId: string;
    itemType: string;
    name: string;
    status: string;
    lastSyncStatus: number;
    lastModifiedDate: number;
    selectedDependencies: ISyncStatus[];
    sharedDependencies: ISyncStatus[];
    unavailableDependencies: ISyncStatus[];
    fromSharedDependency?: boolean;
}

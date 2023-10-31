/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export interface SynchronizationPageConditions {
    canSyncHomepage: boolean;
    pageHasUnavailableDependencies: boolean;
    pageHasSyncStatus: boolean;
    pageHasNoDepOrNoSyncStatus: boolean;
}

export * from './SynchronizationModule';
export * from './components';

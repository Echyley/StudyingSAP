export declare enum JOB_STATUS {
    RUNNING = "RUNNING",
    ERROR = "ERROR",
    FAILURE = "FAILURE",
    FINISHED = "FINISHED",
    UNKNOWN = "UNKNOWN",
    ABORTED = "ABORTED",
    PENDING = "PENDING"
}
export interface ISyncJob {
    catalogId: string;
    name: string;
    sourceCatalogVersion: string;
    targetCatalogVersion: string;
    creationDate: string;
    startDate: string;
    endDate: string;
    syncStatus: JOB_STATUS;
    syncResult: string;
    code: string;
    lastModifiedDate: string;
}

export declare class PaginationHelper {
    private count;
    private page;
    private totalCount;
    private totalPages;
    constructor(initialData?: any);
    reset(): void;
    getCount(): number;
    getPage(): number;
    getTotalCount(): number;
    getTotalPages(): number;
    isLastPage(): boolean;
}

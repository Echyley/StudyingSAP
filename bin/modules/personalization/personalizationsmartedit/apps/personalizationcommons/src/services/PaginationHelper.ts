/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
export class PaginationHelper {
    private count: number;
    private page: number;
    private totalCount: number;
    private totalPages: number;

    constructor(initialData: any = {}) {
        this.count = initialData.count || 0;
        this.page = initialData.page || 0;
        this.totalCount = initialData.totalCount || 0;
        this.totalPages = initialData.totalPages || 0;
    }

    public reset(): void {
        this.count = 50;
        this.page = -1;
        this.totalPages = 1;
        this.totalCount = 0;
    }

    public getCount(): number {
        return this.count;
    }

    public getPage(): number {
        return this.page;
    }

    public getTotalCount(): number {
        return this.totalCount;
    }

    public getTotalPages(): number {
        return this.totalPages;
    }

    public isLastPage(): boolean {
        return this.getPage() === this.getTotalPages();
    }
}

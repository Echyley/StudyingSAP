import { InfiniteScrollingComponent, TechnicalUniqueIdAware } from './InfiniteScrollingComponent';
export declare class TestInfiniteScrollingFakeComponent {
    infiniteScrollingComponentRef: InfiniteScrollingComponent<TechnicalUniqueIdAware>;
    pageSize: number;
    mask: string;
    context: {
        items: any[];
    };
    fetchPage: (mask: string, pageSize: number, currentPage: number) => Promise<any>;
    constructor();
    setItems(newItems: any): void;
}

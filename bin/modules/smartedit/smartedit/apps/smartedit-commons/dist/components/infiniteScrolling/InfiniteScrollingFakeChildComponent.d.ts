import { Page } from '@smart/utils';
import { InfiniteScrollingComponent, TechnicalUniqueIdAware } from './InfiniteScrollingComponent';
export declare const items: {
    items: {
        id: number;
        name: string;
    }[];
};
export declare function fetchPage(mask: string, pageSize: number, currentPage: number, selectedItems?: any): Promise<Page<any>>;
export declare class InfiniteScrollingFakeChildComponent {
    infiniteScrollingComponentRef: InfiniteScrollingComponent<TechnicalUniqueIdAware>;
    pageSize: number;
    showItemsAboveIndex: number;
    mask: string;
    context: {
        items: any[];
    };
    fetchPage: (mask: string, pageSize: number, currentPage: number) => Promise<any>;
    constructor();
}

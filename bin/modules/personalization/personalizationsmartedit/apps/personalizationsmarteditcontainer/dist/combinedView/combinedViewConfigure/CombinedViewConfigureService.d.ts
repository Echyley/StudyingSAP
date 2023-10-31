import { OnDestroy } from '@angular/core';
import { CombinedViewSelectItem } from 'personalizationcommons';
import { Observable } from 'rxjs';
export declare class CombinedViewConfigureService implements OnDestroy {
    private catalogFilterSubject;
    private selectedItemsSubject;
    private selectedItemsSubscription;
    private selectedItems;
    constructor();
    ngOnDestroy(): void;
    setCatalogFilter(catalogFilter: string): void;
    getCatalogFilter$(): Observable<string>;
    setSelectedItems(items: CombinedViewSelectItem[]): void;
    isItemSelected: (item: CombinedViewSelectItem) => boolean;
}

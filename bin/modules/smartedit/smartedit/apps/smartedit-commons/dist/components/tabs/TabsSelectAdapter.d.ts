import { ISelectAdapter, ISelectItem } from '@smart/utils';
import { Tab } from './types';
export declare class TabsSelectAdapter implements ISelectAdapter {
    static transform(item: Tab, id: number): ISelectItem<Tab>;
}

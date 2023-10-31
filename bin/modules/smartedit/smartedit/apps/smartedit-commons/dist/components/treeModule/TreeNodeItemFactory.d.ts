import { ITreeNodeItem, TreeNodeItemDTO } from './types';
/**
 * A service used to generate instance of node item to be consumed by {@link TreeComponent}.
 */
export declare class TreeNodeItemFactory {
    /**
     * @param dto DTO based on which the class is returned.
     *
     * Returns a class depending on itemtype.
     */
    get<T, D extends TreeNodeItemDTO>(dto: D): ITreeNodeItem<T>;
}

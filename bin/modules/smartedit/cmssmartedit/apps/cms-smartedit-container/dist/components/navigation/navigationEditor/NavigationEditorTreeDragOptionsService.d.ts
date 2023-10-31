import { NavigationNodeItem, TreeDragAndDropEvent, TreeDragAndDropOptions } from 'smarteditcommons';
declare type dragAndDropFunction = (event: TreeDragAndDropEvent<NavigationNodeItem>) => void;
export declare class NavigationEditorTreeDragOptions {
    private dragAndDropHandler;
    /**
     * Sets the dragAndDrop callback handler
     *
     * @param dragAndDropFunc Function to be called when `onDropCallback` is called
     */
    setup(dragAndDropFunc: dragAndDropFunction): void;
    /**
     * Exposes methods of this service to a literal object.
     *
     * This literal object is used by `TreeComponent#setNodeActions` (smarteditcommons)
     * It sets new context and "inject" param for all methods using `.bind`
     *
     * It is done this way, so TreeComponent can easily iterate over the methods, if we would passed instance of this class it wouldn't be easy to iterate over these methods
     */
    getDragOptions(): TreeDragAndDropOptions<NavigationNodeItem>;
    private onDropCallback;
    private allowDropCallback;
    private beforeDropCallback;
}
export {};

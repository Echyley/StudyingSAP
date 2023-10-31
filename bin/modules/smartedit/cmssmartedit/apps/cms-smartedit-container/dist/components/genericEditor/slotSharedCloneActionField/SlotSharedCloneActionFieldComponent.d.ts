import { GenericEditorWidgetData, ICMSPage } from 'smarteditcommons';
declare enum CloneAction {
    'clone' = "clone",
    'useExisting' = "reference",
    'remove' = "remove"
}
export declare class SlotSharedCloneActionFieldComponent {
    data: GenericEditorWidgetData<any>;
    page: ICMSPage;
    cloneAction: typeof CloneAction;
    constructor(data: GenericEditorWidgetData<any>);
}
export {};

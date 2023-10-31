import { LogService } from '@smart/utils';
import { SystemEventService } from '../../../services/SystemEventService';
import { GenericEditorInfo } from '../types';
export declare class GenericEditorStackService {
    private systemEventService;
    private logService;
    static EDITOR_PUSH_TO_STACK_EVENT: string;
    static EDITOR_POP_FROM_STACK_EVENT: string;
    private _editorsStacks;
    constructor(systemEventService: SystemEventService, logService: LogService);
    isAnyGenericEditorOpened(): boolean;
    areMultipleGenericEditorsOpened(): boolean;
    getEditorsStack(editorStackId: string): GenericEditorInfo[];
    isTopEditorInStack(editorStackId: string, editorId: string): boolean;
    private pushEditorEventHandler;
    private popEditorEventHandler;
    private validateId;
}

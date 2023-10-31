import { IConfirmationModalService, IUriContext, LogService, CmsitemsRestService, IUserTrackingService } from 'smarteditcommons';
import { NavigationNodeEditorModalService } from '../navigationNodeEditor/NavigationNodeEditorModalService';
import { NavigationEditorNodeService } from './NavigationEditorNodeService';
import { NavigationTreeActions } from './types';
export declare class NavigationEditorTreeActions {
    private logService;
    private cmsitemsRestService;
    private confirmationModalService;
    private navigationEditorNodeService;
    private navigationNodeEditorModalService;
    private userTrackingService;
    private static readonly READY_ONLY_ERROR_I18N;
    private readOnly;
    private rootNodeUid;
    private uriContext;
    constructor(logService: LogService, cmsitemsRestService: CmsitemsRestService, confirmationModalService: IConfirmationModalService, navigationEditorNodeService: NavigationEditorNodeService, navigationNodeEditorModalService: NavigationNodeEditorModalService, userTrackingService: IUserTrackingService);
    /**
     * Sets fields required for this service to work properly
     */
    setup(readOnly: boolean, rootNodeUid: string, uriContext: IUriContext): void;
    /**
     * Exposes methods of this service to a literal object.
     *
     * This literal object is used by `TreeComponent#setNodeActions` (smarteditcommons)
     * It sets new context and "inject" treeService param for all methods using `.bind`
     *
     * It is done this way, so TreeComponent can easily iterate over the methods, if we would passed instance of this class it wouldn't be easy to iterate over these methods
     */
    getActions(): NavigationTreeActions;
    private getNavigationNodeCMSItemByUid;
    private hasNotMoved;
    private getEntriesCommaSeparated;
    /** Actions */
    private isReadOnly;
    private hasChildren;
    private fetchData;
    private removeItem;
    private performMove;
    private dragAndDrop;
    private moveUp;
    private moveDown;
    private isMoveUpAllowed;
    private isMoveDownAllowed;
    private refreshNode;
    private refreshParentNode;
    private editNavigationNode;
    private addTopLevelNode;
    private getEntryString;
    private getEntryTooltipString;
    private addNewChild;
    private addNewSibling;
    private getDropdownItems;
    private _findNodeById;
    private _expandIfNeeded;
}

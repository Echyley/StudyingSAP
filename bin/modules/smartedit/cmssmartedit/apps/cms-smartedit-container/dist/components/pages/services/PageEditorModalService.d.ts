import { CrossFrameEventService, LogService, SystemEventService, ICMSPage } from 'smarteditcommons';
import { GenericEditorModalService } from '../../../services';
import { PageEditorModalConfigService } from './PageEditorModalConfigService';
/**
 * The page editor modal service module provides a service that allows opening an editor modal for a given page. The editor modal is populated with a save and cancel button, and is loaded with the
 * editorTabset of cmssmarteditContainer as its content, providing a way to edit
 * various fields of the given page.
 *
 * Convenience service to open an editor modal window for a given page's data.
 */
export declare class PageEditorModalService {
    private genericEditorModalService;
    private crossFrameEventService;
    private systemEventService;
    private pageEditorModalConfigService;
    private logService;
    constructor(genericEditorModalService: GenericEditorModalService, crossFrameEventService: CrossFrameEventService, systemEventService: SystemEventService, pageEditorModalConfigService: PageEditorModalConfigService, logService: LogService);
    /**
     * Opens Editor Modal.
     *
     * The editor modal is initialized with a title in the format '<TypeName> Editor', ie: 'Paragraph Editor'.
     * The editor modal is also wired with a save and cancel button.
     *
     * @returns A promise that resolves to the data returned by the modal when it is closed.
     */
    open(page: ICMSPage): Promise<any>;
}

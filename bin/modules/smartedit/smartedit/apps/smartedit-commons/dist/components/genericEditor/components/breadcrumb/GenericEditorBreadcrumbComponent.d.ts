import { GenericEditorComponent } from '../../GenericEditorComponent';
import { GenericEditorStackService } from '../../services';
import { GenericEditorInfo } from '../../types';
import './genericEditorBreadcrumb.scss';
/**
 * Component responsible for rendering a breadcrumb on top of the generic editor
 * when there is more than one editor opened on top of each other.
 * This will happen when editing nested components.
 *
 * @param editorStackId The string that identifies the stack of editors being edited together.
 */
export declare class GenericEditorBreadcrumbComponent {
    private genericEditorStackService;
    private ge;
    private editorsStack;
    constructor(genericEditorStackService: GenericEditorStackService, ge: GenericEditorComponent);
    getEditorsStack(): GenericEditorInfo[];
    showBreadcrumb(): boolean;
    getComponentName(breadcrumbItem: GenericEditorInfo): any;
}

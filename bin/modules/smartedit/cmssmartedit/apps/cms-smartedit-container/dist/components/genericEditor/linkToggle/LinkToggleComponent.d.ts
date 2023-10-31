import { GenericEditorWidgetData, GenericEditorField } from 'smarteditcommons';
export interface LinkToggleDTO {
    linkToggle?: {
        urlLink?: string;
        external?: boolean;
    };
}
export declare class LinkToggleComponent {
    field: GenericEditorField;
    model: LinkToggleDTO;
    constructor(data: GenericEditorWidgetData<LinkToggleDTO>);
    clearUrlLink(): void;
    checkUrlLink(): void;
}

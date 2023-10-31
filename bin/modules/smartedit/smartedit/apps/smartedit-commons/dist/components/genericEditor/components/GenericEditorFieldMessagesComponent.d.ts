import { GenericEditorField } from '../types';
export declare class GenericEditorFieldMessagesComponent {
    field: GenericEditorField;
    qualifier: string;
    errors: string[];
    warnings: string[];
    private previousMessages;
    ngDoCheck(): void;
    getFilteredMessagesByType(messageType: string): string[];
}

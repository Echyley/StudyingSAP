import { IContextualMenuConfiguration } from 'smarteditcommons';
export declare abstract class IEditorEnablerService {
    enableForComponents(componentTypes: string[]): void;
    onClickEditButton({ slotUuid, componentAttributes }: IContextualMenuConfiguration): Promise<void>;
    isSlotEditableForNonExternalComponent(config: IContextualMenuConfiguration): Promise<boolean>;
}

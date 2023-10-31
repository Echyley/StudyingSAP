/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IContextualMenuConfiguration } from 'smarteditcommons';

export abstract class IEditorEnablerService {
    public enableForComponents(componentTypes: string[]): void {
        'proxyFunction';
    }

    public onClickEditButton({
        slotUuid,
        componentAttributes
    }: IContextualMenuConfiguration): Promise<void> {
        'proxyFunction';
        return null;
    }

    public isSlotEditableForNonExternalComponent(
        config: IContextualMenuConfiguration
    ): Promise<boolean> {
        'proxyFunction';
        return null;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Payload } from 'smarteditcommons';
export interface CmsDragAndDropDragInfo extends Payload {
    /**
     * The smartedit id of the component.
     */
    componentId: string;
    componentUuid: string;
    componentType: string;
    slotUuid: string;
    /**
     * The smartedit id of the slot from which the component originates.
     */
    slotId: string;
    slotOperationRelatedId?: string;
    slotOperationRelatedType?: string;
    /**
     * The boolean that determines if the component should be cloned or not.
     */
    cloneOnDrop?: boolean;
}

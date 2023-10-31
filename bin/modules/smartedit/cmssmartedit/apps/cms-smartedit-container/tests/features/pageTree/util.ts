/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ICMSComponent } from 'cmscommons';
import { SlotNode, ComponentNode } from 'cmssmarteditcontainer/services';
import { PageTreeNode } from 'smarteditcommons';

export const createPageTreeNode = (type: string, index: string): PageTreeNode => ({
    componentId: `${type}-id-${index}`,
    componentUuid: `${type}-uuid-${index}`,
    componentTypeFromPage: `${type}-type-${index}`,
    elementUuid: `${type}-element-uuid-${index}`,
    containerId: `${type}-container-id-${index}`,
    containerType: `${type}-container-type-${index}`,
    catalogVersionUuid: 'catalog-version-uuid',
    isExpanded: false,
    childrenNode: []
});

export const createCMSComponent = (index: string, slots: string[]): ICMSComponent => ({
    name: `component-name-${index}`,
    typeCode: `component-type-code-${index}`,
    itemtype: 'ComponentType',
    uid: `component-id-${index}`,
    uuid: `component-uuid-${index}`,
    catalogVersion: 'catalog-version-uuid',
    visible: true,
    restricted: false,
    cloneable: true,
    slots
});

export const createSlotNode = (index: string): SlotNode => ({
    componentId: `component-id-${index}`,
    componentUuid: `component-uuid-${index}`,
    componentTypeFromPage: `component-type-${index}`,
    elementUuid: `component-element-uuid-${index}`,
    containerId: `component-container-id-${index}`,
    containerType: `component-container-type-${index}`,
    catalogVersionUuid: 'catalog-version-uuid',
    isExpanded: false,
    childrenNode: [],
    componentNodes: [],
    hiddenComponentCount: 0
});

export const createComponentNode = (index: string): ComponentNode => ({
    componentId: `component-id-${index}`,
    componentUuid: `component-uuid-${index}`,
    componentTypeFromPage: `component-type-${index}`,
    elementUuid: `component-element-uuid-${index}`,
    containerId: `component-container-id-${index}`,
    containerType: `component-container-type-${index}`,
    catalogVersionUuid: 'catalog-version-uuid',
    isExpanded: false,
    childrenNode: [],
    name: `component-name-${index}`,
    typeCode: `component-type-code-${index}`,
    itemtype: `component-type-${index}`,
    uid: `component-id-${index}`,
    uuid: `component-uuid-${index}`,
    catalogVersion: 'catalog-version-uuid',
    visible: true,
    restricted: false,
    cloneable: true,
    slots: ['slot-uuit-1'],
    isHidden: false
});

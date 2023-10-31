/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { InjectionToken, Type } from '@angular/core';
import { SeData } from 'personalizationcommons';
import { Pagination, SelectItem, TypedMap } from 'smarteditcommons';
import { PERSONALIZATION_MODEL_STATUS_CODES } from './utils';

export interface ItemWithStatus {
    status: PERSONALIZATION_MODEL_STATUS_CODES;
}
export interface BaseItem {
    type: string;
    catalog: string;
    catalogVersion: string;
    code: string;
}

export type CustomizationTrigger = BaseItem;

export interface CustomizationAction extends BaseItem {
    rank: number;
    componentCatalog: string;
    componentId: string;
    containerId: string;
}

export interface CustomizationVariation {
    active?: boolean;
    actions?: CustomizationAction[];
    catalog: string;
    catalogVersion: string;
    catalogVersionNameL10N?: string;
    tempcode?: string;
    code: string;
    commerceCustomizations?: TypedMap<number>;
    enabled?: boolean;
    name?: string;
    numberOfComponents?: number;
    numberOfCommerceActions?: number;
    rank?: number;
    status?: PERSONALIZATION_MODEL_STATUS_CODES;
    triggers?: Trigger[];
    isNew?: boolean;
}

export interface CustomizationVariationTreeItem extends CustomizationVariation {
    statusNotDeleted?: boolean;
    subMenu?: boolean;
    isCommerceCustomizationEnabled?: boolean;
}

export interface Customization {
    active?: boolean;
    catalog: string;
    catalogVersion: string;
    code: string;
    enabledStartDate?: string;
    enabledEndDate?: string;
    name: string;
    description?: string;
    rank: number;
    status?: PERSONALIZATION_MODEL_STATUS_CODES;
    statusBoolean?: boolean;
    variations?: CustomizationVariation[];
}

export interface CustomizationTreeItem {
    active?: boolean;
    catalog: string;
    catalogVersion: string;
    code: string;
    enabledStartDate?: string;
    enabledEndDate?: string;
    name: string;
    rank: number;
    status?: PERSONALIZATION_MODEL_STATUS_CODES;
    statusBoolean?: boolean;
    variations?: CustomizationVariationTreeItem[];
    enabled?: boolean;
    collapsed?: boolean;
    subMenu?: boolean;
}

export interface CustomizationItem {
    customization: Customization;
    variation: CustomizationVariation;
}

export interface CombinedViewSelectItem extends CustomizationItem, SelectItem {
    highlighted?: boolean;
}

export interface CustomizationVariationComponents {
    components: string[];
}

export interface CustomizationStatus {
    code: string;
    text: string;
    modelStatuses: PERSONALIZATION_MODEL_STATUS_CODES[];
}

// TargetGroup
export interface TargetGroupState {
    code: string;
    name: string;
    expression: Trigger[];
    /** Whether it applies to all users. */
    isDefault: boolean;
    showExpression: boolean;
    selectedVariation: CustomizationVariation;
}

// Target Group Triggers / Expressions
export enum TriggerActionId {
    AND = 'AND',
    OR = 'OR',
    NOT = 'NOT'
}

export enum TriggerType {
    DEFAULT_TRIGGER = 'defaultTriggerData',
    SEGMENT_TRIGGER = 'segmentTriggerData',
    EXPRESSION_TRIGGER = 'expressionTriggerData',
    GROUP_EXPRESSION = 'groupExpressionData',
    SEGMENT_EXPRESSION = 'segmentExpressionData',
    NEGATION_EXPRESSION = 'negationExpressionData',
    CONTAINER_TYPE = 'container',
    ITEM_TYPE = 'item',
    DROPZONE_TYPE = 'dropzone'
}

export interface SegmentExpression {
    catalog: string;
    catalogVersion: string;
    code: string;
    type: TriggerType;
    groupBy?: TriggerActionId;
    segments?: Segment[];
}

export interface Segment {
    code: string;
    description?: string;
    // id is option as it's created for SelectComponent (so it can handle data properly)
    id?: string;
}

export interface TriggerAction {
    id: TriggerActionId;
    name: string;
}

export interface Trigger {
    type: TriggerType;
    uid?: string;
    nodes?: Trigger[];
    operation?: TriggerAction;
    code?: string;
    selectedSegment?: Segment;
    expression?: ExpressionData;
}

export interface ExpressionTrigger extends Trigger {
    expression: ExpressionData;
}

export interface SegmentTrigger extends Trigger {
    groupBy: TriggerActionId;
    segments: Segment[];
}

export interface ContainerTrigger extends Trigger {
    operation: TriggerAction;
    nodes: Trigger[];
}

export interface ExpressionData {
    type: TriggerType;
    operator: any;
    elements: any[];
    element: any;
    code: any;
}

export interface CustomizationsFilter {
    currentSize: number;
    currentPage: number;
    name?: string;
    active?: string;
    statuses?: PERSONALIZATION_MODEL_STATUS_CODES[];
    catalogs?: string;
    pageId?: string;
    pageCatalogId?: string;
}

export interface CustomizationsList {
    customizations: Customization[];
    pagination: Pagination;
}

// injector for personlization-search
export interface PersonlizationSearchExtensionComponent {
    component: Type<any>;
}

export interface PersonlizationSearchExtensionInjector {
    seData: SeData;
    actions: any[];
    getCustomization: () => Customization;
    getVariation: () => CustomizationVariation;
    getSelectedTypeCode: () => string;
    addAction: (action: any, comparer: (p1, p2) => boolean) => void;
    isActionInSelectDisabled: (action: any, comparer: (p1, p2) => boolean) => any;
}

export const PERSONLIZATION_SEARCH_EXTENSION_TOKEN =
    new InjectionToken<PersonlizationSearchExtensionComponent>(
        'PERSONLIZATION_SEARCH_EXTENSION_TOKEN'
    );

export const PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN =
    new InjectionToken<PersonlizationSearchExtensionInjector>(
        'PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN'
    );

// injector for personlization-promotion
export interface PersonlizationPromotionExtensionComponent {
    component: Type<any>;
}
export interface PersonlizationPromotionExtensionInjector {
    getSelectedTypeCode: () => string;
    addAction: (action: any, comparer: (p1, p2) => boolean) => void;
    isActionInSelectDisabled: (action: any, comparer: (p1, p2) => boolean) => any;
}

export const PERSONLIZATION_PROMOTION_EXTENSION_TOKEN =
    new InjectionToken<PersonlizationPromotionExtensionComponent>(
        'PERSONLIZATION_PROMOTION_EXTENSION_TOKEN'
    );

export const PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN =
    new InjectionToken<PersonlizationPromotionExtensionInjector>(
        'PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN'
    );

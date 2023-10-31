/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
/**
 * @ngdoc interface
 * @name SuSelect.interface:ISelectItem
 * @description
 * Object consumed by SuSelect
 */
export interface ISelectItem<T> {
    /**
     * @ngdoc property
     * @name id
     * @propertyOf SuSelect.interface:ISelectItem
     * @description
     * Unique identifier
     */
    id: number;
    /**
     * @ngdoc property
     * @name label
     * @propertyOf SuSelect.interface:ISelectItem
     * @description
     * Descriptive label to be displayed
     */
    label: string;
    /**
     * @ngdoc property
     * @name listItemClassName
     * @propertyOf SuSelect.interface:ISelectItem
     * @description
     * Classname of list item
     */
    listItemClassName?: string;
    /**
     * @ngdoc property
     * @name value
     * @propertyOf SuSelect.interface:ISelectItem
     * @description
     * Value carried by dropdown item
     */
    value: T;
}

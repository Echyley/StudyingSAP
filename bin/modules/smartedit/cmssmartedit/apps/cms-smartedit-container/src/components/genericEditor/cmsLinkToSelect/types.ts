/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IGenericEditorDropdownSelectedOptionEventData } from 'smarteditcommons';

export enum LinkToOption {
    content = 'content',
    product = 'product',
    category = 'category',
    external = 'external'
}

export interface SelectOption {
    id: LinkToOption;
    structureApiMode: string;
    hasCatalog?: boolean;
}

export type SelectedOption = IGenericEditorDropdownSelectedOptionEventData<SelectOption>;

export interface CmsLinkToSelectOption {
    currentSelectedOptionValue: LinkToOption;
    external: boolean;
    linkTo: LinkToOption;
    category?: string;
    product?: string;
    productCatalog?: string;
    contentPage?: string;
    url?: string;
    urlLink?: string;
    linkToggle?: {
        urlLink?: string;
    };
}

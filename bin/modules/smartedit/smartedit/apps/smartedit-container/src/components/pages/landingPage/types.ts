/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { IGenericEditorDropdownSelectedOption, TypedMap } from 'smarteditcommons';

/**
 * @internal
 * @ignore
 */
export interface ISelectedSite extends IGenericEditorDropdownSelectedOption {
    uid: string;
    contentCatalogs: string[];
    name: TypedMap<string>;
    previewUrl: string;
}

export const SITES_ID = 'sites-id';

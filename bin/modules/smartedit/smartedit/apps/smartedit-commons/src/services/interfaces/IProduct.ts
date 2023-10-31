/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SelectItem } from '../../components';

/**
 * Interface for product information
 */
export interface IProduct {
    uid: string;
    catalogId: string;
    catalogVersion: string;
    code: string;
    description: {
        [index: string]: string;
    };
    name: {
        [index: string]: string;
    };
    thumbnailMediaCode: string;
}

export type ProductSelectItem = IProduct & SelectItem;

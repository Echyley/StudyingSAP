/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

export interface Theme {
    code: string;
    name: string;
    sequence?: number;
    selected?: boolean;
    style?: {
        code?: string;
        mine?: string;
        url?: string;
    };
    thumbnail: {
        code?: string;
        mime?: string;
        url?: string;
    };
}

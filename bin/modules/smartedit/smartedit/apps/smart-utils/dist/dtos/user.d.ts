/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
export interface User {
    uid: string;
    displayName: string;
    readableLanguages: string[];
    writeableLanguages: string[];
}

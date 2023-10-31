/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
export interface User {
    // Note:
    // - We should be careful when adding fields to this DTO. It should not contain any
    // confidential or personal information.
    uid: string;
    displayName: string;
    readableLanguages: string[];
    writeableLanguages: string[];
}

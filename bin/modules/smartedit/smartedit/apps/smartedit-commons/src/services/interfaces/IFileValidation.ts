/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

import { ErrorContext } from '@smart/utils';

export abstract class IFileValidation {
    buildAcceptedFileTypesList(): string {
        'proxyFunction';
        return null;
    }

    public validate(
        file: File,
        maxUploadFileSize: number,
        errorsContext: ErrorContext[]
    ): Promise<ErrorContext[] | void> {
        'proxyFunction';
        return null;
    }
}

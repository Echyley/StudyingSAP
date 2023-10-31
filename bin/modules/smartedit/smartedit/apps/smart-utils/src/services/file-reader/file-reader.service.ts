/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';

@Injectable()
export class FileReaderService {
    public read(
        file: File | Blob,
        config?: {
            onError: (ev: ProgressEvent<FileReader>) => any;
            onLoadEnd: (ev: ProgressEvent<FileReader>) => any;
        }
    ): FileReader {
        const fileReader = new FileReader();

        if (config?.onError) {
            fileReader.onerror = config.onError;
        }
        if (config?.onLoadEnd) {
            fileReader.onloadend = config.onLoadEnd;
        }

        fileReader.readAsArrayBuffer(file);
        return fileReader;
    }
}

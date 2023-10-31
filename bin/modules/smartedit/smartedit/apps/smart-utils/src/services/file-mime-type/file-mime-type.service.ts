/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { ISettingsService } from '../../interfaces';
import { promiseUtils } from '../../utils';
import { FileReaderService } from '../file-reader';

@Injectable()
export class FileMimeTypeService {
    constructor(
        private fileReaderService: FileReaderService,
        private settingsService: ISettingsService
    ) {}

    public isFileMimeTypeValid(file: File | Blob): Promise<boolean> {
        return new Promise(async (resolve, reject) => {
            const { error: mimeTypesError, data: mimeTypes } = await promiseUtils.attempt(
                this.settingsService.getStringList('smartedit.validFileMimeTypeCodes')
            );
            if (mimeTypesError) {
                reject(false);
                return;
            }

            this.fileReaderService.read(file, {
                onLoadEnd: (ev) => {
                    if (
                        !this.validateMimeTypeFromFile(ev.target.result as ArrayBuffer, mimeTypes)
                    ) {
                        reject();
                        return;
                    }
                    resolve(true);
                },
                onError: () => {
                    reject();
                }
            });
        });
    }

    /**
     *  Mimetype is valid when starting bytes of the file are matching the Mimetype byte pattern.
     *  For example. 89 50 4E 47 is a png file, so if the first 4 bytes are 89504E47 it is recognized as a png file.
     *
     *  Read more on File Signatures and Image Type Pattern Matching
     *  - https://en.wikipedia.org/wiki/List_of_file_signatures
     *  - https://mimesniff.spec.whatwg.org/#image-type-pattern-matching-algorithm
     */
    private validateMimeTypeFromFile(loadedFile: ArrayBuffer, mimeTypes: string[]): boolean {
        const fileAsBytes = new Uint8Array(loadedFile).subarray(0, 32);
        const header = fileAsBytes.reduce((head, byte) => {
            let byteAsStr = byte.toString(16);
            if (byteAsStr.length === 1) {
                byteAsStr = '0' + byteAsStr;
            }
            head += byteAsStr;
            return head;
        }, '');

        return mimeTypes.some((mimeTypeCode) =>
            header.toLowerCase().includes(mimeTypeCode.toLowerCase())
        );
    }
}

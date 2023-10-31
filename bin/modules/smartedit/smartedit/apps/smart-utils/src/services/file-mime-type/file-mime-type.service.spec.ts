/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISettingsService } from '../../interfaces';
import { FileReaderService } from '../file-reader';
import { FileMimeTypeService } from './file-mime-type.service';

describe('FileMimeTypeService', () => {
    let fileReaderService: jasmine.SpyObj<FileReaderService>;
    let settingsService: jasmine.SpyObj<ISettingsService>;

    let service: FileMimeTypeService;
    beforeEach(() => {
        fileReaderService = jasmine.createSpyObj<FileReaderService>('seFileReader', ['read']);

        settingsService = jasmine.createSpyObj<ISettingsService>('settingsService', [
            'getStringList'
        ]);

        service = new FileMimeTypeService(fileReaderService, settingsService);
    });

    beforeEach(() => {
        settingsService.getStringList.and.returnValue(Promise.resolve(['89504E47']));
    });

    describe('isFileMimeTypeValid', () => {
        it('will return a resolved promise when the file mime type is valid', async () => {
            const mockFile = mockFileFromHex(['0x89', '0x50', '0x4e', '0x47']);
            fileReaderService.read.and.callFake((async (file: Blob, config) => {
                config.onLoadEnd({
                    target: {
                        result: await file.arrayBuffer()
                    }
                });
            }) as any);

            const isValid = await service.isFileMimeTypeValid(mockFile);
            expect(isValid).toBe(true); // is valid when the promise is resolved or true is returned;
        });

        it('will return a rejected promise when the file mime type is invalid', async () => {
            const mockFile = mockFileFromHex(['0x84', '0x83', '0x35', '0x53']);
            fileReaderService.read.and.callFake((async (file, config) => {
                config.onLoadEnd({
                    target: {
                        result: await file.arrayBuffer()
                    }
                });
            }) as any);

            try {
                await service.isFileMimeTypeValid(mockFile);
                expect(false).toBe(true);
            } catch {
                expect(true).toBe(true);
            }
        });

        it('will return a rejected promise when the file fails to load', async () => {
            const mockFile = mockFileFromHex(['0x89', '0x50', '0x4e', '0x47']);
            fileReaderService.read.and.callFake(((file, config) => {
                config.onError();
            }) as any);

            try {
                await service.isFileMimeTypeValid(mockFile);
                expect(false).toBe(true);
            } catch (err) {
                expect(true).toBe(true);
            }
        });
    });

    function mockFileFromHex(hexArray: string[]): File | Blob {
        const intArray = hexArray.map((hex) => parseInt(hex, 16));
        const byteArray = new Uint8Array(intArray);
        const blob = new Blob([byteArray]);
        return blob;
    }
});

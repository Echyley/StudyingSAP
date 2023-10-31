/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { FileReaderService } from './file-reader.service';

describe('FileReaderService', () => {
    let service: FileReaderService;
    beforeEach(() => {
        service = new FileReaderService();
    });

    it('GIVEN config is not specified THEN it returns FileReader without event handlers', () => {
        const file = new File([], 'testfile.txt');

        const fileReader = service.read(file);

        expect(fileReader.onloadend).toBeNull();
        expect(fileReader.onerror).toBeNull();
    });

    it('GIVEN config is specified THEN it sets the handlers', () => {
        const file = new File([], 'testfile.txt');
        const mockOnError = (_ev: ProgressEvent<FileReader>) => null;
        const mockOnLoadEnd = (_ev: ProgressEvent<FileReader>) => null;

        const fileReader = service.read(file, { onError: mockOnError, onLoadEnd: mockOnLoadEnd });

        expect(fileReader.onerror).toEqual(jasmine.any(Function));
        expect(fileReader.onloadend).toEqual(jasmine.any(Function));
    });
});

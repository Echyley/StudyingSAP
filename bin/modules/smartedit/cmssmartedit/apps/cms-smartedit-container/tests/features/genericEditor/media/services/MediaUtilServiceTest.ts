/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { MediaUtilService } from 'cmssmarteditcontainer/components/genericEditor/media/services';
import { FILE_VALIDATION_CONFIG, GenericEditorMediaType } from 'smarteditcommons';

describe('MediaUtilService', () => {
    let seMediaUtilService: MediaUtilService;

    beforeEach(() => {
        seMediaUtilService = new MediaUtilService();
    });

    it('should return Image type list', () => {
        const result = seMediaUtilService.getAcceptedFileTypes(GenericEditorMediaType.IMAGE);
        expect(result).toBe(FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.IMAGE);
    });

    it('should return Video type list', () => {
        const result = seMediaUtilService.getAcceptedFileTypes(GenericEditorMediaType.VIDEO);
        expect(result).toBe(FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.VIDEO);
    });

    it('should return Document type list', () => {
        const result = seMediaUtilService.getAcceptedFileTypes(GenericEditorMediaType.PDF_DOCUMENT);
        expect(result).toBe(FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.PDF_DOCUMENT);
    });

    it('default should return Document type list', () => {
        const result = seMediaUtilService.getAcceptedFileTypes(undefined);
        expect(result).toBe(FILE_VALIDATION_CONFIG.ACCEPTED_FILE_TYPES.DEFAULT);
    });
});

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { MediaFileSelectorComponent } from 'cmssmarteditcontainer/components/genericEditor/media/components';

describe('MediaFileSelector', () => {
    let component: MediaFileSelectorComponent;
    beforeEach(() => {
        const injector = jasmine.createSpyObj('$injector', ['get']);
        component = new MediaFileSelectorComponent(injector, null);
    });

    it('initializes default values properly', () => {
        component.ngOnInit();

        expect(component.disabled).toBe(false);
        expect(component.customClass).toBe('');
        expect(component.selectionMode).toBe('replace');
    });

    describe('buildAcceptedFileTypesList', () => {
        it('should build a comma separated list of file extensions with the period prefix', () => {
            component.acceptedFileTypes = ['a', 'b', 'c'];

            expect(component.buildAcceptedFileTypesList()).toEqual('.a,.b,.c');
        });
    });

    it('GIVEN selectionMode is "replace" THEN it returns true', () => {
        component.selectionMode = 'replace' as any;

        expect(component.isReplaceMode()).toBe(true);
    });

    it('WHEN file is selected THEN onSelect is called', () => {
        component.ngOnInit();
        const emitSpy = spyOn(component.onFileSelect, 'emit');
        const mockFileList = [new File([''], '')] as unknown as FileList;
        component.onSelect(mockFileList);

        expect(emitSpy).toHaveBeenCalledWith(mockFileList);
    });
});

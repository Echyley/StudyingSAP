/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { VersionsSearchComponent } from 'cmssmarteditcontainer/components/versioning';

describe('VersionsSearchComponent', () => {
    let component: VersionsSearchComponent;
    beforeEach(() => {
        component = new VersionsSearchComponent();
    });

    it('WHEN destroyed THEN it should unsubscribe from search term subscription', async () => {
        await component.ngOnInit();
        component.searchTerm = 'expected';

        await component.ngOnDestroy();

        component.onChange('after destroyed');
        expect(component.searchTerm).toBe('expected');
    });

    describe('onChange', () => {
        let searchTermChangeEmitSpy: jasmine.Spy;
        const clock = jasmine.clock();
        beforeEach(async () => {
            clock.install();

            searchTermChangeEmitSpy = spyOn(component.searchTermChange, 'emit');
            await component.ngOnInit();
        });

        afterEach(() => {
            clock.uninstall();
        });

        it(`WHEN user enters data into the search field THEN it should set search term
            AND show reset button
            AND notify parent component
          `, () => {
            const expected = 'version1';

            component.onChange(expected);
            clock.tick(501);

            expect(component.searchTerm).toBe(expected);
            expect(component.showResetButton).toBe(true);
            expect(searchTermChangeEmitSpy).toHaveBeenCalledWith(expected);

            // should update only recent value
            component.onChange('version2');
            clock.tick(100);
            expect(component.searchTerm).toBe(expected);
        });

        it(`WHEN reset button is clicked THEN it should empty search term
            AND hide reset button
            AND notify parent component
        `, () => {
            component.searchTerm = 'version1';

            component.resetSearchBox();

            expect(component.searchTerm).toBe('');
            expect(component.showResetButton).toBe(false);
            expect(searchTermChangeEmitSpy).toHaveBeenCalledWith('');
        });
    });
});

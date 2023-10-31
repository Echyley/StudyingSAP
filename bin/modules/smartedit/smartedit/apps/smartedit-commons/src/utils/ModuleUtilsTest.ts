/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Component, NgModule } from '@angular/core';
import { ModuleUtils } from './ModuleUtils';

@Component({
    selector: 'main-component',
    template: `<p>Main Component</p>`
})
class MockMainComponent {}

@NgModule({
    declarations: [MockMainComponent],
    entryComponents: [MockMainComponent],
    exports: [MockMainComponent]
})
class MockMainModule {}

describe('ModuleUtils test', () => {
    let moduleUtils: ModuleUtils;

    beforeEach(() => {
        moduleUtils = new ModuleUtils();
    });

    it('getNgModule get module will return null if not exist', () => {
        expect(moduleUtils.getNgModule('mockTestModule')).toBeNull();
    });

    it('getNgModule get module will return Module if exsit', () => {
        window.__smartedit__.modules.mockMainModule = MockMainModule as any;

        expect(moduleUtils.getNgModule('mockMainModule')).toBeDefined();
    });
});

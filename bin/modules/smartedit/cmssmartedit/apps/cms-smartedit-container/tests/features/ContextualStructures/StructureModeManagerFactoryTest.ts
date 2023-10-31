/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import {
    ModeManager,
    StructureModeManagerFactory
} from 'cmssmarteditcontainer/dao/StructureModeManagerFactory';

describe('StructureModeManagerFactory', () => {
    const factory = new StructureModeManagerFactory();
    let modeManager: ModeManager;
    beforeEach(() => {
        modeManager = factory.createModeManager(['add', 'edit', 'create']);
    });

    describe('validateMode', () => {
        it('GIVEN supported mode THEN it will return true', function () {
            expect(modeManager.validateMode('add')).toBe(true);
        });

        it('GIVEN not supported mode THEN it will throw an exception', function () {
            const actual = () => modeManager.validateMode('dummy');

            expect(actual).toThrow();
        });
    });
});

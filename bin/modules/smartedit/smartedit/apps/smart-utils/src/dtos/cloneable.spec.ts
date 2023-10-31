/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */

import { CloneableUtils } from '../utils';
import { Cloneable } from './cloneable';

describe('cloneableUtils', () => {
    let cloneableUtils: CloneableUtils;

    const arrayElement = {
        a: 5,
        b: 'somestring',
        c: true,
        d() {
            return true;
        },
        e: $('someJQueryElement')
    };

    const strippedArrayElement = {
        a: 5,
        b: 'somestring',
        c: true,
        d: null,
        e: null
    } as any as Cloneable;

    const objectElement = {
        a: 5,
        b: 'somestring',
        c: true,
        d() {
            return true;
        },
        e: $('someJQueryElement'),
        f: [arrayElement],
        g: Promise.resolve('somePromise'),
        h: document.createElement('someElement')
    };

    const strippedObjectElement = {
        a: 5,
        b: 'somestring',
        c: true,
        d: null,
        e: null,
        f: [strippedArrayElement],
        g: null,
        h: null
    } as any as Cloneable;

    const source = {
        a: 5,
        b: 'somestring',
        c: true,
        d() {
            return true;
        },
        e: $('someJQueryElement'),
        f: [arrayElement],
        g: Promise.resolve('somePromise'),
        h: document.createElement('someElement'),
        i: objectElement
    };

    const strippedSource = {
        a: 5,
        b: 'somestring',
        c: true,
        d: null,
        e: null,
        f: [strippedArrayElement],
        g: null,
        h: null,
        i: strippedObjectElement
    } as any as Cloneable;

    beforeEach(() => {
        cloneableUtils = new CloneableUtils();
    });

    it('makeCloneable will strip a copy of the object from anything that is not allowed to cross the gateway', () => {
        expect(cloneableUtils.makeCloneable(source)).toEqual(strippedSource);
    });
});

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { UrlUtils } from './url-utils';

describe('UrlUtils', () => {
    const urlUtils = new UrlUtils();

    const newQueryKey = 'newQueryKey';
    const newQueryValue = 'newQueryValue';
    const expectNewQueryUrl = 'https://domain?newQueryKey=newQueryValue';

    it('Will add the missing query param to url when url has no existing query params', () => {
        const sourceUrl = 'https://domain';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectNewQueryUrl
        );
    });

    it('Will add the missing query param to a string when url has other query params', () => {
        const sourceUrl = 'https://domain?otherQueryKey=otherQueryValue';
        const expectUrl = 'https://domain?otherQueryKey=otherQueryValue&newQueryKey=newQueryValue';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectUrl
        );
    });

    it('Will update value of an existing query param with no value', () => {
        const sourceUrl = 'https://domain?newQueryKey=';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectNewQueryUrl
        );
    });

    it('Will update value of an existing query param with a value', () => {
        const sourceUrl = 'https://domain?newQueryKey=oldQueryValue';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectNewQueryUrl
        );
    });

    it('Will update value of an existing query param with a value in middle of other query params', () => {
        const sourceUrl = 'https://domain?newQueryKey=oldQueryValue&otherQueryKey=otherQueryValue';
        const expectUrl = 'https://domain?newQueryKey=newQueryValue&otherQueryKey=otherQueryValue';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectUrl
        );
    });

    it('Will update value of an existing query param with a value in middle of other query params', () => {
        const sourceUrl = 'https://domain?newQueryKey=oldQueryValue&otherQueryKey=otherQueryValue';
        const expectUrl = 'https://domain?newQueryKey=newQueryValue&otherQueryKey=otherQueryValue';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectUrl
        );
    });

    it('Will keep any anchors at the end of the url', () => {
        const sourceUrl = 'https://domain#blablalba';
        const expectUrl = 'https://domain?newQueryKey=newQueryValue#blablalba';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectUrl
        );
    });

    it('Will keep any anchors at the end of the url with existing query params before the anchor', () => {
        const sourceUrl = 'https://domain?otherQueryKey=otherQueryValue#blablalba';
        const expectUrl =
            'https://domain?otherQueryKey=otherQueryValue&newQueryKey=newQueryValue#blablalba';

        expect(urlUtils.updateUrlParameter(sourceUrl, newQueryKey, newQueryValue)).toEqual(
            expectUrl
        );
    });
});

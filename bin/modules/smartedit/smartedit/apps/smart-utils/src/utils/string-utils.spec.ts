/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import { StringUtils } from './string-utils';

describe('StringUtilsTests', () => {
    const stringUtils: StringUtils = new StringUtils();

    it('isBlank will return true if a variable is undefined or null or empty', function () {
        expect(stringUtils.isBlank('')).toBe(true);
        expect(stringUtils.isBlank(null)).toBe(true);
        expect(stringUtils.isBlank('null')).toBe(true);
        expect(stringUtils.isBlank(undefined)).toBe(true);
        expect(stringUtils.isBlank('not blank')).toBe(false);
    });

    it('regexp test positively with alphanumerical pattern', function () {
        const regexp = stringUtils.regExpFactory('someAlpha1');

        expect(regexp.test('someAlpha1')).toBe(true);
    });

    it('regexp test negatively with alphanumerical pattern', function () {
        const regexp = stringUtils.regExpFactory('someAlpha1');

        expect(regexp.test('someurl')).toBe(false);
    });

    it('regexp test positively with wildcards pattern', function () {
        const regexp = stringUtils.regExpFactory('*some*Alpha1*');

        expect(regexp.test('bla_some_bla_Alpha1_bla')).toBe(true);
    });

    it('regexp test negatively with wildcards pattern', function () {
        const regexp = stringUtils.regExpFactory('*some*Alpha1*');

        expect(regexp.test('bla_some_bla_Alpha2_bla')).toBe(false);
    });

    it('regexp test positively with ready to use regexp string pattern', function () {
        const regexp = stringUtils.regExpFactory('^[a-z]+$');

        expect(regexp.test('somelowercase')).toBe(true);
    });

    it('regexp test negatively with ready to use regexp string pattern', function () {
        const regexp = stringUtils.regExpFactory('^[a-z]+$');

        expect(regexp.test('someUppercase')).toBe(false);
    });

    it('regExpFactory will convert a given pattern into a regular expression', function () {
        const pattern1 = '*1234';
        const regExp1 = stringUtils.regExpFactory(pattern1);
        expect(regExp1).toEqual(/^.*1234$/g);

        const pattern2 = '^((?!Middle).)*$';
        const regExp2 = stringUtils.regExpFactory(pattern2);
        expect(regExp2).toEqual(/^((?!Middle).)*$/g);
    });

    describe('WHEN sanitize is called', function () {
        it('with a parenthesis THEN it gives parenthesis prefixed by a backslash', function () {
            const _string = stringUtils.sanitize('(');
            expect(_string).toEqual(String.raw`\(`);
        });
        it('with a parenthesis prefixed by a backslash THEN it gives the same', function () {
            const _string = stringUtils.sanitize('(');
            expect(_string).toEqual(String.raw`\(`);
        });
        it('with a string containing parantheses THEN it prefixes each parentheses by backslash', function () {
            const _string = stringUtils.sanitize('hello(1)');
            expect(_string).toEqual(String.raw`hello\(1\)`);
        });
        it('with a string containing parentheses with backslashes THEN it will not prepend the backslash again', function () {
            const _string = stringUtils.sanitize('hello(1)2');
            expect(_string).toEqual(String.raw`hello\(1\)2`);
        });
        it('with complex string THEN it gives the correct result with all parentheses escaped', function () {
            const _string = stringUtils.sanitize('fun(((((crazy)))))');
            expect(_string).toEqual(String.raw`fun\(\(\(\(\(crazy\)\)\)\)\)`);
        });
    });
});

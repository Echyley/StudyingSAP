/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = {
    extends: './node_modules/smartedit-build/config/.eslintrc.js',
    parserOptions: {
        tsconfigRootDir: __dirname
    },
    overrides: [
        {
            files: 'tests/**/*Test.{js,ts}',
            rules: {
                'arrow-body-style': 'off',
                'max-classes-per-file': 'off',
                'prefer-spread': 'off',
                'spaced-comment': 'off',
                '@typescript-eslint/await-thenable': 'off',
                '@typescript-eslint/no-empty-function': 'off',
                '@typescript-eslint/no-floating-promises': 'off',
                '@typescript-eslint/no-unsafe-assignment': 'off',
                '@typescript-eslint/no-unsafe-call': 'off',
                '@typescript-eslint/no-unsafe-member-access': 'off',
                '@typescript-eslint/no-unsafe-return': 'off',
                '@typescript-eslint/unbound-method': 'off',
                '@typescript-eslint/restrict-plus-operands': 'off'
            }
        },
        {
            files: '{src,tests}/**/*.js',
            rules: {
                'no-var': 'off'
            }
        }
    ]
};

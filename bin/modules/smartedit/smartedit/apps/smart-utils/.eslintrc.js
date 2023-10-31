/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = {
    extends: './node_modules/smartedit-build/config/.eslintrc.js',
    parserOptions: {
        tsconfigRootDir: __dirname
    },
    overrides: [
        {
            files: 'src/utils/**.ts',
            rules: {
                'max-classes-per-file': 'off',
                'prefer-rest-params': 'off',
                'prefer-spread': 'off',
                '@typescript-eslint/ban-ts-comment': 'off',
                '@typescript-eslint/explicit-module-boundary-types': 'off',
                '@typescript-eslint/no-unsafe-assignment': 'off',
                '@typescript-eslint/no-unsafe-call': 'off',
                '@typescript-eslint/no-unsafe-member-access': 'off',
                '@typescript-eslint/no-unsafe-return': 'off',
                '@typescript-eslint/unbound-method': 'off'
            }
        },
        {
            files: 'src/unit/**.ts',
            rules: {
                'spaced-comment': 'off',
                '@typescript-eslint/no-unsafe-return': 'off',
                'prefer-spread': 'off'
            }
        },
        {
            files: 'src/**/*.spec.ts',
            rules: {
                'max-classes-per-file': 'off',
                'prefer-spread': 'off',
                'spaced-comment': 'off',
                '@typescript-eslint/no-floating-promises': 'off',
                '@typescript-eslint/no-unsafe-assignment': 'off',
                '@typescript-eslint/no-unsafe-call': 'off',
                '@typescript-eslint/no-unsafe-member-access': 'off',
                '@typescript-eslint/no-unsafe-return': 'off',
                '@typescript-eslint/unbound-method': 'off',
                '@typescript-eslint/restrict-plus-operands': 'off'
            }
        }
    ]
};

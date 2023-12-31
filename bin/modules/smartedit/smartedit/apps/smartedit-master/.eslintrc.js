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
            files: 'webpack/**/*.js',
            rules: {
                '@typescript-eslint/no-var-requires': 'off',
                '@typescript-eslint/no-unsafe-return': 'off',
                '@typescript-eslint/no-unsafe-argument': 'off',
                '@typescript-eslint/no-unsafe-call': 'off'
            }
        },
        {
            files: 'scripts/**/*.js',
            rules: {
                '@typescript-eslint/no-var-requires': 'off',
                '@typescript-eslint/no-unsafe-call': 'off'
            }
        }
    ]
};

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: [
            'internalSmarteditCommons',
            'prodbuildwebappinjector',
            'devbuildwebappinjector',
            'prettierCheck',
            'installPreCommitGitHook'
        ],
        config: function(data, conf) {
            const paths = require('../paths');

            return {
                ...conf,
                prodbuildwebappinjector: {
                    command:
                        'webpack --mode production --config web/app/webappinjector/webpack.conf.js'
                },
                devbuildwebappinjector: {
                    command:
                        'webpack --mode development --devtool source-map --config web/app/webappinjector/webpack.conf.js'
                },
                internalSmarteditCommons: {
                    command: `tsc -p ${
                        paths.tsconfig.libSmarteditCommons
                    } --emitDeclarationOnly --stripInternal false --declarationDir ${
                        paths.smarteditcommons.lib
                    }`
                },
                prettierCheck: {
                    command: 'exit 0'
                },
                installPreCommitGitHook: {
                    command: 'node ./smartedit-custom-build/scripts/precommitHook.js'
                }
            };
        }
    };
};

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function () {
    return {
        targets: ['options', 'test', 'test_only', 'packageDev', 'package', 'buildDocs'],
        config: function (data, conf) {
            var paths = require('../paths');
            const APP_PATH = 'web/app/**/*';

            return {
                options: {
                    atBegin: true
                },
                test: {
                    files: paths.watchFiles.concat([APP_PATH, paths.tests.allUnit]),
                    tasks: ['test']
                },
                test_only: {
                    files: paths.watchFiles.concat([paths.tests.allUnit]),
                    tasks: ['unit']
                },
                packageDev: {
                    files: paths.watchFiles.concat([APP_PATH]),
                    tasks: ['packageDev']
                },
                package: {
                    files: paths.watchFiles.concat([APP_PATH]),
                    tasks: ['package']
                },
                docs: {
                    files: paths.watchFiles,
                    tasks: ['ngdocs'],
                    options: {
                        atBegin: true
                    }
                },
                buildDocs: {
                    files: ['smartedit-build/**/*', 'smartedit-custom-build/**/*'],
                    tasks: ['ngdocs:build'],
                    options: {
                        atBegin: true
                    }
                }
            };
        }
    };
};

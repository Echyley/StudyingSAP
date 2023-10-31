/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = function () {
    return {
        targets: [
            'generateLibSmarteditCommonsTsConfig',
            'generateKarmaSmarteditcommonsTsConfig',
            'generateE2eSmarteditTsConfig',
            'generateE2eSmarteditContainerTsConfig',
            'generateE2eScriptsTsConfig',
            'generateAngularStorefrontTsConfig',
            'generateVendorTsConfig',
            'generateComopodocSmarteditCommonsTsConfig',
            'generateComopodocSmarteditTsConfig',
            'generateComopodocSmarteditContainerTsConfig'
        ],
        config: function (data, conf) {
            const lodash = require('lodash');
            const path = require('path');
            const paths = require('../paths');
            const bundlePaths = global.smartedit.bundlePaths;
            const tsConfigTemplates = require('../../smartedit-build/config/templates')
                .tsConfigTemplates;
            const tsconfigUtil = global.smartedit.taskUtil.tsconfig;
            const COMMON_PATH = 'web/app/common/*';

            // ====== Karma ======
            conf.generateKarmaSmarteditcommonsTsConfig = {
                dest: paths.tsconfig.karmaSmarteditcommons,
                data: lodash.cloneDeep(tsConfigTemplates.karmaSmarteditcommons)
            };
            tsconfigUtil.addTestPaths(conf.generateKarmaSmarteditcommonsTsConfig.data);

            /*
             * in smarteditcommons. tests and sources are together so we mustn't resolve from jsTarget
             */
            conf.generateKarmaSmarteditcommonsTsConfig.data.compilerOptions.baseUrl = '../../';

            const declaration = {
                declaration: true,
                declarationDir: '../../temp/types',
                stripInternal: true
            };

            const smarteditPaths = {
                smartedit: ['web/app/smartedit'],
                'smartedit/*': ['web/app/smartedit/*']
            };

            const smarteditContainerPaths = {
                smarteditcontainer: ['web/app/smarteditcontainer'],
                'smarteditcontainer/*': ['web/app/smarteditcontainer/*']
            };

            const smarteditCommonsPaths = {
                smarteditcommons: ['web/app/common'],
                'smarteditcommons/*': [COMMON_PATH]
            };

            const excludedPatterns = ['smartedit', 'smarteditcontainer'];

            function addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf,
                arrayOfPath = [],
                declareTypes
            ) {
                conf.compilerOptions.paths = conf.compilerOptions.paths || {};

                if (declareTypes !== false) {
                    lodash.merge(conf.compilerOptions, declaration);
                }

                Object.keys(conf.compilerOptions.paths).forEach((key) => {
                    key = key.replace(/[^a-zA-z]/g, '');
                    if (excludedPatterns.some((pattern) => pattern.match(new RegExp(`^${key}$`)))) {
                        delete conf.compilerOptions.paths[key];
                    }
                });

                conf.compilerOptions.typeRoots = conf.compilerOptions.typeRoots.filter((key) => {
                    return key.indexOf(bundlePaths.bundleDirName + '/@types') === -1;
                });

                arrayOfPath.forEach((paths) => {
                    lodash.merge(conf.compilerOptions.paths, lodash.cloneDeep(paths));
                });
            }

            /**
             * For Angular support, emitDecoratorMetadata must be enabled.
             * Adding emitDecoratorMetadata to compilerOptions forces TypeScript to save type information as metadata (it uses Reflect.metadata to do this).
             * Reflect.metadata stores all type information about all objects currently known in a Map, leaving our objects unchanged.
             * Angular DI can then query this Map to determine what to inject.
             *
             * @param {object} conf the webpack configuration
             */
            function enableDecoratorMetadata(conf) {
                conf.compilerOptions.emitDecoratorMetadata = true;
            }

            // Overrides the 'smarteditcommons' path in tsconfig to use the declaration files internal to smartedit where
            // all types are exposed instead of being stripped.
            // not to be added to generateKarmaSmarteditcommonsTsConfig!
            function overrideSmarteditCommonsWithInternalPath(conf) {
                lodash.merge(conf.compilerOptions.paths || {}, {
                    smarteditcommons: [path.resolve(paths.smarteditcommons.lib)]
                });
            }

            // ====== e2e ======

            const smarteditCommonsLibPaths = {
                smarteditcommons: [path.resolve(bundlePaths.external.generated.commons.lib)]
            };

            conf.generateE2eSmarteditTsConfig = {
                dest: bundlePaths.external.generated.tsconfig.e2eSmartedit,
                data: lodash.cloneDeep(tsConfigTemplates.devSmartedit)
            };
            conf.generateE2eSmarteditTsConfig.data.compilerOptions.paths = smarteditCommonsLibPaths;
            conf.generateE2eSmarteditTsConfig.data.compilerOptions.types =
                conf.generateE2eSmarteditTsConfig.data.compilerOptions.types || [];
            conf.generateE2eSmarteditTsConfig.data.compilerOptions.types.push('node');
            conf.generateE2eSmarteditTsConfig.shared = true;
            enableDecoratorMetadata(conf.generateE2eSmarteditTsConfig.data);

            conf.generateE2eSmarteditContainerTsConfig = {
                dest: bundlePaths.external.generated.tsconfig.e2eSmarteditContainer,
                data: lodash.cloneDeep(tsConfigTemplates.devSmarteditContainer)
            };
            conf.generateE2eSmarteditContainerTsConfig.data.compilerOptions.paths = smarteditCommonsLibPaths;
            conf.generateE2eSmarteditContainerTsConfig.data.compilerOptions.types =
                conf.generateE2eSmarteditContainerTsConfig.data.compilerOptions.types || [];
            conf.generateE2eSmarteditContainerTsConfig.data.compilerOptions.types.push('node');
            conf.generateE2eSmarteditContainerTsConfig.shared = true;
            enableDecoratorMetadata(conf.generateE2eSmarteditContainerTsConfig.data);

            conf.generateE2eScriptsTsConfig = {
                dest: bundlePaths.external.generated.tsconfig.e2eScripts,
                data: lodash.cloneDeep(tsConfigTemplates.baseConfig)
            };
            conf.generateE2eScriptsTsConfig.data.compilerOptions.paths = smarteditCommonsLibPaths;
            conf.generateE2eScriptsTsConfig.shared = true;
            enableDecoratorMetadata(conf.generateE2eScriptsTsConfig.data);

            // ====== Commons Lib ======
            conf.generateLibSmarteditCommonsTsConfig = {
                dest: paths.tsconfig.libSmarteditCommons,
                data: lodash.cloneDeep(tsConfigTemplates.baseConfig)
            };

            Object.assign(conf.generateLibSmarteditCommonsTsConfig.data.compilerOptions, {
                declaration: true,
                declarationDir: '../../' + bundlePaths.external.generated.commons.lib,
                stripInternal: true
            });

            const smarteditcommonsPaths = [
                {
                    'smarteditcommons/*': [COMMON_PATH],
                    smarteditcommons: ['web/app/common']
                }
            ];
            conf.generateLibSmarteditCommonsTsConfig.data.include =
                paths.tsConfigInclude.sources.common;
            conf.generateLibSmarteditCommonsTsConfig.data.exclude =
                paths.tsConfigExclude.sources.common;
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateLibSmarteditCommonsTsConfig.data,
                [
                    {
                        'smarteditcommons/*': [COMMON_PATH]
                    }
                ],
                false
            );
            enableDecoratorMetadata(conf.generateLibSmarteditCommonsTsConfig.data);

            // ======== PROD ========
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateProdSmarteditTsConfig.data, [
                smarteditPaths
            ]);
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateProdSmarteditContainerTsConfig.data,
                [smarteditContainerPaths]
            );

            overrideSmarteditCommonsWithInternalPath(conf.generateProdSmarteditTsConfig.data);
            overrideSmarteditCommonsWithInternalPath(
                conf.generateProdSmarteditContainerTsConfig.data
            );
            conf.generateProdSmarteditTsConfig.data.include =
                paths.tsConfigInclude.sources.smartedit;
            conf.generateProdSmarteditTsConfig.data.compilerOptions.declarationDir =
                '../../temp/types/smartedit';
            conf.generateProdSmarteditContainerTsConfig.data.include =
                paths.tsConfigInclude.sources.smarteditContainer;

            enableDecoratorMetadata(conf.generateProdSmarteditTsConfig.data);
            enableDecoratorMetadata(conf.generateProdSmarteditContainerTsConfig.data);

            // ======== DEV ========
            addTypesDeclarationAndRemovePathsAndTypeRoots(conf.generateDevSmarteditTsConfig.data, [
                smarteditPaths
            ]);
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateDevSmarteditContainerTsConfig.data,
                [smarteditContainerPaths]
            );

            overrideSmarteditCommonsWithInternalPath(conf.generateDevSmarteditTsConfig.data);
            overrideSmarteditCommonsWithInternalPath(
                conf.generateDevSmarteditContainerTsConfig.data
            );
            conf.generateDevSmarteditTsConfig.data.include =
                paths.tsConfigInclude.sources.smartedit;
            conf.generateDevSmarteditTsConfig.data.compilerOptions.declarationDir =
                '../../temp/types/smartedit';
            conf.generateDevSmarteditContainerTsConfig.data.include =
                paths.tsConfigInclude.sources.smarteditContainer;

            enableDecoratorMetadata(conf.generateDevSmarteditTsConfig.data);
            enableDecoratorMetadata(conf.generateDevSmarteditContainerTsConfig.data);

            // ======== KARMA ========
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateKarmaSmarteditTsConfig.data,
                [smarteditPaths],
                false
            );
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateKarmaSmarteditContainerTsConfig.data,
                [smarteditContainerPaths],
                false
            );
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateKarmaSmarteditcommonsTsConfig.data,
                smarteditcommonsPaths,
                false
            );

            overrideSmarteditCommonsWithInternalPath(conf.generateKarmaSmarteditTsConfig.data);
            overrideSmarteditCommonsWithInternalPath(
                conf.generateKarmaSmarteditContainerTsConfig.data
            );

            conf.generateKarmaSmarteditTsConfig.data.include = paths.tsConfigInclude.test.smartedit;
            conf.generateKarmaSmarteditContainerTsConfig.data.include =
                paths.tsConfigInclude.test.smarteditContainer;
            conf.generateKarmaSmarteditcommonsTsConfig.data.include =
                paths.tsConfigInclude.test.common;

            enableDecoratorMetadata(conf.generateKarmaSmarteditTsConfig.data);
            enableDecoratorMetadata(conf.generateKarmaSmarteditContainerTsConfig.data);

            // ======== IDE ========
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateIDETsConfig.data,
                [smarteditPaths, smarteditContainerPaths, smarteditCommonsPaths],
                false
            );
            conf.generateIDETsConfig.data.include = conf.generateIDETsConfig.data.include.concat(
                paths.tsConfigInclude.ide
            );

            // ======== ANGULAR STOREFRONT FOR TESTS ========
            conf.generateAngularStorefrontTsConfig = {
                dest: bundlePaths.external.generated.tsconfig.angularStorefront,
                data: lodash.cloneDeep(tsConfigTemplates.devSmarteditContainer)
            };

            const angularStorefrontInclude = [
                global.smartedit.bundlePaths.bundleRoot + '/test/e2e/dummystorefront/angular/**/*'
            ];
            addTypesDeclarationAndRemovePathsAndTypeRoots(
                conf.generateAngularStorefrontTsConfig.data,
                [],
                false
            );
            conf.generateAngularStorefrontTsConfig.data.include = angularStorefrontInclude;
            conf.generateAngularStorefrontTsConfig.data.compilerOptions.baseUrl =
                global.smartedit.bundlePaths.bundleRoot + '/test/e2e/dummystorefront/angular';

            enableDecoratorMetadata(conf.generateAngularStorefrontTsConfig.data);

            // ======== VENDOR CHUNK ========
            conf.generateVendorTsConfig = {
                dest: paths.tsconfig.vendor,
                data: lodash.cloneDeep(tsConfigTemplates.baseConfig)
            };
            conf.generateVendorTsConfig.shared = true;
            conf.generateVendorTsConfig.data.compilerOptions.baseUrl = '../../';
            conf.generateVendorTsConfig.data.include = paths.tsConfigInclude.vendor;
            enableDecoratorMetadata(conf.generateVendorTsConfig.data);

            // ======== COMPODOC ========

            conf.generateComopodocSmarteditCommonsTsConfig = {
                dest: paths.tsconfig.compodocSmarteditCommons,
                data: lodash.cloneDeep(tsConfigTemplates.baseConfig),
                shared: true
            };
            conf.generateComopodocSmarteditCommonsTsConfig.data.compilerOptions.baseUrl = '../../';
            conf.generateComopodocSmarteditCommonsTsConfig.data.include =
                paths.tsConfigInclude.compodocSmarteditCommons;
            enableDecoratorMetadata(conf.generateComopodocSmarteditCommonsTsConfig.data);

            conf.generateComopodocSmarteditTsConfig = {
                dest: paths.tsconfig.compodocSmartedit,
                data: lodash.cloneDeep(tsConfigTemplates.baseConfig),
                shared: true
            };
            conf.generateComopodocSmarteditTsConfig.data.compilerOptions.baseUrl = '../../';
            conf.generateComopodocSmarteditTsConfig.data.include =
                paths.tsConfigInclude.compodocSmartedit;
            enableDecoratorMetadata(conf.generateComopodocSmarteditTsConfig.data);

            conf.generateComopodocSmarteditContainerTsConfig = {
                dest: paths.tsconfig.compodocSmarteditContainer,
                data: lodash.cloneDeep(tsConfigTemplates.baseConfig),
                shared: true
            };
            conf.generateComopodocSmarteditContainerTsConfig.data.compilerOptions.baseUrl =
                '../../';
            conf.generateComopodocSmarteditContainerTsConfig.data.include =
                paths.tsConfigInclude.compodocSmarteditContainer;
            enableDecoratorMetadata(conf.generateComopodocSmarteditContainerTsConfig.data);

            return conf;
        }
    };
};

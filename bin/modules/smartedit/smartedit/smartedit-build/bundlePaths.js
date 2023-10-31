/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = (function () {
    const path = require('path');

    const SE_BUILD_FOLDER = 'smartedit-build';
    const EXTENSION_CONFIG_DIR = 'smartedit-custom-build';
    const GEN_PATH = EXTENSION_CONFIG_DIR + '/generated';
    const WEBPACK_CONFIG_PATH = EXTENSION_CONFIG_DIR + '/webpack';
    const KARMA_CONFIG_PATH = EXTENSION_CONFIG_DIR + '/karma';
    const BUNDLE_ROOT = path.join(process.cwd(), SE_BUILD_FOLDER);
    const backwardCompatibilityResults = 'diffAnalysisResults';
    const LIB_PATH = `${SE_BUILD_FOLDER}/lib`;
    const COMMONS_LIB_PATH = `${LIB_PATH}/smarteditcommons`;
    const VENDOR_LIB_PATH = `${LIB_PATH}/vendor`;

    return {
        bundleDirName: SE_BUILD_FOLDER,
        bundleRoot: BUNDLE_ROOT,
        libPath: LIB_PATH,
        extensionPath: EXTENSION_CONFIG_DIR,
        genPath: GEN_PATH,
        webpackConfigPath: WEBPACK_CONFIG_PATH,
        karmaConfigPath: KARMA_CONFIG_PATH,
        external: {
            // Anything outside of the bundle
            grunt: {
                configDir: path.resolve(`${EXTENSION_CONFIG_DIR}/config`),
                tasksDir: path.resolve(`${EXTENSION_CONFIG_DIR}/tasks`)
            },
            generated: {
                /**
                 * @deprecated Deprecated since 1905. Please use paths in 'external.karma'.
                 */
                karma: {
                    smartedit: `${GEN_PATH}/karma.smartedit.conf.js`,
                    smarteditContainer: `${GEN_PATH}/karma.smarteditContainer.conf.js`,
                    smarteditCommons: `${GEN_PATH}/karma.smarteditcommons.conf.js`
                },
                tsconfig: {
                    prodSmartedit: `${GEN_PATH}/tsconfig.prod.smartedit.json`,
                    prodSmarteditContainer: `${GEN_PATH}/tsconfig.prod.smarteditContainer.json`,
                    devSmartedit: `${GEN_PATH}/tsconfig.dev.smartedit.json`,
                    devSmarteditContainer: `${GEN_PATH}/tsconfig.dev.smarteditContainer.json`,
                    karmaSmartedit: `${GEN_PATH}/tsconfig.karma.smartedit.json`,
                    karmaSmarteditContainer: `${GEN_PATH}/tsconfig.karma.smarteditContainer.json`,
                    e2eSmartedit: `${BUNDLE_ROOT}/tsconfig/generated/tsconfig.e2e.smartedit.json`,
                    e2eSmarteditContainer: `${BUNDLE_ROOT}/tsconfig/generated/tsconfig.e2e.smarteditContainer.json`,
                    e2eScripts: `${BUNDLE_ROOT}/tsconfig/generated/tsconfig.e2e.scripts.json`,
                    ide: `${GEN_PATH}/tsconfig.ide.json`,
                    angularStorefront: `${BUNDLE_ROOT}/tsconfig/generated/tsconfig.angularStorefront.json`
                },
                /**
                 * @deprecated Deprecated since 1905. Please use paths in 'external.webpack'.
                 */
                webpack: {},
                commons: {
                    dist: COMMONS_LIB_PATH + '/dist/',
                    manifest: COMMONS_LIB_PATH + '/dist/smarteditcommons.manifest.json',
                    lib: COMMONS_LIB_PATH + '/src'
                },
                vendor: {
                    manifest: VENDOR_LIB_PATH + '/dist/vendor.dll.manifest.json'
                }
            },
            karma: {
                smartedit: `${KARMA_CONFIG_PATH}/karma.smartedit.conf.js`,
                smarteditContainer: `${KARMA_CONFIG_PATH}/karma.smarteditContainer.conf.js`,
                smarteditCommons: `${KARMA_CONFIG_PATH}/karma.smarteditcommons.conf.js`
            },
            webpack: {
                angularStorefront: `${WEBPACK_CONFIG_PATH}/webpack.angularstorefront.config.js`
            }
        },
        build: {
            tsfmt: `${SE_BUILD_FOLDER}/config/tsfmt.json`,
            jshintrc: `${SE_BUILD_FOLDER}/config/.jshintrc`,
            grunt: {
                gruntLoader: path.resolve(path.join(BUNDLE_ROOT, 'config/grunt-utils/loader.js')),
                configDir: path.resolve(path.join(BUNDLE_ROOT, 'config/config')),
                gruntUtilsDir: path.resolve(path.join(BUNDLE_ROOT, 'config/grunt-utils')),
                tasksDir: path.resolve(path.join(BUNDLE_ROOT, 'config/tasks'))
            },
            util: {
                // @deprecated since 1808 - use global.smartedit.taskUtil.protractor instead.
                e2eshardPath: path.resolve(
                    path.join(BUNDLE_ROOT, 'config/grunt-utils/task-utils/protractor')
                )
            }
        },
        test: {
            testFileSuffix: 'Test',
            unit: {
                root: path.resolve(path.join(BUNDLE_ROOT, 'test/unit')),
                utilsForCommonModules: [
                    {
                        pattern: '**/*.js.map',
                        included: false
                    },
                    'node_modules/angular-mocks/angular-mocks.js',
                    path.join(
                        BUNDLE_ROOT,
                        'webroot/static-resources/thirdparties/ckeditor/ckeditor.js'
                    ),
                    path.join(BUNDLE_ROOT, 'test/unit/generated/*.js'),
                    path.join(BUNDLE_ROOT, 'test/unit/vendors.ts'),
                    path.join(BUNDLE_ROOT, 'test/unit/commonBeforeEachTS.ts'),
                    path.join(BUNDLE_ROOT, 'test/unit/*.js')
                ],
                commonUtilModules: [
                    {
                        pattern: '**/*.js.map',
                        included: false
                    },
                    'node_modules/angular-mocks/angular-mocks.js',
                    path.join(BUNDLE_ROOT, 'lib/vendors/vendor_chunk.js'),
                    path.join(
                        BUNDLE_ROOT,
                        'webroot/static-resources/thirdparties/ckeditor/ckeditor.js'
                    ),
                    path.join(BUNDLE_ROOT, 'lib/smarteditcommons/dist/smarteditcommons.js'),
                    path.join(BUNDLE_ROOT, 'test/unit/generated/*.js'),
                    path.join(BUNDLE_ROOT, 'test/unit/vendors.ts'),
                    path.join(BUNDLE_ROOT, 'test/unit/commonBeforeEachTS.ts'),
                    path.join(BUNDLE_ROOT, 'test/unit/*.js'),
                    path.join(BUNDLE_ROOT, 'test/unit/outerTestDowngradedModule.ts')
                ],
                smarteditThirdPartyJsFiles: [
                    path.join(
                        BUNDLE_ROOT,
                        'webroot/static-resources/dist/smartedit/js/prelibraries.js'
                    )
                ],
                smarteditContainerUnitTestFiles: [
                    path.join(
                        BUNDLE_ROOT,
                        'webroot/static-resources/dist/smarteditcontainer/js/thirdparties.js'
                    )
                ]
            },
            e2e: {
                root: 'jsTests/e2e',
                listTpl: path.join(BUNDLE_ROOT, 'config/templates/list.tpl.html'),
                listDest: 'jsTests/e2e/list.html',
                applicationPath: 'jsTests/e2e/smartedit.html',
                fakeAngularPage: `/${SE_BUILD_FOLDER}/test/e2e/dummystorefront/fakeAngularEmptyPage.html`,
                protractor: {
                    conf: path.join(BUNDLE_ROOT, 'test/e2e/protractor/protractor-conf.js'),
                    savePath: 'jsTarget/test/junit/protractor'
                },
                pageObjects: path.join(BUNDLE_ROOT, 'test/e2e/pageObjects'),
                componentObjects: path.join(BUNDLE_ROOT, 'test/e2e/componentObjects'),
                webappinjectors: {
                    root: path.join(BUNDLE_ROOT, 'test/e2e/dummystorefront/imports/webappinjector')
                }
            }
        },
        tools: {
            seInjectableInstrumenter: {
                js:
                    './smartedit-build/config/tools/tsInstrument/generated/seInjectableInstrumenter.js'
            }
        },
        report: {
            backwardCompatibilityResults: backwardCompatibilityResults,
            instrument_functions_file: `${backwardCompatibilityResults}/VERSION/instrument_functions.data`,
            instrument_directives_file: `${backwardCompatibilityResults}/VERSION/instrument_directives.data`,
            instrument_service_not_exists_file: `${backwardCompatibilityResults}/VERSION/instrument_service_not_exists.data`
        },
        webAppTargetTs: ['jsTarget/web/**/*.ts', 'jsTarget/test/e2e/**/*.ts']
    };
})();

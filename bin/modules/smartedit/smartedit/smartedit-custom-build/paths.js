/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = (function () {
    const bundlePaths = require('../smartedit-build/bundlePaths');
    const paths = {
        bundlePaths
    };
    const MATCH_URL = '/**/*.html';
    const COMMON_JS_PATH = '/**/*.js';
    const COMMON_TS_PATH = '/**/*.ts';

    // grunt
    paths.gruntTasks = {
        dir: 'gruntTasks'
    };
    paths.gruntTasks.allJs = paths.gruntTasks.dir + COMMON_JS_PATH;

    // common
    paths.common = {
        dir: 'web/app/common'
    };
    paths.common.allJs = paths.common.dir + COMMON_JS_PATH;
    paths.common.allTs = paths.common.dir + COMMON_TS_PATH;
    paths.common.allHtml = paths.common.dir + MATCH_URL;

    // web
    paths.web = {
        dir: 'web'
    };
    paths.web.allHtml = paths.web.dir + MATCH_URL;
    paths.web.allJs = paths.web.dir + COMMON_JS_PATH;
    paths.web.allTs = paths.web.dir + COMMON_TS_PATH;

    //SmartEdit
    paths.web.smartEdit = {
        dir: 'web/app/smartedit'
    };
    paths.web.smartEdit.allJs = paths.web.smartEdit.dir + COMMON_JS_PATH;
    paths.web.smartEdit.allTs = paths.web.smartEdit.dir + COMMON_TS_PATH;
    paths.web.smartEdit.allHtml = paths.web.smartEdit.dir + MATCH_URL;

    paths.web.smartEdit.styling = {
        dir: 'web/app/smartedit/styling'
    };

    paths.web.smartEdit.styling.all = paths.web.dir + '/app/**/*.+(scss|css|less)';

    paths.web.webroot = {
        dir: paths.web.dir + '/webroot'
    };
    paths.web.webroot.all = paths.web.webroot.dir + '/**/*';
    paths.web.webroot.staticResources = {
        dir: paths.web.webroot.dir + '/static-resources'
    };
    paths.web.webroot.staticResources.smartEdit = {
        dir: paths.web.webroot.staticResources.dir + '/dist/smartedit/js'
    };
    paths.web.webroot.staticResources.smartEdit.css = {
        dir: paths.web.webroot.staticResources.smartEdit.dir + '/css'
    };
    paths.web.webroot.staticResources.smartEdit.css.all =
        paths.web.webroot.staticResources.smartEdit.css.dir + '/*.css';
    paths.web.webroot.staticResources.smartEdit.css.outerStyling =
        paths.web.webroot.staticResources.smartEdit.css.dir + '/outer-styling.css';
    paths.web.webroot.staticResources.smartEdit.css.innerStyling =
        paths.web.webroot.staticResources.smartEdit.css.dir + '/inner-styling.css';
    paths.web.webroot.staticResources.smartEdit.css.temp = {
        dir: paths.web.webroot.staticResources.smartEdit.css.dir + '/temp'
    };

    //SmartEditContainer
    paths.web.smarteditcontainer = {
        dir: 'web/app/smarteditcontainer'
    };
    paths.web.smarteditcontainer.allJs = paths.web.smarteditcontainer.dir + COMMON_JS_PATH;
    paths.web.smarteditcontainer.allTs = paths.web.smarteditcontainer.dir + COMMON_TS_PATH;
    paths.web.smarteditcontainer.allHtml = paths.web.smarteditcontainer.dir + MATCH_URL;
    paths.web.smarteditcontainer.components = {
        dir: paths.web.smarteditcontainer + '/components'
    };
    paths.web.smarteditcontainer.components.allJs =
        paths.web.smarteditcontainer.components.dir + COMMON_JS_PATH;
    paths.web.smarteditcontainer.dao = {
        dir: paths.web.smarteditcontainer + '/dao'
    };
    paths.web.smarteditcontainer.dao.allJs = paths.web.smarteditcontainer.dao.dir + COMMON_JS_PATH;

    paths.web.smarteditcontainer.services = {
        dir: paths.web.smarteditcontainer + '/services'
    };
    paths.web.smarteditcontainer.services.allJs =
        paths.web.smarteditcontainer.services.dir + COMMON_JS_PATH;

    // techne
    paths.techne = {
        dir: 'node_modules/techne/'
    };
    paths.techne.allFonts = [paths.techne.dir + 'dist/bootstrap/fonts/*'];

    // localization
    paths.smartEditLocalesProperties = 'resources/localization/smartedit-locales_en.properties';

    // ################## TESTS ##################
    paths.tests = {};
    paths.tests.allUnit = 'test/unit/**/*';
    paths.tests.allE2eTSMocks = ['test/e2e/**/inner*.ts', 'test/e2e/**/outer*.ts'];
    paths.tests.allE2e = ['test/e2e/**/*Test.js'];

    paths.thirdparties = {
        dir: 'node_modules'
    };

    // TODO: eliminate duplication in functions below
    paths.getSmarteditThirdpartiesFiles = function () {
        return [
            'node_modules/jquery/dist/jquery.min.js',
            'web/app/vendor/noConflict.js',
            'node_modules/angular/angular.min.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.min.js',
            'node_modules/angular-translate/dist/angular-translate.min.js',
            'node_modules/angular-sanitize/angular-sanitize.min.js',
            'node_modules/ui-select/dist/select.min.js',
            'node_modules/moment/min/moment-with-locales.min.js',
            'node_modules/popper.js/dist/umd/popper.min.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.min.js',
            'node_modules/crypto-js/crypto-js.min.js',
            'web/webroot/static-resources/thirdparties/blockumd/unblockumd.js'
        ];
    };
    paths.getSmarteditThirdpartiesDevFiles = function () {
        return [
            'node_modules/jquery/dist/jquery.js',
            'web/app/vendor/noConflict.js',
            'node_modules/angular/angular.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
            'node_modules/angular-translate/dist/angular-translate.js',
            'node_modules/angular-sanitize/angular-sanitize.js',
            'node_modules/ui-select/dist/select.js',
            'node_modules/moment/min/moment-with-locales.js',
            'node_modules/popper.js/dist/umd/popper.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.js',
            'node_modules/crypto-js/crypto-js.js',
            'web/webroot/static-resources/thirdparties/blockumd/unblockumd.js'
        ];
    };

    paths.getContainerThirdpartiesDevFiles = function () {
        return [
            'node_modules/jquery/dist/jquery.js',
            'web/app/vendor/noConflict.js',
            'node_modules/angular/angular.js',
            'node_modules/angular-animate/angular-animate.js',
            'node_modules/angular-route/angular-route.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js',
            'node_modules/angular-translate/dist/angular-translate.js',
            'node_modules/angular-sanitize/angular-sanitize.js',
            'node_modules/ui-select/dist/select.js',
            'node_modules/scriptjs/dist/script.js',
            'node_modules/moment/min/moment-with-locales.js',
            'node_modules/angular-ui-tree/dist/angular-ui-tree.js',
            'node_modules/popper.js/dist/umd/popper.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.js',
            'node_modules/crypto-js/crypto-js.js'
        ];
    };
    paths.containerThirdpartiesFiles = function () {
        return [
            'node_modules/jquery/dist/jquery.min.js',
            'web/app/vendor/noConflict.js',
            'node_modules/angular/angular.min.js',
            'node_modules/angular-animate/angular-animate.min.js',
            'node_modules/angular-route/angular-route.min.js',
            'node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.min.js',
            'node_modules/angular-translate/dist/angular-translate.min.js',
            'node_modules/angular-sanitize/angular-sanitize.min.js',
            'node_modules/ui-select/dist/select.min.js',
            'node_modules/scriptjs/dist/script.min.js',
            'node_modules/moment/min/moment-with-locales.min.js',
            'node_modules/angular-ui-tree/dist/angular-ui-tree.min.js',
            'node_modules/popper.js/dist/umd/popper.min.js',
            'node_modules/ng-infinite-scroll/build/ng-infinite-scroll.min.js',
            'node_modules/crypto-js/crypto-js.min.js'
        ];
    };

    paths.watchFiles = [
        'web/app/common/**/*',
        'web/app/smartedit/**/*',
        'web/app/smarteditcontainer/**/*'
    ];

    /**
     * Task: COPY: toDummystorefront
     */
    paths.copyToDummystorefront =
        bundlePaths.bundleRoot + '/test/e2e/dummystorefront/imports/generated/';

    /**
     * Note: config includes Gruntfile.js
     * (+ any other config @ the root)
     *
     *
     * Task: jsbeautifier
     * Ext: js, html  ????css
     * Type: source, config, allTest
     */
    paths.jsbeautifier = [
        'web/app/+(common|smartedit)*/**/*.+(js|html)',
        'Gruntfile.js',
        'test/**/*.+(js|html)',
        'smartedit-build/**/*.+(js|html)',
        '!**/webApplicationInjector.js',
        '!**/generated/**/*',
        '!' + bundlePaths.bundleRoot + '/webroot/**/*'
    ];

    /**
     * Task: jshint
     * Ext: js
     * Type: source, config, allTest
     */
    paths.jshint = [
        'web/app/+(common|smartedit)*/**/*.js',
        'web/webApplicationInjector.js',
        'test/**/*.js',
        '!**/generated/**/*',
        '!**/generated_*'
    ];

    /**
     * Task: tsformatter
     * Ext: ts
     * Type: source, config, allTest
     */
    paths.tsformatter = [
        'web/app/**/*.ts',
        'test/**/*.ts',
        'smartedit-build/**/*.ts',
        '!' + bundlePaths.bundleRoot + '/**/*',
        '!**/generated/**/*'
    ];

    /**
     * Task: ngDocs
     * Ext: js, ts
     * Type: smartedit+CommonSource, smarteditContainer+CommonSources
     */
    paths.ngdocs = {};
    paths.ngdocs.smartedit = [
        'node_modules/@smart/utils/**/*.d.ts',
        'web/app/+(common|smartedit)/**/*.+(ts|js)',
        '!**/generated/**/*'
    ];
    paths.ngdocs.smarteditcontainer = [
        'node_modules/@smart/utils/**/*.d.ts',
        'web/app/+(common|smarteditcontainer)/**/*.+(ts|js)',
        '!**/generated/**/*'
    ];

    /**
     * Task: checkI18nKeysCompliancy
     * Ext: js, ts, html
     * Type: sources
     */
    paths.checkI18nKeysCompliancy = ['web/app/+(common|smartedit)*/**/*.+(ts|js|html)'];

    /**
     * Task: checkNoFocus
     * Ext: js, ts
     * Type: test
     */
    paths.checkNoFocus = [
        'test/**/*.+(js|ts)',
        'web/app/**/*' + bundlePaths.test.testFileSuffix + '.+(js|ts)',
        '!**/generated/**/*',
        '!**/generated_*'
    ];

    //TODO
    // /**
    //  * Task: watch
    //  * Ext: js, ts
    //  * Type: test
    //  */
    // paths.watch = {};
    // paths.watch. = [
    //     'test/**/*.+(js|ts)',
    // ];

    /**
     * Task: uglify, concat, copy, clean, webpack
     * -- Leave as is for now, revist later, too much cherry-picking
     *
     *
     *
     *
     *
     */

    /**
     * Entrypoints
     */
    paths.entrypoints = {
        angularStorefront: './smartedit-build/test/e2e/dummystorefront/angular/index.ts',
        smartedit: './jsTarget/web/app/smartedit/index.ts',
        smarteditcommons: './jsTarget/web/app/common/index.ts',
        smarteditbootstrap: './jsTarget/web/app/smartedit/smarteditbootstrap.ts',
        smarteditcontainer: './jsTarget/web/app/smarteditcontainer/index.ts'
    };

    /**
     * Webpack Configuration
     */
    paths.webpack = {
        libSmarteditCommons: `${bundlePaths.webpackConfigPath}/webpack.lib.smarteditcommons.config.js`,
        karmaSmarteditcommons: `${bundlePaths.webpackConfigPath}/webpack.karma.smarteditcommons.config.js`
    };

    /**
     * TS Configuration
     */
    paths.tsconfig = {
        libSmarteditCommons: `${bundlePaths.genPath}/tsconfig.lib.smarteditcommons.json`,
        karmaSmarteditcommons: `${bundlePaths.genPath}/tsconfig.karma.smarteditcommons.json`,
        vendor: `${bundlePaths.genPath}/tsconfig.vendor.json`,
        compodocSmarteditCommons: `${bundlePaths.genPath}/tsconfig.compodoc.smarteditcommons.json`,
        compodocSmartedit: `${bundlePaths.genPath}/tsconfig.compodoc.smartedit.json`,
        compodocSmarteditContainer: `${bundlePaths.genPath}/tsconfig.compodoc.smarteditcontainer.json`
    };

    /**
     * SmarteditProperties
     */
    paths.smarteditproperties = {
        smartedit: './jsTarget/web/app/smartedit'
    };

    /**
     * SmarteditContainerProperties
     */
    paths.smarteditcontainerproperties = {
        smarteditcontainer: './jsTarget/web/app/smarteditcontainer'
    };

    /**
     *  SmarteditLibPath
     */
    paths.smarteditcommons = {
        sources: 'web/app/common',
        jsTarget: './jsTarget/web/app/common',
        lib: bundlePaths.extensionPath + '/lib/smarteditcommons'
    };

    /**
     * Code coverage
     */
    paths.coverage = {
        dir: './jsTarget/test/coverage',
        smarteditcommonsDirName: 'smarteditcommons',
        smarteditDirName: 'smartedit',
        smarteditcontainerDirName: 'smarteditcontainer'
    };

    // app
    paths.webAppTargetTs = 'jsTarget/web/app/**/*.ts';

    /**
     * TypeScript instrumentation
     */
    paths.tools = {
        seInjectableInstrumenter: {
            src: bundlePaths.bundleRoot + '/config/tools/tsInstrument/*.ts',
            dest: bundlePaths.bundleRoot + '/config/tools/tsInstrument/generated/'
        }
    };

    /**
     * TypeScript tsconfig include
     * path are relative to location of tsconfig file.
     */
    const bundleUnitTestFiles = bundlePaths.bundleRoot + '/test/unit/**/*';
    const tsConfingIncludeCommons = ['../../jsTarget/web/app/common/**/*'];
    const tsConfingIncludeSmartedit = ['../../jsTarget/web/app/smartedit/**/*'];
    const tsConfingIncludeSmarteditContainer = [
        '../../jsTarget/web/app/smarteditcontainer/**/*',
        '../../jsTarget/web/app/smarteditloader/**/*'
    ];
    paths.tsConfigInclude = {};
    paths.tsConfigInclude.sources = {
        smartedit: tsConfingIncludeSmartedit,
        smarteditContainer: tsConfingIncludeSmarteditContainer,
        common: tsConfingIncludeCommons
    };
    paths.tsConfigInclude.test = {
        smartedit: paths.tsConfigInclude.sources.smartedit.concat([
            '../../test/unit/smartedit/unit/**/*',
            bundleUnitTestFiles
        ]),
        smarteditContainer: paths.tsConfigInclude.sources.smarteditContainer.concat([
            '../../test/unit/smarteditcontainer/unit/**/*',
            bundleUnitTestFiles
        ]),
        common: paths.tsConfigInclude.sources.common.concat([
            '../../web/app/common/**/*' + bundlePaths.test.testFileSuffix + '.ts',
            bundleUnitTestFiles
        ])
    };
    paths.tsConfigInclude.ide = ['../../test/**/*', bundleUnitTestFiles];
    paths.tsConfigInclude.vendor = ['../../web/app/vendor/**/*'];

    paths.tsConfigInclude.compodocSmarteditCommons = [
        '../../compodocs/common/**/*.ts',
        '../../compodocs/common/**/*.js'
    ];

    paths.tsConfigInclude.compodocSmartedit = [
        '../../compodocs/smartedit/**/*.ts',
        '../../compodocs/smartedit/**/*.js'
    ];

    paths.tsConfigInclude.compodocSmarteditContainer = [
        '../../compodocs/smarteditcontainer/**/*.ts',
        '../../compodocs/smarteditcontainer/**/*.js'
    ];

    paths.tsConfigExclude = {};
    paths.tsConfigExclude.sources = {
        common: ['../../web/app/common/**/*' + bundlePaths.test.testFileSuffix + '.ts']
    };
    /**
     * Webfont
     */
    paths.webfont = {
        src: 'web/app/smartedit/styling/icons/*.svg',
        dest: 'web/webroot/static-resources/dist/smartedit/fonts/',
        destLess: 'web/app/smartedit/styling/shared/',
        relativeFontPath: '../fonts/',
        webrootFontPath: '../../../../../../web/webroot/static-resources/dist/smartedit/fonts/'
    };

    paths.compodoc = {
        temp: './compodocs',
        source: './web/app',
        output: {
            general: './jsTarget/compodoc/',
            smarteditCommons: './jsTarget/compodoc/smarteditcommons',
            smartedit: './jsTarget/compodoc/smartedit',
            smarteditContainer: './jsTarget/compodoc/smarteditcontainer'
        },
        customLogo: './web/webroot/static-resources/images/SAP_scrn_R.png'
    };

    return paths;
})();

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = {
    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: process.cwd(),

    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],

    decorators: ['karma-jasmine'],

    junitReporter: {
        outputDir: 'junit', // results will be saved as $outputDir/$browserName.xml
        outputFile: 'testReport.xml', // if included, results will be saved as $outputDir/$browserName/$outputFile
        suite: '' // suite will become the package name attribute in xml testsuite element
    },

    preprocessors: {
        '**/*.ts': ['webpack', 'sourcemap']
    },

    // DEFINE THESE IN EXTENSION CONFIG
    // ================================
    //
    // coverageReporter: {
    // },
    //
    // junitReporter: {
    // },

    // // list of files / patterns to load in the browser
    // each file acts as entry point for the webpack configuration
    // files: [
    // ],
    //
    // // list of files to exclude
    // exclude: [
    // ],

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    // coverage reporter generates the coverage
    reporters: ['spec', 'junit', 'coverage-istanbul'],

    specReporter: {
        suppressPassed: true,
        suppressSkipped: true
    },

    // web server port
    port: 9876,

    // enable / disable colors in the output (reporters and logs)
    colors: true,

    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    // logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,
    autoWatchBatchDelay: 1000,

    reportSlowerThan: 500,

    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['ChromeHeadless'],

    // https://github.com/karma-runner/karma-chrome-launcher
    customLaunchers: {
        ChromeHeadless: {
            base: 'Chrome',
            flags: [
                '--no-sandbox', // required to run without privileges in Docker
                '--headless',
                '--disable-gpu',
                '--disable-translate',
                '--disable-extensions',
                '--disable-web-security',
                '--remote-debugging-port=9222' // Without a remote debugging port, Google Chrome exits immediately.
            ],
            debug: true
        }
    },

    browserNoActivityTimeout: 20000,

    mime: {
        'text/x-typescript': ['ts', 'tsx']
    },

    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: true,

    proxies: {
        '/static-resources/images/': '/base/static-resources/images/'
    },

    plugins: [
        'karma-webpack',
        'karma-jasmine',
        'karma-sourcemap-loader',
        'karma-chrome-launcher',
        'karma-junit-reporter',
        'karma-spec-reporter',
        'karma-coverage-istanbul-reporter'
    ],

    browserConsoleLogOptions: {
        level: 'log',
        format: '%b %T: %m',
        terminal: true
    }
};

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function() {
    return {
        targets: ['toDummystorefront', 'sources', 'dev', 'ckeditor', 'thirdPartySourceMaps'],
        config: function(data, conf) {
            const paths = require('../paths');

            return {
                ckeditor: {
                    files: [
                        {
                            cwd: paths.thirdparties.dir + '/ckeditor4',
                            expand: true,
                            flatten: false,
                            src: ['**', '!**/samples/**', '!package.json'],
                            dest: 'web/webroot/static-resources/thirdparties/ckeditor'
                        }
                    ]
                },
                images: {
                    files: [
                        {
                            cwd: paths.thirdparties.dir + '/select2',
                            expand: true,
                            flatten: false,
                            src: ['**/*.+(png)'],
                            dest: 'web/webroot/static-resources/thirdparties/select2'
                        }
                    ]
                },
                toDummystorefront: {
                    files: [
                        {
                            expand: true,
                            flatten: true,
                            src: [
                                paths.thirdparties.dir + '/angular/angular.min.js', // needed for fakeAngularEmptyPage.html
                                paths.thirdparties.dir + '/lodash/lodash.min.js',
                                paths.thirdparties.dir + '/jquery/dist/jquery.min.js',
                                paths.thirdparties.dir + '/scriptjs/dist/script.min.js',
                                'node_modules/bootstrap/dist/css/bootstrap.css'
                            ],
                            dest: paths.copyToDummystorefront
                        },
                        {
                            expand: true, // TODO remove this, we shouldn't have to give out the font in this hackish way
                            flatten: false,
                            cwd: 'web/webroot/static-resources/dist/smartedit',
                            src: ['fonts/**/*'],
                            dest:
                                global.smartedit.bundlePaths.bundleRoot +
                                '/test/e2e/dummystorefront/imports'
                        }
                    ]
                },
                sources: {
                    files: [
                        // includes files within path
                        {
                            timestamp: true,
                            expand: true,
                            flatten: false,
                            src: [
                                paths.common.allJs,
                                'web/app/common/**/*.ts',
                                '!web/app/common/**/*' +
                                    global.smartedit.bundlePaths.test.testFileSuffix +
                                    '.+(js|ts)',
                                'web/app/smarteditloader/**/*.js',
                                'web/app/smarteditloader/**/*.ts',
                                paths.web.smarteditcontainer.allJs,
                                'web/app/smarteditcontainer/**/*.ts',
                                paths.web.smartEdit.allJs,
                                'web/app/smartedit/**/*.ts',
                                'web/app/vendor/**/*.ts',
                                paths.web.smartEdit.styling.all,

                                paths.common.allHtml,
                                paths.web.allHtml
                            ],
                            dest: 'jsTarget/'
                        }
                    ]
                },
                e2e: {
                    files: [
                        // includes files within path
                        {
                            expand: true,
                            flatten: false,
                            src: [paths.tests.allE2eTSMocks],
                            dest: 'jsTarget/'
                        }
                    ]
                },
                dev: {
                    files: [
                        {
                            expand: true,
                            flatten: true,
                            src: [
                                'smartedit-build/lib/smarteditcommons/dist/smarteditcommons.js*(.map)',
                                'smartedit-build/lib/vendors/*'
                            ],
                            dest:
                                paths.web.webroot.staticResources.dir + '/dist/smarteditcommons/js'
                        }
                    ]
                },
                fonts: {
                    files: [
                        {
                            expand: true,
                            flatten: true,
                            src: [paths.techne.allFonts],
                            dest: paths.web.webroot.staticResources.dir + '/dist/smartedit/fonts'
                        }
                    ]
                },

                thirdPartySourceMaps: {
                    /**
                     * This copying is only to remove console errors from some browsers
                     * CMSX-6695, CMSX-6695, CMSX-4969
                     */
                    files: [
                        {
                            expand: true,
                            flatten: true,
                            src: [
                                'node_modules/ui-select/dist/select.min.css.map',
                                'node_modules/popper.js/dist/umd/popper.min.js.map'
                            ],
                            dest: 'web/webroot/static-resources/dist/smartedit/css/'
                        },
                        {
                            expand: true,
                            flatten: true,
                            src: [
                                'node_modules/ui-select/dist/select.min.css.map',
                                'node_modules/popper.js/dist/umd/popper.min.js.map'
                            ],
                            dest: 'web/webroot/static-resources/smarteditcontainer/css/'
                        }
                    ]
                }
            };
        }
    };
};

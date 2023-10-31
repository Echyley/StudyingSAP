/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = function() {
    return {
        targets: ['landingPage', 'smarteditE2e'],
        config: function(data, conf) {
            conf.landingPage = {
                // TODO - really the html below is duplicated with the bundle version, there should be only one to maintain DRY principle

                preSmarteditContent: `
    <!--3rd prty libs-->    
    <script src="static-resources/dist/smarteditcontainer/js/thirdparties.js"></script>
    <script src="static-resources/dist/smarteditcommons/js/vendor_chunk.js"></script>
    <script src="static-resources/thirdparties/ckeditor/ckeditor.js"></script>
                `,
                // base smartedit files
                smarteditContent: `
    <!--libs-->
    <script src="static-resources/dist/smarteditcommons/js/smarteditcommons.js"></script>
    <script src="static-resources/dist/smarteditcontainer/js/smarteditcontainer.js"></script>
                `,

                bundleContent: '',

                // path and file to output to
                // the path must be either absolute or relative to the root of the extension
                dest: 'web/webroot/WEB-INF/views/index.jsp'
            };

            conf.smarteditE2e = {
                headerContent: '',
                // TODO: smartedit.bundlePaths.test.e2e.applicationPath once smartedit/test is renamed to smartedit/jsTests
                dest: 'test/e2e/smartedit.html'
            };

            return conf;
        }
    };
};

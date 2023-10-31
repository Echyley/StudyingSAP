/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function(grunt) {

    var phantomJSPattern = 'node_modules/**/phantomjs*';

    var testsRoot = "jsTests/tests/";

    // Helper Functions
    function endsWith(inputStr, suffix) {
        return inputStr.match(suffix + "$");
    }

    grunt.registerTask('multiKarma', function() {
        //if npmtestancillary is not present, phantomjs drivers won't be present
        if (grunt.file.expand({
                filter: 'isFile'
            }, phantomJSPattern).length > 0) {

            var testSuites = [];

            grunt.file.expand({
                filter: 'isDirectory'
            }, testsRoot + "*").forEach(function(dir) {
                if (!endsWith(dir, "/utils")) {
                    var folderName = dir.replace(testsRoot, "");
                    testSuites.push(folderName);
                }
            });
            testSuites.forEach(function(suite) {
                grunt.task.run('karma:' + suite);
            });
        } else {
            grunt.log.warn('multiKarma grunt phase was not run since no phantomjs driver found under ' + phantomJSPattern);
        }
    });
}

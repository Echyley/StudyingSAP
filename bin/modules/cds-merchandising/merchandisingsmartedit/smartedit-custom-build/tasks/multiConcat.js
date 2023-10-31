/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = function(grunt) {

    // Helper Functions
    function endsWith(inputStr, suffix) {
        return inputStr.match(suffix + "$");
    }

    grunt.registerTask('multiConcat', function() {
        var multiConcatTask = [];

        grunt.file.expand({
            filter: 'isDirectory'
        }, "jsTarget/web/features/*").forEach(function(dir) {
            if (!endsWith(dir, "/merchandisingsmarteditcommons")) {
                var folderName = dir.replace("jsTarget/web/features/", "");
                multiConcatTask.push(folderName);
            }
        });

        multiConcatTask.forEach(function(folderName) {
            grunt.task.run('concat:' + folderName);
        });
    });

};

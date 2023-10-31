/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
module.exports = function() {

    /***
     *  Naming:
     *  File or Files masks should end in File or Files,
     *  ex: someRoot.path.myBlaFiles = /root/../*.*
     *
     *  General rules:
     *  No copy paste
     *  No duplicates
     *  Avoid specific files when possible, try to specify folders
     *  What happens to merchandisingsmartedit, happens to merchandisingsmarteditContainer
     *  Try to avoid special cases and exceptions
     */
    var lodash = require('lodash');

    var paths = {};

    paths.tests = {};

    paths.tests.root = 'jsTests';
    paths.tests.testsRoot = paths.tests.root + '/tests';
    paths.tests.merchandisingsmarteditContainerTestsRoot = paths.tests.testsRoot + '/merchandisingsmarteditContainer';
    paths.tests.merchandisingsmarteditContainere2eTestsRoot = paths.tests.merchandisingsmarteditContainerTestsRoot + '/e2e';

    paths.tests.merchandisingsmartedite2eTestFiles = paths.tests.root + '/e2e/**/*Test.js';
    paths.tests.merchandisingsmarteditContainere2eTestFiles = paths.tests.merchandisingsmarteditContainere2eTestsRoot + '/**/*Test.js';

    paths.e2eFiles = [
        paths.tests.merchandisingsmartedite2eTestFiles,
        paths.tests.merchandisingsmarteditContainere2eTestFiles
    ];

    /**
     * Code coverage
     */
    paths.coverage = {
        dir: './jsTarget/test/coverage',
        smarteditDirName: 'smartedit',
        smarteditcontainerDirName: 'smarteditcontainer',
        smarteditcommonsDirName: 'smarteditcommons'
    };

    return paths;

}();

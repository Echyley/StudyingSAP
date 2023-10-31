/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');

const {
    group,
    webpack: { entry, alias, output }
} = require('../../smartedit-build/builders');

const commonsAlias = alias('cmscommons', resolve('./jsTarget/web/features/cmscommons'));

const smartedit = group(
    commonsAlias,
    alias('cmssmartedit', resolve('./jsTarget/web/features/cmssmartedit')),
    output({
        path: resolve('./jsTarget/'),
        filename: '[name].js',
        sourceMapFilename: '[file].map'
    })
);
const smarteditContainer = group(
    commonsAlias,
    alias('cmssmarteditcontainer', resolve('./jsTarget/web/features/cmssmarteditContainer')),
    output({
        path: resolve('./jsTarget/'),
        filename: '[name].js',
        sourceMapFilename: '[file].map'
    })
);

module.exports = {
    smarteditKarma: () => group(smartedit),
    smarteditContainerKarma: () => group(smarteditContainer),
    smartedit: () =>
        group(
            smartedit,
            entry({
                cmssmartedit: resolve('./jsTarget/web/features/cmssmartedit/index.ts')
            })
        ),
    smarteditContainer: () =>
        group(
            smarteditContainer,
            entry({
                cmssmarteditContainer: resolve(
                    './jsTarget/web/features/cmssmarteditContainer/index.ts'
                )
            })
        )
};

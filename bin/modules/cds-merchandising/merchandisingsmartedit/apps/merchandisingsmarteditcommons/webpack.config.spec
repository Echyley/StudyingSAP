const {
    resolve
} = require('path');
const base = require('smartedit-build/config/webpack/shared/webpack.bare.config');
const {
    compose,
    webpack: {
        alias, karma
    }
} = require('smartedit-build/builders');
module.exports = compose(
    karma(),
    alias('merchandisingsmarteditcommons', resolve('src'))
)(base);

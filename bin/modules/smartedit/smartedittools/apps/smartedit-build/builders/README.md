/\*

-   Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
-
-   This software is the confidential and proprietary information of SAP
-   ("Confidential Information"). You shall not disclose such Confidential
-   Information and shall use it only in accordance with the terms of the
-   license agreement you entered into with SAP.
    \*/

/\*\*

-   @ngdoc overview
-   @name ConfigurationBuilder
-   @description

# SmartEdit Configuration Builders

Builders are a modular approach to building JavaScript configuration objects by leveraging functional programming.
For example, a builder can be responsible for adding a value to a key, or a builder can have a combination of other builders
and these builders are then responsible for adding TS or SASS support to a webpack configuration. You can configure builder
groups based on your requirements for each bundle.

A simple builder is a composition of a function that accepts arguments for customization in which it returns another function
that accepts the entire configuration object to perform operations on. The following example shows how to create two builders
to add milk and sugar to a `config` object.

```
const addMilkBuilder = (quantity) => (config) => {
   if (quantity instanceof Number) {
       config = { ...config, milk: (config.milk || 0) + quantity}
   }
   return config;
};
const addSugarBuilder = (quantity) => (config) => {
   if (quantity instanceof Number) {
       config = { ...config, sugar: (config.sugar || 0) + quantity}
   }
   return config;
}
```

The body of the two functions are similar, so thus we can make the adding more generic.

```
const addNumberAtKeyBuilder = (key, number) => (config) => {
   if (quantity instanceof Number) {
       config = { ...config, [key]: (config[key] || 0) + number}
   }
   return config;
};

const addMilkBuilder = (quantity) => addNumberAtKey('milk', quantity);
const addSugarBuilder = (quantity) => addNumberAtKey('sugar', quantity);
```

We can create a more complex builder by grouping the builders. For example, we can create a builder that adds two milks and
two sugars in one operation.The `group` builder combines all builders passed into its arguments and executes them sequentially.
In addition, any existing configuration will be fed into the builder's arguments and its properties will be extended.

```
const addTwoSugarsAndTwoMilksBuilder = group(
    addMilkBuilder(2),
    addSugarBuilder(2)
);

const coldCoffee = {
   milk: 1,
   sugar: 3,
   waterTemp: '24C'
};

const somewhatBetterCoffee = addTwoSugarsAndTwoMilksBuilder(coldCoffee);
```

`somewhatBetterCoffee` will output.

```
{
   milk: 3,
   sugar: 5,
   waterTemp: '24C'
}
```

## Available Builders

Builders reside and are to be imported from the `smartedit-build/builders` folder. At the moment, there are a few generic
builders and specific builders for Karma and Webpack.

Generic:

-   {@link ConfigurationBuilder.service:add add}
-   {@link ConfigurationBuilder.service:compose compose}
-   {@link ConfigurationBuilder.service:execute execute}
-   {@link ConfigurationBuilder.service:compose group} (An alias to compose).
-   {@link ConfigurationBuilder.service:merge merge}
-   {@link ConfigurationBuilder.service:operate operate}
-   {@link ConfigurationBuilder.service:set set}
-   {@link ConfigurationBuilder.service:unset unset}

Webpack:

-   {@link ConfigurationBuilder.service:webpack.addModule addModule}
-   {@link ConfigurationBuilder.service:webpack.alias alias}
-   {@link ConfigurationBuilder.service:webpack.devtool devtool}
-   {@link ConfigurationBuilder.service:webpack.entry entry}
-   {@link ConfigurationBuilder.service:webpack.external external}
-   {@link ConfigurationBuilder.service:webpack.mode mode}
-   {@link ConfigurationBuilder.service:webpack.output output}
-   {@link ConfigurationBuilder.service:webpack.plugin plugin}
-   {@link ConfigurationBuilder.service:webpack.rule rule}

Webpack Plugins:

-   {@link ConfigurationBuilder.service:webpack.coveragePlugin coveragePlugin}
-   {@link ConfigurationBuilder.service:webpack.karmaErrorsPlugin karmaErrorsPlugin}

Webpack Loaders

-   {@link ConfigurationBuilder.service:webpack.tsLoader tsLoader}
-   {@link ConfigurationBuilder.service:webpack.sassLoader sassLoader}

Webpack Presets

-   {@link ConfigurationBuilder.service:webpack.karma karma}
-   {@link ConfigurationBuilder.service:webpack.prod prod}
-   {@link ConfigurationBuilder.service:webpack.dev dev}
-   {@link ConfigurationBuilder.service:webpack.minify minify}

Karma:

-   {@link ConfigurationBuilder.service:karma.coverage coverage}
-   {@link ConfigurationBuilder.service:karma.headless headless}
-   {@link ConfigurationBuilder.service:karma.webpack webpack}

#### Importing

```
const {
   add,
   compose,
   execute,
   group,
   merge,
   operate,
   set,
   unset,
   webpack: {
       addModule,
       alias,
       devtool,
       entry,
       external,
       mode,
       output,
       plugin,
       rule,

       // Plugins
       coveragePlugin,
       karmaErrorsPlugin,

       // Loaders
       tsLoader,
       sassLoader,

       // Presets
       karma,
       prod,
       dev,
       minify,
       dllPlugins
   },
   karma: {
       coverage,
       headless,
       webpack
   },
} = require('smartedit-build/builders');
```

## Quick Usage

The following example shows how to build a webpack configuration object for a webpack bundle that uses existing builders
and a customizable entry path.

```
const { compose, webpack } = require('./smartedit-build/builders');
const { devtool, entry, output, rule, plugin } = webpack;

const customEntryPathWebpackBuilder = (entryPath) => compose(
   devtool('source-maps'),
   entry({
       app: [
           entryPath
       ]
   }),
   output( {
       path: 'dist',
       filename: "[name].js",
       sourceMapFilename: "[file].map"
   }),
   rule({
       test: /\.css$/,
       use: ['style-loader', 'css-loader'],
   }),
   rule({
       test: /\.ts/,
       use: ['ts-loader'],
   }),
   plugin(new Plugin())
);

const webpackConfig = customEntryPathWebpackBuilder('custom_entry_path')();

console.log(webpackConfig);
```

It will output the following with a custom config.

```
{
    entry: {
       app: [
           'custom_entry_path'
       ]
    },
    output: {
        path: 'dist',
        filename: "[name].js",
        sourceMapFilename: "[file].map"
    }
    devtool: 'source-map',
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
            },
            {
                test: /\.ts/,
                use: ['ts-loader'],
            }
        ]
    },
    plugins: [
        Plugin { ... },
    ]
}
```

### Extending configurations

You may extend an existing configuration by passing the default configuration object into the builder's arguments.

```
const defaultWebpackConfig = {
   ...
};

const webpackConfig = customEntryPathWebpackBuilder('somewhere_else')(defaultWebpackConfig);
```

## Upgrading from GenerateWebpackConfig and GenerateKarmaConfig for SmartEdit Extensions

As of release 1905, SmartEdit has deprecated GenerateWebpackConfig and GenerateKarmaConfig Grunt tasks in order to simply
the Karma and Webpack configurations. To upgrade to the new configurations, all extensions must import a base configuration
of files denoted with an `ext` in their filenames in the smartedit-build/config/webpack or smartedit-build/config/karma
folders. Extensions can use builders provided in the smartedit-build/builders folder to override properties or write their
own configurations in pure JavaScript.

### Upgrading Webpack Configurations

1.  Create a `webpack` folder in `smartedit-custom-build`.

2.  Create six Webpack configuration files containing the following JavaScript code:

#### webpack.dev.smartedit.config.js

```
const base = require('../../smartedit-build/config/webpack/webpack.ext.dev.smartedit.config');

const {
   compose
} = require('../../smartedit-build/builders');

const {
   smartedit
} = require('./webpack.shared.config');

module.exports = compose(
   smartedit()
)(base);
```

#### webpack.dev.smarteditContainer.config.js

```
const base = require('../../smartedit-build/config/webpack/webpack.ext.dev.smarteditContainer.config');

const {
   compose
} = require('../../smartedit-build/builders');

const {
       smarteditContainer
} = require('./webpack.shared.config');

module.exports = compose(
   smarteditContainer()
)(base);
```

#### webpack.karma.smartedit.config.js

```
const base = require('../../smartedit-build/config/webpack/webpack.ext.karma.smartedit.config');

const {
   compose
} = require('../../smartedit-build/builders');

const {
   smarteditKarma
} = require('./webpack.shared.config');

module.exports = compose(
   smarteditKarma()
)(base);
```

#### webpack.karma.smarteditContainer.config.js

```
const base = require('../../smartedit-build/config/webpack/webpack.ext.karma.smartedit.config');

const {
   compose
} = require('../../smartedit-build/builders');

const {
   smarteditContainerKarma
} = require('./webpack.shared.config');

module.exports = compose(
   smarteditContainerKarma()
)(base);
```

#### webpack.prod.smartedit.config.js

```
const base = require('../../smartedit-build/config/webpack/webpack.ext.prod.smartedit.config');

const {
   compose
} = require('../../smartedit-build/builders');

const {
   smartedit
} = require('./webpack.shared.config');

module.exports = compose(
   smartedit()
)(base);
```

#### webpack.prod.smarteditContainer.config.js

```
   const base = require('../../smartedit-build/config/webpack/webpack.ext.prod.smarteditContainer.config');

   const {
       compose
   } = require('../../smartedit-build/builders');

   const {
       smarteditContainer
   } = require('./webpack.shared.config');

   module.exports = compose(
       smarteditContainer()
   )(base);
```

3.  Create a `webpack.shared.config` within the `smartedit-custom-build/webpack` folder to hold all of your custom webpack configurations.
    An example from CMSSmartEdit is shown below.

```
const { resolve } = require('path');

const {
   group,
   webpack: {
       entry,
       alias
   }
} = require('../../smartedit-build/builders');

const commonsAlias = alias('cmscommons', resolve("./jsTarget/web/features/cmscommons"));

const smartedit = group(
   commonsAlias,
   alias('cmssmartedit', resolve("./jsTarget/web/features/cmssmartedit"))
);
const smarteditContainer = group(
   commonsAlias,
   alias('cmssmarteditcontainer', resolve("./jsTarget/web/features/cmssmarteditContainer")),
);

module.exports = {
   smarteditKarma: () => group(
       smartedit
   ),
   smarteditContainerKarma: () => group(
       smarteditContainer
   ),
   smartedit: () => group(
       smartedit,
       entry({
           'cmssmartedit': resolve('./jsTarget/web/features/cmssmartedit/index.ts')
       })
   ),
   smarteditContainer: () => group(
       smarteditContainer,
       entry({
           'cmssmarteditContainer': resolve('./jsTarget/web/features/cmssmarteditContainer/index.ts')
       })
   )
};
```

### Upgrading Karma Configurations

1.  Create a `karma` folder in `smartedit-custom-build`.

2.  Create two Karma configuration files within the karma folder containing the following JavaScript code and provide
    the required information:

#### karma.smartedit.conf.js

```
const base = require('../../smartedit-build/config/karma/karma.ext.smartedit.conf');

const {
   compose,
   merge,
   add
} = require('../../smartedit-build/builders');

const karma = compose(
   merge({
       singleRun: true,
       junitReporter: {
           outputDir: '<- path ->', // Report output  folder.
           outputFile: 'testReport.xml'
       },

       // List of files / patterns to load in the browser.
       files: [],

       // Proxies
       proxies: { },

       webpack: require('../webpack/webpack.karma.smartedit.config'),
   }),
   add('exclude', [
       // Files to exclude.
   ], true)
)(base);

module.exports = function(config) {
   config.set(karma);
};
```

#### karma.smarteditContainer.conf.js

```
const base = require('../../smartedit-build/config/karma/karma.ext.smarteditContainer.conf');

const {
   compose,
   merge,
   add
} = require('../../smartedit-build/builders');

const karma = compose(
   merge({
       singleRun: true,
       junitReporter: {
           outputDir: '<- path ->', // Report output  folder.
           outputFile: 'testReport.xml'
       },

       // List of files / patterns to load in the browser.
       files: [],

       // Proxies
       proxies: { },

       webpack: require('../webpack/webpack.karma.smarteditContainer.config'),
   }),
   add('exclude', [
       // Files to exclude.
   ], true)
)(base);

module.exports = function(config) {
   config.set(karma);
};
```

3.  Edit the `smartedit-custom-build/config/karma.js` file to add coverage configuration when executing `grunt coverage`. In
    the body of the task, use the `coverage` builder to overwrite the `config.<target>.options` object and set the coverage
    directory and sub-directory paths for `smartedit` and `smarteditContainer`.

```
const { coverage } = require('../../smartedit-build/builders/karma');

if (grunt.option('coverage')) {
   config.smartedit.options = coverage(
       paths.coverage.dir,
       paths.coverage.subdir
   )(config.smartedit.options);

   config.smarteditContainer.options = coverage(
       paths.coverage.dir,
       paths.coverage.subdir
   )(config.smarteditContainer.options);
}
```

\*/

<h1 align="center">SmartEdit Packagr</h1>

The smartedit-packagr is the tool used to build SmartEdit libraries (npm packages). The bundler performs all the tasks needed to allow the library to be consumed by the [smartedit-master](../../../smartedit/apps/smartedit-master) package to produce the final SmartEdit scripts.

## Table of Contents

<h2 align="center">Getting Started</h2>

- [Table of Contents](#table-of-contents)
- [Prerequisites](#prerequisites)
- [Executing SmartEdit Packagr](#executing-smartedit-packagr)
- [Testing SmartEdit Packagr](#testing-smartedit-packagr)
- [Configuration Options](#configuration-options)
- [Libraries Directory Structure](#libraries-directory-structure)
- [Styles](#styles)
  - [Configuration Options](#configuration-options-1)
  - [Rewriting URLs in Less Files](#rewriting-urls-in-less-files)
  - [Rewriting URLs in Sass and CSS Files](#rewriting-urls-in-sass-and-css-files)
- [Angular](#angular)
- [TypeScript](#typescript)
  - [Configuration Options](#configuration-options-2)
- [External](#external)
  - [Configuration Options](#configuration-options-3)

## Prerequisites

To execute the smartedit-packagr it must be added to rush.json (this should be the case out-of-the-box) and it must be installed with Rush. To do this, please follow the instructions in [smartedittools](../../#commands). Once this is done, the smartedit-packagr will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-packagr folder. The smartedit-packagr and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Executing SmartEdit Packagr

The smartedit-packagr must be executed through NPM. To do so, the package.json of the library to build must have a script that references the @smartedit/packagr (se-packagr). The following snippet shows the minimum setup needed to use the smartedit-packagr.

```json
{
    "name": "example-se-library",
    "version": "1.0.0",
    "scripts": {
        "build": "se-packagr"
    },
    "devDependencies": {
        "@smartedit/packagr": "0.0.1"
    }
}
```

When executed, the smartedit-packagr will read two files: the `package.json` and the `smartedit.json configuration` files. By default, it will try to find them at the library's root folder. However, this can be configured by passing the `--options=<path-to-smartedit.json-file>` and the `--package=<path-to-package.json-file>` parameters.

## Testing SmartEdit Packagr

The smartedit-packagr contains a group of unit and integration tests that help ensure it is working properly. These tests can be executed with the following command:

```bash
# Must be executed in the smartedit-packagr root folder
npm run test
```

<h2 align="center">Introduction</h2>
The smartedit-packagr is the tool used to build SmartEdit libraries (npm packages). The bundler executes the following tasks:

-   Styling: Finds and compiles all Sass, Less, and CSS files and produces a single CSS file for the whole library. It will also copy assets referenced in these style files into a standard location (`/dist/assets`) whenever necessary.
-   Angular: Resolves inner HTML and styling templates and appends them to the output JS file.
-   SmartEdit instrumentation: Finds SmartEdit TypeScript decorators, such as @SeComponent, @SeModule, amongst others, and performs instrumentation as necessary. This includes injecting some decorators payloads to enable more decorator complex cases (e.g., parameter sharing between decorators).
-   TypeScript: Performs type checking and transpiles TypeScript code to JavaScript code.
-   Legacy: Gathers all JavaScript code and legacy HTML templates and concatenates them into the output JS file.
-   External Dependencies: Marks any dependency (including peer dependencies) as external. This means that these dependencies won't be included in the produced JS file. Instead, they will be handled by Webpack when the final SmartEdit files are generated.

The smartedit-packagr relies on [Rollup](https://rollupjs.org/guide/en/), on third-party plugins, and in-house plugins. These last ones are described in the [Plugins](#plugins) section in more detail.

## Configuration Options

The smartedit-packagr requires two configuration files: the standard `package.json` and `smartedit.json`.

-   package.json: This must be a valid package.json file. It must have a name, a version, and must specify the `@smartedit/packagr` as a dev-dependency.
-   smartedit.json: This file contains configuration options related to SmartEdit (not all options are needed by the smartedit-packagr).

## Libraries Directory Structure

By default, the smartedit-packagr requires the following folder structure:

```
library-to-compile
├── src
│     └──  index.ts
├── package.json
├── smartedit.json
└── tsconfig.json
```

Once the smartedit-packagr has completed its work, it will copy the output into the library's dist folder, as shown below:

```
library-to-compile
├── dist
│     ├── index.js // The entry point of the library. Contains all the code compiled and bundled.
│     ├── index.d.ts // The types entry point. This is needed to provide type information in TypeScript. There might be other *.d.ts files, but this is always the entry.
│     ├── styles.css // This file contains all the styles found and compiled.
│     ├── assets // Folder containing any asset found while processing styles living outside the current library.
├── src
│     └──  index.ts
├── package.json
├── smartedit.json
└── tsconfig.json
```

<h2 align="center">Plugins</h2>

## Styles

The Rollup styles plugin (styles.plugin.ts) is used to compile .less, .sass, or .css files. Whenever Rollup finds files of any of these
types, the plugin will read them, compile them, and concatenate them into a file called styles.css.

During the compilation, the plugin also
rewrites URLs as needed (note that URLs in Less and Sass are handled differently). This is particularly important when importing styles from external libraries; the URLs in those stylesheets won't be relative to the current library and might not be properly referenced when used in SmartEdit. By rewritting URLs, the plugin ensures that they are relative to the current library and that the referenced assets can be found when used in SmartEdit.

> 🚧 Important Note
>
> Styling files are only included if they are imported through an `import` statement (except for global styles). Otherwise, the files are never picked by this plugin.

### Configuration Options

The styles plugin can be configured through the `smartedit.json` file in the library. The following snippet describes all the possible options:

```js
{
    "style": {
        /**
         * Optional parameter.
         * It represents the path to a SASS file that contains styles to share with all other SASS files.
         * Note: This path is relative from the root of the library being packaged.
        */
        "global",
        /**
         * Optional parameter.
         * An array containing rules to rewrite URLs in Sass or in plain CSS stylesheets.
         */
        "urlsToRewrite"
    }
}
```

### Rewriting URLs in Less Files

Due to the way the less compiler works, it is possible to customize the way style files are compiled. The styles.plugin leverages that functionality to rewrite URLs in the following way:

1. URLs starting with a tilde (~): The tilde is replaced with `node_modules/`. For example:

    Original URL

    ```less
    background: url('~/example/img/img1.png');
    ```

    Rewritten URL (tilde)

    ```less
    background: url('node_modules/img/img1.png');
    ```

2. If the URLs are pointing to assets in other libraries (node_modules), the assets are copied to the `/dist/assets` folder in the current library.
3. URLs are rewritten to ensure they are relative from the `dist/` folder of the current library. For example:

    Original

    ```less
    .test {
        background: url('~/example/img/img1.png');
    }
    ```

    Rewritten URL (after tilde and making it relative)

    ```less
    .test {
        background: url('assets/img1.png');
    }
    ```

### Rewriting URLs in Sass and CSS Files

Currently, it is not possible to customize the full compilation process in Sass. Therefore, instead of rewriting URLs during compilation, the URL rewriting process is handled after Sass is finished executing. This is done with a tool called PostCSS.

To rewrite URLs the plugin needs to be given an array of rules describing how to rewrite them. Each rule contains a pattern that the plugin will use to identify the URLs that need rewriting and a path to the location where the assets being referenced reside. If a Sass (or CSS) file contains a URL that matches the pattern, the corresponding asset will be copied to the `dist/assets` folder, and the URL will be rewritten to be relative to the `dist/` folder in the current library.

For example, the following rule is telling the plugin to rewrite any URL matching `../../img/*.png` and that the assets are found in the path `node_modules/example/img`:

```js
    "style": {
        "urlsToRewrite": [{
            "urlMatcher": '../../img/*.png',
            "assetsLocation": 'node_modules/example/img/'
        }]
    }
```

```scss
.test2 {
    background: url('../../images/img1.png');
}
```

When compiled, the plugin will rewrite the matching URL and copy the asset to the right path.

```css
.test2 {
    background: url('assets/img1.png');
}
```

## Angular

The Angular plugin (angular.plugin.ts) is a wrapper around the [rollup-angular-plugin](https://github.com/cebor/rollup-plugin-angular). The plugin reads Angular decorators and processes any HTML or styles referenced in those decorators in the following way:

-   Template: The HTML will be minified and left inline.
-   TemplateUrl: The file will be read and the HTML will be minified, and inlined.
-   Styles: The Styles will be compiled, minified, and left inline.
-   StyleUrls: The files will be read, compiled, minified and inlined.

For example, the following snippet contains an Angular component that references a template and styles:

```ts
@Component({
    selector: 'test-imports',
    template: `
        <div>
            This is an inline example
        </div>
    `,
    styleUrls: ['./component.scss']
})
export class TestImportsComponent {
    constructor() {}
}
```

After the plugin executes, the code will be left like follows (the code will be compiled further in other plugins):

```ts
@Component({
    selector: 'test-imports',
    template: `
        <div>This is an inline example</div>
    `,
    styles: ['.main-content{color:#fff}.component{color:#fff}']
})
export class TestImportsComponent {
    constructor() {}
}
```

## TypeScript

The TypeScript plugin (typescript.plugin.ts) is a wrapper around the [rollup-plugin-typescript2](https://github.com/ezolenko/rollup-plugin-typescript2) plugin. This wrapper specifies some default TypeScript configuration that any SmartEdit library must have and then delegates to the rollup-plugin-typescript2 so that it can perform the TypeScript compilation.

As part of the configuration process, this wrapper specifies a TypeScript transformer that will add instrumentation code during the TypeScript compilation. The transformer looks for SmartEdit decorators (such as @SeComponent, @SeModule) and adds extra code to enable certain SmartEdit functionality. In particular, it does :

-   Add extra metadata: Reads the information passed to the `Injectable` and `Component` Angular decorators and stores them in a way that is accessible to other SmartEdit decorators.

### Configuration Options

The TypeScript plugin can be configured through the `smartedit.json` file in the library. The following snippet describes all the possible options:

```js
{
    "typescript": {
        "config" // Optional parameter. The path (relative to the root of the library) to the TypeScript configuration file. Defaults to tsconfig.json.
        "entry" // Optional parameter. The path (relative to the root of the library) to the entry TypeScript file. Defaults to ./src/index.ts.
        "dist" // Optional parameter. The path (relative to the root of the library) to the output file. Defaults to ./dist/index.js
    },
    "instrumentation": {
        "skipInstrumentation" // Optional boolean parameter. If true, the plugin won't instrument SmartEdit decorators. Defaults to false.
    },
}
```

## External

The External plugin (external.plugin.ts) is a Rollup plugin used to mark all dependencies (dependencies and peer-dependencies) in the package.json as external. This is important to ensure that libraries won't duplicate code between them. Instead, by leaving the dependencies as external, the smartedit-master (through Webpack) will read these external dependencies and inject them only once. This also allows further optimizations.

### Configuration Options

The External plugin can be configured through the `smartedit.json` file in the library. The following snippet describes all the possible options:

```js
{
    dependencies; // Optional. An array with the name of dependencies that should also be marked as external. This defaults to [].
}
```

<h2 align="center">Extending SmartEdit Packagr</h2>

The SmartEdit Packagr is a final npm package. It cannot be extended or customized. It can only be configured through the `smartedit.json` file in each library. However, most of the bundler's functionality resides in its Rollup plugins. If the bundler does not meet all the needs of a library, it is possible to create a separate NodeJS script or npm package and call Rollup with the desired smartedit-packagr's plugins or other third-party plugins. This gives the implementer full power in how the SmartEdit libraries are compiled.
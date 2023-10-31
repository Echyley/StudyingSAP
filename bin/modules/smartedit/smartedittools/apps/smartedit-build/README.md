<h1 align="center">SmartEdit Build</h1>
The smartedit-build is an npm library that exposes the old SmartEdit Build system (Grunt based). Currently, it's only used to run unit tests.

> 🚧 Important Note
>
> It is not recommended to rely on functionality provided by this library, other than for running unit tests. This library will probably be deprecated in the future and, eventually, removed.

## Prerequisites

To execute the smartedit-build it must be added to `rush.json` (this should be the case out-of-the-box) and it must be installed with Rush. To do this, please follow the instructions in [smartedittools](../../#commands). Once this is done, the smartedit-build will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-build folder. The smartedit-build and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Unit Tests

The smartedit-build exposes an executable called `se-test`, which can be used to execute unit tests in SmartEdit libraries. Any library that needs unit tests executed through this functionality must comply with the following:

-   The smartedit-build runs on top of [Karma](https://karma-runner.github.io/latest/index.html). Therefore, the library must have a valid `karma.conf.js` at the root directory.
-   Unit tests must be written in TypeScript or JavaScript using [Jasmine](https://jasmine.github.io/) syntax.

The snippet below shows the minimum `package.json` setup needed to run `se-test` in a SmartEdit library:

```js
{
    "name": "some-smartedit-library",
    "version": "0.0.1",
    "private": true,
    "main": "dist/index.js",
    "typings": "dist/index.d.ts",
    "description": "Some SmartEdit library",
    "scripts": {
        "test": "se-test", // Tests will be run in the command line
        "test:dev": "se-test --browser", // A browser window will open and tests will be run inside. Useful for debugging.
        "test:coverage": "se-test --coverage" // Tests will be run and test coverage will be calculated.
    },
    "devDependencies": {
        "smartedit-build": "0.0.1",
        "@types/jasmine": "3.3.0",
        "@types/node": "14.11.2",
        "typescript": "4.1.5"
    },
}
```

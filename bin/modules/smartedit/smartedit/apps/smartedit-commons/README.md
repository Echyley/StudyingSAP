<h1 align="center">SmartEdit Commons</h1>
The smartedit-commons library contains functionality that can be used by the core SmartEdit libraries and extensions in both, the SmartEdit container and SmartEdit inner applications.

## Prerequisites

To execute the smartedit-commons library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the smartedit-commons will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-commons library folder. The smartedit-commons library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The smartedit-commons library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point the library is ready to be consumed by other libraries in SmartEdit.

## Running Unit Tests

The following command can be used to run the smartedit-commons unit tests in the background:

```bash
npm run test
```

It is also possible to run unit tests in a browser window. This is useful when debugging tests (it is possible to set break-points). To do so, execute the following command:

```bash
npm run test:dev
```

## Generating documentation

The following command can be used to generate the smartedit-commons documentation:

```bash
npm run docs
```

## Styles

`dist/styles/styles.css` is a bundle of `src/components` and `styling/less/*`

`styling/sass` are not included in the bundle, instead they are imported by the individual app (`smartedit/styling/sass/styles.scss` and `smartedit-container/styling/sass/styles.scss`).

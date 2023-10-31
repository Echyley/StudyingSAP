<h1 align="center">SmartEdit</h1>

The cmssmartedit library contains a hybris Angular application that extends the SmartEdit application. It provides CMS functionality to the storefront.

## Prerequisites

To execute the cmssmartedit library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the cmssmartedit will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the cmssmartedit library folder. The cmssmartedit library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The cmssmartedit library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by (smartedit-master)[../smartedit-master].

## Running Unit Tests

The following command can be used to run the cmssmartedit unit tests in the background:

```bash
npm run test
```

It is also possible to run unit tests in a browser window. This is useful when debugging tests (it allows setting break-points). To do so, execute the following command:

```bash
npm run test:dev
```

## Generating documentation

The following command can be used to generate the cmssmartedit documentation:

```bash
npm run docs
```

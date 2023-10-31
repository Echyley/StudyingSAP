<h1 align="center">CMS Commons</h1>
The cmscommons library contains common functionality shared in both, the [CMS SmartEdit Container](../cms-smartedit-container) and the [CMS SmartEdit applications](../cms-smartedit).

## Prerequisites

To execute the cmscommons library it must be added to `rush.json`. To do this, please build the smartedittools as described in the [smartedittools README.md](../../#commands). Once this is done, the cmscommons will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the cmscommons library folder. The cmscommons library and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Building

The cmscommons library can be built using the following command:

```bash
npm run build
```

After the build is done, the output will be in the `dist/` folder. At this point, the library is ready to be consumed by other libraries in CMS SmartEdit.

## Generating documentation

The following command can be used to generate the cmscommons documentation:

```bash
npm run docs
```

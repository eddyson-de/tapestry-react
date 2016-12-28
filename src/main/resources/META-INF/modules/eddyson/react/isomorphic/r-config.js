require.load = function (context, moduleName, url) {
    console.log("load custom: " + moduleName);
    __tapestry.loadModule(moduleName);
    context.completeLoad(moduleName);
};
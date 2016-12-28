require.load = function (context, moduleName, url) {
    console.log("load module: " + moduleName);
    __tapestry.loadModule(moduleName);
    context.completeLoad(moduleName);
};
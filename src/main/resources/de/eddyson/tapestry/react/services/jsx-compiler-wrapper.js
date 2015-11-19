var Babel = require("babel-core");
var react = require("babel-preset-react");
var es2015 = require("babel-preset-es2015");
var amd = require("babel-plugin-transform-es2015-modules-amd")

compileJSX = function(input, filename, outputamd) {
    try {
        var plugins = [];
        if (outputamd){
          plugins.push(amd);
        }
        return { output: Babel.transform(input, {filename: filename, compact: false, ast: false, presets: [react, es2015], plugins: plugins }).code };
    }
    catch (err) {
        return { exception: err.toString() };
    }
};
var Babel = require("babel-core");
var react = require("babel-preset-react");
var latest = require("babel-preset-latest");
var amd = require("babel-plugin-transform-es2015-modules-amd")
var inlineReplaceVariables = require("babel-plugin-inline-replace-variables")

compileJSX = function(input, filename, outputamd, useColoredOutput, loadReactPreset, productionMode) {
    try {
        var plugins = [
          [inlineReplaceVariables, {
            "__DEV__": !productionMode
          }]
        ];
        if (outputamd){
          plugins.push(amd);
        }
        var presets = [latest];
        if (loadReactPreset){
          presets.push(react);
        }
        var config = {filename: filename,
                      compact: false,
                      ast: false,
                      babelrc: false,
                      presets: presets,
                      plugins: plugins,
                      highlightCode: useColoredOutput};
        return { output: Babel.transform(input, config).code };
    }
    catch (err) {
        return { exception: err.toString() };
    }
};

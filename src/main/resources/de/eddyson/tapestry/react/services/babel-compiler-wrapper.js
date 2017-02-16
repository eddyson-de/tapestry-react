var Babel = require("babel-core");
var react = require("babel-preset-react");
var latest = require("babel-preset-latest");
var stage3 = require("babel-preset-stage-3");
var amd = require("babel-plugin-transform-es2015-modules-amd")
var inlineReplaceVariables = require("babel-plugin-inline-replace-variables")

compileJSX = function(inputs, outputamd, useColoredOutput, loadReactPreset, productionMode, useStage3) {
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
        if (useStage3){
          presets.push(stage3);
        }
        var output = {};
        Object.keys(inputs).forEach(function(filename){
          var config = {
              filename: filename,
              compact: false,
              ast: false,
              babelrc: false,
              presets: presets,
              plugins: plugins,
              highlightCode: useColoredOutput
          };
          var input = inputs[filename];
          output[filename] = Babel.transform(input, config).code;
        });
        return { output: output };
    }
    catch (err) {
        return { exception: err.toString() };
    }
};

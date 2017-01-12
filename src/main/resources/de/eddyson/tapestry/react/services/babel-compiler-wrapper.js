var Babel = require("babel-core");
var react = require("babel-preset-react");
var es2015 = require("babel-preset-es2015");
var amd = require("babel-plugin-transform-es2015-modules-amd")
var inlineReplaceVariables = require("babel-plugin-inline-replace-variables")

compileJSX = function(input, filename, outputamd, useColoredOutput, loadReactPreset, productionMode) {
  var plugins = [
    [inlineReplaceVariables, {
      "__DEV__": !productionMode
    }]
  ];
  if (outputamd){
    plugins.push(amd);
  }
  var presets = [es2015];
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
  return Babel.transform(input, config).code;
};
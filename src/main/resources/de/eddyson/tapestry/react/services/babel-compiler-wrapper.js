var B = typeof Babel !== "undefined" ? Babel :  module.exports;

compileJSX = function(inputs, outputamd, useColoredOutput, loadReactPreset, productionMode, useStage3) {
    try {
        var plugins = [
          ['inline-replace-variables', {
            "__DEV__": !productionMode
          }]
        ];
        if (outputamd){
          plugins.push('transform-es2015-modules-amd');
        }
        var presets = ['latest'];
        if (loadReactPreset){
          presets.push('react');
        }
        if (useStage3){
          presets.push('stage-3');
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
          output[filename] = B.transform(input, config).code;
        });
        return { output: output };
    }
    catch (err) {
        return { exception: err.toString() };
    }
};

var B = typeof Babel !== "undefined" ? Babel :  module.exports;

// include https://github.com/wssgcg1213/babel-plugin-inline-replace-variables
// see https://github.com/babel/babel-standalone/issues/82
var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

B.registerPlugin('inline-replace-variables', function (babel) {
  var t = babel.types;
  return {
    visitor: {
      Identifier: function Identifier(path, state) {
        if (path.parent.type === 'MemberExpression') {
          return;
        }
        if (path.parent.type === 'ClassMethod') {
          return;
        }
        if (path.isPure()) {
          return;
        }
        if (!state.opts.hasOwnProperty(path.node.name)) {
          return;
        }
        var replacement = state.opts[path.node.name];
        if (replacement !== undefined) {
          var type = typeof replacement === 'undefined' ? 'undefined' : _typeof(replacement);
          if (type === 'boolean') {
            path.replaceWith(t.booleanLiteral(replacement));
          } else {
            // treat as string
            var str = String(replacement);
            path.replaceWith(t.stringLiteral(str));
          }
        }
      }
    }
  };
});

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
          presets.push('stage3');
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

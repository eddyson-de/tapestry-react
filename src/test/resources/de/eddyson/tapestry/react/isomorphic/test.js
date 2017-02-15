define('test', ['react'], function(React) {
    var Test = React.createClass({
      render: function() {
        return React.createElement('div', null, "Hello " + this.props.name)
      }
    });
    return Test;
});
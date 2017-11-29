# tapestry-react

[![Build Status](https://travis-ci.org/eddyson-de/tapestry-react.svg?branch=master)](https://travis-ci.org/eddyson-de/tapestry-react)
[![Greenkeeper badge](https://badges.greenkeeper.io/eddyson-de/tapestry-react.svg)](https://greenkeeper.io/)
[![Join the chat at https://gitter.im/eddyson-de/tapestry-react](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/eddyson-de/tapestry-react?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Use React (http://facebook.github.io/react/index.html) together with Tapestry (http://tapestry.apache.org/).

This library provides basic integration for using JSX templates with Tapestry.

## Usage


### `build.gradle`:
```groovy
repositories {
  jcenter()
}

dependencies {
  runtime 'de.eddyson:tapestry-react:0.33.0'
}

```

That's it, now you can import modules written in JSX. Just give them the `.jsx(m)` extension and they will be compiled to JavaScript automatically.

### `/META-INF/modules/app/react-test.jsx`:
```javascript
define(['t5/core/dom', 'react', 'react-dom'], function(dom, React, ReactDOM) {
  var HelloMessage = React.createClass({
    render: function() {
      return <div>Hello {this.props.name}</div>;
    }
  });
  var mountNode = (dom('example')).element;
  ReactDOM.render(<HelloMessage name="John" />, mountNode);
});

```

## Components
You can also use the `ReactComponent` component to keep the components' code separate from the page code:

### `/META-INF/modules/app/react/HelloMessage.jsx`:
```javascript
define(['react'], function(React) {
  return React.createClass({
    render: function() {
      return <div>Hello {this.props.name}</div>;
    }
  });
});
```

### `/org/example/app/pages/ReactDemo.tml`:
```html
<html title="React Demo Index"
	xmlns:t="http://tapestry.apache.org/schema/tapestry_5_4.xsd"
	xmlns:r="tapestry-library:react">
	<div class="row">
		<div class="col-md-10 col-md-offset-1 col-lg-8 col-lg-offset-2">
			<r:reactcomponent module="app/react/HelloMessage" name="John"/>
		</div>
	</div>
</html>
```

## ECMAScript 6 modules => AMD
If you want to write your classes as ES6 rather than AMD moudules, just use the `.jsxm` file extension to switch the transpiler to AMD output.

### `/META-INF/modules/app/react/HelloMessage.jsxm`:
```javascript
import React from 'react'

export default class HelloMessage extends React.Component {

  constructor(props){
    super(props);  
  }
  
  render(){
    return (
      <div>Hello {this.props.name}!</div>
    )
  }
}

```
## Development code
If you want code to be executed only in development mode but not in production, you can use the `__DEV__` pseudo variable:
```javascript

if (__DEV__) {
  MyComponent.propTypes = {
    ...
  }
}

```
This will be compiled to `if (true)` or `if (false)` depending on the value of the `tapestry.production-mode` symbol.

## Standalone compiler
If you want to compile code outside of a Tapestry application (e.g. in your Gradle build), you can use the `de.eddyson.tapestry.react.StandaloneCompiler` and `de.eddyson.tapestry.react.readers.CompilingBabelReader` classes.


## Demo?
Unfortunately, there is no live demo available, but the test application can be examined by running `./gradlew runTestApp` and pointing your browser to `http://localhost:9040/`.

## Notes
### Speeding things up in production
Compiling templates can take some time. Combined with minification, this can quickly lead to Require.js timeouts in production.  
To speed things up, you can have the files pre-compiled and minified upon registry startup using https://github.com/eddyson-de/tapestry-minification-cache-warming.
### Calling server-side code
You will probably end up having a lot of React components that do not have an associated page class. If this is the case and you find yourself wanting a proper REST API rather than component- or page-level event handlers, have a look at https://github.com/tynamo/tapestry-resteasy.

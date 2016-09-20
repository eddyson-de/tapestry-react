# tapestry-react [![Build Status](https://travis-ci.org/eddyson-de/tapestry-react.svg?branch=master)](https://travis-ci.org/eddyson-de/tapestry-react)

[![Join the chat at https://gitter.im/eddyson-de/tapestry-react](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/eddyson-de/tapestry-react?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
Use React (http://facebook.github.io/react/index.html) together with Tapestry (http://tapestry.apache.org/).

This library provides basic integration for using JSX and CJSX templates with Tapestry.

## Usage


### `build.gradle`:
```groovy
respositories {
  jcenter()
}

dependencies {
  runtime 'de.eddyson:tapestry-react:0.12.3'
}

```

That's it, now you can import modules written in (C)JSX. Just give them the `.(c)jsx` extension and they will be compiled to JavaScript automatically.  
**Note: CJSX transformation may not work with React 0.14 and/or React 15 features.** 

### `/META-INF/modules/app/react-test.cjsx`:
```coffeescript
define ['t5/core/dom', 'react', 'react-dom'], (dom, React, ReactDOM)->
  HelloMessage = React.createClass
    render: -> <div>Hello {this.props.name}</div>
  mountNode = (dom 'example').element
  ReactDOM.render <HelloMessage name="John" />, mountNode
  return
```

## Components
You can also use the `ReactComponent` component to keep the components' code separate from the page code:

### `/META-INF/modules/app/react/HelloMessage.cjsx`:
```coffeescript
define ['react'], (React)->
  React.createClass
    render: -> <div>Hello {this.props.name}</div>

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

## Demo?
Unfortunately, there is no live demo available, but the test application can be examined by running `./gradlew runTestApp` and pointing your browser to `http://localhost:9040/`.

## Notes
### Speeding things up in production
Compiling templates can take some time, especially when using CJSX. Combined with minification, this can quickly lead to Require.js timeouts in production.  
To speed things up, you can have the files pre-compiled and minified upon registry startup using https://github.com/eddyson-de/tapestry-minification-cache-warming.
### Calling server-side code
You will probably end up having a lot of React components that do not have an associated page class. If this is the case and you find yourself wanting a proper REST API rather than component- or page-level event handlers, have a look at https://github.com/tynamo/tapestry-resteasy.

# tapestry-react [![Build Status](https://travis-ci.org/eddyson-de/tapestry-react.svg?branch=master)](https://travis-ci.org/eddyson-de/tapestry-react)
Use React (http://facebook.github.io/react/index.html) together with Tapestry (http://tapestry.apache.org/).

This library provides basic integration for using JSX and CJSX templates with Tapestry.

## Usage


### `build.gradle`:
```groovy
respositories {
  jcenter()
}

dependencies {
  runtime 'de.eddyson:tapestry-react:0.1.0'
}

```

That's it, now you can import modules written in (C)JSX. Just give them the `.(c)jsx` extension and they will be compiled to JavaScript automatically.

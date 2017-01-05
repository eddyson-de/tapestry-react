# r.js incompatibilities

Date: 2017-01-05

As of writing this, the r.js library in it's current version (2.3.2) has an incompatibility with nashorn (Oracle Java 8 1.8.0_111) interpreter:

This problematic code is starting in line 300 of r.js:

```javascript
/**
 * Helper function for iterating over an array. If the func returns
 * a true value, it will break out of the loop.
 */
function each(ary, func) {
    if (ary) {
        var i;
        for (i = 0; i < ary.length; i += 1) {
            if (ary[i] && func(ary[i], i, ary)) {
                break;
            }
        }
    }
}
```

This code will throw an AssertionError with the message 'duplicate code' when running with -ae (assertions enabled) as
soon as the require() function is invoked.

We needed to patch the function:

```javascript
/**
 * Helper function for iterating over an array. If the func returns
 * a true value, it will break out of the loop.
 */
function each(ary, func) {
    if (ary) {
        var i;
        for (i = 0; i < ary.length; i += 1) {
            if (ary[i] && func.apply(null, [ary[i], i, ary])) {
                break;
            }
        }
    }
}
```
The change is only syntactic, from `func(ary[i], i, ary)` to `func.apply(null, [ary[i], i, ary])`
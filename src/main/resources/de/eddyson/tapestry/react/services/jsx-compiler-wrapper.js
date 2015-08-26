function compileJSX(input, filename) {
    try {
        return { output: JSXTransformer.transform(input).code };
    }
    catch (err) {
        return { exception: err.toString() };
    }
}
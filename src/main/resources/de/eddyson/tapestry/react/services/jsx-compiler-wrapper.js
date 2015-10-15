function compileJSX(input, filename) {
    try {
        return { output: babel.transform(input, {ast: false}).code };
    }
    catch (err) {
        return { exception: err.toString() };
    }
}
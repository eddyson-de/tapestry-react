function compileJSX(input, filename, outputamd) {
    try {
        return { output: babel.transform(input, {filename: filename, compact: false, ast: false, modules: outputamd ? "amd": "common"}).code };
    }
    catch (err) {
        return { exception: err.toString() };
    }
}
import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';

export default {
    context: 'window',
    input: './src/main/resources/assets/app.js',
    output: {
        file: './src/main/resources/static/assets/bundle.js',
        format: 'umd',
    },
    plugins: [
        resolve(),
        commonjs()
    ]
};

import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';

export default {
    input: './src/main/resources/assets/app.js',
    output: {
        file: './src/main/resources/static/bundle.js',
        format: 'umd',
    },
    plugins: [
        resolve(),
        commonjs()
    ]
};

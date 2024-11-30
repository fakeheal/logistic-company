export default {
    input: './src/main/resources/assets/app.js',
    output: {
        file: './src/main/resources/static/bundle.js',
        format: 'umd',
    },
    external: ['flowbite'],
};
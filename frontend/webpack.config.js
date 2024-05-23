const CopyPlugin = require("copy-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const HtmlWebpackPlugin = require('html-webpack-plugin');

const path = require("path");
module.exports = {
    //devtool: 'source-map',
    output: {
        filename: 'react-app.js',
        //path: path.resolve(__dirname, '../backend/build/resources/main/static/dist'),
        path: path.resolve(__dirname, 'build'),
    },
    /*plugins: [
        new CopyPlugin({
          patterns: [
            { from: path.resolve("./build/static/css"), to: path.resolve("../backend/build/resources/main/static/css") },
            { from: path.resolve("./build/static/js"), to: path.resolve("../backend/build/resources/main/static/js") },
            { from: path.resolve("./build/static/media"), to: path.resolve("../backend/build/resources/main/static/media") },
          ],
        }),
      ],*/
    plugins: [
        new HtmlWebpackPlugin({
          template: "./src/index.html"
        })
      ],
    plugins: [new MiniCssExtractPlugin({
        filename: 'style.css',
    })],
    module: {
        rules: [{
            test: /\.js|\.jsx$/,
            exclude: /node_modules/,
            loader: "babel-loader",
            options: {
                presets: [
                '@babel/preset-env',
                ['@babel/preset-react', {"runtime": "automatic"}]
                ]
            }

        }, {
            test: /\.css$/i,
            //exclude: /node_modules/,
            //use: [MiniCssExtractPlugin.loader, "css-loader"], //Mini for production
            use: ["style-loader", "css-loader"],    //technically only for development mode
        }, {
            test: /\.svg$/,
            use: "file-loader",
        }, {
            test: /\.(png|jpg|jpeg|ico)$/i,
            type: 'asset/resource',
        }, {
            test: /\.tsx?$/,
            use: 'ts-loader',
            exclude: /node_modules/,
        },]
    },
    resolve: {
        extensions: ['.tsx', '.js', '.jsx']
    }
};
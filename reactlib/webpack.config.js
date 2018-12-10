var packageJSON = require('./package.json');
var path = require('path');
var webpack = require('webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const autoprefixer = require('autoprefixer');

const PATHS = {
	//  resource context root on Tomcat landing page will be 'webjar/packageName'
  build: path.join(__dirname, 'target', 'classes', 'META-INF', 'resources', 'webjars', packageJSON.name)
};

module.exports = {
	// root of the import tree	
	entry: './src/index.js',

  output: {
    path: PATHS.build,
    publicPath: '/assets/', // resource contex root on the dev-server landing page
    filename: 'app-bundle.js'
  },
  
  module: {
      rules: [
         {
            test: /\.jsx?$/,
            exclude: /node_modules/,
            loader: 'babel-loader',
				
            query: {
               presets: ['env', 'stage-0', 'react']
            }
         },
         {
        	  test: /\.scss$/,
        	  use: [
        		  'style-loader',
        		  MiniCssExtractPlugin.loader,
        		  'css-loader',
        		  {
        		  	loader: 'postcss-loader',  
        		  	options: {
        		  		plugins() {
        	                return [autoprefixer('last 2 version')];
        	            }
        		  	}
        		  },
        		  'resolve-url-loader',
        		  'sass-loader?sourceMap']
         },
         {
             test: /\.(jpe?g|png|gif|svg)$/i,
             use: [
                 {
                	 // This encodes the entire file in output css
                	 // We should be using file-loader for larger images but we do not have any as of yet.
                	 // Additional steps to ensure that the file path is resolved in tomcat deploy
                	 loader: 'url-loader',
                	 options: {
                	   limit: 10000
                	}
                 }
             ]
         },
         {
             test: /.(ttf|otf|eot|svg|woff(2)?)(\?[a-z0-9]+)?$/,
             use: [{
               loader: 'file-loader',
               options: {
                 name: '[name].[ext]',
                 outputPath: 'fonts/',    // where the fonts will go
                 publicPath: '/assets/fonts'       // override the default path
               }
             }]
          }
      ]
   },
   
   plugins: [
	   new MiniCssExtractPlugin({
		      filename: 'style.css'
		    })
	],
   
   devServer: {
	    port: 3000,
	    proxy: {
	        '/delegate': {
	          target: 'http://localhost:8080',
	          secure: false
	        }
	      }
   },
   
   devtool: 'source-map'
};

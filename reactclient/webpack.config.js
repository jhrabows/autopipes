var packageJSON = require('./package.json');
var path = require('path');
const fs = require('fs');
var webpack = require('webpack');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const autoprefixer = require('autoprefixer');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

const PATHS = {
	//  resource context root on Tomcat landing page will be 'webjars/packageName'
  build: path.join(__dirname, 'target', 'classes', 'META-INF', 'resources', 'webjars', packageJSON.name)
};

// actual path derived from symbolic link
const sharedServicePath = fs.realpathSync(
		path.resolve(__dirname, 'node_modules/shared-service')
);


module.exports = {
	// root of the import tree	
	entry: './src/index.js',
	
	resolve: {
		modules: [__dirname, 'node_modules'],
		alias:{
		    react: path.join('./node_modules', 'react'),
		    'react-dom': path.join('./node_modules', 'react-dom'),
		    'shared-service': sharedServicePath
		}
	},

  output: {
    path: PATHS.build,
    publicPath: '/assets/', // resource context root on the node server landing page
    filename: 'app-bundle.js'
  },
  
  module: {
      rules: [
         {
            test: /\.jsx?$/,
            loader: 'babel-loader',
            // code that requires babel: src and anything in node_modules that is not already compiled by babel
            include: [
            	path.resolve(__dirname, "src"),
            	sharedServicePath
            ],
				
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
                 publicPath: './fonts'       // override the default path
               }
             }]
          }
      ]
   },
   
   optimization: {
	   minimizer: [
		   new UglifyJsPlugin({
			      uglifyOptions:{
			        output: {
			        	ascii_only: true
			        }
			      }
			})
		]
   },
   
   plugins: [
	   new MiniCssExtractPlugin({
			// name of the minified css
			filename: 'style.css'
		}),
		// stripping unused moment locale
		new webpack.ContextReplacementPlugin(
		  /moment[\/\\]locale$/,
		  /en-us/
		)
	],
   
   devServer: {
	    port: 3000,
	    proxy: {
	        '/service': {
	          target: 'http://localhost:9090', // location of application server
	          secure: false
	        }
	      }
   },
   
   devtool: 'source-map'
};

const colors = require('colors');
const webLogger = require('../lib/logger');

exports.red = (str) => {
  webLogger.info(str);
  console.log(colors.red(str));
};

exports.green = (str) => {
  webLogger.info(str);
  console.log(colors.green(str));
};

exports.yellow = (str) => {
  webLogger.info(str);
  console.log(colors.yellow(str));
};

exports.grey = (str) => {
  webLogger.info(str);
  console.log(colors.grey(str));
};

exports.gray = (str) => {
  webLogger.info(str);
  console.log(colors.gray(str));
};

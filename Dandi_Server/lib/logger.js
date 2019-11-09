const Winston = require('winston');
const WinstonDaily = require('winston-daily-rotate-file');

module.exports = Winston.createLogger({
  level: 'info',
  showLevel: true,
  format: Winston.format.combine(
    Winston.format.json(),
    Winston.format.timestamp({
      format: 'YYYY-MM-DD HH:mm:ss',
    }),
    Winston.format.simple(),
  ),
  transports: [
    new Winston.transports.File({
      name: 'info-file',
      filename: './logs/All-Log.log',
    }),

    new WinstonDaily({
      name: 'info-file',
      filename: './logs/Date/Log_%DATE%.log',
      datePattern: 'YYYY-MM-DD',
    }),
  ],
});

const router = require('express').Router();
const webLogCtrl = require('./webLog.ctrl');

router.get('/', webLogCtrl.getLog);

module.exports = router;

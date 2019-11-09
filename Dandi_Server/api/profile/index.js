const router = require('express').Router();
const tokenMiddleware = require('../../middleware/auth'); // middleware
const imageCtrl = require('./profile.ctrl');

router.get('/', tokenMiddleware, imageCtrl.getProfile);

module.exports = router;

const router = require('express').Router();

router.use('/auth', require('./auth'));
router.use('/admin-page', require('./adminPage'));
router.use('/channel', require('./channel'));
router.use('/channel-admin', require('./channelAdmin'));
router.use('/school', require('./school'));
router.use('/channel-event', require('./channelEvent'));
router.use('/profile', require('./profile'));
router.use('/image', require('./image'));
router.use('/weblog', require('../webLog'));

module.exports = router;

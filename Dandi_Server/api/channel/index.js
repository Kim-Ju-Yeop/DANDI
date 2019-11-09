const router = require('express').Router();
const tokenMiddleware = require('../../middleware/auth'); // middleware
const channelCtrl = require('./channel.ctrl');

router.get('/', tokenMiddleware, channelCtrl.checkChannel);
router.post('/add', tokenMiddleware, channelCtrl.addChannel);
router.get('/search', tokenMiddleware, channelCtrl.searchChannel);
router.get('/join', tokenMiddleware, channelCtrl.joinChannel);
router.delete('/delete', tokenMiddleware, channelCtrl.deleteChannel);
router.delete('/leave', tokenMiddleware, channelCtrl.leaveChannel);
router.get('/info', tokenMiddleware, channelCtrl.channelInfo);

module.exports = router;

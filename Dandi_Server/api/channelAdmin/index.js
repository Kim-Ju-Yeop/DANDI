const router = require('express').Router();
const tokenMiddleware = require('../../middleware/auth'); // middleware
const channelAdminCtrl = require('./channelAdmin.ctrl');

router.get('/', tokenMiddleware, channelAdminCtrl.createChannel);
router.get('/await', tokenMiddleware, channelAdminCtrl.getAwaitUser);
router.get('/allow', tokenMiddleware, channelAdminCtrl.allowUser);
router.delete('/reject', tokenMiddleware, channelAdminCtrl.rejectUser);
router.put('/update', tokenMiddleware, channelAdminCtrl.updateChannel);
router.delete('/forced-exit', tokenMiddleware, channelAdminCtrl.forceExit);

module.exports = router;

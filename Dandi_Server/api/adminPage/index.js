const router = require('express').Router();
const tokenMiddleware = require('../../middleware/auth'); // middleware
const adminPageCtrl = require('./adminPage.ctrl');

router.get('/', tokenMiddleware, adminPageCtrl.getUserData);
router.get('/await', tokenMiddleware, adminPageCtrl.getAwaitUser);
router.get('/allow', tokenMiddleware, adminPageCtrl.allowUser);
router.delete('/reject', tokenMiddleware, adminPageCtrl.rejectUser);

module.exports = router;

const router = require('express').Router();
const authCtrl = require('./auth.ctrl');

router.post('/login', authCtrl.login);
router.post('/sign-up', authCtrl.signUp);
router.post('/sign-up/is-overlapped', authCtrl.getIsOverlapped);
router.post('/sign-up/email', authCtrl.sendEmailAuth);

module.exports = router;

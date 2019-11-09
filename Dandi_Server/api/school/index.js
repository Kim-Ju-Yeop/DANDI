const router = require('express').Router();
const tokenMiddleware = require('../../middleware/auth'); // middleware
const schoolCtrl = require('./school.ctrl');

router.get('/search-school', schoolCtrl.searchByName);
router.get('/search-class', schoolCtrl.getClassInfo);
router.get('/events', tokenMiddleware, schoolCtrl.getSchoolEvent);

module.exports = router;

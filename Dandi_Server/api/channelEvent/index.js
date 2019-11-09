const router = require('express').Router();
const tokenMiddleware = require('../../middleware/auth');
const channelEvent = require('./channelEvent.ctrl');

router.get('/', tokenMiddleware, channelEvent.getChannelEvent);
router.post('/add', tokenMiddleware, channelEvent.addEvent);
router.delete('/delete', tokenMiddleware, channelEvent.deleteEvent);
router.put('/update', tokenMiddleware, channelEvent.updateEvent);
router.get('/search', tokenMiddleware, channelEvent.searchEvent);

module.exports = router;

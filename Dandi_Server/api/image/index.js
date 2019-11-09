const router = require('express').Router();
const multer = require('multer');
const tokenMiddleware = require('../../middleware/auth'); // middleware
const imageCtrl = require('./image.ctrl');

const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, './public/image/');
  },
  filename: (req, file, cb) => {
    cb(null, `${file.fieldname}_${Date.now()}_${file.originalname}`);
  },
});

const upload = multer({ storage });

router.post('/upload/profile', tokenMiddleware, upload.single('profile_pic'), imageCtrl.uploadProfile);
router.post('/upload/thumbnail', tokenMiddleware, upload.single('thumbnail'), imageCtrl.uploadThumbnail);

module.exports = router;

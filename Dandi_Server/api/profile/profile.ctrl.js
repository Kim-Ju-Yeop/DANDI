const models = require('../../models');
const colorConsole = require('../../lib/console');
const { searchById } = require('../school/school.ctrl');
const { getProfileUrl } = require('../image/image.ctrl');

exports.getProfile = async (req, res) => {
  colorConsole.green('[profile] 프로필 조회');
  const { user } = req;
  const { user_id: userId } = req.query; // querystring (user_id : other_id)

  if (!userId) { // get my profile
    try {
      const userInfo = await models.User.getUser(user.user_id);

      searchById(userInfo.school)
        .then(async (schoolInfo) => {
          userInfo.school = schoolInfo;
          userInfo.profile_pic = await getProfileUrl(req, userInfo.user_id);
          colorConsole.gray('<response>');
          colorConsole.gray({ userInfo });

          return res.status(200).json({
            status: 200,
            message: '프로필 조회에 성공하였습니다.',
            data: { userInfo },
          });
        })
        .catch(async (err) => {
          if (err.status === 404) {
            colorConsole.yellow(err.message);
            return res.status(404).json({
              status: 404,
              message: err.message,
            });
          }
          colorConsole.red(err.message);
          return res.status(500).json({
            status: 500,
            message: '프로필 조회에 실패하였습니다.',
          });
        });
    } catch (err) {
      colorConsole.red(err.message);
      return res.status(500).json({
        status: 500,
        message: '프로필 조회에 실패하였습니다.',
      });
    }
  } else {
    // get other profile
    try {
      colorConsole.gray('<request>');
      colorConsole.gray({ user_id: userId });

      const userInfo = await models.User.getUser(userId);

      if (!userInfo) {
        colorConsole.yellow('[profile] 유저 정보가 존재하지 않습니다.');
        return res.status(404).json({
          status: 404,
          message: '유저 정보가 존재하지 않습니다.',
        });
      }

      searchById(userInfo.school)
        .then(async (schoolInfo) => {
          userInfo.school = schoolInfo;
          userInfo.profile_pic = await getProfileUrl(req, userId);

          if (userInfo.school.school_code !== user.school) {
            colorConsole.yellow('[profile] 다른 학교 유저입니다.');
            return res.status(403).json({
              status: 403,
              message: '다른 학교 유저입니다.',
            });
          }

          if (!userInfo.isPublic) {
            delete userInfo.user_phone;
            delete userInfo.user_email;
          }

          colorConsole.gray('<response>');
          colorConsole.gray({ userInfo });

          return res.status(200).json({
            status: 200,
            message: '프로필 조회에 성공하였습니다.',
            data: { userInfo },
          });
        })
        .catch(async (err) => {
          if (err.status === 404) {
            colorConsole.yellow(err.message);
            return res.status(404).json({
              status: 404,
              message: err.message,
            });
          }
          colorConsole.red(err.message);
          return res.status(500).json({
            status: 500,
            message: '프로필 조회에 실패하였습니다.',
          });
        });
    } catch (err) {
      colorConsole.red(err.message);
      return res.status(500).json({
        status: 500,
        message: '프로필 조회에 실패하였습니다.',
      });
    }
  }

  return true;
};

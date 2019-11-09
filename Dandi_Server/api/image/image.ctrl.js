const fs = require('fs');
const path = require('path');
const models = require('../../models');
const colorConsole = require('../../lib/console');
const imageInfo = require('../../config/imageInfo');

exports.uploadProfile = async (req, res) => {
  colorConsole.green('[image] 프로필 이미지 업로드');
  const { user } = req;

  colorConsole.gray('<request>');
  colorConsole.gray({ file: req.file });

  if (!req.file) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  try {
    await models.User.updateProfile(user.user_id, req.file.filename);
    return res.status(200).json({
      status: 200,
      message: '프로필 사진 업로드에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '프로필 사진 업로드에 실패하였습니다.',
    });
  }
};

exports.uploadThumbnail = async (req, res) => {
  colorConsole.green('[image] 채널 이미지 업로드');
  const { user } = req;
  const { channel_id: channelId } = req.query; // querystring(channel_id : upload channel)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_id: channelId, file: req.file });

  if (!(req.file && channelId)) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  try {
    if (!await models.Channel.getIsFounder(user.user_id, channelId)) {
      colorConsole.yellow('[image] 채널 이비지 업로드 권한이 없습니다.');
      return res.status(403).json({
        status: 403,
        message: '채널 이비지 업로드 권한이 없습니다.',
      });
    }

    await models.Channel.updateThumbnail(channelId, req.file.filename);
    return res.status(200).json({
      status: 200,
      message: '채널 이미지 업로드에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 이미지 업로드에 실패하였습니다.',
    });
  }
};

exports.getProfileUrl = async (req, userId) => {
  const user = await models.User.getUser(userId);
  let { profile_pic: profilePic } = user;

  if (!fs.existsSync(path.join(__dirname, `../../public/image/${profilePic}`))) {
    profilePic = null;
  }

  let profileUrl;
  if (!profilePic) {
    profileUrl = `${req.origin}/static/image/${imageInfo.basic_profile}`;
  } else {
    profileUrl = `${req.origin}/static/image/${profilePic}`;
  }

  return profileUrl;
};

exports.getThumbnailUrl = async (req, channelId) => {
  let { thumbnail } = await models.Channel.getChannel(channelId);

  if (!fs.existsSync(path.join(__dirname, `../../public/image/${thumbnail}`))) {
    thumbnail = null;
  }

  let thumbnailUrl;
  if (!thumbnail) {
    thumbnailUrl = `${req.origin}/static/image/${imageInfo.basic_thumbnail}`;
  } else {
    thumbnailUrl = `${req.origin}/static/image/${thumbnail}`;
  }

  return thumbnailUrl;
};

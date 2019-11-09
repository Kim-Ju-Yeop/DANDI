const models = require('../../models');
const colorConsole = require('../../lib/console');
const Validation = require('../../lib/validation');
const { getThumbnailUrl } = require('../image/image.ctrl');
const { getProfileUrl } = require('../image/image.ctrl');

exports.addChannel = async (req, res) => {
  colorConsole.green('[channel] 채널 추가');
  const { body, user } = req;

  body.school_id = user.school;
  body.create_user = user.user_id;

  colorConsole.gray('<request>');
  colorConsole.gray({ body });
  let createdChannel;

  try {
    await Validation.validateAddChannel(body);
  } catch (err) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다',
    });
  }

  try {
    const channelExist = await models.Channel.getChannelForCreate(body.school_id, body.name);

    if (channelExist) {
      colorConsole.yellow('[channel] 이미 채널이 존재합니다.');
      return res.status(409).json({
        status: 409,
        message: '이미 채널이 존재합니다.',
      });
    }

    createdChannel = await models.Channel.createChannel(body);
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 개설에 실패하였습니다.',
    });
  }

  try {
    await models.ChannelUser.joinChannel({
      user_id: user.user_id,
      channel_id: createdChannel.id,
      isAllowed: true,
      pushNotify: false,
    });

    return res.status(200).json({
      status: 200,
      message: '채널 개설에 성공하였습니다.',
      data: { createdChannel },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 개설에 실패하였습니다.',
    });
  }
};

exports.deleteChannel = async (req, res) => {
  colorConsole.green('[channel] 채널 삭제');
  const { user } = req;
  const { channel_id: channelId } = req.query; // querystring (channel_id : delete channel id)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_id: channelId });

  if (!channelId) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  try {
    const channel = await models.Channel.getChannel(channelId);

    if (!channel) {
      colorConsole.yellow('[channel] 채널 정보가 존재하지 않습니다.');
      return res.status(404).json({
        status: 404,
        message: '채널 정보가 존재하지 않습니다.',
      });
    }

    if (channel.create_user !== user.user_id) {
      colorConsole.yellow('[channel] 삭제 권한이 없습니다.');
      return res.status(403).json({
        status: 403,
        message: '삭제 권한이 없습니다.',
      });
    }

    await models.Channel.deleteChannel(channelId);
    return res.status(200).json({
      status: 200,
      message: '채널 삭제에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 삭제에 실패하였습니다.',
    });
  }
};

exports.joinChannel = async (req, res) => {
  colorConsole.green('[channel] 채널 가입');
  const { user } = req;
  const { channel_id: channelId } = req.query; // querystring (channel_id : join channel id)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_id: channelId });

  if (!channelId) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다',
    });
  }

  try {
    const channelUser = await models.ChannelUser.getUserStatus(user.user_id, channelId);

    if (channelUser) {
      colorConsole.yellow('[channel] 이미 가입된 채널입니다.');
      return res.status(409).json({
        status: 409,
        message: '이미 가입된 채널입니다.',
      });
    }

    const channel = await models.Channel.getChannel(channelId);
    const channelPublic = channel.isPublic;

    await models.ChannelUser.joinChannel({
      user_id: user.user_id,
      channel_id: channelId,
      isAllowed: channelPublic,
      pushNotify: user.pushNotify,
    });

    return res.status(200).json({
      status: 200,
      message: '채널 가입에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 가입에 실패하였습니다.',
    });
  }
};

exports.leaveChannel = async (req, res) => {
  colorConsole.green('[channel] 채널 탈퇴');
  const { user } = req;
  const { channel_id: channelId } = req.query; // querystring (channel_id : leave channel id)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_id: channelId });

  if (!channelId) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다',
    });
  }

  try {
    const channel = await models.Channel.getChannel(channelId);

    if (!channel) {
      colorConsole.yellow('[channel] 채널 정보가 존재하지 않습니다.');
      return res.status(404).json({
        status: 404,
        message: '채널 정보가 존재하지 않습니다.',
      });
    }

    if (channel.create_user === user.user_id) {
      colorConsole.yellow('[channel] 탈퇴가 불가능한 유저입니다.');
      return res.status(403).json({
        status: 403,
        message: '탈퇴가 불가능한 유저입니다.',
      });
    }
    await models.ChannelUser.leaveChannel(user.user_id, channelId);
    return res.status(200).json({
      status: 200,
      message: '채널 탈퇴에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 탈퇴에 실패하였습니다',
    });
  }
};

exports.searchChannel = async (req, res) => {
  colorConsole.green('[channel] 채널 검색');
  const { user } = req;
  const { channel_name: channelName } = req.query; // querystring (channel_name : channel name)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_name: channelName });

  try {
    let channels;

    if (!channelName) {
      channels = await models.Channel.getChannelBySchool(user.school);
    } else {
      channels = await models.Channel.getChannelByNameAndSchool(channelName, user.school);
    }

    if (!channels.length) {
      colorConsole.yellow('[channel] 채널 정보가 존재하지 않습니다.');
      return res.status(204).json({
        status: 204,
        message: '채널 정보가 존재하지 않습니다.',
      });
    }

    for (let i = 0; i < channels.length; i += 1) {
      // eslint-disable-next-line no-await-in-loop
      const userStatus = await models.ChannelUser.getUserStatus(user.user_id, channels[i].id);

      if (!userStatus) {
        channels[i].userStatus = 0;
      } else if (!userStatus.isAllowed) {
        channels[i].userStatus = 1;
      } else {
        channels[i].userStatus = 2;
      }
      // eslint-disable-next-line no-await-in-loop
      channels[i].thumbnail = await getThumbnailUrl(req, channels[i].id);
      // eslint-disable-next-line no-await-in-loop
      channels[i].createUser = await models.User.getUserData(channels[i].create_user);
      // eslint-disable-next-line no-await-in-loop
      channels[i].createUser.profile_pic = await getProfileUrl(req, channels[i].createUser.user_id);
    }

    colorConsole.gray('<response>');
    colorConsole.gray({ channels });

    return res.status(200).json({
      status: 200,
      message: '채널 조회에 성공하였습니다.',
      data: { channels },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 조회에 실패하였습니다.',
    });
  }
};

exports.channelInfo = async (req, res) => {
  colorConsole.green('[channel] 채널 정보');
  const { channel_id: channelId } = req.query; // querystring (channel_id : search channel id)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_id: channelId });

  if (!channelId) {
    colorConsole.yellow('[channel] 검증 오류입니다.');
    return res.status(400).json({ status: 400, message: '검증 오류입니다.' });
  }

  try {
    const channelInfo = await models.Channel.getChannel(channelId);

    if (!channelInfo) {
      colorConsole.yellow('[channel] 채널 정보가 존재하지 않습니다.');
      return res.status(404).json({
        status: 404,
        message: '[channel] 채널 정보가 존재하지 않습니다.',
      });
    }

    channelInfo.thumbnail = await getThumbnailUrl(req, channelId);
    channelInfo.users = await models.ChannelUser.getChannelUserByChannel(channelId);

    for (let i = 0; i < channelInfo.users.length; i += 1) {
      // eslint-disable-next-line no-await-in-loop
      channelInfo.users[i] = await models.User.getUserData(channelInfo.users[i].user_id);
      // eslint-disable-next-line no-await-in-loop
      channelInfo.users[i].profile_pic = await getProfileUrl(req, channelInfo.users[i].user_id);
    }

    colorConsole.gray('<response>');
    colorConsole.gray({ channelInfo });
    return res.status(200).json({
      status: 200,
      message: '채널 정보 조회에 성공하였습니다.',
      data: { channelInfo },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '채널 정보 조회에 실패하였습니다.',
    });
  }
};

exports.checkChannel = async (req, res) => {
  colorConsole.green('[channel] 가입 채널 조회');
  const { user } = req;

  try {
    const joinedChannel = await models.ChannelUser.getChannelByAllowedUser(user.user_id);

    if (!joinedChannel.length) {
      colorConsole.yellow('[channel] 가입 채널이 존재하지 않습니다.');
      return res.status(204).json({
        status: 204,
        message: '가입 채널이 존재하지 않습니다.',
      });
    }

    for (let i = 0; i < joinedChannel.length; i += 1) {
      // eslint-disable-next-line no-await-in-loop
      joinedChannel[i] = await models.Channel.getChannel(joinedChannel[i].channel_id);
      // eslint-disable-next-line no-await-in-loop
      joinedChannel[i].thumbnail = await getThumbnailUrl(req, joinedChannel[i].id);
    }

    colorConsole.gray('<response>');
    colorConsole.gray({ joinedChannel });

    return res.status(200).json({
      status: 200,
      message: '가입 채널 조회에 성공하였습니다.',
      data: { joinedChannel },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '가입 채널 조회에 실패하였습니다.',
    });
  }
};

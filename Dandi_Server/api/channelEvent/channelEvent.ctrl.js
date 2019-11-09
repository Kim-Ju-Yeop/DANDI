const models = require('../../models');
const colorConsole = require('../../lib/console');
const Validation = require('../../lib/validation');
const { getThumbnailUrl } = require('../image/image.ctrl');

exports.getChannelEvent = async (req, res) => {
  colorConsole.green('[channelEvent] 일정 조회');
  const { user } = req;
  const { channel_id: channelId } = req.query; // querystring (channel_id : get events channel)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_id: channelId });

  try {
    let events = [];
    if (!channelId) {
      const joinedChannel = await models.ChannelUser.getChannelByAllowedUser(
        user.user_id,
      );

      for (let i = 0; i < joinedChannel.length; i += 1) {
        events = events.concat(
          // eslint-disable-next-line no-await-in-loop
          await models.ChannelEvent.getEventByChannel(joinedChannel[i].channel_id),
        );
      }
    } else {
      if (!(await models.ChannelUser.getIsMember(user.user_id, channelId))) {
        colorConsole.yellow('[channelEvent] 일정 조회 권한이 없습니다.');
        return res.status(403).json({
          status: 403,
          message: '일정 조회 권한이 없습니다.',
        });
      }
      events = await models.ChannelEvent.getEventByChannel(channelId);
    }

    for (let i = 0; i < events.length; i += 1) {
      // eslint-disable-next-line no-await-in-loop
      const userInfo = await models.User.getUserData(events[i].author);
      // eslint-disable-next-line no-await-in-loop
      const channelInfo = await models.Channel.getChannel(events[i].channel_id);
      // eslint-disable-next-line no-await-in-loop
      channelInfo.thumbnail = await getThumbnailUrl(req, events[i].channel_id);

      events[i].channel = channelInfo;
      events[i].author = {
        user_id: userInfo.user_id,
        user_name: userInfo.user_name,
      };
      delete events[i].channel_id;
    }

    colorConsole.gray('<response>');
    colorConsole.gray({ events });

    return res.status(200).json({
      status: 200,
      message: '일정 조회에 성공하였습니다.',
      data: { events },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '일정 조회에 실패하였습니다.',
    });
  }
};

exports.addEvent = async (req, res) => {
  colorConsole.green('[channelEvent] 일정 추가');
  const { user, body } = req;
  const { channel_id: channelId } = req.query; // querystring(channel_id : event channel_id)

  body.author = user.user_id;
  body.channel_id = channelId;

  colorConsole.gray('<request>');
  colorConsole.gray({ body });

  try {
    if (!channelId) {
      throw new Error();
    }

    await Validation.validateAddEvent(body);
  } catch (err) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  try {
    if (!(await models.ChannelUser.getIsMember(user.user_id, channelId))) {
      colorConsole.yellow('[channelEvent] 일정 추가 권한이 없습니다.');
      return res.status(403).json({
        status: 403,
        message: '일정 추가 권한이 없습니다.',
      });
    }

    await models.ChannelEvent.createEvent(body);
    return res.status(200).json({
      status: 200,
      message: '일정 추가에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '일정 추가에 실패하였습니다.',
    });
  }
};

exports.deleteEvent = async (req, res) => {
  colorConsole.green('[channelEvent] 일정 삭제');
  const { user } = req;
  const { event_id: eventId } = req.query; // querystring (event_id : delete event_id)

  colorConsole.gray('<request>');
  colorConsole.gray({ event_id: eventId });

  if (!eventId) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  try {
    const { channel_id: channelId } = await models.ChannelEvent.getEvent(eventId);

    if (!(await models.ChannelUser.getIsMember(user.user_id, channelId))) {
      colorConsole.yellow('[channelEvent] 일정 삭제 권한이 없습니다.');
      return res.status(403).json({
        status: 403,
        message: '일정 삭제 권한이 없습니다.',
      });
    }

    await models.ChannelEvent.deleteEvent(eventId);

    return res.status(200).json({
      status: 200,
      message: '일정 삭제에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '일정 삭제에 실패하였습니다.',
    });
  }
};

exports.updateEvent = async (req, res) => {
  colorConsole.green('[channelEvent] 일정 변경');
  const { user, body } = req;
  const { event_id: eventId } = req.query; // querystirng(event_id : update event id)
  body.author = user.user_id;

  colorConsole.gray('<request>');
  colorConsole.gray({ event_id: eventId, body });

  try {
    if (!eventId) {
      colorConsole.yellow('검증 오류입니다.');
      return res.status(400).json({
        status: 400,
        message: '검증 오류입니다.',
      });
    }
    const event = await models.ChannelEvent.getEvent(eventId);
    body.start_date = body.start_date || event.start_date;
    body.end_date = body.end_date || event.end_date;
    await Validation.validateUpdateEvent(body);
  } catch (err) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  try {
    const { channel_id: channelId } = await models.ChannelEvent.getEvent(eventId);

    if (!(await models.ChannelUser.getIsMember(user.user_id, channelId))) {
      colorConsole.yellow('[channelEvent] 일정 변경 권한이 없습니다.');
      return res.status(403).json({
        status: 403,
        message: '일정 변경 권한이 없습니다.',
      });
    }

    await models.ChannelEvent.updateEvent(eventId, body);
    return res.status(200).json({
      status: 200,
      message: '일정 변경에 성공하였습니다.',
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '일정 변경에 실패하였습니다.',
    });
  }
};

exports.searchEvent = async (req, res) => {
  colorConsole.green('[channelEvent] 일정 검색');
  const { user } = req;
  const {
    channel_id: channelId,
    keyword,
  } = req.query; // querystring (channel_id : search channel_id, keyword : search keyword)

  colorConsole.gray('<request>');
  colorConsole.gray({ channel_id: channelId, keyword });

  if (!(channelId && keyword)) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다',
    });
  }

  try {
    if (!(await models.ChannelUser.getIsMember(user.user_id, channelId))) {
      colorConsole.yellow('[channelEvent] 일정 검색 권한이 없습니다.');
      return res.status(403).json({
        status: 403,
        message: '일정 검색 권한이 없습니다.',
      });
    }

    const events = await models.ChannelEvent.getEventByChannelAndKeyword(
      channelId,
      keyword,
    );

    if (!events.length) {
      colorConsole.yellow('[channelEvent] 검색 결과가 존재하지 않습니다.');
      return res.status(204).json({
        status: 204,
        message: '검색 결과가 존재하지 않습니다.',
      });
    }

    for (let i = 0; i < events.length; i += 1) {
      // eslint-disable-next-line no-await-in-loop
      const userInfo = await models.User.getUserData(events[i].author);
      // eslint-disable-next-line no-await-in-loop
      const channelInfo = await models.Channel.getChannel(events[i].channel_id);
      // eslint-disable-next-line no-await-in-loop
      channelInfo.thumbnail = await getThumbnailUrl(req, events[i].channel_id);

      events[i].channel = channelInfo;
      events[i].author = {
        user_id: userInfo.user_id,
        user_name: userInfo.user_name,
      };
      delete events[i].channel_id;
    }

    colorConsole.gray('<response>');
    colorConsole.gray({ events });

    return res.status(200).json({
      status: 200,
      message: '일정 검색에 성공하였습니다.',
      data: { events },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '일정 검색에 실패하였습니다.',
    });
  }
};

const models = require('../../models');
const email = require('../../lib/email');
const colorConsole = require('../../lib/console');
const { getProfileUrl } = require('../image/image.ctrl');

exports.getUserData = async (req, res) => {
  colorConsole.green('[adminPage] 가입 유저 조회');
  const { user } = req;

  if (user.permission !== 0) {
    colorConsole.yellow('[adminPage] 조회 권한이 없습니다.');
    return res.status(403).json({
      status: 403,
      message: '조회 권한이 없습니다.',
    });
  }
  try {
    const userData = await models.User.getAllowedUser();

    if (!userData.length) {
      colorConsole.yellow('[adminPage] 가입된 유저가 없습니다.');
      return res.status(204).json({
        status: 204,
        message: '가입된 유저가 없습니다.',
      });
    }

    for (let i = 0; i < userData.length; i += 1) {
      // eslint-disable-next-line no-await-in-loop
      userData[i].profile_pic = await getProfileUrl(req, userData[i].user_id);
    }
    return res.status(200).json({
      status: 200,
      message: '가입된 유저 조회에 성공하였습니다.',
      data: { userData },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '가입된 유저 조회에 실패하였습니다.',
    });
  }
};

exports.getAwaitUser = async (req, res) => {
  colorConsole.green('[adminPage] 승인대기 유저 조회');
  const { user } = req;

  if (user.permission !== 0) {
    colorConsole.yellow('[adminPage] 조회 권한이 없습니다.');
    return res.status(403).json({
      status: 403,
      message: '조회 권한이 없습니다.',
    });
  }

  try {
    const awaitUsers = await models.User.getAwaitUser();

    if (!awaitUsers.length) {
      colorConsole.yellow('[adminPage] 승인대기 유저가 존재하지 않습니다');
      return res.status(204).json({
        status: 204,
        message: '승인대기 유저가 존재하지 않습니다',
      });
    }

    for (let i = 0; i < awaitUsers.length; i += 1) {
      // eslint-disable-next-line no-await-in-loop
      awaitUsers[i].profile_pic = await getProfileUrl(req, awaitUsers[i].user_id);
    }

    colorConsole.gray('<response>');
    colorConsole.gray({ awaitUsers });
    return res.status(200).json({
      status: 200,
      message: '승인대기 유저 조회에 성공하였습니다.',
      data: { awaitUsers },
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '승인대기 유저 조회에 실패하였습니다',
    });
  }
};

exports.allowUser = async (req, res) => {
  colorConsole.green('[adminPage] 승인대기 유저 승인');
  const { user } = req;
  const { allow_id: allowId } = req.query; // querystring (user_id : allow user id)

  colorConsole.gray('<request>');
  colorConsole.gray({ allow_id: allowId });

  if (!allowId) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다',
    });
  }

  const allowUser = await models.User.getUser(allowId);

  if (user.permission !== 0) {
    colorConsole.yellow('[adminPage] 승인 권한이 없습니다.');
    return res.status(403).json({
      status: 403,
      message: '승인 권한이 없습니다.',
    });
  }

  if (!allowUser) {
    colorConsole.yellow('[adminPage] 가입신청이 존재하지 않습니다.');
    return res.status(404).json({
      status: 404,
      message: '가입 신청이 존재하지 않습니다',
    });
  }

  try {
    await models.User.allowUser(allowId);
    await email.sendEmail(allowUser.user_email, '[단디] 회원가입이 승인되었습니다.', email.getAllowForm());
    return res.status(200).json({
      status: 200,
      message: '회원가입 승인에 성공하였습니다.',
    });
  } catch (err) {
    if (err.message === 'No recipients defined') {
      return res.status(200).json({
        status: 200,
        message: '회원가입 승인에 성공하였습니다.',
      });
    }
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '회원가입 승인에 실패하였습니다.',
    });
  }
};

exports.rejectUser = async (req, res) => {
  colorConsole.green('[adminPage] 승인대기 유저 거절');
  const { user } = req;
  const { reject_id: rejectId } = req.query; // querystring (user_id : reject user id)

  colorConsole.gray('<request>');
  colorConsole.gray({ reject_id: rejectId });

  if (!rejectId) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다',
    });
  }

  const rejectUser = await models.User.getUser(rejectId);

  if (user.permission !== 0) {
    colorConsole.yellow('[adminPage] 거절 권한이 없습니다.');
    return res.status(403).json({
      status: 403,
      message: '거절 권한이 없습니다.',
    });
  }

  if (!rejectUser) {
    colorConsole.yellow('[adminPage] 가입신청이 존재하지 않습니다.');
    return res.status(404).json({
      status: 404,
      message: '가입신청이 존재하지 않습니다.',
    });
  }

  try {
    await models.User.rejectUser(rejectId);
    await email.sendEmail(rejectUser.user_email, '[단디] 회원가입이 거절되었습니다.', email.getRejectForm());
    return res.status(200).json({
      status: 200,
      message: '회원가입 거절이 완료되었습니다.',
    });
  } catch (err) {
    if (err.message === 'No recipients defined') {
      colorConsole.yellow('이메일이 존재하지 않습니다.');
      return res.status(200).json({
        status: 200,
        message: '회원가입 거절에 성공하였습니다.',
      });
    }
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '회원가입 거절에 실패하였습니다.',
    });
  }
};

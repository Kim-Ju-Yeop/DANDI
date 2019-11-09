require('dotenv').config();

const request = require('request-promise');
const colorConsole = require('../../lib/console');
const neisUrl = require('../../lib/neis');

exports.searchByName = async (req, res) => {
  colorConsole.green('[school] 학교 조회');
  const { school_name: schoolName } = req.query;
  const url = neisUrl({
    subUrl: 'schoolInfo',
    schoolName: encodeURI(schoolName),
  });
  let schoolInfo;

  colorConsole.gray('<request>');
  colorConsole.gray({ school_name: schoolName });

  if (!schoolName) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  await request(url, (error, response, _schoolInfo) => {
    try {
      if (error) {
        throw error;
      }

      _schoolInfo = JSON.parse(_schoolInfo);

      if (_schoolInfo.RESULT !== undefined) {
        if (_schoolInfo.RESULT.CODE === 'INFO-200') { // no school info
          colorConsole.yellow('[school] 학교 정보가 존재하지 않습니다.');
          return res.status(404).json({
            status: 404,
            message: '학교 정보가 존재하지 않습니다.',
          });
        }
      }
      let listCount = _schoolInfo.schoolInfo[0].head[0].list_total_count;
      if (listCount > 1000) {
        listCount = 1000;
      }
      const schoolList = [];

      for (let i = 0; i < listCount; i += 1) {
        schoolList[i] = {
          school_name: _schoolInfo.schoolInfo[1].row[i].SCHUL_NM,
          school_locate: _schoolInfo.schoolInfo[1].row[i].ORG_RDNMA,
          office_code: _schoolInfo.schoolInfo[1].row[i].ATPT_OFCDC_SC_CODE,
          school_code: _schoolInfo.schoolInfo[1].row[i].SD_SCHUL_CODE,
          school_type: _schoolInfo.schoolInfo[1].row[i].SCHUL_KND_SC_NM,
        };
      }

      schoolInfo = schoolList;
      return res.status(200).json({
        status: 200,
        message: '학교 조회에 성공하였습니다.',
        data: { schoolInfo },
      });
    } catch (err) {
      colorConsole.red(err.message);
      return res.status(500).json({
        status: 500,
        message: '학교 조회에 실패하였습니다.',
      });
    }
  });

  return true;
};

exports.searchById = async (schoolId) => {
  const url = neisUrl({
    subUrl: 'schoolInfo',
    schoolCode: encodeURI(schoolId),
  });
  let schoolInfo;

  // eslint-disable-next-line no-async-promise-executor
  return new Promise(async (resolve, reject) => {
    await request(url, (err, response, _schoolInfo) => {
      if (err) {
        return reject(err);
      }

      _schoolInfo = JSON.parse(_schoolInfo);

      if (_schoolInfo.RESULT !== undefined) {
        if (_schoolInfo.RESULT.CODE === 'INFO-200') { // no school info
          // eslint-disable-next-line prefer-promise-reject-errors
          return reject({
            status: 404,
            message: '학교 정보가 존재하지 않습니다.',
          });
        }
      }

      _schoolInfo = {
        school_name: _schoolInfo.schoolInfo[1].row[0].SCHUL_NM,
        school_locate: _schoolInfo.schoolInfo[1].row[0].ORG_RDNMA,
        office_code: _schoolInfo.schoolInfo[1].row[0].ATPT_OFCDC_SC_CODE,
        school_code: _schoolInfo.schoolInfo[1].row[0].SD_SCHUL_CODE,
        school_type: _schoolInfo.schoolInfo[1].row[0].SCHUL_KND_SC_NM,
      };

      schoolInfo = _schoolInfo;
      return schoolInfo;
    });
    return resolve(schoolInfo);
  });
};

exports.getSchoolInfo = async (req, res) => {
  colorConsole.green('[school] 학교 정보 조회');
  const { school_name: schoolName } = req.query;

  colorConsole.gray('<request>');
  colorConsole.gray({ school_name: schoolName });

  if (!schoolName) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  try {
    const schoolInfo = await exports.searchByName(schoolName);
    colorConsole.gray('<response>');
    colorConsole.gray({ schoolInfo });
    return res.status(200).json({
      status: 200,
      message: '학교 정보 조회에 성공하였습니다.',
      data: { schoolInfo },
    });
  } catch (err) {
    if (err.status === 404) {
      colorConsole.yellow(err.message);
      return res.status(404).json({
        status: 404,
        message: '학교 정보가 존재하지 않습니다.',
      });
    }
  }

  return true;
};

exports.getSchoolEvent = async (req, res) => {
  colorConsole.green('[school] 학사일정 조회');
  const { user } = req;
  const { year } = req.query;
  let { month } = req.query;

  colorConsole.gray('<request>');
  colorConsole.gray({ year, month });
  let url;

  if (month && (!parseInt(month, 10))) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }

  let schoolInfo;
  try {
    schoolInfo = await exports.searchById(user.school);
  } catch (err) {
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
      message: '학사일정 조회에 실패하였습니다.',
    });
  }

  if (month) {
    if (parseInt(month, 10) < 10) {
      month = `0${parseInt(month, 10)}`;
    }
    url = neisUrl({
      subUrl: 'SchoolSchedule',
      officeCode: schoolInfo.office_code,
      schoolCode: user.school,
      yymm: `${year}${month}`,
    });
  } else {
    url = neisUrl({
      subUrl: 'SchoolSchedule',
      officeCode: schoolInfo.office_code,
      schoolCode: user.school,
    });
  }

  await request(url, (error, response, schoolEvent) => {
    try {
      if (error) {
        colorConsole.red(error.message);
        return res.status(500).json({
          status: 500,
          message: '학사일정 조회에 실패하였습니다.',
        });
      }
      schoolEvent = JSON.parse(schoolEvent);

      if (schoolEvent.RESULT !== undefined) {
        if (schoolEvent.RESULT.CODE === 'INFO-200') { // no class info
          colorConsole.yellow('[school] 학사일정이 존재하지 않습니다.');
          return res.status(204).json({
            status: 204,
            message: '학사일정이 존재하지 않습니다.',
          });
        }
      }

      const eventCount = schoolEvent.SchoolSchedule[0].head[0].list_total_count;
      const events = [];

      for (let i = 0; i < eventCount; i += 1) {
        const title = schoolEvent.SchoolSchedule[1].row[i].EVENT_NM;
        let startDate = schoolEvent.SchoolSchedule[1].row[i].AA_YMD;
        let endDate = schoolEvent.SchoolSchedule[1].row[i].AA_YMD;

        startDate = `${startDate.substring(0, 4)}-${startDate.substring(4, 6)}-${startDate.substring(6, 8)}`;
        endDate = `${endDate.substring(0, 4)}-${endDate.substring(4, 6)}-${endDate.substring(6, 8)}`;
        events[i] = { title, start_date: startDate, end_date: endDate };
      }

      colorConsole.gray('<response>');
      colorConsole.gray({ events });

      return res.status(200).json({
        status: 200,
        message: '학사일정 조회에 성공하였습니다.',
        data: { events },
      });
    } catch (err) {
      colorConsole.red(err.message);
      return res.status(500).json({
        status: 500,
        message: '학사일정 조회에 실패하였습니다.',
      });
    }
  });

  return true;
};

exports.getClassInfo = async (req, res) => {
  colorConsole.green('[school] 반 정보 조회');
  const {
    school_id: schoolId,
    office_id: officeId,
    grade,
  } = req.query;

  colorConsole.gray('<request>');
  colorConsole.gray({ school_id: schoolId, office_id: officeId, grade });

  if (!(schoolId && officeId && grade)) {
    colorConsole.yellow('검증 오류입니다.');
    return res.status(400).json({
      status: 400,
      message: '검증 오류입니다.',
    });
  }
  const url = neisUrl({
    subUrl: 'classInfo',
    officeCode: officeId,
    schoolCode: schoolId,
    schoolGrade: grade,
  });
  try {
    await request(url, (err, response, classInfo) => {
      if (err) {
        colorConsole.red(err.message);
        return res.status(500).json({
          status: 500,
          message: '학년 정보 조회에 실패히였습니다.',
        });
      }

      classInfo = JSON.parse(classInfo);

      if (classInfo.RESULT !== undefined) {
        if (classInfo.RESULT.CODE === 'INFO-200') { // no class info
          colorConsole.yellow('[school] 학년 정보가 존재하지 않습니다.');
          return res.status(404).json({
            status: 404,
            message: '학년 정보가 존재하지 않습니다.',
          });
        }
      }
      const classCount = classInfo.classInfo[0].head[0].list_total_count;

      colorConsole.gray('<response>');
      colorConsole.gray({ classCount });

      return res.status(200).json({
        status: 200,
        message: '학년 정보 조회에 성공하였습니다.',
        data: { classCount },
      });
    });
  } catch (err) {
    colorConsole.red(err.message);
    return res.status(500).json({
      status: 500,
      message: '학년 정보 조회에 실패히였습니다.',
    });
  }

  return true;
};

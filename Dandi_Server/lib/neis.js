require('dotenv').config();

const { NEIS_KEY } = process.env;

module.exports = (params) => {
  let baseUrl = `http://open.neis.go.kr/hub/${params.subUrl}?Type=json&KEY=${NEIS_KEY}&psize=1000`;

  Object.keys(params).forEach((param) => {
    if (param === 'schoolCode') {
      baseUrl += `&SD_SCHUL_CODE=${params[param]}`;
    }
    if (param === 'officeCode') {
      baseUrl += `&ATPT_OFCDC_SC_CODE=${params[param]}`;
    }
    if (param === 'schoolGrade') {
      baseUrl += `&GRADE=${params[param]}`;
    }
    if (param === 'yymm') {
      baseUrl += `&AA_YMD=${params[param]}`;
    }
    if (param === 'schoolName') {
      baseUrl += `&SCHUL_NM=${params[param]}`;
    }
  });

  return baseUrl;
};

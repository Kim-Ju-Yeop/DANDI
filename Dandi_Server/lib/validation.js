/* eslint-disable no-useless-catch */
const Joi = require('@hapi/joi');

const getStringToDate = (strDate) => new Date(
  strDate.substr(0, 4),
  strDate.substr(5, 2),
  strDate.substr(8, 2),
);

exports.validateMemberSignup = async (body) => {
  const schema = Joi.object().keys({
    user_id: Joi.string().required(),
    user_pw: Joi.string().required(),
    user_name: Joi.string().required(),
    permission: Joi.number().integer(),
    user_email: Joi.string().required(),
    user_phone: Joi.string(),
    school: Joi.string().required(),
    school_grade: Joi.number().integer(),
    school_class: Joi.number().integer(),
    profile_pic: Joi.string(),
    pushNotify: Joi.boolean().required(),
    isPublic: Joi.boolean().required(),
    isAllowed: Joi.boolean().required(),
  });
  try {
    return await Joi.validate(body, schema);
  } catch (err) {
    throw err;
  }
};

exports.validateAddEvent = async (body) => {
  const schema = Joi.object().keys({
    title: Joi.string().required(),
    content: Joi.string(),
    start_date: Joi.date().required(),
    end_date: Joi.date().required(),
    author: Joi.string().required(),
    channel_id: Joi.number().integer().required(),
  });
  try {
    await Joi.validate(body, schema);
    const startDate = getStringToDate(body.start_date);
    const endDate = getStringToDate(body.end_date);
    if (startDate.getTime() > endDate.getTime()) {
      throw new Error();
    }
    return;
  } catch (err) {
    throw err;
  }
};

exports.validateUpdateEvent = async (body) => {
  const schema = Joi.object().keys({
    title: Joi.string(),
    content: Joi.string(),
    start_date: Joi.date(),
    end_date: Joi.date(),
    author: Joi.string(),
  });
  try {
    await Joi.validate(body, schema);
    const startDate = getStringToDate(body.start_date);
    const endDate = getStringToDate(body.end_date);
    if (startDate.getTime() > endDate.getTime()) {
      throw new Error();
    }
    return;
  } catch (err) {
    throw err;
  }
};

exports.validateAddChannel = async (body) => {
  const schema = Joi.object().keys({
    name: Joi.string().required(),
    explain: Joi.string().required(),
    isPublic: Joi.boolean().required(),
    school_id: Joi.string().required(),
    create_user: Joi.string().required(),
    color: Joi.string(),
    thumbnail: Joi.string(),
  });
  try {
    return await Joi.validate(body, schema);
  } catch (err) {
    throw err;
  }
};

exports.validateUpdateChannel = async (body) => {
  const schema = Joi.object().keys({
    isPublic: Joi.string(),
    color: Joi.string(),
    explain: Joi.string(),
  });
  try {
    return await Joi.validate(body, schema);
  } catch (err) {
    throw err;
  }
};

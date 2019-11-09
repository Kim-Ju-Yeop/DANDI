require('dotenv').config();

const jwt = require('jsonwebtoken');

const { JWT_SECRET } = process.env;

exports.createToken = async (userId) => {
  const payload = {
    user_id: userId,
  };
  const options = {
    expiresIn: '12h',
    issuer: 'dandi',
    subject: 'token',
  };

  return jwt.sign(payload, JWT_SECRET, options);
};

exports.verifyToken = async (token) => {
  return jwt.verify(token, JWT_SECRET);
};

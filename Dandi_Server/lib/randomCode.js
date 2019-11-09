module.exports = (length) => {
  let code = '';
  const chars = '0123456789';

  for (let i = 0; i < length; i += 1) {
    const rnum = Math.floor(Math.random() * chars.length);
    code += chars.substring(rnum, rnum + 1);
  }

  return code;
};

const nodemailer = require('nodemailer');
const emailAccount = require('../config/emailAccount');

exports.sendEmail = async (email, title, content) => {
  const transporter = nodemailer.createTransport({
    service: 'Gmail',
    auth: {
      user: emailAccount.id,
      pass: emailAccount.pw,
    },
  });

  const mailOptions = {
    from: emailAccount.id,
    to: email,
    subject: title,
    html: content,
  };

  // eslint-disable-next-line no-useless-catch
  try {
    await transporter.sendMail(mailOptions);
  } catch (err) {
    throw err;
  }
};

exports.getAuthForm = (content) => `
  <div style="
    margin: 0;
    background-color: #F1B71C;
    text-align: center;
    padding-bottom: 2.5%;
    padding-top: 2.5%;
    font-family: '나눔고딕';
    ">
    <div style="
      background-color: white;
      margin-left: 10%;
      width: 80%;
      padding-top: 5%;
      padding-bottom: 5%;
      border-radius: 30px;
      color: gray;
      font-weight: bolder;
      font-size: 150%;
    ">
      아래 인증 코드를 정확히 입력해주세요.
      <div style="
        background-color: #F1B71C;
        margin-left: 32%;
        width: 36%;
        margin-top: 2.5%;
        padding-top: 2.5%;
        padding-bottom: 2.5%;
        color: white;
        font-size: 180%;
        font-weight: bolder;
      ">
        ${content}
      </div>

      <div style="
        font-size: 75%;
        font-weight: normal;
        margin-top: 2%;
      ">
        회원가입을 위해 인증 코드를 입력하고 E-mail 인증을 완료하세요.
      </div>
    </div>
  </div>
`;

exports.getAllowForm = () => `
  <div style="
    margin: 0;
    background-color: #F1B71C;
    text-align: center;
    padding-bottom: 2.5%;
    padding-top: 2.5%;
    font-family: '나눔고딕';
    ">
    <div style="
      background-color: white;
      color: #F1B71C;
      margin-left: 10%;
      width: 80%;
      padding-top: 5%;
      padding-bottom: 5%;
      border-radius: 30px;
      font-weight: bolder;
      font-size: 200%;
    ">
      DANDI 회원가입이 승인되었습니다.
      <div style="
        font-size: 75%;
        font-weight: normal;
        margin-top: 2%;
        color: gray;
      ">
        로그인 후 DANDI를 사용해보세요!
      </div>
    </div>
  </div>
`;

exports.getRejectForm = () => `
  <div style="
    margin: 0;
    background-color: #F1B71C;
    text-align: center;
    padding-bottom: 2.5%;
    padding-top: 2.5%;
    font-family: '나눔고딕';
    ">
    <div style="
      background-color: white;
      color: #F1B71C;
      margin-left: 10%;
      width: 80%;
      padding-top: 5%;
      padding-bottom: 5%;
      border-radius: 30px;
      font-weight: bolder;
      font-size: 200%;
    ">
      DANDI 회원가입이 거절되었습니다.
      <div style="
        font-size: 75%;
        font-weight: normal;
        margin-top: 2%;
        color: gray;
      ">
        자세한 사항은 bindteam.4th@gmail.com로 문의해주세요.
      </div>
    </div>
  </div>
`;

module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    user_id: {
      field: 'user_id',
      type: DataTypes.STRING(45),
      primaryKey: true,
      autoIncrement: false,
      allowNull: false,
    },
    user_pw: {
      field: 'user_pw',
      type: DataTypes.STRING,
      allowNull: false,
    },
    user_name: {
      field: 'user_name',
      type: DataTypes.STRING(45),
      allowNull: false,
    },
    permission: {
      field: 'permission',
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    user_email: {
      field: 'user_email',
      type: DataTypes.STRING,
      allowNull: false,
    },
    user_phone: {
      field: 'user_phone',
      type: DataTypes.STRING(45),
      allowNull: true,
    },
    school: {
      field: 'school',
      type: DataTypes.STRING(45),
      allowNull: false,
    },
    school_grade: {
      field: 'school_grade',
      type: DataTypes.INTEGER,
      allowNull: true,
    },
    school_class: {
      field: 'school_class',
      type: DataTypes.INTEGER,
      allowNull: true,
    },
    profile_pic: {
      field: 'profile_pic',
      type: DataTypes.STRING(500),
      allowNull: true,
    },
    pushNotify: {
      field: 'push_notify',
      type: DataTypes.BOOLEAN,
      allowNull: false,
    },
    isPublic: {
      field: 'is_public',
      type: DataTypes.BOOLEAN,
      allowNull: false,
    },
    isAllowed: {
      field: 'is_allowed',
      type: DataTypes.BOOLEAN,
      defaultValue: false,
      allowNull: false,
    },
  });

  User.associate = (models) => {
    models.User.hasMany(models.ChannelEvent, {
      foreignKey: 'author',
      onDelete: 'cascade',
    });

    models.User.hasMany(models.Channel, {
      foreignKey: 'create_user',
      onDelete: 'cascade',
    });
  };

  User.getUser = (userId) => User.findOne({
    attributes: [
      'user_id',
      'user_name',
      'permission',
      'user_email',
      'user_phone',
      'school',
      'school_grade',
      'school_class',
      'profile_pic',
      'pushNotify',
      'isPublic',
      'isAllowed',
    ],
    where: {
      user_id: userId,
    },
    raw: true,
  });

  User.getUserData = (userId) => User.findOne({
    attributes: [
      'user_id',
      'user_name',
      'permission',
      'school',
      'school_grade',
      'school_class',
      'profile_pic',
      'pushNotify',
      'isPublic',
      'isAllowed',
    ],
    where: {
      user_id: userId,
    },
    raw: true,
  });

  User.getAllowedUser = () => User.findAll({
    attributes: [
      'user_id',
      'user_name',
      'permission',
      'user_email',
      'user_phone',
      'school',
      'school_grade',
      'school_class',
      'profile_pic',
      'pushNotify',
      'isPublic',
      'isAllowed',
    ],
    where: {
      isAllowed: true,
    },
    raw: true,
  });

  User.getUserForLogin = (userId, userPw) => User.findOne({
    where: {
      user_id: userId,
      user_pw: userPw,
    },
    raw: true,
  });

  User.getAwaitUser = () => User.findAll({
    attributes: [
      'user_id',
      'user_name',
      'permission',
      'user_email',
      'user_phone',
      'school',
      'school_grade',
      'school_class',
      'profile_pic',
      'pushNotify',
      'isPublic',
      'isAllowed',
    ],
    where: {
      isAllowed: false,
    },
    raw: true,
  });

  User.allowUser = (userId) => User.update({
    isAllowed: true,
  }, {
    where: {
      user_id: userId,
      isAllowed: false,
    },
  });

  User.rejectUser = (userId) => User.destroy({
    where: {
      user_id: userId,
      isAllowed: false,
    },
  });

  User.createUser = (data) => User.create({
    user_id: data.user_id,
    user_pw: data.user_pw,
    user_name: data.user_name,
    permission: data.permission,
    user_email: data.user_email,
    user_phone: data.user_phone,
    school: data.school,
    school_grade: data.school_grade,
    school_class: data.school_class,
    profile_pic: data.profile_pic,
    pushNotify: data.pushNotify,
    isPublic: data.isPublic,
    isAllowed: data.isAllowed,
  });

  User.updateUser = (userId, data) => User.update(data, {
    where: {
      user_id: userId,
    },
  });

  User.updateProfile = (userId, profilePic) => User.update({
    profile_pic: profilePic,
  }, {
    where: {
      user_id: userId,
    },
  });

  return User;
};

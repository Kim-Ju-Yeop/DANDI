module.exports = (sequelize, DataTypes) => {
  const ChannelUser = sequelize.define('ChannelUser', {
    id: {
      field: 'id',
      type: DataTypes.INTEGER(45),
      primaryKey: true,
      autoIncrement: true,
      allowNull: false,
    },
    user_id: {
      field: 'user_id',
      type: DataTypes.STRING(45),
      allowNull: false,
    },
    channel_id: {
      field: 'channel_id',
      type: DataTypes.INTEGER(45),
      allowNull: false,
    },
    isAllowed: {
      field: 'is_allowed',
      type: DataTypes.BOOLEAN,
      allowNull: false,
    },
    pushNotify: {
      field: 'push_notify',
      type: DataTypes.BOOLEAN,
      allowNull: false,
    },
  });

  ChannelUser.associate = (models) => {
    models.ChannelUser.belongsTo(models.Channel, {
      foreignKey: 'channel_id',
      onDelete: 'cascade',
    });

    models.ChannelUser.belongsTo(models.User, {
      foreignKey: 'user_id',
      onDelete: 'cascade',
    });
  };

  ChannelUser.getChannelUserByUser = (userId) => ChannelUser.findAll({
    where: {
      user_id: userId,
    },
    raw: true,
  });

  ChannelUser.getChannelUserByChannel = (channelId) => ChannelUser.findAll({
    where: {
      channel_id: channelId,
      isAllowed: true,
    },
    raw: true,
  });

  ChannelUser.getChannelByAllowedUser = (userId) => ChannelUser.findAll({
    where: {
      user_id: userId,
      isAllowed: true,
    },
    raw: true,
  });

  ChannelUser.getIsMember = (userId, channelId) => ChannelUser.findOne({
    where: {
      user_id: userId,
      channel_id: channelId,
      isAllowed: true,
    },
    raw: true,
  });

  ChannelUser.getUserStatus = (userId, channelId) => ChannelUser.findOne({
    where: {
      user_id: userId,
      channel_id: channelId,
    },
    raw: true,
  });

  ChannelUser.getAwaitUser = (channelId) => ChannelUser.findAll({
    where: {
      channel_id: channelId,
      isAllowed: false,
    },
    raw: true,
  });

  ChannelUser.joinChannel = (data) => ChannelUser.create({
    user_id: data.user_id,
    channel_id: data.channel_id,
    isAllowed: data.isAllowed,
    pushNotify: data.pushNotify,
  });

  ChannelUser.allowUser = (userId, channelId) => ChannelUser.update({
    isAllowed: true,
  }, {
    where: {
      user_id: userId,
      channel_id: channelId,
    },
  });

  ChannelUser.rejectUser = (userId, channelId) => ChannelUser.destroy({
    where: {
      user_id: userId,
      channel_id: channelId,
      isAllowed: false,
    },
  });

  ChannelUser.deleteChannelUser = (userId, channelId) => ChannelUser.destroy({
    where: {
      user_id: userId,
      channel_id: channelId,
    },
  });

  ChannelUser.deleteChannelUserByChannel = (channelId) => ChannelUser.destroy({
    where: {
      channel_id: channelId,
    },
  });

  ChannelUser.leaveChannel = (userId, channelId) => ChannelUser.destroy({
    where: {
      user_id: userId,
      channel_id: channelId,
    },
  });

  return ChannelUser;
};

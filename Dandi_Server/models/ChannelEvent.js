const Sequelize = require('sequelize');

module.exports = (sequelize, DataTypes) => {
  const ChannelEvent = sequelize.define('ChannelEvent', {
    id: {
      field: 'id',
      type: DataTypes.INTEGER(45),
      primaryKey: true,
      autoIncrement: true,
      allowNull: false,
    },
    channel_id: {
      field: 'channel_id',
      type: DataTypes.INTEGER(45),
      allowNull: false,
    },
    title: {
      field: 'title',
      type: DataTypes.STRING(45),
      allowNull: false,
    },
    content: {
      field: 'content',
      type: DataTypes.STRING(500),
      allowNull: true,
    },
    start_date: {
      field: 'start_date',
      type: DataTypes.DATEONLY,
      allowNull: false,
    },
    end_date: {
      field: 'end_date',
      type: DataTypes.DATEONLY,
      allowNull: false,
    },
    author: {
      field: 'author',
      type: DataTypes.STRING(45),
      allowNull: false,
    },
  });

  ChannelEvent.associate = (models) => {
    models.ChannelEvent.belongsTo(models.Channel, {
      foreignKey: 'channel_id',
      onDelete: 'cascade',
    });

    models.ChannelEvent.belongsTo(models.User, {
      foreignKey: 'author',
      onDelete: 'cascade',
    });
  };

  ChannelEvent.getEvent = (id) => ChannelEvent.findOne({
    where: {
      id,
    },
    raw: true,
  });

  ChannelEvent.getEventByChannel = (channelId) => ChannelEvent.findAll({
    where: {
      channel_id: channelId,
    },
    raw: true,
  });

  ChannelEvent.getEventByChannelAndKeyword = (channelId, keyword) => ChannelEvent.findAll({
    where: {
      title: { [Sequelize.Op.like]: `%${keyword}%` },
      channel_id: channelId,
    },
    raw: true,
  });

  ChannelEvent.createEvent = (data) => ChannelEvent.create({
    channel_id: data.channel_id,
    title: data.title,
    start_date: data.start_date,
    end_date: data.end_date,
    author: data.author,
    content: data.content,
  });

  ChannelEvent.updateEvent = (id, data) => ChannelEvent.update(data, {
    where: {
      id,
    },
  });

  ChannelEvent.deleteEvent = (id) => ChannelEvent.destroy({
    where: {
      id,
    },
  });

  ChannelEvent.deleteEventByChannel = (channelId) => ChannelEvent.destroy({
    where: {
      channel_id: channelId,
    },
  });

  return ChannelEvent;
};

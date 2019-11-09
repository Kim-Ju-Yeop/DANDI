const path = require('path');
require('./models');
require('dotenv').config();

const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const origin = require('./middleware/origin'); // middleware
const colorConsole = require('./lib/console');
const api = require('./api');

const { PORT } = process.env;

const app = express();

// middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cors());
app.use(origin);

app.use('/static', express.static(path.join(__dirname, '/public')));

// API router
app.use('/', api);

app.listen(PORT, () => {
  colorConsole.green(`server is running at port ${PORT}`);
});

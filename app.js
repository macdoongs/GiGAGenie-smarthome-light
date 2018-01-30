var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');

var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var config = require('config.json')('./config/config.json');

var morgan = require('morgan');

var fs = require('fs');

var app = express();



var logDirectory = './log';

// ensure log directory exists
if (!fs.existsSync(logDirectory)){
  fs.mkdirSync(logDirectory);
}

var FileStreamRotator = require('file-stream-rotator');

// create a rotating write stream
var accessLogStream = FileStreamRotator.getStream({
  date_format: 'YYYYMMDD',
  filename: logDirectory + '/access-%DATE%.log',
  frequency: 'daily',
  verbose: false
});


var CORS = require('cors')();

app.use(CORS);


// robots.txt
app.use(function (req, res, next) {
  if ('/robots.txt' == req.url) {
    res.type('text/plain');
    res.send('User-agent: *\nAllow:/$\nDisallow:/');
  } else {
    next();
  }
});

// setup the logger
//app.use( morgan('combined', {stream: accessLogStream}));
app.use( morgan('dev', {stream: accessLogStream}));

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(morgan('dev'));
app.use(bodyParser.json({limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb', extended:false, parameterLimit: 1000000}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// routes module
// Web module
var index = require('./routes/web/index');


// Web page routes
app.use('/', index);


app.use(function(req, res, next){
  next();
});


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var error = new Error('Not Found');
  error.status = 404;
  next(error);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
  app.use(function(error, req, res, next) {
    res.status(error.status || 500);
    res.render('error', {
      title: global.title,
      message: error.message,
      error: error
    });
  });
}

// production error handler
// no stacktraces leaked to user
app.use(function(error, req, res, next) {
  res.status(error.status || 500);
  res.render('error', {
    title: global.title,
    message: error.message,
    error: {}
  });
});

module.exports = app;

# Dandi
일정 공유 캘린더(server)

## config

>#### database.js
>```
>module.exports = {
>	host : 'localhost',
>	user : 'root',
>	password : 'password',
>	database : 'database'
>}
>```

>#### emailAccount.js
>```
>module.exports = {
> id: 'id', // google id
> pw: 'password', // google password
>};
>```

>#### imageInfo.js
>```
>exports.basic_profile = 'basicprofile.ext';
>exports.basic_thumbnail = 'basicthumbnail.ext';
>```

## dotenv
>```
>PORT=3000
>NEIS_KEY = 'neis key'
>JWT_SECRET = 'jwt secret'
>```
## file

>```
>./public/image/basicprofile.ext
>./public/image/basicthumbnail.ext
>```

## start
>```
>yarn start
>```


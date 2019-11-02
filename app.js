const sqlite3 = require("better-sqlite3")
const geolib = require("geolib")
const express = require("express")

const app = express()

global.db = sqlite3("./db/POIs.db")

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", '*');
    res.header("Access-Control-Allow-Credentials", true);
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
    res.header("Access-Control-Allow-Headers", 'Origin,X-Requested-With,Content-Type,Accept,content-type,application/json');
    next();
});

app.use(require("./src/routes/crowdsourcing-web-app"))
app.use(require("./src/routes/native-app"))

app.listen(80);




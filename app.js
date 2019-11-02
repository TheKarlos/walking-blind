const sqlite3 = require("better-sqlite3")
const geolib = require("geolib")
const express = require("express")

const app = express()

global.db = sqlite3("./db/POIs.db")

app.use(require("./src/routes/crowdsourcing-web-app"))
app.use(require("./src/routes/native-app"))

app.listen(80);




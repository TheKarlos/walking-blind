const geolib = require("geolib");

const crowdsourcingRoutes = require("express").Router();

crowdsourcingRoutes.get("/crowdsourcing/getPOIs", function (req, res, next) {
    // get POIs within bounds of provided coords
    const Location = {
        latitude: req.query.clat,
        longitude: req.query.clon,
    }

    const boundDist = geolib.getDistance({ latitude: req.query.nelat, longitude: req.query.nelon }, { latitude: req.query.swlat, longitude: req.query.swlon });

    // lookup database for all POIs within bounds
    var POIs = []
    var dbPOIs = global.db.prepare("SELECT * FROM `PointsOfInterest`").all()

    dbPOIs.forEach(POI => {
        if (geolib.getDistance(Location, { latitude: POI["Latitude"], longitude: POI["Longitude"] }) <= boundDist)
            POIs.push(POI);
    });

    res.json(POIs);
});

crowdsourcingRoutes.get("/crowdsourcing/addPOI", function (req, res, next) {
    // add POI given name, coords and effect distance
    var returnObj = {
        success: true,
        message: ""
    }

    if (global.db.prepare("SELECT * FROM `PointsOfInterest` WHERE Latitude=? AND Longitude=?").get(req.query.lat, req.query.lon)) {
        returnObj.success = false;
        returnObj.message = `POI @ ${req.query.lat + ',' + req.query.lon} already exists!`
        res.json(returnObj);

        return;
    }

    global.db.prepare("INSERT INTO `PointsOfInterest` (Name, Latitude, Longitude, Radius) VALUES (?,?,?,?)").run(req.query.name, req.query.lat, req.query.lon, req.query.rad)
    res.json(returnObj);
});

module.exports = crowdsourcingRoutes;
const nativeAppRoutes = require("express").Router();

nativeAppRoutes.get("/app/getLocationPOIs", function(req, res, next){
    // sees if the user intersects with any POIs
    const Location = {
        latitude: req.query.lat,
        longitude: req.query.lon,
    }

    // lookup database for all intersecting POIs
    var POIs = []
    var dbPOIs = global.db.prepare("SELECT * FROM `PointsOfInterest`").all()

    dbPOIs.forEach(POI => {
        if (geolib.getDistance(Location, { latitude: POI["Latitude"], longitude: POI["Longitude"] }) <= POI["Radius"])
            POIs.push(POI);
    });

    res.json(POIs);
});

module.exports = nativeAppRoutes;
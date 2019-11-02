const crowdsourcingRoutes = require("express").Router();

crowdsourcingRoutes.get("/crowdsourcing/getPOIs", function(req, res, next){
    // get POIs within 1 km of provided coords

});

crowdsourcingRoutes.get("/crowdsourcing/addPOI", function(req, res, next){
    // add POI given name, coords and effect distance

});

module.exports = nativeAppRoutes;
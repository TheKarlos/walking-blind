import React, { useState, useEffect } from 'react';
// import { Map, InfoWindow, Marker, GoogleApiWrapper } from 'google-maps-react';
import GoogleMapReact from 'google-map-react';
import AddPOIDialog from './AddPOIDialog';

export default function MapView(props) {
    const API_BASE_URL = 'http://10.41.40.205:8080'
    const [open, setOpen] = useState(false);
    const [coords, setCoords] = useState({});
    const [boundPoints, setBoundPoints] = useState({});
    const [pois, setPois] = useState([]);
    const [map, setMap] = useState({});
    const [maps, setMaps] = useState({});
    const [circles, setCircles] = useState([]);

    async function getPOIs(){
        //console.log(boundPoints);
        if (boundPoints.center){
            //console.log("makes req")
            const result = await fetch(API_BASE_URL + `/crowdsourcing/getPOIs?clat=${boundPoints.center.lat}&clon=${boundPoints.center.lng} &nelat=${boundPoints.ne.lat}&nelon=${boundPoints.ne.lng}&swlat=${boundPoints.sw.lat}&swlon=${boundPoints.sw.lng}`);
            let data = await result.json()
            console.log(data);
            setPois(data)
        }
        // setPois(result.json)
    }

    function removeCircles(){
        circles.forEach(circle => circle.setMap(null));
        setCircles([])
    }

    useEffect(() => {
        getPOIs();
        removeCircles();
        if(maps.Circle)
            renderCircles(map, maps);
    }, [boundPoints])
    const handleOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handlePOIClick= (lat, lng) => {
        setCoords({
            lat: lat,
            lng: lng
        })
        console.log(coords);
        handleOpen();
    }
    //  const getPOIs = async () => {
    //     console.log('req started');
    //     useEffect(() => {
    //         const result = await fetch(API_BASE_URL + `/crowdsourcing/getPOIs?clat=${boundPoints.center.lat}&clon=${boundPoints.center.lng} &nelat=${boundPoints.ne.lat}&nelon=${boundPoints.ne.lng}&swlat=${boundPoints.sw.lat}&swlon=${boundPoints.sw.lng}`)
    //     })
        
    //     .then(res => res.json())
    //     .then(data => {setPois(data)})                         
    // }
    const handleMapBoundsChange = (center, bounds) => {
        console.log(center, bounds)
        // setBoundPoints({});
        setBoundPoints({
            center: center,
            ne: bounds.ne,
            sw: bounds.sw
        })
        console.log(center);
        console.log('rerender: -----------------')
        console.log(boundPoints)
    }
    let pointsData = [
        { lat: 53.8067, lng: -1.5550 },
        { lat: 53.7950, lng: -1.5474 }
    ]
    // render circles for all points returned by API
    async function renderCircles(map, maps) {
        console.log('init: -------------------');
        console.log(boundPoints);
        // await getPOIs();
        console.log(pois);
        pois.map((centre) => {
            circles.push(new maps.Circle({
                strokeColor: '#FF0000',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillColor: '#FF0000',
                fillOpacity: 0.3,
                map,
                center: {lat: parseFloat(centre.Latitude), lng: parseFloat(centre.Longitude)},
                radius: centre.Radius,
                label: "test"
            }));
            circles.push(new maps.Marker({
                position: {lat: parseFloat(centre.Latitude), lng: parseFloat(centre.Longitude)},
                map: map,
                title: centre.Name
            }))
            setCircles(circles);
        })
    }
    return (
        <div style={{ width: '100%', height: '100vh' }}>
            <GoogleMapReact
                bootstrapURLKeys={{ key: 'AIzaSyAMnkLF3UN8Zv-uePvtqv6-ZMj8wDX0C4k' }}
                defaultCenter={{
                    lat: 53.80688,
                    lng: -1.55266
                }}
                defaultZoom={16}
                yesIWantToUseGoogleMapApiInternals={true}
                onGoogleApiLoaded={({ map, maps }) => {setMap(map); setMaps(maps)}}
                onClick={({ x, y, lat, lng, event }) => handlePOIClick(lat,lng)}
                onChange={({ center, zoom, bounds, marginBounds }) => {handleMapBoundsChange(center, bounds);}}
            >
            </GoogleMapReact>
            <AddPOIDialog coords={coords} onClose={handleClose} open={open}/>
        </div>
    );
}
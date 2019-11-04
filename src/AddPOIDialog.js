import React, {useState} from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import TextField from '@material-ui/core/TextField';
import Button from '@material-ui/core/Button';

export default function AddPOIDialog(props) {
    const API_BASE_URL = 'http://10.41.40.205:8080'
    const { coords, onClose, open } = props;
    const [placeName, setPlaceName] = useState();
    const [placeRad, setPlaceRad] = useState();

    function sendPOI(){
        console.log(placeName, placeRad);
        fetch(API_BASE_URL + `/crowdsourcing/addPOI?name=${placeName}&lat=${coords.lat}&lon=${coords.lng}&rad=${placeRad}`)
        .then(res => res.json)
        .then(data => console.log(data))
        onClose();
    }
    return (
        <Dialog onClose={onClose} aria-labelledby="simple-dialog-title" open={open}>
            <DialogTitle id="simple-dialog-title">Add new POI</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    Latitude: {coords.lat}
                    <br />
                    Longitude: {coords.lng}
                    <TextField
                        id="place-name"
                        label="Name"
                        placeholder="Name of place"
                        fullWidth
                        margin="normal"
                        InputLabelProps={{
                            shrink: true,
                        }}
                        onChange={(e) => setPlaceName(e.target.value)}

                    />
                    <TextField
                        id="standard-full-width"
                        label="Radius"
                        placeholder="Radius of geofence"
                        fullWidth
                        margin="normal"
                        InputLabelProps={{
                            shrink: true,
                        }}
                        onChange={(e) => setPlaceRad(e.target.value)}
                    />
                </DialogContentText>
            </DialogContent>
            <Button variant="contained" onClick={sendPOI}>
                Add
            </Button>
        </Dialog>
    )
}

import { useState, useEffect } from "react";
import {
  Alert,
  Dialog,
  DialogActions,
  DialogTitle,
  DialogContent,
  CircularProgress,
  Collapse,
  List,
  ListItem,
  TextField,
  Typography,
  FormControl,
  MenuItem,
  Select,
  InputLabel,
} from "@mui/material";
import { apiUrl } from "../App";

function CreateReservationPage() {
  const [specialists, setSpecialists] = useState([]);
  const [selectedSpecialist, setSelectedSpecialist] = useState("");
  const [email, setEmail] = useState("");
  const [success, setSuccess] = useState(false);
  const [failure, setFailure] = useState(false);
  const [specialistError, setSpecialistError] = useState(false);
  const [emailError, setEmailError] = useState(false);
  const [createdVisit, setCreatedVisit] = useState({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetch(`${apiUrl}/specialists/`)
      .then((response) => response.json())
      .then(setSpecialists);
  }, []);

  const createReservation = (e) => {
    e.preventDefault();
    setLoading(true);
    setSpecialistError(false);
    setEmailError(false);
    setSuccess(false);
    setFailure(false);
    if (selectedSpecialist == "" || email == "") {
      if (selectedSpecialist == "") {
        setSpecialistError(true);
      }
      if (email == "") {
        setEmailError(true);
      }
    } else {
      fetch(`${apiUrl}/visits/create/${selectedSpecialist}?email=${email}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error("Failed to create reservation");
          }
        })
        .then((jsonResponse) => {
          setCreatedVisit(jsonResponse);
          setLoading(false);
          setSelectedSpecialist("");
          setEmail("");
          setSuccess(true);
          setFailure(false);
        })
        .catch((error) => {
          setLoading(false);
          setFailure(true);
          setSuccess(false);
          setTimeout(() => {
            setFailure(false);
          }, 5000);
        });
    }
  };

  return (
    <div className="mx-3">
      <h2 className="my-5">Create reservation</h2>

      <Collapse in={failure}>
        <Alert
          onClose={() => {
            setFailure(false);
          }}
          severity="error"
          className="mb-3"
        >
          Failed to create reservation
        </Alert>
      </Collapse>

      <Dialog open={success} onClose={() => setSuccess(false)}>
        <DialogTitle className="mt-2 mb-2">Reservation successful</DialogTitle>
        <DialogContent>
          <Typography gutterBottom>
            Thank you for using our services. Your request has been processed.
            <span className="fw-bold">
              {" "}
              Make sure you save your reservation code
            </span>
          </Typography>
          <List>
            <ListItem disableGutters>
              Reservation code: {createdVisit?.reservationCode}
            </ListItem>
            <ListItem disableGutters>
              Reservation time: {createdVisit?.reservationTime}
            </ListItem>
            <ListItem disableGutters>
              Specialist: {createdVisit?.specialist?.name}
            </ListItem>
          </List>
        </DialogContent>
        <DialogActions>
          <button
            className="btn btn-outline-secondary mb-2 me-2"
            onClick={() =>
              navigator.clipboard.writeText(createdVisit?.reservationCode)
            }
          >
            Copy reservation code
          </button>
          <button
            className="btn btn-outline-primary mb-2 me-2"
            onClick={() => setSuccess(false)}
          >
            Close
          </button>
        </DialogActions>
      </Dialog>

      <form noValidate>
        <FormControl fullWidth size="small" className="mb-3">
          <InputLabel
            id="select-specialist-label"
            error={specialistError}
            required
          >
            Choose a specialist
          </InputLabel>
          <Select
            error={specialistError}
            className="mb-3"
            labelId="select-specialist-label"
            InputLabelProps={{ shrink: true }}
            id="select-specialist"
            label="Choose a specialist"
            fullWidth
            value={selectedSpecialist}
            onChange={(e) => setSelectedSpecialist(e.target.value)}
            required
          >
            {specialists?.map((specialist) => (
              <MenuItem value={specialist.id} key={specialist.id}>
                {specialist.name}
              </MenuItem>
            ))}
          </Select>
          <TextField
            error={emailError}
            onChange={(e) => setEmail(e.target.value)}
            value={email}
            id="email"
            label="Enter your email"
            helperText="Email is required"
            className="form-control mb-3"
            size="small"
            required
          />
        </FormControl>
        {loading ? (
          <CircularProgress className="mb-4" />
        ) : (
          <button
            type="submit"
            className="btn btn-primary"
            onClick={(e) => createReservation(e)}
            disabled={loading}
          >
            Submit
          </button>
        )}
      </form>
    </div>
  );
}

export default CreateReservationPage;

import {
  FormControl,
  TextField,
  Dialog,
  DialogTitle,
  DialogActions,
  DialogContent,
  Typography,
  Alert,
  Collapse,
  List,
  ListItem,
} from "@mui/material";
import { useState } from "react";
import { apiUrl } from "../App";

function ViewReservationPage() {
  const [reservationCode, setReservationCode] = useState("");
  const [reservationCodeError, setReservationCodeError] = useState(false);
  const [reservation, setReservation] = useState({});
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [success, setSuccess] = useState(false);
  const [failure, setFailure] = useState(false);
  const [failedFetch, setFailedFetch] = useState(false);
  const [cancelled, setCancelled] = useState(false);
  const [email, setEmail] = useState("");
  const [emailError, setEmailError] = useState(false);

  const checkReservation = (e) => {
    e.preventDefault();
    setLoading(true);
    setReservationCodeError(false);
    setFailedFetch(false);
    if (reservationCode == "") {
      setReservationCodeError(true);
      setLoading(false);
    } else {
      fetch(`${apiUrl}/visits/get/${reservationCode}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error("Failed to fetch reservation");
          }
        })
        .then((jsonResponse) => {
          setReservation(jsonResponse);
          setLoading(false);
          setReservationCode("");
          setFailedFetch(false);
        })
        .catch((error) => {
          setLoading(false);
          setFailedFetch(true);
          setTimeout(() => {
            setFailedFetch(false);
          }, 5000);
        });
    }
  };

  const cancelReservation = (e) => {
    e.preventDefault();
    setLoading(true);
    setEmailError(false);
    setSuccess(false);
    setFailure(false);
    if (email == "") {
      setEmailError(true);
      setLoading(false);
    } else {
      fetch(
        `${apiUrl}/visits/cancel/${reservation?.reservationCode}?email=${email}`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
          },
        }
      )
        .then((response) => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error("Failed to cancel reservation");
          }
        })
        .then((jsonResponse) => {
          setReservation(jsonResponse);
          setEmail("");
          setOpen(false);
          setLoading(false);
          setSuccess(true);
          setFailure(false);
          setTimeout(() => {
            setSuccess(false);
          }, 5000);
        })
        .catch((error) => {
          setLoading(false);
          setOpen(false);
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
      <h2 className="my-5">Reservation details</h2>

      <Collapse in={failedFetch}>
        <Alert
          onClose={() => {
            setFailedFetch(false);
          }}
          severity="error"
          className="mb-3"
        >
          Failed to fetch reservation
        </Alert>
      </Collapse>

      <Collapse in={failure}>
        <Alert
          onClose={() => {
            setFailure(false);
          }}
          severity="error"
          className="mb-3"
        >
          Failed to cancel reservation
        </Alert>
      </Collapse>
      <Collapse in={success}>
        <Alert
          onClose={() => {
            setSuccess(false);
          }}
          severity="success"
          className="mb-3"
        >
          Reservation successfully cancelled
        </Alert>
      </Collapse>

      <Dialog open={open} onClose={() => setOpen(false)}>
        <DialogTitle className="mt-2 mb-2">Cancel reservation</DialogTitle>
        <DialogContent>
          <Typography className="mb-3">
            Are you sure you want to cancel your reservation{" "}
            <span className="fw-bold">{reservation?.reservationCode}</span>?
          </Typography>
          <form>
            <TextField
              error={emailError}
              onChange={(e) => setEmail(e.target.value)}
              value={email}
              id="email"
              label="Enter your email"
              helperText="Email is required"
              className="form-control"
              size="small"
              required
            />
          </form>
        </DialogContent>
        <DialogActions>
          <button
            className="btn btn-outline-danger mb-2 me-2"
            onClick={(e) => cancelReservation(e)}
          >
            Confirm delete
          </button>
          <button
            className="btn btn-outline-primary mb-2 me-2"
            onClick={() => {
              setOpen(false);
              setEmailError(false);
            }}
          >
            Cancel
          </button>
        </DialogActions>
      </Dialog>

      {!reservation?.reservationCode && (
        <form noValidate>
          <TextField
            error={reservationCodeError}
            onChange={(e) => setReservationCode(e.target.value)}
            value={reservationCode}
            id="reservation-code"
            label="Enter reservation code"
            helperText="Reservation code is required"
            className="form-control mb-3"
            size="small"
            required
          />
          <button
            type="submit"
            className="btn btn-primary mb-4"
            onClick={(e) => checkReservation(e)}
            disabled={loading}
          >
            Submit
          </button>
        </form>
      )}

      {reservation?.reservationCode && (
        <div id="reservation-details">
          <div className="mb-2">
            <span className="fw-bold">Reservation code: </span>
            {reservation?.reservationCode}
          </div>
          <div className="mb-2">
            <span className="fw-bold">Reservation time: </span>
            {reservation?.reservationTime}
          </div>
          <div className="mb-2">
            <span className="fw-bold">Specialist: </span>
            {reservation?.specialist?.name}
          </div>
          <div className="mb-4">
            <span className="fw-bold">Status: </span>
            {reservation?.visitStatus.toLowerCase()}
          </div>
          <button
            type="submit"
            className="btn btn-outline-danger mb-4 me-2"
            onClick={() => setOpen(true)}
            disabled={loading || reservation?.visitStatus !== "PENDING"}
          >
            Cancel reservation
          </button>
          <button
            type="submit"
            className="btn btn-outline-secondary mb-4"
            onClick={() => setReservation({})}
            disabled={loading}
          >
            Clear
          </button>
        </div>
      )}
    </div>
  );
}

export default ViewReservationPage;

import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import jwtDecode from "jwt-decode";
import { apiUrl } from "../App";
import DoneIcon from "@mui/icons-material/Done";
import PlayArrowIcon from "@mui/icons-material/PlayArrow";
import { Collapse, Alert } from "@mui/material";

function ReservationListPage() {
  const [reservations, setReservations] = useState([]);
  const token = localStorage.getItem("jwtToken");
  const [inProgress, setInProgress] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  let specialistId = "";

  if (token) {
    const decodedToken = jwtDecode(token);
    specialistId = decodedToken.specialistId;
  }

  const fetchReservations = () => {
    fetch(`${apiUrl}/visits/${specialistId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => response.json())
      .then((jsonResponse) => {
        const anyInProgress = jsonResponse.some(
          (reservation) => reservation.visitStatus === "IN_PROGRESS"
        );
        setInProgress(anyInProgress);
        setReservations(jsonResponse);
      });
  };

  useEffect(fetchReservations, []);

  const startVisit = (e, visitId) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (inProgress === true) {
      setError("A visit is already in progress");
    } else {
      fetch(`${apiUrl}/visits/${specialistId}/start/${visitId}?`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error("Failed to start visit");
          }
        })
        .then((jsonResponse) => {
          fetchReservations();
          setSuccess("Visit started");
          setTimeout(() => {
            setSuccess("");
          }, 5000);
        })
        .catch((error) => {
          setError("An error has ocurred");
          setTimeout(() => {
            setError("");
          }, 5000);
        });
    }
  };

  const endVisit = (e, visitId) => {
    e.preventDefault();
    setError("");
    setSuccess("");
    if (inProgress === false) {
      setError("No visits are in progress");
    } else {
      fetch(`${apiUrl}/visits/${specialistId}/end/${visitId}?`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error("Failed to start visit");
          }
        })
        .then((jsonResponse) => {
          fetchReservations();
          setSuccess("Visit ended");
          setTimeout(() => {
            setSuccess("");
          }, 5000);
        })
        .catch((error) => {
          setError("An error has ocurred");
          setTimeout(() => {
            setError("");
          }, 5000);
        });
    }
  };

  return (
    <div className="mx-3">
      <h2 className="my-5">List of reservations</h2>

      <Collapse in={!!error}>
        <Alert
          onClose={() => {
            setError("");
          }}
          severity="error"
          className="mb-3"
        >
          {error}
        </Alert>
      </Collapse>

      <Collapse in={!!success}>
        <Alert
          onClose={() => {
            setSuccess("");
          }}
          severity="success"
          className="mb-3"
        >
          {success}
        </Alert>
      </Collapse>

      <table className="table table-hover shadow p-3 mb-5 bg-body rounded align-middle">
        <thead className="table-light">
          <tr>
            <th>Reservation time</th>
            <th>Reservation code</th>
            <th>Customer email</th>
            <th>Status</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {reservations?.map((reservation) => (
            <tr key={reservation?.id} id={reservation?.id}>
              <td>{reservation?.reservationTime}</td>
              <td>{reservation?.reservationCode}</td>
              <td>{reservation?.customer?.email}</td>
              <td>{reservation?.visitStatus.toLowerCase()}</td>
              <td className="text-end">
                {reservation?.visitStatus == "PENDING" ? (
                  <button
                    className="btn btn-outline-primary me-1 my-1 btn-link"
                    title="Start"
                    disabled={inProgress}
                    onClick={(e) => startVisit(e, reservation?.id)}
                  >
                    <PlayArrowIcon />
                  </button>
                ) : (
                  <button
                    className="btn btn-outline-primary me-1 my-1 btn-link"
                    title="Complete"
                    onClick={(e) => endVisit(e, reservation?.id)}
                  >
                    <DoneIcon />
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ReservationListPage;

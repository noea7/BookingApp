import { useState, useEffect } from "react";
import { apiUrl } from "../App";

function ServiceDepartmentPage() {
  const [reservations, setReservations] = useState([]);
  const token = localStorage.getItem("jwtToken");

  const fetchReservations = () => {
    fetch(`${apiUrl}/visits/`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => response.json())
      .then((jsonResponse) => {
        setReservations(jsonResponse);
      });
  };

  useEffect(() => {
    fetchReservations();
    const interval = setInterval(fetchReservations, 3000);
    return clearInterval(interval);
  }, []);

  return (
    <div className="mx-3">
      <h2 className="my-5">List of reservations</h2>

      <table className="table table-hover shadow p-3 mb-5 bg-body rounded align-middle">
        <thead className="table-light">
          <tr>
            <th>Reservation time</th>
            <th>Specialist</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {reservations?.map((reservation) => (
            <tr key={reservation?.id} id={reservation?.id}>
              <td>{reservation?.reservationTime}</td>
              <td>{reservation?.specialist?.name}</td>
              <td>{reservation?.visitStatus.toLowerCase()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ServiceDepartmentPage;

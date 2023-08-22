import { BrowserRouter, Route, Routes } from "react-router-dom";
import jwtDecode from "jwt-decode";
import Navigation from "./components/Navigation";
import CreateReservationPage from "./pages/CreateReservation";
import ViewReservationPage from "./pages/ViewReservation";
import LoginPage from "./pages/Login";
import ReservationListPage from "./pages/ReservationList";
import ServiceDepartmentPage from "./pages/ServiceDepartment";

const apiUrl = "http://localhost:3000/api/v1";

function App() {
  const token = localStorage.getItem("jwtToken");
  let specialistId = "";

  if (token) {
    const decodedToken = jwtDecode(token);
    specialistId = decodedToken.specialistId;
  }

  return (
    <div className="App">
      <BrowserRouter>
        <Navigation />
        <div className="container-xxl">
          <Routes>
            <Route
              path="/"
              element={
                token ? <ReservationListPage /> : <CreateReservationPage />
              }
            />
            {!token && (
              <Route path="/create" element={<CreateReservationPage />} />
            )}
            {!token && <Route path="/view" element={<ViewReservationPage />} />}
            {!token && <Route path="/login" element={<LoginPage />} />}
            {token && <Route path="/list" element={<ReservationListPage />} />}
            {token && <Route path="/department" element={<ServiceDepartmentPage />} />}
          </Routes>
        </div>
      </BrowserRouter>
    </div>
  );
}

export default App;
export { apiUrl };

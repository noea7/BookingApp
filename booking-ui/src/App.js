import { BrowserRouter, Route, Routes } from "react-router-dom";
import jwtDecode from "jwt-decode";
import Navigation from "./components/Navigation";
import CreateReservationPage from "./pages/CreateReservation";
import ViewReservationPage from "./pages/ViewReservation";

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
            <Route path="/create" element={<CreateReservationPage />} />
            <Route path="/view" element={<ViewReservationPage />} />
          </Routes>
        </div>
      </BrowserRouter>
    </div>
  );
}

export default App;
export { apiUrl };
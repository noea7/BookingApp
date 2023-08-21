import { Link } from "react-router-dom";
import logout from "./logout";

function Navigation() {
  let token = localStorage.getItem("jwtToken");

  return (
    <header className="bg-light">
      <div className="container-xxl">
        <nav className="navbar navbar-expand-lg bg-light">
          <div className="container-fluid">
            <button
              className="navbar-toggler"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#navbarSupportedContent"
              aria-controls="navbarSupportedContent"
              aria-expanded="false"
              aria-label="Toggle navigation"
            >
              <span className="navbar-toggler-icon"></span>
            </button>
            <div
              className="collapse navbar-collapse"
              id="navbarSupportedContent"
            >
              <div className="navbar-nav me-auto mb-2 mb-lg-0">
                <div className="nav-item">
                  <Link to="/create" className="nav-link">
                    Reserve
                  </Link>
                </div>
                <div className="nav-item">
                  <Link to="/view" className="nav-link">
                    Check reservation
                  </Link>
                </div>
                {token && (
                  <div className="nav-item">
                    <button className="nav-link" onClick={logout}>
                      Logout
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </nav>
      </div>
    </header>
  );
}

export default Navigation;

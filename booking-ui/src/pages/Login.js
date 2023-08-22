import { useState } from "react";
import { Alert, Collapse, TextField } from "@mui/material";
import { apiUrl } from "../App";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loginFailure, setLoginFailure] = useState(false);

  const handleLogin = (event) => {
    event.preventDefault();
    setLoginFailure(false);

    fetch(`${apiUrl}/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username,
        password,
      }),
    })
      .then((response) => {
        if (response.ok) {
          return response.text();
        } else {
          throw new Error("Invalid credentials");
        }
      })
      .then((token) => {
        localStorage.setItem("jwtToken", token);
        window.location.href = "/";
      })
      .catch((error) => {
        setLoginFailure(true);
        setTimeout(() => {
          setLoginFailure(false);
        }, 5000);
      });
  };

  return (
    <div className="mx-3">
      <div className="mx-auto">
        <h2 className="my-5">Log in or register</h2>

        <Collapse in={loginFailure}>
          <Alert
            onClose={() => {
              setLoginFailure(false);
            }}
            severity="error"
            className="mb-3"
          >
            Log in failed
          </Alert>
        </Collapse>

        <TextField
          required
          onChange={(e) => setUsername(e.target.value)}
          className="form-control mb-3"
          size="small"
          id="username"
          label="Username"
          value={username}
        />
        <TextField
          required
          onChange={(e) => setPassword(e.target.value)}
          className="form-control mb-5"
          size="small"
          id="password"
          label="Password"
          type="password"
          value={password}
        />
        <button className="btn btn-primary me-2 mb-5" onClick={handleLogin}>
          Login
        </button>
      </div>
    </div>
  );
}

export default LoginPage;

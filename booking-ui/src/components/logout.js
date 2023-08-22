const logout = () => {
  localStorage.removeItem("jwtToken");
  window.location.href = "/";
};

export default logout;

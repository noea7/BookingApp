const logout = () => {
  localStorage.removeItem("jwtToken");
  window.location.reload();
};

export default logout;

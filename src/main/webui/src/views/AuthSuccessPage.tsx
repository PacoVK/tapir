import React from "react";

const AuthSuccessPage = () => {
  localStorage.removeItem("tapir");
  window.close();
  return (
    <>
      <h1>Success closing this window now</h1>
    </>
  );
};

export default AuthSuccessPage;

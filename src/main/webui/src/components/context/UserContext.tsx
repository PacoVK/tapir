import React, { useContext, useState } from "react";
import { User } from "../../types";
import useInterval from "../../hooks/useInterval";

interface IUserContext {
  user: User;
  isAdmin: boolean;
}

const defaultContext: IUserContext = {
  user: {} as User,
  isAdmin: false,
};

const REFRESH_INTERVAL = 240000;

const UserContext = React.createContext<IUserContext>(defaultContext);

export const UserProvider = ({
  fetchedUser,
  children,
}: {
  fetchedUser: User;
  children: any;
}) => {
  const [user, setUser] = useState(fetchedUser);
  const [isAdmin, setIsAdmin] = useState(fetchedUser.roles.includes("admin"));

  useInterval(async () => {
    await fetch("tapir/user");
  }, REFRESH_INTERVAL);

  return (
    <UserContext.Provider
      value={{
        user,
        isAdmin,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};

export const useUserContext = () => useContext(UserContext);

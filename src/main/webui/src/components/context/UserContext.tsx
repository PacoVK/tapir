import React, {useContext, useState} from "react";
import {User} from "../../types";

interface IUserContext {
    user: User,
    isAdmin: boolean,
}

const defaultContext: IUserContext = {
    user: {} as User,
    isAdmin: false,
}

const UserContext = React.createContext<IUserContext>(defaultContext);

export const UserProvider = ({ fetchedUser, children }: {fetchedUser: User, children: any}) => {
    const [user, setUser] = useState(fetchedUser);
    const [isAdmin, setIsAdmin] = useState(fetchedUser.roles.includes("admin"));

    return (
        <UserContext.Provider
            value={{
                user,
                isAdmin
            }}
        >
            {children}
        </UserContext.Provider>
    );
};

export const useUserContext = () =>  useContext(UserContext);
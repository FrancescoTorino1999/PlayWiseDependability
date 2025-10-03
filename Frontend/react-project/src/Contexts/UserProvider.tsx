import React, { createContext, useState, useContext, useEffect } from 'react';

const UserContext = createContext('');

export const UserProvider = ({ children }) => {
  const [usernameGlobal, setUsernameGlobal] = useState('');

  useEffect(() => {
    const savedUsername = localStorage.getItem('authToken');
    if (savedUsername) {
      setUsernameGlobal(savedUsername);
    }
  }, []);

  const updateUsername = (newUsername) => {
    setUsernameGlobal(newUsername);
    localStorage.setItem('authToken', newUsername);
  };

  return (
    <UserContext.Provider value={{ usernameGlobal, updateUsername }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => useContext(UserContext);
import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import LoginBtn from './LoginPage';

const AccountModal = ({ setProfileURL }) => {
  const [isLogin, setIsLogin] = useState(false);
  
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (token) {
      setIsLogin(true);
    }
  }, [token]);


  const handleLogOut = () => {
    setIsLogin(false);
    setProfileURL(null);
    localStorage.removeItem('token');
    localStorage.removeItem('ProfileURL');
  };

  return (
    <AccountModalDiv>
      {isLogin ? (
        <AccountLogout onClick={() => handleLogOut()}>로그아웃</AccountLogout>
      ) : (
        <AccountModalItem>
          <LoginBtn />
        </AccountModalItem>
      )}
    </AccountModalDiv>
  );
};


export default AccountModal;
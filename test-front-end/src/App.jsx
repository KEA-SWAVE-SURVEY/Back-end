import React from 'react';
import Button from '@material-ui/core/Button';
import { makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme) => ({
  root: {
    '& > *': {
      margin: theme.spacing(1),
    },
  },
}));

const App = () => {
  const classes = useStyles();
  const provider = "google"; // 로그인한 플랫폼에 따라 변경
  const client_id = "278703087355-limdvm0almc07ldn934on122iorpfdv5.apps.googleusercontent.com"; // Google API Console에서 발급한 클라이언트 ID를 입력
  const redirect_uri = "http://localhost:3000/oauth/callback/google"; // 등록한 리다이렉트 URI를 입력
  const response_type = "code";
  const scope = "openid profile email";

  const handleGoogleLogin = async () => {
    const url = `https://accounts.google.com/o/oauth2/auth?client_id=${client_id}&redirect_uri=${redirect_uri}&response_type=${response_type}&scope=${scope}`;
    window.location.href = url;
  };

  const handleCodeExchange = async (code) => {
    const response = await fetch(`http://localhost:8080/api/oauth/token`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
      },
      body: `grant_type=authorization_code&provider=${provider}&code=${code}&client_id=${client_id}&redirect_uri=${redirect_uri}`, // 인가 코드와 함께 클라이언트 ID, 리다이렉트 URI를 전달
    });
    const data = await response.json();
    console.log(data); // 토큰 정보 확인
  };
  

  React.useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get("code");
    if (code) {
      handleCodeExchange(code);
    }
  }, []);

  return (
    <div className={classes.root}>
      <Button variant="contained" color="primary" onClick={handleGoogleLogin}>
        구글 로그인
      </Button>
    </div>
  );
};

export default App;

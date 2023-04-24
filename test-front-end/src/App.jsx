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
  const goolge_provider = "google"; // 로그인한 플랫폼에 따라 변경
  const goolge_client_id = "278703087355-limdvm0almc07ldn934on122iorpfdv5.apps.googleusercontent.com"; // Google API Console에서 발급한 클라이언트 ID를 입력
  const goolge_redirect_uri = "http://localhost:3000/oauth/callback/google"; // 등록한 리다이렉트 URI를 입력
  const goolge_response_type = "code";
  const goolge_scope = "openid profile email";

  
  const kakao_provider = "kakao"; // 로그인한 플랫폼에 따라 변경
  const kakao_client_id = "4646a32b25c060e42407ceb8c13ef14a"; // Kakao API Console에서 발급한 클라이언트 ID를 입력
  const kakao_redirect_uri = "http://localhost:3000/oauth/callback/kakao"; // 등록한 리다이렉트 URI를 입력
  const kakao_response_type = "code";
  const kakao_scope = "openid profile email";

  
  const git_provider = "git"; // 로그인한 플랫폼에 따라 변경
  const git_client_id = "Iv1.986aaa4d78140fb7"; // Git API Console에서 발급한 클라이언트 ID를 입력
  const git_redirect_uri = "http://localhost:3000/oauth/callback/git"; // 등록한 리다이렉트 URI를 입력
  const git_response_type = "code";
  const git_scope = "openid profile email";

  const handleGoogleLogin = async () => {
    const url = `https://accounts.google.com/o/oauth2/auth?client_id=${goolge_client_id}&redirect_uri=${goolge_redirect_uri}&response_type=${goolge_response_type}&scope=${goolge_scope}`;
    window.location.href = url;
  };

  const handleKakaoLogin = async () => {
    const url = `https://kauth.kakao.com/oauth/authorize?client_id=${kakao_client_id}&redirect_uri=${kakao_redirect_uri}&response_type=${kakao_response_type}&scope=${kakao_scope}`;
    window.location.href = url;
  };

  const handleGitLogin = async () => {
    const url = `https://github.com/login/oauth/authorize?client_id=${git_client_id}&redirect_uri=${git_redirect_uri}&response_type=${git_response_type}&scope=${git_scope}`;
    window.location.href = url;
  };

  const handleCodeExchange = async (code) => {
    const response = await fetch(`http://localhost:8080/api/oauth/token`, {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8"
      },
      body: `grant_type=authorization_code&provider=${git_provider}&code=${code}&client_id=${git_client_id}&redirect_uri=${git_redirect_uri}`, // 인가 코드와 함께 클라이언트 ID, 리다이렉트 URI를 전달
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
      <Button variant="contained" color="primary" onClick={handleKakaoLogin}>
        카카오 로그인
      </Button>
      <Button variant="contained" color="primary" onClick={handleGitLogin}>
        깃허브 로그인
      </Button>
    </div>
  );
};

export default App;

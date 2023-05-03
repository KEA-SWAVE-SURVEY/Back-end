package com.example.demo.user.controller;


import com.example.demo.user.domain.User;
import com.example.demo.user.service.UserService2;
import com.example.demo.util.OAuth.JwtProperties;
import com.example.demo.util.OAuth.OauthToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService2 userService2;

    // 구글 추가 버전, requestParam으로 provider 받음
    @PostMapping("/oauth/token")
    public ResponseEntity getLogin(@RequestParam("code") String code, @RequestParam("provider") String provider) { //(1)
        // 넘어온 인가 코드를 통해 access_token 발급
        OauthToken oauthToken = userService2.getAccessToken(code, provider);

        // 발급 받은 accessToken 으로 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = userService2.SaveUserAndGetToken(oauthToken.getAccess_token(), provider);

        HttpHeaders headers = new HttpHeaders();

        // 1 param : header name(key), 2 param : header value
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        return ResponseEntity.ok().headers(headers).body("\"success\"");

    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) { //(1)

        User user = userService2.getUser(request);
        System.out.println(user.getUserCode());
        System.out.println(user.getEmail());
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/post")
    public ResponseEntity<Object> CurrentUser(HttpServletRequest request, @RequestBody String data) { //(1)

        User user = userService2.getUser(request);
        System.out.println(user.getUserCode());
        System.out.println(user.getEmail());
        System.out.println(user.getNickname());
        System.out.println(data);
        return ResponseEntity.ok().body(user);
    }

}

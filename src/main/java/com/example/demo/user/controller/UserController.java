package com.example.demo.user.controller;


import com.example.demo.user.domain.User;
import com.example.demo.user.service.UserService;
import com.example.demo.util.OAuth.JwtProperties;
import com.example.demo.util.OAuth.OauthToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // 프론트에서 인가코드 받아오는 url
    @GetMapping("/oauth/token")
    public ResponseEntity getLogin(@RequestParam("code") String code) { //(1)

        // 넘어온 인가 코드를 통해 access_token 발급
        OauthToken oauthToken = (OauthToken) userService.getAccessToken(code);

        // 발급 받은 accessToken 으로 카카오 회원 정보 DB 저장 후 JWT 를 생성
        String jwtToken = userService.SaveUserAndGetToken(oauthToken.getAccess_token());

        HttpHeaders headers = new HttpHeaders();

        // 1 param : header name(key), 2 param : header value
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

        return ResponseEntity.ok().headers(headers).body("success");
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) { //(1)

        User user = userService.getUser(request);
        System.out.println(user.getUserCode());
        System.out.println(user.getKakaoEmail());
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/post")
    public ResponseEntity<Object> CurrentUser(HttpServletRequest request, @RequestBody String data) { //(1)

        User user = userService.getUser(request);
        System.out.println(user.getUserCode());
        System.out.println(user.getKakaoEmail());
        System.out.println(user.getKakaoNickname());
        System.out.println(data);
        return ResponseEntity.ok().body(user);
    }

}

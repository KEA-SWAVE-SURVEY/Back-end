package com.example.demo.util.page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort.*;

@Setter @Getter
@NoArgsConstructor
public class PageRequest {
    private String method; // grid or list
    private String sortProperties; // date or title
    private int page = 1;
    private int size = 5;
    private String direct;
    private Direction direction;

    public PageRequest(String method, String sortProperties, String direct) {
        this.method = method;
        this.sortProperties = sortProperties;
        this.direct = direct;
        this.direction = getDirection(direct);
    }

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 5;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection(String direct) {
        if (direct.equals("ascending")) { // ascending
            this.direction = Direction.ASC;
        }
        else { // descending
            this.direction = Direction.DESC;
        }
        return direction;
    }

    /**
     * Pageable 만들어서 PageReauest.of() 로 보내주기
     * @param sortProperties : regDate or title
     * @param direction : ascending or descending
     * @return
     */
    public org.springframework.data.domain.PageRequest of(String sortProperties, Direction direction) {
        return org.springframework.data.domain.PageRequest.of(page-1, size, direction, sortProperties);
    }
    // 날짜 순 으로 Page 정렬
    public org.springframework.data.domain.PageRequest ofDate() {
        return org.springframework.data.domain.PageRequest.of(page - 1, size, direction, "regDate");
    }

    // ㄱㄴㄷ 순으로 Page 정렬
    public org.springframework.data.domain.PageRequest ofABC() {
        return org.springframework.data.domain.PageRequest.of(page-1, size, direction, "title");
    }
}
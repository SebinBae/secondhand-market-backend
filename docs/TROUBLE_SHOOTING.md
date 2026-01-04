# Trouble Shooting
프로젝트를 진행하면서 발생한 문제점들과 해결법 서술합니다.

## io.jsonwebtoken.Jwts 클래스
토큰을 검증(Parsing)하는 과정에서 Jwts 클래스의 내부 메소드인 parseClaimsJws()와 parseClaimsJwt() 차이를 착각하여 페이로드 검증에 통과하여도 토큰 검증에 실패하는 문제점이 발생했었습니다. 

parseClaimsJws() 메소드 : 서명이 적용된 JWT를 파싱할 때 사용하는 메소드
parseClaimsJwt() 메소드 : 서명이 적용되지 않은 JWT를 파싱할 때 사용하는 메소드

## Spring Boot 3.x와 Querydsl
Spring Boot 2.x에서 Querydsl을 세팅하여 사용하다가 Spring Boot 3.x에서 Querydsl을 세팅하여
사용하니 다음과 같은 차이점이 있었습니다.

+ build.gradle 설정 차이
+ Querydsl이 생성하는 Q클래스가 Spring Boot 3.x에서는 jakarta 기반으로 변경되었음.

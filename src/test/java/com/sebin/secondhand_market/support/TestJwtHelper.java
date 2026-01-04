package com.sebin.secondhand_market.support;

import com.sebin.secondhand_market.global.security.JwtProvider;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestJwtHelper {

  @Autowired
  JwtProvider jwtProvider;

  public String create(UUID userId){
    return jwtProvider.createToken(userId);
  }
}

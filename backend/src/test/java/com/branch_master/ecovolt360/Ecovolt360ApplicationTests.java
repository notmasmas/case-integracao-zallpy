package com.branch_master.ecovolt360;


import com.branch_master.ecovolt360.auth.application.port.PasswordVerifier;
import com.branch_master.ecovolt360.auth.application.port.TokenIssuer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "spring.flyway.enabled=false")
class Ecovolt360ApplicationTests {

	@MockitoBean
	private PasswordVerifier passwordVerifier;

	@MockitoBean
	private TokenIssuer tokenIssuer;


	@Test
	void contextLoads() {
	}

}

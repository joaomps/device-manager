package com.joaomps.devicemanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@TestPropertySource(properties = {
		"spring.jpa.hibernate.ddl-auto=none",
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver"
})
@AutoConfigureTestDatabase
class DeviceManagerApplicationTests {

	@Test
	void contextLoads() {
		// Empty test
	}

}

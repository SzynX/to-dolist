package org.example.to_dolist;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TodoListApplicationTest {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodTest() {

		String[] args = {};
		TodoListApplication.main(args);
	}
}

package com.xxl;

import com.xxl.domain.Student;
import com.xxl.domain.StudentMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class ApplicationTests {

	@Autowired
	private StudentMapper studentMapper;

	@Test
	@Rollback
	public void findByName() throws Exception {
		studentMapper.insert("AAA", 20);
		Student u = studentMapper.findByName("AAA");
		Assert.assertEquals(20, u.getAge().intValue());
	}

}
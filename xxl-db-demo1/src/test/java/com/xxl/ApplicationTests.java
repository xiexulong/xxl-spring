package com.xxl;

import com.xxl.domain.p.Student;
import com.xxl.domain.p.StudentRepository;
import com.xxl.domain.s.Message;
import com.xxl.domain.s.MessageRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private MessageRepository messageRepository;

	@Before
	public void setUp() {
	}

	/**
	 drop database if exists xxl-db-demo2;
	 create database if not exists xxl-db-demo2 default charset=UTF8MB4;
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {

		studentRepository.save(new Student("aaa", 10));
		studentRepository.save(new Student("bbb", 20));
		studentRepository.save(new Student("ccc", 30));
		studentRepository.save(new Student("ddd", 40));
		studentRepository.save(new Student("eee", 50));

		Assert.assertEquals(5, studentRepository.findAll().size());

		messageRepository.save(new Message("o1", "aaaaaaaaaa"));
		messageRepository.save(new Message("o2", "bbbbbbbbbb"));
		messageRepository.save(new Message("o3", "cccccccccc"));

		Assert.assertEquals(3, messageRepository.findAll().size());

	}


}

package acmecollege.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDStudent extends JUnitBase{
	
	private EntityManager manager;
	private EntityTransaction entityTrans;
	
	private static Student student;
	
	private static final String FIRST_NAME = "Abdul";
	private static final String LAST_NAME = "Mazed";
	
	
	@BeforeAll
	static void setupInit() {
		student = new Student();
		student.setFirstName(FIRST_NAME);
		student.setLastName(LAST_NAME);
	}
	
	@BeforeEach
	void setup() {
		manager = getEntityManager();
		entityTrans = manager.getTransaction();
	}
	
	@AfterEach
	void tearDown() {
		manager.close();
	}
	
	
	@Test
	void test01_Empty() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		
		Root<Student> root = query.from(Student.class);
		query.select(builder.count(root));
		
		TypedQuery<Long> typedQuery = manager.createQuery(query);
		
		long result = typedQuery.getSingleResult();

		assertThat(result, is(comparesEqualTo(0L)));
	}
	
	@Test
	void test02_Create() {
		entityTrans.begin();
		student = new Student();
		student.setFirstName("Abdul");
		student.setLastName("Mazed");
		manager.persist(student);
		entityTrans.commit();

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr where cr.id = :id
		Root<Student> root = query.from(Student.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq = manager.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
	}
	
	@Test
	void test03_CreateInvalid() {
		entityTrans.begin();
		Student student2 = new Student();
		student.setFirstName(FIRST_NAME);
		//student.setLastName(LAST_NAME);
		// We expect a failure because course is part of the composite key
		assertThrows(PersistenceException.class, () -> manager.persist(student2));
		entityTrans.commit();
	}

	@Test
	void test04_Read() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for Student
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select cr from Student
		Root<Student> root = query.from(Student.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Student> tq = manager.createQuery(query);
		// Get the result as row count
		List<Student> students = tq.getResultList();

		assertThat(students, contains(equalTo(student)));
	}

	@Test
	void test06_Update() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select cr from Contact cr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Student> tq = manager.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		String newFirstName = "Heungmin";
		String newLastName = "Son";


		entityTrans.begin();
		returnedStudent.setFirstName(newFirstName);
		returnedStudent.setLastName(newLastName);

		manager.merge(returnedStudent);
		entityTrans.commit();

		returnedStudent = tq.getSingleResult();
		
		assertThat(returnedStudent.getFirstName(), equalTo(newFirstName));
		assertThat(returnedStudent.getLastName(), equalTo(newLastName));

	}

	@Test
	void test09_Delete() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Student> query = builder.createQuery(Student.class);
		// Select cr from CourseRegistration cr
		Root<Student> root = query.from(Student.class);
		query.select(root);
		query.where(builder.equal(root.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Student> tq = manager.createQuery(query);
		tq.setParameter("id", student.getId());
		// Get the result as row count
		Student returnedStudent = tq.getSingleResult();

		entityTrans.begin();
		// Add another row to db to make sure only the correct row is deleted
		Student student2 = new Student();
		student2.setFirstName("Manuel");
		student2.setLastName("Turizo");

		manager.persist(student2);
		entityTrans.commit();

		entityTrans.begin();
		manager.remove(returnedStudent);
		entityTrans.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(p) from Professor p where p.id = :id
		Root<Student> root2 = query2.from(Student.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Student_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = manager.createQuery(query2);
		tq2.setParameter("id", returnedStudent.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = manager.createQuery(query2);
		tq3.setParameter("id", student2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}

}

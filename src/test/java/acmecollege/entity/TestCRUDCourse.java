package acmecollege.entity;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDCourse extends JUnitBase{
	private EntityManager manager;
	private EntityTransaction entityTrans;
	
	private static Course course;
	
	private static final String COURSE_CODE = "CST8277";
	private static final String COURSE_TITLE = "Enterprise Application Programming";
	private static final int YEAR = 2023;
	private static final String SEMESTER = "Spring";
	private static final int CREDIT_UNITS = 3;
	private static final byte ONLINE = 0;
	
	@BeforeAll
	static void setupAllInit() {
		course = new Course();
		course.setCourse("CST8277", "Enterprise Application Programming", 2023, "SPRING", 3, (byte) 0);

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
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr
		Root<Course> root = query.from(Course.class);
		query.select(builder.count(root));
		// Create query and set the parameter
		TypedQuery<Long> tq = manager.createQuery(query);
		// Get the result as row count
		long result = tq.getSingleResult();

		assertThat(result, is(comparesEqualTo(0L)));

	}

	@Test
	void test02_Create() {
		entityTrans.begin();
		course = new Course();
		course.setCourseCode(COURSE_CODE);
		course.setCourseTitle(COURSE_TITLE );
		course.setYear(YEAR);
		course.setSemester(SEMESTER);
		course.setCreditUnits(CREDIT_UNITS);
		course.setOnline(ONLINE);
		manager.persist(course);
		entityTrans.commit();

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		// Select count(cr) from CourseRegistration cr where cr.id = :id
		Root<Course> root = query.from(Course.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq = manager.createQuery(query);
		tq.setParameter("id", course.getId());
		// Get the result as row count
		long result = tq.getSingleResult();

		// There should only be one row in the DB
		assertThat(result, is(greaterThanOrEqualTo(1L)));
//		assertEquals(result, 1);
	}
	
	
	@Test
	void test03_CreateInvalid() {
		entityTrans.begin();
		CourseRegistration course2 = new CourseRegistration();
		course.setCourseCode(COURSE_CODE);
		//course.setCourseTitle(COURSE_TITLE );
		course.setYear(YEAR);
		course.setSemester(SEMESTER);
		course.setCreditUnits(CREDIT_UNITS);
		course.setOnline(ONLINE);
		// We expect a failure because course is part of the composite key
		assertThrows(PersistenceException.class, () -> manager.persist(course2));
		entityTrans.commit();
	}
	
	@Test
	void test04_Read() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for CourseRegistration
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		// Select cr from CourseRegistration cr
		Root<Course> root = query.from(Course.class);
		query.select(root);
		// Create query and set the parameter
		TypedQuery<Course> tq = manager.createQuery(query);
		// Get the result as row count
		List<Course> courses = tq.getResultList();

		assertThat(courses, contains(equalTo(course)));
	}

	@Test
	void test06_Update() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		// Select cr from Contact cr
		Root<Course> root = query.from(Course.class);
		query.select(root);
		query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Course> tq = manager.createQuery(query);
		tq.setParameter("id", course.getId());
		// Get the result as row count
		Course returnedCourse = tq.getSingleResult();

		String newCourse_Code = "CST8276";
		String newCourse_Title = "Advanced Database Topics";
		int newYear = 2023;
		String newSemester = "SPRING";
		int newCredit_Units = 2;
		byte newOnline = 1;

		
		entityTrans.begin();
		returnedCourse.setCourseCode(newCourse_Code);
		returnedCourse.setCourseTitle(newCourse_Title);
		returnedCourse.setYear(newYear);
		returnedCourse.setSemester(newSemester);
		returnedCourse.setCreditUnits(newCredit_Units);
		returnedCourse.setOnline(newOnline);
		
		manager.merge(returnedCourse);
		entityTrans.commit();

		returnedCourse = tq.getSingleResult();

		assertThat(returnedCourse.getCourseCode(), equalTo(newCourse_Code));
		assertThat(returnedCourse.getCourseTitle(), equalTo(newCourse_Title));
		assertThat(returnedCourse.getYear(), equalTo(newYear));
		assertThat(returnedCourse.getSemester(), equalTo(newSemester));
		assertThat(returnedCourse.getCreditUnits(), equalTo(newCredit_Units));
		assertThat(returnedCourse.getOnline(), equalTo(newOnline));
		
	}
	
	@Test
	void test09_Delete() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		// Create query for Contact
		CriteriaQuery<Course> query = builder.createQuery(Course.class);
		// Select cr from CourseRegistration cr
		Root<Course> root = query.from(Course.class);
		query.select(root);
		query.where(builder.equal(root.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Course> tq = manager.createQuery(query);
		tq.setParameter("id", course.getId());
		// Get the result as row count
		Course returnedCourse = tq.getSingleResult();

		entityTrans.begin();
		// Add another row to db to make sure only the correct row is deleted
		Course course2 = new Course();
		course2.setCourseCode("CST8109");
		course2.setCourseTitle("Network Programming");
		course2.setYear(2022);
		course2.setSemester("SPRING");
		course2.setCreditUnits(2);
		course2.setOnline((byte) 0);
		
		manager.persist(course2);
		entityTrans.commit();

		entityTrans.begin();
		manager.remove(returnedCourse);
		entityTrans.commit();

		// Create query for long as we need the number of found rows
		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		// Select count(p) from Professor p where p.id = :id
		Root<Course> root2 = query2.from(Course.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Course_.id), builder.parameter(Integer.class, "id")));
		// Create query and set the parameter
		TypedQuery<Long> tq2 = manager.createQuery(query2);
		tq2.setParameter("id", returnedCourse.getId());
		// Get the result as row count
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		// Create query and set the parameter
		TypedQuery<Long> tq3 = manager.createQuery(query2);
		tq3.setParameter("id", course2.getId());
		// Get the result as row count
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}
	
}

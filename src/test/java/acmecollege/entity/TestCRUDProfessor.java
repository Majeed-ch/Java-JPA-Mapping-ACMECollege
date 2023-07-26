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
public class TestCRUDProfessor extends JUnitBase{
	private EntityManager manager;
	private EntityTransaction entityTrans;
	
	private static Professor professor;
	
	private static final String FIRST_NAME = "Abdul";
	private static final String LAST_NAME = "Mazed";
	private static final String DEPARTMENT = "IT";
	
	
	@BeforeAll
	static void setupAllInit() {
		professor = new Professor();
		professor.setFirstName(FIRST_NAME);
		professor.setLastName(LAST_NAME);
		professor.setDepartment(DEPARTMENT);

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
		Root<Professor> root = query.from(Professor.class);
		query.select(builder.count(root));
		TypedQuery<Long> tq = manager.createQuery(query);
		long result = tq.getSingleResult();

		assertThat(result, is(comparesEqualTo(0L)));

	}

	@Test
	void test02_Create() {
		entityTrans.begin();
		professor = new Professor();
		professor.setFirstName(FIRST_NAME);
		professor.setLastName(LAST_NAME);
		professor.setDepartment(DEPARTMENT);
		
		manager.persist(professor);
		entityTrans.commit();

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
		TypedQuery<Long> tq = manager.createQuery(query);
		tq.setParameter("id", professor.getId());
		long result = tq.getSingleResult();

		assertThat(result, is(greaterThanOrEqualTo(1L)));
	}


	@Test
	void test03_CreateInvalid() {
		entityTrans.begin();
		Professor professor2 = new Professor();
		professor.setFirstName(FIRST_NAME);
		professor.setLastName(LAST_NAME);
		professor.setDepartment(DEPARTMENT);
		assertThrows(PersistenceException.class, () -> manager.persist(professor2));
		entityTrans.commit();
	}

	@Test
	void test04_Read() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(root);
		TypedQuery<Professor> tq = manager.createQuery(query);
		List<Professor> professors = tq.getResultList();

		assertThat(professors, contains(equalTo(professor)));
	}

	@Test
	void test06_Update() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(root);
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
		TypedQuery<Professor> tq = manager.createQuery(query);
		tq.setParameter("id", professor.getId());
		Professor returnedProfessor = tq.getSingleResult();

		String newFirstName = "Heungmin";
		String newLastName = "Son";
		String newDepartment = "IT Support";


		entityTrans.begin();
		returnedProfessor.setFirstName(newFirstName);
		returnedProfessor.setLastName(newLastName);
		returnedProfessor.setDepartment(newDepartment);

		manager.merge(returnedProfessor);
		entityTrans.commit();

		returnedProfessor = tq.getSingleResult();
		
		assertThat(returnedProfessor.getFirstName(), equalTo(newFirstName));
		assertThat(returnedProfessor.getLastName(), equalTo(newLastName));
		assertThat(returnedProfessor.getDepartment(), equalTo(newDepartment));
		

	}

	@Test
	void test09_Delete() {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Professor> query = builder.createQuery(Professor.class);
		Root<Professor> root = query.from(Professor.class);
		query.select(root);
		query.where(builder.equal(root.get(Professor_.id), builder.parameter(Integer.class, "id")));
		TypedQuery<Professor> tq = manager.createQuery(query);
		tq.setParameter("id", professor.getId());
		Professor returnedProfessor = tq.getSingleResult();

		entityTrans.begin();
		Professor professor2 = new Professor();
		professor2.setFirstName("Manuel");
		professor2.setLastName("Turizo");
		professor2.setDepartment("Cloud");
		
		manager.persist(professor2);
		entityTrans.commit();

		entityTrans.begin();
		manager.remove(returnedProfessor);
		entityTrans.commit();

		CriteriaQuery<Long> query2 = builder.createQuery(Long.class);
		Root<Professor> root2 = query2.from(Professor.class);
		query2.select(builder.count(root2));
		query2.where(builder.equal(root2.get(Professor_.id), builder.parameter(Integer.class, "id")));
		TypedQuery<Long> tq2 = manager.createQuery(query2);
		tq2.setParameter("id", returnedProfessor.getId());
		long result = tq2.getSingleResult();
		assertThat(result, is(equalTo(0L)));

		TypedQuery<Long> tq3 = manager.createQuery(query2);
		tq3.setParameter("id", professor2.getId());
		result = tq3.getSingleResult();
		assertThat(result, is(equalTo(1L)));
	}


	
}

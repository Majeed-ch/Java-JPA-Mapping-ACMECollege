package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-08-29T15:22:33.304-0400")
@StaticMetamodel(CourseRegistration.class)
public class CourseRegistration_ {
	public static volatile SingularAttribute<CourseRegistration, CourseRegistrationPK> id;
	public static volatile SingularAttribute<CourseRegistration, Student> student;
	public static volatile SingularAttribute<CourseRegistration, Course> course;
	public static volatile SingularAttribute<CourseRegistration, Professor> professor;
	public static volatile SingularAttribute<CourseRegistration, Integer> numericGrade;
	public static volatile SingularAttribute<CourseRegistration, String> letterGrade;
}

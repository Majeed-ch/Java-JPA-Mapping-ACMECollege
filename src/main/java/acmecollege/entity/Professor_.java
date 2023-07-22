package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2023-07-22T19:21:55.883-0400")
@StaticMetamodel(Professor.class)
public class Professor_ extends PojoBase_ {
	public static volatile SingularAttribute<Professor, String> firstName;
	public static volatile SingularAttribute<Professor, String> lastName;
	public static volatile SingularAttribute<Professor, String> department;
	public static volatile SetAttribute<Professor, CourseRegistration> courseRegistrations;
}
